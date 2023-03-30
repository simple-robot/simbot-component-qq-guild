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


/**
 * Native平台下的 [BasePlatformBot] 实现。
 * _没有什么额外的能力提供。_
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
}


