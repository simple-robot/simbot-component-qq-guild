/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.internal

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.atomicfu.AtomicLong
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.getAndUpdate
import kotlinx.atomicfu.updateAndGet
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import love.forte.simbot.logger.Logger
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.qguild.*
import love.forte.simbot.qguild.DisposableHandle
import love.forte.simbot.qguild.api.GatewayApis
import love.forte.simbot.qguild.api.GatewayInfo
import love.forte.simbot.qguild.api.user.GetBotInfoApi
import love.forte.simbot.qguild.event.Intents
import love.forte.simbot.qguild.event.Ready
import love.forte.simbot.qguild.event.Shard
import love.forte.simbot.qguild.event.Signal
import love.forte.simbot.qguild.model.User
import love.forte.simbot.util.stageloop.loop
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.Volatile


/**
 * implementation for [Bot].
 * @author ForteScarlet
 */
internal class BotImpl(
    override val ticket: Bot.Ticket,
    override val configuration: BotConfiguration,
) : Bot {
    internal val logger = LoggerFactory.getLogger("love.forte.simbot.qguild.bot.${ticket.appId}")

    // private val parentJob: Job
    override val coroutineContext: CoroutineContext
    private val supervisorJob: Job

    init {
        val configContext = configuration.coroutineContext
        val configJob = configContext[Job]
        supervisorJob = SupervisorJob(configJob)
        coroutineContext = configContext + supervisorJob + CoroutineName("QGBot.${ticket.appId}")
        configCheck()
    }

    private fun configCheck() {
        if (configuration.intents.value == 0) {
            logger.warn("Bot(appId=${ticket.appId}) intents value is ZERO")
        }
    }

    internal val wsDecoder = Signal.Dispatch.dispatchJson {
        isLenient = true
        ignoreUnknownKeys = true
    }

    internal val wsClient: HttpClient = configuration.let {
        val wsClientEngine = it.wsClientEngine
        val wsClientEngineFactory = it.wsClientEngineFactory

        when {
            wsClientEngine != null -> HttpClient(wsClientEngine) {
                configWsHttpClient()
            }

            wsClientEngineFactory != null -> HttpClient(wsClientEngineFactory) {
                configWsHttpClient()
            }

            else -> HttpClient {
                configWsHttpClient()
            }
        }
    }


    private fun HttpClientConfig<*>.configWsHttpClient() {
        install(ContentNegotiation) {
            json(wsDecoder)
        }

        install(WebSockets) {
            // ...?
        }
    }

    override val apiClient: HttpClient = configuration.let {
        val apiClientEngine = it.apiClientEngine
        val apiClientEngineFactory = it.apiClientEngineFactory

        when {
            apiClientEngine != null -> HttpClient(apiClientEngine) {
                configApiHttpClient()
            }

            apiClientEngineFactory != null -> HttpClient(apiClientEngineFactory) {
                configApiHttpClient()
            }

            else -> HttpClient {
                configApiHttpClient()
            }
        }
    }

    private fun HttpClientConfig<*>.configApiHttpClient() {
        install(ContentNegotiation) {
            json(configuration.apiDecoder)
        }

        val apiHttpRequestTimeoutMillis = configuration.apiHttpRequestTimeoutMillis
        val apiHttpConnectTimeoutMillis = configuration.apiHttpConnectTimeoutMillis
        val apiHttpSocketTimeoutMillis = configuration.apiHttpSocketTimeoutMillis

        if (apiHttpRequestTimeoutMillis != null || apiHttpConnectTimeoutMillis != null || apiHttpSocketTimeoutMillis != null) {
            install(HttpTimeout) {
                apiHttpRequestTimeoutMillis?.also { requestTimeoutMillis = it }
                apiHttpConnectTimeoutMillis?.also { connectTimeoutMillis = it }
                apiHttpSocketTimeoutMillis?.also { socketTimeoutMillis = it }
            }
        }

        install(HttpRequestRetry) {
            maxRetries = 3
        }
    }

    override val apiServer: Url = configuration.serverUrl
    internal val botToken = "Bot ${ticket.appId}.${ticket.token}"
    override val shard: Shard = configuration.shard
    override val apiDecoder: Json = configuration.apiDecoder
    internal val intents: Intents = configuration.intents


    internal val processorQueue: SimpleConcurrentQueue<EventProcessor> = createSimpleConcurrentQueue()
    internal val preProcessorQueue: SimpleConcurrentQueue<EventProcessor> = createSimpleConcurrentQueue()


    override fun registerPreProcessor(processor: EventProcessor): DisposableHandle {
        preProcessorQueue.add(processor)
        return DisposableHandleImpl(preProcessorQueue, processor)
    }

    override fun registerProcessor(processor: EventProcessor): DisposableHandle {
        processorQueue.add(processor)
        return DisposableHandleImpl(processorQueue, processor)
    }

    private class DisposableHandleImpl(queue: SimpleConcurrentQueue<EventProcessor>, subject: EventProcessor) :
        DisposableHandle {

        @Suppress("unused")
        private val disposed = atomic(false) // 0

        @Volatile
        private var queueRef: WeakRef<SimpleConcurrentQueue<EventProcessor>>? = WeakRef(queue)

        @Volatile
        private var subjectRef: WeakRef<EventProcessor>? = WeakRef(subject)

        override fun dispose() {
            if (!disposed.compareAndSet(expect = false, update = true)) {
                return
            }

            val queue = queueRef?.let { ref ->
                ref.get().also {
                    ref.clear()
                    queueRef = null
                }
            }
            val subject = subjectRef?.let { ref ->
                ref.get().also {
                    ref.clear()
                    subjectRef = null
                }
            }

            if (queue == null || subject == null) {
                return
            }

            queue.remove(subject)
        }

        private companion object {
        }
    }

    /**
     * 用于 [start] 或内部重新连接的锁。
     */
    internal val connectLock = Mutex()

    private val stageLoopJob = atomic<Job?>(null)
    private val _client = atomic<ClientImpl?>(null)

    override val client: Bot.Client? get() = _client.value

    internal fun getAndUpdateClient(function: (ClientImpl?) -> ClientImpl?): ClientImpl? {
        return _client.getAndUpdate { function(it) }
    }

    override suspend fun start(): Boolean = start(GatewayApis.Normal.requestBy(this))

    override suspend fun start(gateway: GatewayInfo): Boolean = connectLock.withLock {
        val gatewayInfo: GatewayInfo = GatewayApis.Normal.requestBy(this)

        logger.debug("Request gateway {} by shard {}", gatewayInfo, shard)

        val state = Connect(this, shard, gatewayInfo)

//        val loop = StageLoop<Stage>()

        logger.debug("Create state loop {}", state)


        var st: State? = state
        do {
            logger.debug("Current state: {}", st)
            st = st?.invoke()
        } while (st != null && st !is ReceiveEvent)

        if (st == null) {
            // 当前状态为空且尚未进入事件接收状态
            throw IllegalStateException("The current state is null and not yet in the event receiving state")
        }

        val stageLoopJob: Job = launch {
            st.loop()
        }

        this.stageLoopJob.getAndUpdate { old ->
            old?.cancel()
            stageLoopJob
        }

        return true
    }

    override suspend fun cancel(reason: Throwable?): Boolean {
        if (supervisorJob.isCancelled) return false

        supervisorJob.cancel(reason?.let { CancellationException(it.message, it) })
        supervisorJob.join()
        return true
    }

    override suspend fun join() {
        supervisorJob.join()
    }


    internal inner class ClientImpl(
        val readyData: Ready.Data,
        val sessionInfo: SessionInfo,
        val wsSession: DefaultClientWebSocketSession,
    ) : Bot.Client {
        override val bot: Bot get() = this@BotImpl
        override val seq: Long get() = sessionInfo.seq.value
        override val isActive: Boolean get() = wsSession.isActive

        suspend fun cancelAndJoin(reason: Throwable? = null) {
            val cancel = reason?.let { CancellationException(it.message, it) }
            val sessionJob = wsSession.coroutineContext[Job]!!
            sessionJob.cancel(cancel)
            sessionJob.join()
        }

        fun cancel(reason: Throwable? = null) {
            val cancel = reason?.let { CancellationException(it.message, it) }
            val sessionJob = wsSession.coroutineContext[Job]!!
            sessionJob.cancel(cancel)
        }

        override fun toString(): String {
            return "ClientImpl(shard=$shard, session=$readyData, seq=$seq)"
        }
    }



    //// self api

    override suspend fun me(): User {
        return GetBotInfoApi.requestBy(this)
    }

}


internal class AtomicLongRef(initValue: Long = 0) {
    private val atomicValue: AtomicLong = atomic(initValue)
    var value: Long
        get() = atomicValue.value
        set(value) {
            atomicValue.value = value
        }

    fun updateAndGet(function: (Long) -> Long): Long = atomicValue.updateAndGet(function)
}

internal data class SessionInfo(
    val session: DefaultClientWebSocketSession,
    val seq: AtomicLongRef,
    val heartbeatJob: Job,
    val logger: Logger,
    val readyData: Ready.Data,
)

internal data class EventData(val raw: String, val event: Signal.Dispatch)

