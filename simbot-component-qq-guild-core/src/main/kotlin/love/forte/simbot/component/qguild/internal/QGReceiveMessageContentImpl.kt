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

package love.forte.simbot.component.qguild.internal

import love.forte.simbot.ID
import love.forte.simbot.component.qguild.message.MessageParsers
import love.forte.simbot.component.qguild.message.QGReceiveMessageContent
import love.forte.simbot.message.Messages
import love.forte.simbot.qguild.model.Message

/**
 *
 * @author ForteScarlet
 */
internal class QGReceiveMessageContentImpl(override val sourceMessage: Message) : QGReceiveMessageContent() {

    override val messageId: ID = sourceMessage.id.ID

    override val messages: Messages by lazy(LazyThreadSafetyMode.PUBLICATION) {
        MessageParsers.parse(sourceMessage).messages
    }

    override val plainText: String by lazy(LazyThreadSafetyMode.PUBLICATION) {
        QGReceiveMessageContentPlanTextProcessor.process(sourceMessage)
    }

    override fun toString(): String {
        return "QGReceiveMessageContentImpl(messageId=$messageId, sourceMessage=$sourceMessage)"
    }


    override fun equals(other: Any?): Boolean {
        if (other !is QGReceiveMessageContent) return false
        if (other === this) return true
        return messageId == other.messageId
    }

    override fun hashCode(): Int = messageId.hashCode()
}


internal object QGReceiveMessageContentPlanTextProcessor {
    private const val AT_USER_GROUP = "ur"
    private const val AT_USER_VALUE = "uv"

    private const val AT_EVERYONE_GROUP = "all"
    private const val MENTION_CHANNEL_GROUP = "cl"
    private const val MENTION_CHANNEL_VALUE = "cv"

    private const val EMOJI_GROUP = "ej"
    private const val EMOJI_VALUE = "ev"

    private val replaceRegex = Regex(
        "(?<$AT_USER_GROUP><@!?(?<$AT_USER_VALUE>\\d+)>)" +
                "|(?<$AT_EVERYONE_GROUP>@everyone)" +
                "|(?<$MENTION_CHANNEL_GROUP><#(?<$MENTION_CHANNEL_VALUE>\\d+)>)" +
                "|(?<$EMOJI_GROUP><emoji:(?<$EMOJI_VALUE>\\d+)>)"
    )
    // private val mentionRegex = Regex("<@!(?<uid>\\d+)>|<#!(?<cid>\\d+)>")
    /*
        @用户	<@user_id> 或者 <@!user_id>
            解析为 @用户 标签	<@1234000000001>

        @所有人	@everyone
            解析为 @所有人 标签，需要机器人拥有发送 @所有人 消息的权限	@everyone

        #子频道	<#channel_id>
            解析为 #子频道 标签，点击可以跳转至子频道，仅支持当前频道内的子频道	<#12345>

        表情	<emoji:id>
            解析为系统表情，具体表情id参考 Emoji 列表，仅支持type=1的系统表情，type=2的emoji表情直接按字符串填写即可	<emoji:4> 解析为得意表情
     */

    fun process(message: Message): String {
        val atUsers = message.mentions
            .groupingBy { it.id }
            .eachCountTo(mutableMapOf())

        var atEvery = false

        return replaceRegex.replace(message.content) { result ->
            val groups = result.groups

            if (atUsers.isNotEmpty()) {
                groups[AT_USER_GROUP]?.also { atUserGroup ->
                    groups[AT_USER_VALUE]?.also { atUserValue ->
                        return@replace if (atUsers.compute(atUserValue.value) { _, old ->
                                if (old == null || old <= 0) null else old - 1
                            } != null) {
                            ""
                        } else {
                            atUserGroup.value
                        }
                    }
                    atUserGroup.value
                }
            }


            // TODO 是否真的需要解析 #channel ?
            groups[MENTION_CHANNEL_GROUP]?.also {
                return@replace ""
            }

            if (message.mentionEveryone && !atEvery) {
                groups[AT_EVERYONE_GROUP]?.also { atEveryGroup ->
                    atEvery = true
                    return@replace ""
                }
            }

            // TODO emoji?
            groups[EMOJI_GROUP]?.also {
                return@replace ""
            }

            result.value
        }
    }

}
