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
import love.forte.simbot.tencentguild.api.GatewayApis
import love.forte.simbot.tencentguild.api.GatewayInfo
import love.forte.simbot.tencentguild.api.request
import love.forte.simbot.tencentguild.api.user.GetBotInfoApi
import org.slf4j.Logger
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime


internal fun checkResumeCode(code: Short): Boolean {
    return when (code.toInt()) {
        // 4008  发送 payload 过快，请重新连接，并遵守连接后返回的频控信息
        // 4009  连接过期，请重连
        4008, 4009 -> true
        // 4900+ 内部错误，请重连
        else -> code >= 4900
    }
}


/**
 * implementation for [TencentBot].
 * @author ForteScarlet
 */
internal class TencentBotImpl(
    override val ticket: TicketImpl,
    override val configuration: TencentBotConfiguration
) : TencentBot {

    // verify bot with bot info api.
    override val botInfo: TencentBotInfo by lazy {
        runBlocking {
            GetBotInfoApi.request(this@TencentBotImpl)
        }
    }

    val logger = LoggerFactory.getLogger("love.forte.simbot.tencentguild.bot.${ticket.appId}")

    private val parentJob: Job
    override val coroutineContext: CoroutineContext
    private val httpClient: HttpClient get() = configuration.httpClient
    private val url: Url get() = configuration.serverUrl
    private val decoder: Json get() = configuration.decoder

    private val processorQueue: ConcurrentLinkedQueue<suspend Signal.Dispatch.(Json) -> Unit> = ConcurrentLinkedQueue()

    init {
        parentJob = SupervisorJob(configuration.parentJob)
        coroutineContext = configuration.coroutineContext + parentJob + CoroutineName("TencentBot.${ticket.appId}")
    }

    override fun processor(processor: suspend Signal.Dispatch.(decoder: Json) -> Unit) {
        processorQueue.add(processor)
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun start(): Boolean {
        if (::clients.isInitialized) return false


        val requestToken = ticket.botToken
        val shardIterFactory = configuration.shardIterFactory
        lateinit var shardIter: IntIterator

        val gatewayInfo: GatewayInfo

        // gateway info.
        var totalShard = configuration.totalShard
        if (totalShard > 0) {
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
            totalShard = info.shards
            gatewayInfo = info
        }

        shardIter = shardIterFactory(totalShard)

        val clientList = shardIter.asFlow()
            .map { shard ->
                logger.debug("Ready for shard $shard in $totalShard")
                Shard(shard, totalShard)
            }
            .buffer(totalShard)
            .map { shard ->
                logger.debug("Create client for shard $shard")
                createClient(shard, gatewayInfo).also { client: ClientImpl ->
                    val time = client.nextDelay
                    logger.debug("Client {} for shard {}", client, shard)
                    if (time.inWholeMilliseconds > 0) {
                        logger.debug("delay wait {} for next......", time)
                        delay(time)
                    }


                }
            }.toList()


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

    override val totalShared: Int = configuration.totalShard

    override lateinit var clients: List<ClientImpl>

    internal inner class ClientImpl(
        override val shard: Shard,
        val sessionData: EventSignals.Other.ReadyEvent.Data,
        private var heartbeatJob: Job,
        private var processingJob: Job,
        private val _seq: AtomicLong,
        private var session: DefaultClientWebSocketSession,
        private val logger: Logger
    ) : TencentBot.Client {
        @OptIn(ExperimentalTime::class)
        val nextDelay: Duration = if (shard.total - shard.value == 1) 0.milliseconds else 5.seconds

        override val bot: TencentBot get() = this@TencentBotImpl
        override val seq: Long get() = _seq.get()
        override val isActive: Boolean get() = session.isActive
        private val _resuming = AtomicBoolean(false)
        override val isResuming: Boolean get() = _resuming.get()

        private var resumeJob = launch {
            val closed = session.closeReason.await()
            resume(closed)
        }

        override fun toString(): String {
            return "TencentBot.Client(shard=$shard, sessionData=$sessionData)"
        }

        /**
         * 重新连接。
         * // TODO 增加日志
         */
        private suspend fun resume(closeReason: CloseReason?) {
            if (closeReason == null) {
                logger.warn("Client $this was closed, but no reason. stop this client without any action, including resuming.")
                return
            }

            val code = closeReason.code
            if (!checkResumeCode(code)) return

            while (!_resuming.compareAndSet(false, true)) {
                logger.info("In resuming now, delay 100ms")
                delay(100)
            }

            logger.info("Resume. reason: $closeReason")

            try {

                heartbeatJob.cancel()
                processingJob.cancel()
                heartbeatJob.join()
                processingJob.join()

                val gatewayInfo = GatewayApis.Normal.request(
                    this@TencentBotImpl.httpClient,
                    server = this@TencentBotImpl.url,
                    token = this@TencentBotImpl.ticket.botToken,
                    decoder = this@TencentBotImpl.decoder,
                )

                val resumeSession = resumeSession(gatewayInfo, sessionData, _seq, logger)
                val (session, _, heartbeatJob, _, _) = resumeSession
                this.session = session
                this.heartbeatJob = heartbeatJob
                val processingJob = processEvent(resumeSession)
                this.processingJob = processingJob
                this.resumeJob = this@TencentBotImpl.launch {
                    val closed = session.closeReason.await()
                    resume(closed)
                }
            } finally {
                _resuming.compareAndSet(true, false)
            }

        }


    }

    private suspend fun createSession(shard: Shard, gatewayInfo: GatewayInfo): SessionInfo {
        val requestToken = ticket.botToken

        val logger = LoggerFactory.getLogger("love.forte.simbot.tencentguild.bot.${ticket.appId}.$shard")
        val intents = configuration.intentsForShardFactory(shard.value)
        val prop = configuration.clientPropertiesFactory(shard.value)

        val identify = Signal.Identify(
            data = Signal.Identify.Data(
                token = requestToken,
                intents = intents,
                shard = shard,
                properties = prop,
            )
        )

        val seq = AtomicLong(-1)

        val session = httpClient.ws { gatewayInfo }

        kotlin.runCatching {

            // receive Hello
            val hello = session.waitHello()

            logger.debug("Received Hello: {}", hello)
            // 鉴权
            // see https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#%E9%89%B4%E6%9D%83ß
            session.send(decoder.encodeToString(Signal.Identify.serializer(), identify))

            // wait for Ready event
            var readyEventData: EventSignals.Other.ReadyEvent.Data? = null
            while (session.isActive) {
                val ready = waitForReady(decoder) { session.incoming.receive() }
                logger.debug("ready: $ready")
                if (ready != null) {
                    readyEventData = ready
                    break
                }
            }
            if (readyEventData == null) {
                println("canceled.")
                session.closeReason.await().err()
            }
            logger.info("Ready Event data: {}", readyEventData)

            val heartbeatJob = session.heartbeatJob(hello, seq)

            return SessionInfo(session, seq, heartbeatJob, logger, readyEventData)
        }.getOrElse {
            // logger.error(it.localizedMessage, it)
            session.closeReason.await().err(it)

        }

    }

    private suspend fun resumeSession(
        gatewayInfo: GatewayInfo,
        sessionData: EventSignals.Other.ReadyEvent.Data,
        seq: AtomicLong,
        logger: Logger
    ): SessionInfo {
        val requestToken = ticket.botToken

        val resume = Signal.Resume(
            Signal.Resume.Data(
                token = requestToken,
                sessionId = sessionData.sessionId,
                seq = seq.get()
            )
        )

        val session = httpClient.ws { gatewayInfo }

        val hello = session.waitHello()

        logger.info("Received Hello: {}", hello)

        // 重连
        // see https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#%E9%89%B4%E6%9D%83ß
        session.send(decoder.encodeToString(Signal.Resume.serializer(), resume))

        val heartbeatJob = session.heartbeatJob(hello, seq)

        return SessionInfo(session, seq, heartbeatJob, logger, sessionData)
    }

    /**
     *
     */
    private suspend fun createClient(shard: Shard, gatewayInfo: GatewayInfo): ClientImpl {
        val sessionInfo = createSession(shard, gatewayInfo)
        val (session, seq, heartbeatJob, logger, sessionData) = sessionInfo

        val processingJob = processEvent(sessionInfo)

        return ClientImpl(shard, sessionData, heartbeatJob, processingJob, seq, session, logger)
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
                try {
                    p(dispatch, decoder)
                } catch (e: Exception) {
                    logger.error("processing failed.", e)
                }
            }
            // 留下最大的值。
            seq.updateAndGet { prev -> max(prev, nowSeq) }
        }.launchIn(this@TencentBotImpl)
    }

    private suspend inline fun HttpClient.ws(crossinline gatewayInfo: () -> GatewayInfo): DefaultClientWebSocketSession {
        return webSocketSession { url(gatewayInfo().url) }
    }

    private suspend inline fun DefaultClientWebSocketSession.waitHello(): Signal.Hello {
        // receive Hello
        var hello: Signal.Hello? = null
        while (isActive) {
            val h = waitForHello(decoder) { incoming.receive() }
            if (h != null) {
                hello = h
                break
            }
        }
        if (hello == null) {
            closeReason.await().err()
        }
        return hello
    }

    private suspend inline fun DefaultClientWebSocketSession.heartbeatJob(hello: Signal.Hello, seq: AtomicLong): Job {
        val heartbeatInterval = hello.data.heartbeatInterval
        val helloIntervalFactory: () -> Long = {
            val r = ThreadLocalRandom.current().nextLong(5000)
            if (r > heartbeatInterval) 0 else heartbeatInterval - r
        }

        // heartbeat Job
        val heartbeatJob = this.launch {
            val serializer = Signal.Heartbeat.serializer()
            while (this.isActive) {
                delay(helloIntervalFactory())
                val hb = Signal.Heartbeat(seq.get().takeIf { it >= 0 })
                send(decoder.encodeToString(serializer, hb))
            }
        }

        return heartbeatJob
    }

}


private data class SessionInfo(
    val session: DefaultClientWebSocketSession,
    val seq: AtomicLong,
    val heartbeatJob: Job,
    val logger: Logger,
    val sessionData: EventSignals.Other.ReadyEvent.Data
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
        println(json)
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