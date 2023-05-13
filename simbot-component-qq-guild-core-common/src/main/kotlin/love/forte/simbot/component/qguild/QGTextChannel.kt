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

package love.forte.simbot.component.qguild

import love.forte.simbot.component.qguild.forum.QGForumChannel
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.model.ChannelType

/**
 * QQ频道中的 **文字子频道**。
 *
 * [QGTextChannel] 是当 [source.type][love.forte.simbot.qguild.model.Channel.type] 为 [ChannelType.TEXT] 类型时的表现。
 *
 *
 * @see QGChannel
 * @see QGGuild
 * @author ForteScarlet
 */
public interface QGTextChannel : QGChannel


/**
 * QQ频道中的 **非文字子频道**。
 *
 * 与 [QGTextChannel] 类型相对，[QGNonTextChannel]
 * 用于统一表示那些不支持消息发送的子频道类型。
 *
 * [QGNonTextChannel] 在默认情况下使用消息发送的API [send]
 * 会直接抛出 [UnsupportedOperationException] 异常。
 *
 * [QGNonTextChannel] 可能会有进一步的子类型实现用来提供更多功能或用以描述它们的类型，
 * 例如：
 * - [QGChannelCategory]
 * - [QGForumChannel]
 *
 * @see QGTextChannel
 * @see QGChannel
 * @see QGChannelCategory
 * @see QGForumChannel
 *
 */
public interface QGNonTextChannel : QGChannel {
    /**
     * 得到当前子频道所属频道服务器
     */
    @JSTP
    override suspend fun guild(): QGGuild

    /**
     * 非文字子频道将会抛出 [UnsupportedOperationException]
     *
     * @throws UnsupportedOperationException 不支持发送消息
     */
    @JST
    override suspend fun send(message: Message): Nothing {
        throw UnsupportedOperationException("The type of channel(id=$id, name=$name) does not support sending messages")
    }

    /**
     * 非文字子频道将会抛出 [UnsupportedOperationException]
     *
     * @throws UnsupportedOperationException 不支持发送消息
     */
    @JST
    override suspend fun send(text: String): Nothing {
        throw UnsupportedOperationException("The type of channel(id=$id, name=$name) does not support sending messages")
    }

    /**
     * 非文字子频道将会抛出 [UnsupportedOperationException]
     *
     * @throws UnsupportedOperationException 不支持发送消息
     */
    @JST
    override suspend fun send(message: MessageContent): Nothing {
        throw UnsupportedOperationException("The type of channel(id=$id, name=$name) does not support sending messages")
    }
}
