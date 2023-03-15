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

package love.forte.simbot.component.qguild.internal

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.component.qguild.QGRole
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.literal
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.api.message.direct.CreateDmsApi
import love.forte.simbot.qguild.api.message.direct.DmsSendApi
import love.forte.simbot.qguild.model.DirectMessageSession
import love.forte.simbot.toTimestamp
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.effectedItemsByFlow
import love.forte.simbot.qguild.model.Member as QGSourceMember

/**
 *
 * @author ForteScarlet
 */
internal class QGMemberImpl(
    override val source: QGSourceMember,
    internal val _guild: QGGuildImpl, // TODO nullable
) : QGMember {

    private val user get() = source.user

    override val id: ID = user.id.ID

    override val bot: QGBot
        get() = _guild.bot.bot

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

    private val dmsInitLock = Mutex()

    @Volatile
    private lateinit var dms: DirectMessageSession

    private suspend fun getDms(): DirectMessageSession {
        if (::dms.isInitialized) {
            return dms
        } else {
            dmsInitLock.withLock {
                if (::dms.isInitialized) {
                    return dms
                }
                return CreateDmsApi.create(user.id, _guild.id.literal).requestBy(bot).also {
                    dms = it
                }
            }
        }
    }

    override val roles: Items<QGRole>
        get() {
            // TODO
            return bot.effectedItemsByFlow {
                _guild.roles.asFlow().filter { it.id.literal in roleIdSet }
            }
        }


    @JvmSynthetic
    override suspend fun send(message: Message): QGMessageReceipt {
        val dms = getDms()
        val currentCoroutineContext = currentCoroutineContext()

        val builder = MessageParsers.parse(message) {
            // TODO try auto-include msgId
            if (this.msgId == null) {
//              val currentEvent =
//                  currentCoroutineContext[EventProcessingContext]?.event?.takeIf { it is QGEvent<*> } as? QGEvent<*>
//
//              val msgId = currentEvent?.sourceEventEntity?.id
//              if (msgId != null) {
//                  this.msgId = msgId
//              }
            }
        }

        return DmsSendApi.create(dms.guildId, builder.build()).requestBy(bot).asReceipt()
    }

    override suspend fun send(text: String): QGMessageReceipt {
        val dms = getDms()

//        val currentEvent =
//            currentCoroutineContext()[EventProcessingContext]?.event?.takeIf { it is QGChannelAtMessageEvent } as? QGChannelAtMessageEvent
//        val msgId = currentEvent?.sourceEventEntity?.id

        val body = MessageSendApi.Body {
            content = text // TODO 转义
        }

        return DmsSendApi.create(dms.guildId, body).requestBy(bot).asReceipt()
    }


    override suspend fun send(message: MessageContent): QGMessageReceipt {
        return send(message.messages)
    }

    override fun toString(): String {
        return "QGMemberImpl(id=$id, nickname=$nickname, username=$username, source=$source, bot=$bot, guild=$_guild)"
    }

    internal fun update(member: QGSourceMember): QGMemberImpl {
        return QGMemberImpl(source, _guild).also {
            val initDms = if (::dms.isInitialized) dms else null
            if (initDms != null) {
                it.dms = initDms
            }
        }
    }
}

