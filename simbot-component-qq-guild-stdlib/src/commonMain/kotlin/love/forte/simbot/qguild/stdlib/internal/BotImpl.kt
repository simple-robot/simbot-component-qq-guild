/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.qguild.stdlib.internal

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import love.forte.simbot.common.atomic.AtomicLong
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.collection.ConcurrentQueue
import love.forte.simbot.common.collection.ExperimentalSimbotCollectionApi
import love.forte.simbot.common.collection.createConcurrentQueue
import love.forte.simbot.common.stageloop.loop
import love.forte.simbot.common.weak.WeakRef
import love.forte.simbot.common.weak.weakRef
import love.forte.simbot.logger.Logger
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.qguild.api.GatewayApis
import love.forte.simbot.qguild.api.GatewayInfo
import love.forte.simbot.qguild.api.user.GetBotInfoApi
import love.forte.simbot.qguild.event.Ready
import love.forte.simbot.qguild.event.Shard
import love.forte.simbot.qguild.event.Signal
import love.forte.simbot.qguild.model.User
import love.forte.simbot.qguild.stdlib.*
import love.forte.simbot.qguild.stdlib.DisposableHandle
import kotlin.concurrent.Volatile
import kotlin.coroutines.CoroutineContext


/**
 * implementation for [Bot].
 * @author ForteScarlet
 */
@OptIn(ExperimentalSimbotCollectionApi::class)
internal class BotImpl(
    override val ticket: Bot.Ticket,
    override val configuration: BotConfiguration,
) : Bot {
    internal val logger = LoggerFactory.getLogger("love.forte.simbot.qguild.bot.${ticket.appId}")

    // private val parentJob: Job
    override val coroutineContext: CoroutineContext
    private val job: Job

    init {
        configCheck()

        val configContext = configuration.coroutineContext
        val parentJob = configContext[Job]
        job = SupervisorJob(parentJob)
        coroutineContext = configContext.minusKey(Job) + job + CoroutineName("QGBot.${ticket.appId}")
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
        WebSockets {
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


    internal val processorQueue: ConcurrentQueue<EventProcessor> = createConcurrentQueue()
    internal val preProcessorQueue: ConcurrentQueue<EventProcessor> = createConcurrentQueue()


    override fun subscribe(sequence: SubscribeSequence, processor: EventProcessor): DisposableHandle {
        return when (sequence) {
            SubscribeSequence.PRE -> {
                preProcessorQueue.add(processor)
                DisposableHandleImpl(preProcessorQueue, processor)
            }

            SubscribeSequence.NORMAL -> {

                processorQueue.add(processor)
                DisposableHandleImpl(processorQueue, processor)
            }
        }
    }

    private class DisposableHandleImpl(queue: ConcurrentQueue<EventProcessor>, subject: EventProcessor) :
        DisposableHandle {

        @Suppress("unused")
        private val disposed = atomic(false) // 0

        @Volatile
        private var queueRef: WeakRef<ConcurrentQueue<EventProcessor>>? = weakRef(queue)

        @Volatile
        private var subjectRef: WeakRef<EventProcessor>? = weakRef(subject)

        override fun dispose() {
            if (!disposed.compareAndSet(expect = false, value = true)) {
                return
            }

            val queue = queueRef?.let { ref ->
                ref.value.also {
                    ref.clear()
                    queueRef = null
                }
            }
            val subject = subjectRef?.let { ref ->
                ref.value.also {
                    ref.clear()
                    subjectRef = null
                }
            }

            if (queue == null || subject == null) {
                return
            }

            queue.remove(subject)
        }

        private companion object
    }

    /**
     * 用于 [start] 或内部重新连接的锁。
     */
    internal val startLock = Mutex()

    // TODO atomic? lock update?
//    private val stageLoopJob = atomicRef<Job?>(null)

    @Volatile
    private var stageLoopJob: Job? = null

    private val clientLock = Mutex()

    // TODO With Lock
    @Volatile
    private var _client: ClientImpl? = null
    override val client: Bot.Client? get() = _client

    internal suspend inline fun updateClient(new: ClientImpl?) {
        clientLock.withLock {
            _client?.cancel()
            _client = new
        }
    }

    override suspend fun start() {
        start(requestData(GatewayApis.Normal))
    }

    override suspend fun start(gateway: GatewayInfo) {
        startLock.withLock {
            stageLoopJob?.cancel()
            stageLoopJob = null

            logger.debug("Request gateway {} by shard {}", gateway, shard)

            val state = Connect(this, shard, gateway)

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

            this.stageLoopJob = stageLoopJob
        }
    }

    override fun cancel(reason: Throwable?) {
        if (!job.isActive) return

        job.cancel(reason?.let { CancellationException(it.message, it) })
    }

    override suspend fun join() {
        job.join()
    }


    internal inner class ClientImpl(
        val readyData: Ready.Data,
        val sessionInfo: SessionInfo,
        val wsSession: DefaultClientWebSocketSession,
    ) : Bot.Client {
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
        return GetBotInfoApi.requestDataBy(this)
    }

}


internal class AtomicLongRef(initValue: Long = 0) {
    private val atomicValue: AtomicLong = atomic(initValue)
    var value: Long
        get() = atomicValue.value
        set(value) {
            atomicValue.value = value
        }

    fun updateAndGet(function: (Long) -> Long): Long {
        while (true) {
            val current = value
            val new = function(current)
            if (atomicValue.compareAndSet(current, new)) {
                return new
            }
        }
    }
}

internal data class SessionInfo(
    val session: DefaultClientWebSocketSession,
    val seq: AtomicLongRef,
    val heartbeatJob: Job,
    val logger: Logger,
    val readyData: Ready.Data,
)

