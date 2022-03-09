/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simbot.tencentguild.core.internal

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import love.forte.simbot.*
import love.forte.simbot.LoggerFactory
import love.forte.simbot.tencentguild.*
import love.forte.simbot.tencentguild.api.*
import love.forte.simbot.tencentguild.api.user.*
import org.slf4j.*
import java.util.concurrent.*
import java.util.concurrent.atomic.*
import kotlin.coroutines.*
import kotlin.math.*


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
            GetBotInfoApi.requestBy(this@TencentBotImpl)
        }
    }

    private val logger = LoggerFactory.getLogger("love.forte.simbot.tencentguild.bot.${ticket.appId}")

    // private val parentJob: Job
    override val coroutineContext: CoroutineContext
    private val httpClient: HttpClient get() = configuration.httpClient
    private val url: Url get() = configuration.serverUrl
    private val decoder: Json get() = configuration.decoder

    private val processorQueue: ConcurrentLinkedQueue<suspend Signal.Dispatch.(Json, () -> Any) -> Unit> =
        ConcurrentLinkedQueue()

    init {
        val parentJob = configuration.coroutineContext[Job]
        val job = SupervisorJob(parentJob)
        coroutineContext = configuration.coroutineContext + job + CoroutineName("TencentBot.${ticket.appId}")
    }

    private val parentJob: Job get() = coroutineContext[Job]!!

    override fun processor(processor: suspend Signal.Dispatch.(decoder: Json, decoded: () -> Any) -> Unit) {
        processorQueue.add(processor)
    }

    private val startLock = Mutex()

    override suspend fun start(): Boolean = startLock.withLock {
        for (client in clients) {
            logger.debug("Restarting, close client {}", client)
            client.cancel()
        }

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
                    if (time > 0) {
                        logger.debug("delay wait {} for next......", time)
                        delay(time)
                    }


                }
            }.toList()


        this.clients = clientList

        return true
    }

    override suspend fun cancel(reason: Throwable?): Boolean {
        if (parentJob.isCancelled) return false

        parentJob.cancel(reason?.let { CancellationException(it.localizedMessage, it) })
        parentJob.join()
        return true
    }

    override suspend fun join() {
        parentJob.join()
    }

    override val totalShared: Int = configuration.totalShard

    override var clients: List<ClientImpl> = emptyList()

    internal inner class ClientImpl(
        override val shard: Shard,
        private val sessionData: EventSignals.Other.ReadyEvent.Data,
        private var heartbeatJob: Job,
        private var processingJob: Job,
        private val _seq: AtomicLong,
        private var session: DefaultClientWebSocketSession,
        private val logger: Logger
    ) : TencentBot.Client {
        val nextDelay: Long = if (shard.total - shard.value == 1) 0 else 5000L // 5s

        override val bot: TencentBot get() = this@TencentBotImpl
        override val seq: Long get() = _seq.get()
        override val isActive: Boolean get() = session.isActive
        private val _resuming = AtomicBoolean(false)
        override val isResuming: Boolean get() = _resuming.get()

        private var resumeJob = launch {
            val closed = session.closeReason.await()
            resume(closed)
        }

        suspend fun cancel(reason: Throwable? = null) {
            val cancel = reason?.let { CancellationException(it.localizedMessage, it) }
            val sessionJob = session.coroutineContext[Job]
            sessionJob?.cancel(cancel)
            resumeJob.cancel(cancel)
            heartbeatJob.cancel(cancel)

            sessionJob?.join()
            heartbeatJob.join()
            processingJob.join()


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
            if (!checkResumeCode(code)) {
                logger.debug("Not resume code: {}, close and restart.", code)
                launch { start() }
                return
            }

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
                this.resumeJob = launch {
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
                logger.trace("readyEventData was null, canceled and waiting closeReason.")
                session.closeReason.await().err()
            }
            logger.debug("Ready Event data: {}", readyEventData)

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

        logger.debug("Received Hello: {}", hello)

        // 重连
        // see https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#%E9%89%B4%E6%9D%83ß
        session.send(decoder.encodeToString(Signal.Resume.serializer(), resume))

        val heartbeatJob = session.heartbeatJob(hello, seq)

        return SessionInfo(session, seq, heartbeatJob, logger, sessionData)
    }

    /**
     * 新建一个 client。
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
                    logger.debug("Frame.Binary: {}", it)
                    null
                }
                // is Frame.Close -> {
                //     // closed. 不需要处理，大概
                //     logger.trace("Frame.Close: {}", it)
                //     null
                // }
                // is Frame.Ping -> {
                //     // nothing
                //     logger.trace("Frame.Ping: {}", it)
                //     null
                // }
                // is Frame.Pong -> {
                //     // nothing
                //     logger.trace("Frame.Pong: {}", it)
                //     null
                // }
                else -> {
                    logger.trace("Other frame: {}", it)
                    null
                }
            }
        }.onEach { dispatch ->
            val nowSeq = dispatch.seq
            if (processorQueue.isNotEmpty()) {
                logger.debug("On dispatch: $dispatch")
                val eventType = dispatch.type
                val signal = EventSignals.events[eventType] ?: run {
                    val e = SimbotIllegalStateException("Unknown event type: $eventType. data: $dispatch")
                    e.process(logger) { "Event receiving" }
                    return@onEach
                }

                val lazy = lazy { decoder.decodeFromJsonElement(signal.decoder, dispatch.data) }
                val lazyDecoded = lazy::value
                launch {
                    processorQueue.forEach { p ->
                        try {
                            p(dispatch, decoder, lazyDecoded)
                        } catch (e: Throwable) {
                            e.process(logger) { "Processing" }
                        }
                    }
                }
            }

            // 留下最大的值。
            seq.updateAndGet { prev -> max(prev, nowSeq) }
        }
            .onCompletion { cause -> logger.debug("Session flow completion. cause: {}", cause) }
            .catch { cause -> logger.error("Session flow on catch: ${cause.localizedMessage}", cause) }
            .launchIn(this)
    }


    private suspend inline fun Throwable.process(logger: Logger, msg: () -> String) {
        val processor = configuration.exceptionHandler
        val m = msg()
        processor?.also { exProcess ->
            try {
                exProcess.process(this)
            } catch (pe: Throwable) {
                logger.error("$m failed, exception processing also failed.")
                logger.error("$m exception: ${this.localizedMessage}", this)
                logger.error("exception processing exception: ${pe.localizedMessage}", pe)
            }
        } ?: run {
            logger.error("$m failed.", this)
        }
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


    //// self api

    override suspend fun me(): TencentBotInfo  {
        return GetBotInfoApi.requestBy(this)
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

        // TODO log

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