/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.internal

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.qguild.*
import love.forte.simbot.qguild.api.GatewayApis
import love.forte.simbot.qguild.api.GatewayInfo
import love.forte.simbot.qguild.api.user.GetBotInfoApi
import love.forte.simbot.qguild.event.*
import love.forte.simbot.qguild.model.User
import love.forte.simbot.util.stageloop.StageLoop
import love.forte.simbot.util.stageloop.loop
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.random.Random


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
 * implementation for [Bot].
 * @author ForteScarlet
 */
internal class BotImpl(
    override val ticket: Bot.Ticket,
    override val configuration: BotConfiguration,
) : Bot {
    private val logger = LoggerFactory.getLogger("love.forte.simbot.tencentguild.bot.${ticket.appId}")

    // private val parentJob: Job
    override val coroutineContext: CoroutineContext
    private val parentJob: Job

    init {
        val configContext = configuration.coroutineContext
        val configJob = configContext[Job]
        parentJob = SupervisorJob(configJob)
        coroutineContext = configContext + parentJob + CoroutineName("QGBot.${ticket.appId}")
    }

    private val wsDecoder = Signal.Dispatch.dispatchJson {
        isLenient = true
        ignoreUnknownKeys = true
    }

    private val wsClient: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(wsDecoder)
        }

        install(WebSockets) {
            // ...?
        }
    }

    override val apiClient: HttpClient = configuration.apiClient ?: HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    override val apiServer: Url = configuration.serverUrl
    internal val botToken = "Bot ${ticket.appId}.${ticket.token}"
    override val shard: Shard = configuration.shard
    override val apiDecoder: Json = configuration.apiDecoder
    private val intents: Intents = configuration.intents
    private val clientProperties = configuration.clientProperties

    private val processorQueue: ConcurrentLinkedQueue<EventProcessor> = ConcurrentLinkedQueue()
    private val preProcessorQueue: ConcurrentLinkedQueue<EventProcessor> = ConcurrentLinkedQueue()

    @Volatile
    private var stageLoopJob: Job? = null

    override fun registerPreProcessor(processor: EventProcessor) {
        preProcessorQueue.add(processor)
    }

    override fun registerProcessor(processor: EventProcessor) {
        processorQueue.add(processor)
    }

    private val startLock = Mutex()

    override suspend fun start(): Boolean = start(GatewayApis.Normal.requestBy(this))

    override suspend fun start(gateway: GatewayInfo): Boolean = startLock.withLock {
        _client?.also {
            logger.debug("Restarting, cancel a client that already exists {}", it)
            it.cancelAndJoin()
        }

        stageLoopJob?.also {
            logger.debug("Restarting, cancel a stage loop job that already exists {}", it)
            it.cancelAndJoin()
            stageLoopJob = null
        }

        val gatewayInfo: GatewayInfo = GatewayApis.Normal.requestBy(this)

        logger.debug("Request gateway {} by shard {}", gatewayInfo, shard)

        val loop = StageLoop<Stage>()

        logger.debug("Create stage loop {}", loop)

        loop.appendStage(Connect(shard, gatewayInfo))

        var next: Stage? = loop.poll()
        while (next != null && next !is ReceiveEvent) {
            // 还没到接收事件的阶段
            loop(next)
            next = loop.poll()
        }

        logger.debug("Loop on stage 'ReceiveEvent'")

        val currentClient = (next as ReceiveEvent).client

        logger.debug("Current client: {}", currentClient)

        _client = currentClient

        stageLoopJob = launch(SupervisorJob(parentJob)) {
            loop.loop { e ->
                logger.error("Loop error: {}", e.localizedMessage, e)
            }
        }

//
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

    @Volatile
    private var _client: ClientImpl? = null

    override val client: Bot.Client
        get() {
            return _client ?: throw IllegalStateException("The client has not been initialized")
        }

    private inner class ClientImpl(
        private val readyData: Ready.Data,
        private val heartbeatJob: Job,
        private val processingJob: Job,
        private val _seq: AtomicLong,
        private val session: DefaultClientWebSocketSession,
    ) : Bot.Client {
        override val bot: Bot get() = this@BotImpl
        override val seq: Long get() = _seq.get()
        override val isActive: Boolean get() = session.isActive

//        private var resumeJob = launch {
//            val closed = session.closeReason.await()
//            resume(closed)
//        }

        suspend fun cancelAndJoin(reason: Throwable? = null) {
            val cancel = reason?.let { CancellationException(it.localizedMessage, it) }
            val sessionJob = session.coroutineContext[Job]!!
            sessionJob.cancel(cancel)
            sessionJob.join()
        }

        override fun toString(): String {
            return "TencentBot.Client(shard=$shard, session=$readyData, seq=${_seq.get()})"
        }

//        /**
//         * 重新连接。
//         * // TODO 增加日志
//         */
//        private suspend fun resume(closeReason: CloseReason?) {
//            if (closeReason == null) {
//                logger.error("Client {} was closed, but no reason. stop this client without any action, including resuming.", this)
//                return
//            }
//
//            val code = closeReason.code
//            if (!checkResumeCode(code)) {
//                logger.debug("Not resume code: {}, try to close and restart.", code)
//                launch { start() }
//                return
//            }
//
//            while (!_resuming.compareAndSet(false, true)) {
//                logger.info("In resuming now, delay 100ms")
//                delay(100)
//            }
//
//            logger.info("Resume. reason: {}", closeReason)
//
//            try {
//                heartbeatJob.cancel()
//                processingJob.cancel()
//                heartbeatJob.join()
//                processingJob.join()
//
//                val gatewayInfo = GatewayApis.Normal.requestBy(this@BotImpl)
//
//                val resumeSession = resumeSession(gatewayInfo, readyData, _seq, logger)
//                val (session, _, heartbeatJob, _, _) = resumeSession
//                this.session = session
//                this.heartbeatJob = heartbeatJob
//                val processingJob = processEvent(resumeSession)
//                this.processingJob = processingJob
//                this.resumeJob = launch {
//                    val closed = session.closeReason.await()
//                    resume(closed)
//                }
//            } finally {
//                _resuming.compareAndSet(true, false)
//            }
//
//        }


    }

//    /**
//     * https://bot.q.qq.com/wiki/develop/api/gateway/reference.html
//     */
//    private suspend fun createSession(shard: Shard, gatewayInfo: GatewayInfo): SessionInfo {
//        val requestToken = botToken
//
//        val logger = LoggerFactory.getLogger("love.forte.simbot.tencentguild.bot.${ticket.appId}.$shard")
//
//        val identify = Signal.Identify(
//            data = Signal.Identify.Data(
//                token = requestToken,
//                intents = intents,
//                shard = shard,
//                properties = prop,
//            )
//        )
//
//
//
////        val seq = AtomicLong(-1)
////
////        val session = wsClient.ws { gatewayInfo }
////
////        kotlin.runCatching {
////
////            // receive Hello
////            val hello = session.waitHello()
////
////            logger.debug("Received Hello: {}", hello)
////            // 鉴权
////            // see https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#%E9%89%B4%E6%9D%83ß
////            session.send(decoder.encodeToString(Signal.Identify.serializer(), identify))
////
////            // wait for Ready event
////            var readyEventData: EventSignals.Other.ReadyEvent.Data? = null
////            while (session.isActive) {
////                val ready = waitForReady(decoder) { session.incoming.receive() }
////                logger.debug("ready: $ready")
////                if (ready != null) {
////                    readyEventData = ready
////                    break
////                }
////            }
////            if (readyEventData == null) {
////                logger.trace("readyEventData was null, canceled and waiting closeReason.")
////                session.closeReason.await().err()
////            }
////            logger.debug("Ready Event data: {}", readyEventData)
////
////            val heartbeatJob = session.heartbeatJob(hello, seq)
////
////            return SessionInfo(session, seq, heartbeatJob, logger, readyEventData)
////        }.getOrElse {
////            // logger.error(it.localizedMessage, it)
////            session.closeReason.await().err(it)
////
////        }
//
//    }

//    private suspend fun resumeSession(
//        gatewayInfo: GatewayInfo,
//        sessionData: EventSignals.Other.ReadyEvent.Data,
//        seq: AtomicLong,
//        logger: Logger,
//    ): SessionInfo {
//        val requestToken = ticket.botToken
//
//        val resume = Signal.Resume(
//            Signal.Resume.Data(
//                token = requestToken,
//                sessionId = sessionData.sessionId,
//                seq = seq.get()
//            )
//        )
//
//        val session = wsClient.ws { gatewayInfo }
//
//        val hello = session.waitHello()
//
//        logger.debug("Received Hello: {}", hello)
//
//        // 重连
//        // see https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#%E9%89%B4%E6%9D%83ß
//        session.send(decoder.encodeToString(Signal.Resume.serializer(), resume))
//
//        val heartbeatJob = session.heartbeatJob(hello, seq)
//
//        return SessionInfo(session, seq, heartbeatJob, logger, sessionData)
//    }
//
//    /**
//     * 新建一个 client。
//     */
//    private suspend fun createClient(shard: Shard, gatewayInfo: GatewayInfo): ClientImpl {
//        val sessionInfo = createSession(shard, gatewayInfo)
//        val (session, seq, heartbeatJob, logger, sessionData) = sessionInfo
//
//        val processingJob = processEvent(sessionInfo)
//
//        return ClientImpl(shard, sessionData, heartbeatJob, processingJob, seq, session, logger)
//    }
//
//
//    private suspend fun processEvent(sessionInfo: SessionInfo): Job {
//        val dispatchSerializer = Signal.Dispatch.serializer()
//        val logger = sessionInfo.logger
//        val seq = sessionInfo.seq
//
//        return sessionInfo.session.incoming.receiveAsFlow().mapNotNull {
//            when (it) {
//
//                is Frame.Text -> {
//                    val eventString = it.readText()
//                    val jsonElement = decoder.parseToJsonElement(eventString)
//                    // maybe op 11: heartbeat ack
//                    val op = jsonElement.jsonObject["op"]?.jsonPrimitive?.int ?: return@mapNotNull null
//                    when (op) {
//                        Opcode.HeartbeatACK.code -> null
//                        Opcode.Dispatch.code -> {
//                            // is dispatch
//                            decoder.decodeFromJsonElement(dispatchSerializer, jsonElement)
//                        }
//
//                        else -> null
//                    }
//                }
//
//                is Frame.Binary -> {
//                    logger.debug("Frame.Binary: {}", it)
//                    null
//                }
//                // is Frame.Close -> {
//                //     // closed. 不需要处理，大概
//                //     logger.trace("Frame.Close: {}", it)
//                //     null
//                // }
//                // is Frame.Ping -> {
//                //     // nothing
//                //     logger.trace("Frame.Ping: {}", it)
//                //     null
//                // }
//                // is Frame.Pong -> {
//                //     // nothing
//                //     logger.trace("Frame.Pong: {}", it)
//                //     null
//                // }
//                else -> {
//                    logger.trace("Other frame: {}", it)
//                    null
//                }
//            }
//        }.onEach { dispatch ->
//            val nowSeq = dispatch.seq
//            if (processorQueue.isNotEmpty() || preProcessorQueue.isNotEmpty()) {
//                logger.debug("On dispatch and processors or pre processors is not empty. dispatch: $dispatch")
//                val eventType = dispatch.type
//                val signal = EventSignals.events[eventType] ?: run {
//                    val e = SimbotIllegalStateException("Unknown event type: $eventType. data: $dispatch")
//                    e.process(logger) { "Event receiving" }
//                    return@onEach
//                }
//
//                val lazy = lazy { decoder.decodeFromJsonElement(signal.decoder, dispatch.data) }
//                val lazyDecoded = lazy::value
//                preProcessorQueue.forEach { preProcessor ->
//                    try {
//                        preProcessor(dispatch, decoder, lazyDecoded)
//                    } catch (e: Throwable) {
//                        e.process(logger) { "Pre processing" }
//                    }
//                }
//
//
//                launch {
//                    processorQueue.forEach { processor ->
//                        try {
//                            processor(dispatch, decoder, lazyDecoded)
//                        } catch (e: Throwable) {
//                            e.process(logger) { "Processing" }
//                        }
//                    }
//                }
//            } else {
//                // 但是 processors 和 pre processors 都是空的
//                logger.debug("On dispatch, but the processors and pre processors are empty, skip. dispatch: $dispatch")
//            }
//
//            // 留下最大的值。
//            val currentSeq = seq.updateAndGet { prev -> max(prev, nowSeq) }
//            logger.debug("Current seq: {}", currentSeq)
//        }.onCompletion { cause ->
//            if (logger.isDebugEnabled) {
//                logger.debug("Session flow completion. cause: {}", cause.toString())
//            }
//        }
//            .catch { cause -> logger.error("Session flow on catch: ${cause.localizedMessage}", cause) }
//            .launchIn(this)
//    }


    private abstract inner class Stage : love.forte.simbot.util.stageloop.Stage<Stage>()

    /**
     * 根据 [gateway] 和 [shard] 连接到ws服务器。
     */
    private inner class Connect(val shard: Shard, val gateway: GatewayInfo) : Stage() {
        override suspend fun invoke(loop: StageLoop<Stage>) {
            val intents = this@BotImpl.intents
            val properties = this@BotImpl.clientProperties

            logger.debug("Connect intents: {}", intents)
            logger.debug("Connect properties: {}", properties)

            val identify = Signal.Identify(
                Signal.Identify.Data(
                    token = botToken,
                    intents = intents,
                    shard = shard,
                    properties = properties,
                )
            )

            logger.debug("Connect identify: {}", identify)
            val session = wsClient.ws { gateway }

            // next: receive Hello
            loop.appendStage(WaitingHello(identify, session))
        }
    }

    /**
     * 等待并接收 [Signal.Hello] .
     *
     * [2.鉴权连接](https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#_2-%E9%89%B4%E6%9D%83%E8%BF%9E%E6%8E%A5)
     *
     */
    private inner class WaitingHello(val identify: Signal.Identify, val session: DefaultClientWebSocketSession) :
        Stage() {
        override suspend fun invoke(loop: StageLoop<Stage>) {
            var hello: Signal.Hello? = null
            while (session.isActive) {
                val frame = session.incoming.receive() as? Frame.Text ?: continue
                val text = frame.readText()
                logger.debug("waiting hello : received frame {}", text)
                val json = wsDecoder.parseToJsonElement(text)
                if (json.jsonObject["op"]?.jsonPrimitive?.int == Opcodes.Hello) {
                    hello = wsDecoder.decodeFromJsonElement(Signal.Hello.serializer(), json)
                    break
                }
            }

            val h = hello
            if (h == null) {
                logger.error("Received Hello failed, nothing received. Awaiting close reason and throw")
                session.closeReason.await().err()
            }

            logger.debug("Received Hello: {}", h)

            // 下一个阶段
            loop.appendStage(WaitingReadyEvent(identify, h.data, session))
        }
    }

    /**
     * 等待并接收 [Ready Event][Ready]
     *
     * [2.鉴权连接](https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#_2-%E9%89%B4%E6%9D%83%E8%BF%9E%E6%8E%A5)
     */
    private inner class WaitingReadyEvent(
        val identify: Signal.Identify,
        val hello: Signal.Hello.Data,
        val session: DefaultClientWebSocketSession
    ) : Stage() {
        override suspend fun invoke(loop: StageLoop<Stage>) {
            // 发送 identify
            session.send(wsDecoder.encodeToString(Signal.Identify.serializer(), identify))

            // 等待ready
            var ready: Ready? = null
            while (session.isActive) {
                val frame = session.incoming.receive() as? Frame.Text ?: continue
                val text = frame.readText()
                logger.debug("waiting ready event : received frame {}", text)
                val json = wsDecoder.parseToJsonElement(text)
                if (json.jsonObject["op"]?.jsonPrimitive?.int == Opcodes.Dispatch) {
                    val dispatch = wsDecoder.decodeFromJsonElement(Signal.Dispatch.serializer(), json)
                    if (dispatch is Ready) {
                        ready = dispatch
                        break
                    }
                }
            }

            val r = ready
            if (r == null) {
                logger.error("Received Ready event failed, nothing received. Awaiting close reason and throw")
                session.closeReason.await().err()
            }

            logger.debug("Received Ready event: {}", r)

            // next: session
            loop.appendStage(HeartbeatJob(hello, r, session))
        }
    }


    private inner class HeartbeatJob(
        val hello: Signal.Hello.Data,
        val ready: Ready,
        val session: DefaultClientWebSocketSession
    ) : Stage() {
        override suspend fun invoke(loop: StageLoop<Stage>) {
            val seq = AtomicLong(-1)

            // 创建心跳任务
            val heartbeatJob = createHeartbeatJob(seq)

            val sessionInfo = SessionInfo(session, seq, heartbeatJob, logger, ready.data)

            // next: process event
            loop.appendStage(CreateClient(sessionInfo, session))
        }


        private fun createHeartbeatJob(seq: AtomicLong): Job {
            val heartbeatInterval = hello.heartbeatInterval

            fun getHelloInterval(): Long {
                val r = Random.nextLong(5000)
                return if (r > heartbeatInterval) 0 else heartbeatInterval - r
            }

//                val heartbeatSupervisorJob = SupervisorJob(session.coroutineContext[Job]!!)

            // heartbeat Job
            return session.launch(CoroutineName("bot.${ticket.appId}.heartbeat")) {
                val serializer = Signal.Heartbeat.serializer()
                while (isActive) {
                    delay(getHelloInterval())
                    val hb = Signal.Heartbeat(seq.get().takeIf { it >= 0 })
                    session.send(wsDecoder.encodeToString(serializer, hb))
                }
            }

        }
    }

    /**
     * 创建 [ClientImpl], 并在异步中处理事件。
     */
    private inner class CreateClient(
        val botClientSession: SessionInfo,
        val session: DefaultClientWebSocketSession
    ) : Stage() {
        override suspend fun invoke(loop: StageLoop<Stage>) {
            // TODO capacity configurable?
            val sharedFlow = MutableSharedFlow<EventData>(extraBufferCapacity = 16)
            val processJob = launchEventProcessJob(sharedFlow)

            val client = ClientImpl(
                botClientSession.readyData,
                botClientSession.heartbeatJob,
                processJob,
                botClientSession.seq,
                session
            )

            // next: receive events
            loop.appendStage(ReceiveEvent(client, botClientSession, sharedFlow, session))
        }

        private fun launchEventProcessJob(flow: SharedFlow<EventData>): Job {
            return flow
                .onEach { (raw, event) ->
                    // 先顺序地使用 preProcessor 处理
                    preProcessorQueue.forEach { processor ->
                        runCatching {
                            event.apply { processor.apply { invoke(raw) } }
                        }.onFailure { e ->
                            if (logger.isDebugEnabled) {
                                logger.debug(
                                    "Event pre-precess failure. raw: {}, event: {}", raw, event
                                )
                            }
                            logger.error("Event pre-precess failure.", e)
                        }
                    }

                    // 然后异步地正常处理
                    // TODO 同步异步 configurable?
                    // bot launch or session launch?
                    launch {
                        processorQueue.forEach { processor ->
                            runCatching {
                                event.apply { processor.apply { invoke(raw) } }
                            }.onFailure { e ->
                                if (logger.isDebugEnabled) {
                                    logger.debug(
                                        "Event precess failure. raw: {}, event: {}", raw, event
                                    )
                                }
                                logger.error("Event precess failure.", e)
                            }
                        }
                    }

                }.onCompletion { cause ->
                    if (cause == null) {
                        logger.debug("Event process flow is completed. No cause.")
                    } else {
                        logger.debug(
                            "Event process flow is completed. Cause: {}", cause.localizedMessage, cause
                        )
                    }
                }.catch { cause ->
                    logger.error(
                        "Event process flow on error: {}", cause.localizedMessage, cause
                    )
                }.launchIn(session)
        }
    }

    /**
     * 从 [session] 中尝试接收并处理下一个事件。
     */
    private inner class ReceiveEvent(
        val client: ClientImpl,
        val sessionInfo: SessionInfo,
        val eventSharedFlow: MutableSharedFlow<EventData>,
        val session: DefaultClientWebSocketSession
    ) : Stage() {
        override suspend fun invoke(loop: StageLoop<Stage>) {
            val seq = sessionInfo.seq
            if (!session.isActive) {
                val reason = session.closeReason.await()
                // TODO reconnect?
                logger.error("Session is closed. reason: {}.", reason)
                return
            }

            // TODO catch CancellationException?

            logger.trace("Receiving next frame ...")
            val frame = session.incoming.receive()
            logger.trace("Received next frame: {}", frame)
            val raw = (frame as? Frame.Text)?.readText() ?: run {
                logger.debug("No Text frame received {}, skip.", frame)
                loop.appendStage(this) // next: this
                return
            }
            val json = wsDecoder.parseToJsonElement(raw)
            when (val opcode = json.getOpcode()) {
                Opcodes.Dispatch -> {
                    // event
                    val dispatch = wsDecoder.decodeFromJsonElement(Signal.Dispatch.serializer(), json)
                    logger.debug("Received dispatch: {}", dispatch)
                    val dispatchSeq = dispatch.seq

                    // 推送事件
                    eventSharedFlow.emit(EventData(raw, dispatch))

                    // seq留下最大值
                    val currentSeq = seq.updateAndGet { pref -> max(pref, dispatchSeq) }
                    logger.trace("Current seq: {}", currentSeq)
                }

                Opcodes.Reconnect -> {
                    // TODO 重新连接
                }

                else -> {
                    logger.debug("Received other opcode: {}", opcode)
                }
            }

            // next: self
            loop.appendStage(this)
        }
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

//    private suspend inline fun DefaultClientWebSocketSession.waitHello(): Signal.Hello {
//        // receive Hello
//        var hello: Signal.Hello? = null
//        while (isActive) {
//            val h = waitForHello(decoder) { incoming.receive() }
//            if (h != null) {
//                hello = h
//                break
//            }
//        }
//        if (hello == null) {
//            closeReason.await().err()
//        }
//        return hello
//    }
//
//    private suspend inline fun DefaultClientWebSocketSession.heartbeatJob(hello: Signal.Hello, seq: AtomicLong): Job {
//        val heartbeatInterval = hello.data.heartbeatInterval
//        val helloIntervalFactory: () -> Long = {
//            val r = ThreadLocalRandom.current().nextLong(5000)
//            if (r > heartbeatInterval) 0 else heartbeatInterval - r
//        }
//
//        // heartbeat Job
//        val heartbeatJob = this.launch {
//            val serializer = Signal.Heartbeat.serializer()
//            while (this.isActive) {
//                delay(helloIntervalFactory())
//                val hb = Signal.Heartbeat(seq.get().takeIf { it >= 0 })
//                send(decoder.encodeToString(serializer, hb))
//            }
//        }
//
//        return heartbeatJob
//    }


    //// self api

    override suspend fun me(): User {
        return GetBotInfoApi.requestBy(this)
    }


}


private data class SessionInfo(
    val session: DefaultClientWebSocketSession,
    val seq: AtomicLong,
    val heartbeatJob: Job,
    val logger: Logger,
    val readyData: Ready.Data,
)

private data class EventData(val raw: String, val event: Signal.Dispatch)


//private inline fun waitForHello(decoder: Json, frameBlock: () -> Frame): Signal.Hello? {
//    val frame = frameBlock()
//    var hello: Signal.Hello? = null
//    // for hello
//    if (frame is Frame.Text) {
//        val json = decoder.parseToJsonElement(frame.readText())
//        if (json.jsonObject["op"]?.jsonPrimitive?.int == Opcode.Hello.code) {
//            hello = decoder.decodeFromJsonElement(Signal.Hello.serializer(), json)
//        }
//    }
//    return hello
//}
//
//
//private inline fun waitForReady(decoder: Json, frameBlock: () -> Frame): EventSignals.Other.ReadyEvent.Data? {
//    val frame = frameBlock()
//    var readyEvent: EventSignals.Other.ReadyEvent.Data? = null
//    // for hello
//    if (frame is Frame.Text) {
//        val json = decoder.parseToJsonElement(frame.readText())
//
//        // TODO log
//
//        if (json.jsonObject["op"]?.jsonPrimitive?.int == Opcode.Dispatch.code) {
//            val dis = decoder.decodeFromJsonElement(Signal.Dispatch.serializer(), json)
//            if (dis.type == EventSignals.Other.ReadyEvent.type) {
//                readyEvent = decoder.decodeFromJsonElement(EventSignals.Other.ReadyEvent.decoder, dis.data)
//            }
//        }
//    }
//    return readyEvent
//}


//@Serializable
//internal data class TicketImpl(
//    @SerialName("app_id")
//    override val appId: String,
//    @SerialName("app_key")
//    override val secret: String,
//    override val token: String,
//) : Bot.Ticket {
//    @Transient
//    override val botToken: String = "Bot $appId.$token"
//
//    override fun toString(): String {
//        return "TicketImpl(appId=$appId, appKey=${secret.hide()}, token=${token.hide()})"
//    }
//}
//
//private fun String.hide(size: Int = 3, hide: String = "********"): String {
//    return if (length <= size) hide
//    else "${substring(0, 3)}$hide${substring(lastIndex - 2, length)}"
//
//}
