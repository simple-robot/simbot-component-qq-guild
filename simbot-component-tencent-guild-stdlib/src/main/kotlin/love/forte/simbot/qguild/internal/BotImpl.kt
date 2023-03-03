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
import java.util.concurrent.atomic.AtomicReference
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


    override fun registerPreProcessor(processor: EventProcessor) {
        preProcessorQueue.add(processor)
    }

    override fun registerProcessor(processor: EventProcessor) {
        processorQueue.add(processor)
    }

    /**
     * 用于 [start] 或内部重新连接的锁。
     */
    private val connectLock = Mutex()

    private val stageLoopJob = AtomicReference<Job>(null)
    private val _client = AtomicReference<ClientImpl?>(null)

    override val client: Bot.Client?
        get() = _client.get()

    override suspend fun start(): Boolean = start(GatewayApis.Normal.requestBy(this))

    override suspend fun start(gateway: GatewayInfo): Boolean = connectLock.withLock {
        val gatewayInfo: GatewayInfo = GatewayApis.Normal.requestBy(this)

        logger.debug("Request gateway {} by shard {}", gatewayInfo, shard)

        val loop = StageLoop<Stage>()

        logger.debug("Create stage loop {}", loop)

        loop.appendStage(Connect(shard, gatewayInfo))

        val stageLoopJob = launch(SupervisorJob(parentJob)) {
            loop.loop { e ->
                logger.error("Loop error: {}", e.localizedMessage, e)
            }
        }

        this.stageLoopJob.getAndUpdate { old ->
            old?.cancel()
            stageLoopJob
        }

//        var next: Stage? = loop.poll()
//        while (next != null && next !is ReceiveEvent) {
//            // 还没到接收事件的阶段
//            loop(next)
//            next = loop.poll()
//        }

//        logger.debug("Loop on stage 'ReceiveEvent'")
//
//        val currentClient = (next as ReceiveEvent).client
//
//        logger.debug("Current client: {}", currentClient)
//
//        _client.getAndUpdate { old ->
//            old?.cancel()
//            currentClient
//        }

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


    private inner class ClientImpl(
        val readyData: Ready.Data,
        val sessionInfo: SessionInfo,
//        private val processingJob: Job,
        val wsSession: DefaultClientWebSocketSession,
    ) : Bot.Client {
        override val bot: Bot get() = this@BotImpl
        override val seq: Long get() = sessionInfo.seq.get()
        override val isActive: Boolean get() = wsSession.isActive

        suspend fun cancelAndJoin(reason: Throwable? = null) {
            val cancel = reason?.let { CancellationException(it.localizedMessage, it) }
            val sessionJob = wsSession.coroutineContext[Job]!!
            sessionJob.cancel(cancel)
            sessionJob.join()
        }

        fun cancel(reason: Throwable? = null) {
            val cancel = reason?.let { CancellationException(it.localizedMessage, it) }
            val sessionJob = wsSession.coroutineContext[Job]!!
            sessionJob.cancel(cancel)
        }

        override fun toString(): String {
            return "TencentBot.Client(shard=$shard, session=$readyData, seq=$seq)"
        }
    }

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
            loop.appendStage(WaitingHello(session) { hello ->
                WaitingReadyEvent(identify, hello.data, session)
            })
        }
    }

    /**
     * 等待并接收 [Signal.Hello] .
     *
     * [2.鉴权连接](https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#_2-%E9%89%B4%E6%9D%83%E8%BF%9E%E6%8E%A5)
     *
     */
    private inner class WaitingHello(
        val session: DefaultClientWebSocketSession,
        val next: WaitingHello.(Signal.Hello) -> Stage?
    ) : Stage() {
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
            next(this, h)?.also { loop.appendStage(it) }
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
            loop.appendStage(HeartbeatJob(hello, r.data, session))
        }
    }


    private inner class HeartbeatJob(
        val hello: Signal.Hello.Data,
        val readyData: Ready.Data,
        val session: DefaultClientWebSocketSession
    ) : Stage() {
        override suspend fun invoke(loop: StageLoop<Stage>) {
            val seq = AtomicLong(-1)

            // 创建心跳任务
            val heartbeatJob = createHeartbeatJob(seq)

            val sessionInfo = SessionInfo(session, seq, heartbeatJob, logger, readyData)

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
            launchEventProcessJob(sharedFlow)

            val client = ClientImpl(
                botClientSession.readyData,
                botClientSession,
                session
            )

            logger.debug("Current client: {}", client)

            // store client
            this@BotImpl._client.getAndUpdate { old ->
                old?.cancel()
                client
            }

            // next: receive events
            loop.appendStage(ReceiveEvent(client, sharedFlow))
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
                    this@BotImpl.launch {
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
     * 从 [session][ClientImpl.wsSession] 中尝试接收并处理下一个事件。
     */
    private inner class ReceiveEvent(
        val client: ClientImpl,
        val eventSharedFlow: MutableSharedFlow<EventData>,
    ) : Stage() {
        override suspend fun invoke(loop: StageLoop<Stage>) {
            val session = client.wsSession
            val seq = client.sessionInfo.seq
            if (!session.isActive) {
                val reason = session.closeReason.await()
                logger.error("Session is closed. reason: {}. Try to resume", reason)
                loop.appendStage(Resume(client))
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
                    // 重新连接
                    logger.debug("Received reconnect signal. Do Resume.")
                    loop.appendStage(Resume(client))
                    return
                }

                else -> {
                    logger.debug("Received other opcode: {}", opcode)
                }
            }

            // next: self
            loop.appendStage(this)
        }
    }


    /**
     * [4. 恢复连接](https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#_4-%E6%81%A2%E5%A4%8D%E8%BF%9E%E6%8E%A5)
     *
     * 有很多原因都会导致连接断开，断开之后短时间内重连会补发中间遗漏的事件，以保障业务逻辑的正确性。
     * 断开重连不需要发送Identify请求。在连接到 Gateway 之后，需要发送 Opcode 6 Resume消息
     *
     */
    private inner class Resume(
        val client: ClientImpl,
    ) : Stage() {
        override suspend fun invoke(loop: StageLoop<Stage>) {
            val newSession = connectLock.withLock {
                // 关闭当前连接
                client.cancelAndJoin()
                val gateway = GatewayApis.Normal.requestBy(this@BotImpl)
                wsClient.ws { gateway }.apply {
                    // 发送 Opcode6
                    val resumeSignal =
                        Signal.Resume(Signal.Resume.Data(this@BotImpl.botToken, client.readyData.sessionId, client.seq))
                    send(wsDecoder.encodeToString(Signal.Resume.serializer(), resumeSignal))
                }
            }

            val nextStage = WaitingHello(newSession) { hello ->
                // 跳过 wait ready, 直接HeartbeatJob
                HeartbeatJob(hello.data, client.readyData, session)
            }
            loop.appendStage(nextStage)

        }
    }


    private suspend inline fun HttpClient.ws(crossinline gatewayInfo: () -> GatewayInfo): DefaultClientWebSocketSession {
        return webSocketSession { url(gatewayInfo().url) }
    }

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


