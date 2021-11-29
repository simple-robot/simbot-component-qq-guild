package love.forte.simbot.tencentguild.core.internal

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.LoggerFactory
import love.forte.simbot.tencentguild.*
import love.forte.simbot.tencentguild.api.*
import org.slf4j.Logger
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.CoroutineContext
import kotlin.math.max


/**
 * implementation for [TencentBot].
 * @author ForteScarlet
 */
internal class TencentBotImpl(
    override val ticket: TicketImpl,
    private val config: TencentBotConfiguration
) : TencentBot {
    private val parentJob: Job
    override val coroutineContext: CoroutineContext
    private val httpClient: HttpClient = config.httpClient
    private val url: Url = config.serverUrl
    private val decoder: Json = config.decoder
    private val processorQueue: ConcurrentLinkedQueue<suspend Signal.Dispatch.(Json) -> Unit> = ConcurrentLinkedQueue()

    init {
        parentJob = SupervisorJob(config.parentJob)
        coroutineContext = config.coroutineContext + parentJob + CoroutineName("TencentBot.${ticket.appId}")
    }

    override fun processor(processor: suspend Signal.Dispatch.(decoder: Json) -> Unit) {
        processorQueue.add(processor)
    }

    override suspend fun start(): Boolean {
        if (::clients.isInitialized) return false


        val requestToken = ticket.botToken
        var sharedIterFactory = config.sharedIterFactory
        lateinit var sharedIter: IntIterator

        val gatewayInfo: GatewayInfo

        // gateway info.
        var totalShared = config.totalShared
        if (totalShared > 0) {
            gatewayInfo = GatewayApis.Normal.request(
                client = httpClient,
                server = url,
                token = requestToken,
                decoder = decoder
            )
        } else {
            val info = GatewayApis.Shared.request(
                client = httpClient,
                server = url,
                token = requestToken,
                decoder = decoder
            )
            totalShared = info.shards
            gatewayInfo = info
        }

        sharedIter = sharedIterFactory(totalShared)

        val clientList = sharedIter.asFlow()
            .map { shared -> Shared(shared, totalShared) }
            .map { shared -> createClient(shared, gatewayInfo) }
            .toList()


        this.clients = clientList

        return true
    }

    override suspend fun cancel(): Boolean {
        if (parentJob.isCancelled) return false
        parentJob.cancelAndJoin()
        return true
    }

    override suspend fun join() {
        parentJob.join()
    }

    override val totalShared: Int = config.totalShared

    override lateinit var clients: List<ClientImpl>

    internal inner class ClientImpl(
        override val shared: Shared,
        private var heartbeatJob: Job,
        private var processingJob: Job,
        private val _s: AtomicLong,
        private var session: DefaultClientWebSocketSession
    ) : TencentBot.Client {
        override val bot: TencentBot get() = this@TencentBotImpl
        override val s: Long get() = _s.get()

        private var resumeJob = launch {
            val closed = session.closeReason.await()
            resume(closed)
        }


        /**
         * 重新连接。
         */
        private suspend fun resume(closeReason: CloseReason?) {
            if (closeReason == null) {
                return
            }

            heartbeatJob.cancel()
            processingJob.cancel()



            TODO()
        }
    }

    private suspend fun createSession(shared: Shared, gatewayInfo: GatewayInfo): SessionInfo {
        val requestToken = ticket.botToken

        val logger = LoggerFactory.getLogger("love.forte.simbot.tencentguild.bot.${ticket.appId}.$shared")
        val intents = config.intentsForSharedFactory(shared.value)
        val prop = config.clientPropertiesFactory(shared.value)

        val identify = Signal.Identify(
            data = Signal.Identify.Data(
                token = requestToken,
                intents = intents,
                shard = shared,
                properties = prop,
            )
        )

        val url = gatewayInfo.url

        val seq = AtomicLong(-1)

        val session = httpClient.webSocketSession {
            url(url)
        }

        // receive Hello
        var hello: Signal.Hello? = null
        while (session.isActive) {
            val h = waitForHello(decoder) { session.incoming.receive() }
            if (h != null) {
                hello = h
                break
            }
        }
        if (hello == null) {
            session.closeReason.await().err()
        }

        logger.info("Received Hello: {}", hello)

        // 鉴权
        // see https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#%E9%89%B4%E6%9D%83ß
        session.send(decoder.encodeToString(Signal.Identify.serializer(), identify))

        // wait for Ready event
        var readyEventData: EventSignals.Other.ReadyEvent.Data? = null
        while (session.isActive) {
            val ready = waitForReady(decoder) { session.incoming.receive() }
            if (ready != null) {
                readyEventData = ready
                break
            }
        }
        if (readyEventData == null) {
            session.closeReason.await().err()
        }
        logger.info("Ready Event data: {}", readyEventData)

        val heartbeatInterval = hello.data.heartbeatInterval
        val helloIntervalFactory: () -> Long = {
            val r = ThreadLocalRandom.current().nextLong(5000)
            if (r > heartbeatInterval) 0 else heartbeatInterval - r
        }

        // heartbeat Job
        val heartbeatJob = session.launch {
            val serializer = Signal.Heartbeat.serializer()
            while (this.isActive) {
                delay(helloIntervalFactory())
                val hb = Signal.Heartbeat(seq.get().takeIf { it >= 0 })
                session.send(decoder.encodeToString(serializer, hb))
            }
        }


        return SessionInfo(session, seq, heartbeatJob, logger)
    }

    /**
     *
     */
    private suspend fun createClient(shared: Shared, gatewayInfo: GatewayInfo): ClientImpl {

        val sessionInfo = createSession(shared, gatewayInfo)
        val (session, seq, heartbeatJob, logger) = sessionInfo

        val processingJob = processEvent(sessionInfo)

        return ClientImpl(shared, heartbeatJob, processingJob, seq, session)
    }


    private suspend fun processEvent(sessionInfo: SessionInfo): Job {
        val dispatchSerializer = Signal.Dispatch.serializer()
        val logger = sessionInfo.logger
        val seq = sessionInfo.seq

        return sessionInfo.session.incoming.receiveAsFlow().mapNotNull {
            when (it) {

                is Frame.Text -> {
                    val eventString = it.readText()
                    val jsonElement = decoder.parseToJsonElement(eventString)
                    // maybe op 11: heartbeat ack
                    val op = jsonElement.jsonObject["op"]?.jsonPrimitive?.int ?: return@mapNotNull null
                    when (op) {
                        Opcode.HeartbeatACK.code -> null
                        Opcode.Dispatch.code -> {
                            // is dispatch
                            decoder.decodeFromJsonElement(dispatchSerializer, jsonElement)
                        }
                        else -> null
                    }
                }
                is Frame.Binary -> {
                    logger.info("Frame.Binary: {}", it)
                    null
                }
                is Frame.Close -> {
                    // closed. 不需要处理，大概
                    logger.info("Frame.Close: {}", it)
                    null
                }
                is Frame.Ping -> {
                    // nothing
                    logger.info("Frame.Ping: {}", it)
                    null
                }
                is Frame.Pong -> {
                    // nothing
                    logger.info("Frame.Pong: {}", it)
                    null
                }
                else -> {
                    logger.info("Unknown frame: {}", it)
                    null
                }
            }
        }.onEach { dispatch ->
            val nowSeq = dispatch.seq
            processorQueue.forEach { p ->
                p(dispatch, decoder)
            }
            // 留下最大的值。
            seq.updateAndGet { prev -> max(prev, nowSeq) }
        }.launchIn(this@TencentBotImpl)
    }


}

