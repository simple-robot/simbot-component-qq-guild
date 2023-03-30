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

import kotlinx.coroutines.await
import love.forte.simbot.qguild.event.Signal
import kotlin.js.Promise
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * JS平台下实现的 [Bot] 基础抽象类，对 [Bot] 提供额外的能力。
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
    public actual fun registerPreProcessor(processor: EventProcessor): DisposableHandle

    /**
     * 添加一个事件处理器。
     *
     */
    public actual fun registerProcessor(processor: EventProcessor): DisposableHandle


    /**
     * 注册一个使用非挂起函数 [processor] 的事件处理器。
     */
    @Api4JS
    public fun registerBlockingProcessor(processor: Signal.Dispatch.(raw: String) -> Unit): DisposableHandle {
        return registerProcessor { raw -> processor.invoke(this, raw) }
    }

    /**
     * 注册一个使用非挂起函数 [processor] 的事件处理器，
     * 并通过 [KClass] 对类型进行筛选。
     */
    @Api4JS
    @JsName("registerBlockingProcessorWithType")
    public fun <E : Signal.Dispatch> registerBlockingProcessor(
        eventType: KClass<out E>,
        processor: E.(raw: String) -> Unit
    ): DisposableHandle {
        return registerProcessor { raw ->
            if (eventType.isInstance(this)) {
                val event = eventType.cast(this)
                processor.invoke(event, raw)
            }
        }
    }

    /**
     * 注册一个使用异步函数（返回值为 [Promise]）类型 [processor] 的事件处理器。
     *
     * [processor] 返回的异步结果会在当前监听函数流程内被[挂起并获取][Promise.await]。
     */
    @Api4JS
    public fun registerAsyncProcessor(processor: Signal.Dispatch.(raw: String) -> Promise<Any?>): DisposableHandle {
        return registerProcessor { raw -> processor.invoke(this, raw).await() }
    }

    /**
     * 注册一个使用异步函数（返回值为 [Promise]）类型 [processor] 的事件处理器，
     * 并通过 [KClass] 对类型进行筛选。
     *
     * [processor] 返回的异步结果会在当前监听函数流程内被[挂起并获取][Promise.await]。
     */
    @Api4JS
    @JsName("registerAsyncProcessorWithType")
    public fun <E : Signal.Dispatch> registerAsyncProcessor(
        eventType: KClass<out E>,
        processor: E.(raw: String) -> Promise<Any?>
    ): DisposableHandle {
        return registerProcessor { raw ->
            if (eventType.isInstance(this)) {
                val event = eventType.cast(this)
                processor.invoke(event, raw).await()
            }
        }
    }
}


