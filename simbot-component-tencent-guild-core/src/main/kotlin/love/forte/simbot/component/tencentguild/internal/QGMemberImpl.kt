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

package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.sync.Mutex
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.QGGuild
import love.forte.simbot.component.tencentguild.QGMember
import love.forte.simbot.component.tencentguild.QGRole
import love.forte.simbot.literal
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.model.DirectMessageSession
import love.forte.simbot.qguild.model.SimpleMember
import love.forte.simbot.toTimestamp
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.effectedItemsByFlow

/**
 *
 * @author ForteScarlet
 */
internal class QGMemberImpl(
    override val bot: QGBotImpl,
    override val source: SimpleMember,
    private val _guild: QGGuildImpl,
) : QGMember {
    private val user = source.user
    override val id: ID = user.id.ID

    override val joinTime: Timestamp
        get() = source.joinedAt.toTimestamp()
    override val nickname: String
        get() = source.nick
    override val avatar: String
        get() = user.avatar
    override val username: String
        get() = user.username

    override suspend fun guild(): QGGuild = _guild

    private val roleIdSet = source.roles.toSet()
    private lateinit var dms: DirectMessageSession
    private val dmsInitLock = Mutex()

    private suspend fun getDms(): DirectMessageSession {
        TODO()
//        if (::dms.isInitialized) {
//            return dms
//        } else {
//            dmsInitLock.withLock {
//                if (::dms.isInitialized) {
//                    return dms
//                }
//                return CreateDmsApi.create(id, _guild.id).requestBy(bot).also {
//                    dms = it
//                }
//            }
//        }
    }

    override val roles: Items<QGRole>
        get() {

            return bot.effectedItemsByFlow {
                _guild.roles.asFlow().filter { it.id.literal in roleIdSet }
            }

        }


    @JvmSynthetic
    override suspend fun send(message: Message): TencentMessageReceipt {
        TODO()
//        val dms = getDms()
//        val currentCoroutineContext = currentCoroutineContext()
//
//        val (messageForSend, fileImage) = MessageParsers.parse(message) {
//            forSending {
//                if (this.msgId == null) {
//                    val currentEvent =
//                        currentCoroutineContext[EventProcessingContext]?.event?.takeIf { it is QGChannelAtMessageEvent } as? QGChannelAtMessageEvent
//
//                    val msgId = currentEvent?.sourceEventEntity?.id
//                    if (msgId != null) {
//                        this.msgId = msgId
//                    }
//                }
//            }
//        }
//
//
//        return DmsSendApi.create(dms.guildId, messageForSend, fileImage).requestBy(bot).asReceipt()
    }

    override suspend fun send(text: String): TencentMessageReceipt {
        TODO()
//        val dms = getDms()
//        val currentEvent =
//            currentCoroutineContext()[EventProcessingContext]?.event?.takeIf { it is QGChannelAtMessageEvent } as? QGChannelAtMessageEvent
//        val msgId = currentEvent?.sourceEventEntity?.id
//
//        return DmsSendApi.create(guildId = dms.guildId, content = text, msgId = msgId).requestBy(bot).asReceipt()
    }


    override suspend fun send(message: MessageContent): TencentMessageReceipt {
        return send(message.messages)
    }

    override fun toString(): String {
        return "TencentMemberImpl(bot=$bot, source=$source, guild=$_guild)"
    }
}