private data class SessionInfo(
    val session: DefaultClientWebSocketSession,
    val seq: AtomicLong,
    val heartbeatJob: Job,
    val logger: Logger
)


private inline fun waitForHello(decoder: Json, frameBlock: () -> Frame): Signal.Hello? {
    val frame = frameBlock()
    var hello: Signal.Hello? = null
    // for hello
    if (frame is Frame.Text) {
        val json = decoder.parseToJsonElement(frame.readText())
        if (json.jsonObject["op"]?.jsonPrimitive?.int == Opcode.Hello.code) {
            hello = decoder.decodeFromJsonElement(Signal.Hello.serializer(), json)
        }
    }
    return hello
}


private inline fun waitForReady(decoder: Json, frameBlock: () -> Frame): EventSignals.Other.ReadyEvent.Data? {
    val frame = frameBlock()
    var readyEvent: EventSignals.Other.ReadyEvent.Data? = null
    // for hello
    if (frame is Frame.Text) {
        val json = decoder.parseToJsonElement(frame.readText())
        if (json.jsonObject["op"]?.jsonPrimitive?.int == Opcode.Dispatch.code) {
            val dis = decoder.decodeFromJsonElement(Signal.Dispatch.serializer(), json)
            if (dis.type == EventSignals.Other.ReadyEvent.type) {
                readyEvent = decoder.decodeFromJsonElement(EventSignals.Other.ReadyEvent.decoder, dis.data)
            }
        }
    }
    return readyEvent
}


@Serializable
internal data class TicketImpl(
    @SerialName("app_id")
    override val appId: String,
    @SerialName("app_key")
    override val appKey: String,
    override val token: String
) : TencentBot.Ticket {
    @Transient
    override val botToken: String = "Bot $appId.$token"
}