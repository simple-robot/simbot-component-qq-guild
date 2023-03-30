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

package love.forte.simbot.component.qguild.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.message.*

/**
 * 在QQ频道中AT（提及）一个子频道
 *
 * Deprecated: 直接使用 [At] 并使其 [type][At.type] 为 [`"channel"`][love.forte.simbot.component.qguild.QQGuildComponent.AT_CHANNEL_TYPE] 即可。
 *
 */
@Suppress("DEPRECATION")
@SerialName("qg.atChannel")
@Serializable
@Deprecated(
    "Just use At(target, type = QQGuildComponent.AT_CHANNEL_TYPE)",
    replaceWith = ReplaceWith(
        "At(target, type = QQGuildComponent.AT_CHANNEL_TYPE)",
        "love.forte.simbot.message.At", "love.forte.simbot.component.qguild.QQGuildComponent"
    )
)
@QGSendOnly
public data class QGAtChannel(
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    public val target: ID
) : QGMessageElement<QGAtChannel> {
    override val key: Message.Key<QGAtChannel>
        get() = Key

    public companion object Key : Message.Key<QGAtChannel> {
        override fun safeCast(value: Any): QGAtChannel? = doSafeCast(value)
    }
}


internal object MentionParser : SendingMessageParser {

    @Suppress("DEPRECATION")
    override suspend fun invoke(
        index: Int,
        element: Message.Element<*>,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        // https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html

        /*
            解析为 #子频道 标签，点击可以跳转至子频道，仅支持当前频道内的子频道
         */
        fun atChannel(id: ID) {
            builderContext.builder.appendContent("<#$id>")
        }

        when (element) {
            is At -> {
                if (element.type == "channel") {
                    atChannel(element.target)
                } else {
                    /*
                        解析为 @用户 标签
                     */
                    builderContext.builder.appendContent("<@${element.target}>")
                }
            }

            is AtAll -> {
                /*
                    解析为 @所有人 标签，需要机器人拥有发送 @所有人 消息的权限
                 */
                builderContext.builder.appendContent("@everyone")
            }

            is QGAtChannel -> {
                atChannel(element.target)
            }
        }
    }
}
