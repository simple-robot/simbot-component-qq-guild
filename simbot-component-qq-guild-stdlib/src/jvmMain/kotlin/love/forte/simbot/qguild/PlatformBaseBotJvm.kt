/*
 * Copyright (c) 2023. ForteScarlet.
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

package love.forte.simbot.qguild

import kotlinx.coroutines.future.await
import love.forte.simbot.Api4J
import love.forte.simbot.qguild.event.Signal
import java.util.concurrent.CompletionStage
import java.util.function.BiConsumer
import java.util.function.BiFunction

/**
 * JVM平台下实现的 [Bot] 基础抽象类，对 [Bot] 提供额外的能力。
 *
 * _仅由内部使用_
 */
@InternalApi
public actual interface BasePlatformBot {
    /**
     * 添加一个事件**预处理器**。
     *
     * [registerPreProcessor] 与 [processor] 不同的是，
     * [registerPreProcessor] 所注册的所有事件处理器会在接收到事件的时候以**同步顺序**的方式依次执行，
     * 而后再交由下游的 [processor] 处理。
     *
     * 也同样因此，尽可能避免在 [registerPreProcessor] 中执行任何耗时逻辑，这可能会对事件处理流程造成严重阻塞。
     *
     * @param processor 用于处理事件的函数类型
     */
    @JvmSynthetic
    public actual fun registerPreProcessor(processor: EventProcessor): DisposableHandle

    /**
     * 添加一个事件处理器。
     *
     */
    @JvmSynthetic
    public actual fun registerProcessor(processor: EventProcessor): DisposableHandle


    /**
     * 注册一个使用非挂起的阻塞函数 [processor] 的事件处理器。
     *
     */
    @Api4J
    public fun registerBlockingProcessor(processor: BiConsumer<Signal.Dispatch, String>): DisposableHandle {
        return registerProcessor { raw -> processor.accept(this, raw) }
    }

    /**
     * 注册一个使用非挂起的阻塞函数 [processor] 的事件处理器，并通过 [Class] 对类型进行筛选。
     */
    @Api4J
    public fun <E : Signal.Dispatch> registerBlockingProcessor(
        eventType: Class<out E>,
        processor: BiConsumer<E, String>
    ): DisposableHandle {
        return registerProcessor { raw ->
            if (eventType.isInstance(this)) {
                val event = eventType.cast(this)
                processor.accept(event, raw)
            }
        }
    }

    /**
     * 注册一个使用结果类型为 [CompletionStage] 的异步函数 [processor] 的事件处理器。
     *
     * [processor] 返回的异步结果会在当前监听函数流程内被[挂起并获取][CompletionStage.await]。
     *
     */
    @Api4J
    public fun registerAsyncProcessor(processor: BiFunction<Signal.Dispatch, String, CompletionStage<Void?>>): DisposableHandle {
        return registerProcessor { raw -> processor.apply(this, raw).await() }
    }

    /**
     * 注册一个使用结果类型为 [CompletionStage] 的异步函数 [processor] 的事件处理器，
     * 并通过 [Class] 对类型进行筛选。
     *
     * [processor] 返回的异步结果会在当前监听函数流程内被[挂起并获取][CompletionStage.await]。
     *
     */
    @Api4J
    public fun <E : Signal.Dispatch> registerAsyncProcessor(
        eventType: Class<out E>,
        processor: BiFunction<E, String, CompletionStage<Void?>>
    ): DisposableHandle {
        return registerProcessor { raw ->
            if (eventType.isInstance(this)) {
                val event = eventType.cast(this)
                processor.apply(event, raw).await()
            }
        }
    }
}


