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

package love.forte.simbot.component.qguild.message

import love.forte.simbot.message.AggregatedMessageReceipt
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.message.SingleMessageReceipt
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.model.Message


/**
 * QQ频道中消息发送后的回执。可能代表一个 [单回执][QGSingleMessageReceipt] 或一个 [聚合回执][QGAggregatedMessageReceipt]。
 *
 * 在发送消息时，如果消息链中出现了无法在同一次 [MessageSendApi] 中就请求的内容，则会将请求拆分为多个请求，并将多个消息合并为一个 [QGAggregatedMessageReceipt]。
 *
 * 更多说明参考 [sendMessage][love.forte.simbot.component.qguild.message.sendMessage] 的文档说明。
 *
 * @see QGSingleMessageReceipt
 * @see QGAggregatedMessageReceipt
 *
 * @author ForteScarlet
 */
public interface QGMessageReceipt : MessageReceipt {

    /**
     * 是否发送成功。
     * 能得到此类型即说明消息已发送成功，始终为 `true`。
     */
    override val isSuccess: Boolean


    /**
     * 消息暂时不支持撤回。始终得到 `false`。
     */
    override suspend fun delete(): Boolean = false
}


/**
 * 代表为一次消息发送请求后的回执结果，是 [QGAggregatedMessageReceipt] 的元素类型。
 */
public abstract class QGSingleMessageReceipt : SingleMessageReceipt(), QGMessageReceipt {

    /**
     * 是否发送成功。
     * 能得到此类型即说明消息已发送成功，始终为 `true`。
     */
    override val isSuccess: Boolean
        get() = true

    /**
     * QQ频道消息发送api发送消息后得到的回执，也就是消息对象。
     */
    public abstract val messageResult: Message
}

/**
 * 多个 [QGSingleMessageReceipt] 聚合后的聚合回执。
 *
 */
public abstract class QGAggregatedMessageReceipt : AggregatedMessageReceipt(), QGMessageReceipt {

    /**
     * 是否发送成功。
     * 能得到此类型即说明消息已发送成功，始终为 `true`。
     */
    override val isSuccess: Boolean
        get() = true

    /**
     * 聚合内容的数量，通常来讲会 `> 1` 。
     */
    abstract override val size: Int

    /**
     * 获取指定索引位的 [QGSingleMessageReceipt]。
     *
     * @throws IndexOutOfBoundsException 索引越界
     */
    abstract override fun get(index: Int): QGSingleMessageReceipt

    /**
     * 得到所有的 [QGSingleMessageReceipt] 的迭代器。
     */
    abstract override fun iterator(): Iterator<QGSingleMessageReceipt>


    /**
     * 遍历内容的所有 [QGSingleMessageReceipt] 并依次使用它们的 [`delete`][QGSingleMessageReceipt.delete]
     */
    override suspend fun delete(): Boolean {
        return super<AggregatedMessageReceipt>.delete()
    }
}
