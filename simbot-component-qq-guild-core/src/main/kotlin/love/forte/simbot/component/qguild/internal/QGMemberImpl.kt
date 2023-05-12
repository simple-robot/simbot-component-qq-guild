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

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.component.qguild.internal.message.asReceipt
import love.forte.simbot.component.qguild.internal.role.QGMemberRoleImpl
import love.forte.simbot.component.qguild.message.MessageParsers
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.QGReceiveMessageContent
import love.forte.simbot.component.qguild.message.sendMessage
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.literal
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.api.message.direct.CreateDmsApi
import love.forte.simbot.qguild.api.message.direct.DmsSendApi
import love.forte.simbot.qguild.model.DirectMessageSession
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.effectedFlowItems
import kotlin.coroutines.CoroutineContext
import love.forte.simbot.qguild.model.Member as QGSourceMember

/**
 *
 * @author ForteScarlet
 */
internal class QGMemberImpl(
    override val bot: QGBotImpl,
    override val source: QGSourceMember,
    private val guildId: ID,
    private val sourceGuild: QGGuildImpl? = null
) : QGMember {
    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()

    private val user get() = source.user

    override val id: ID = user.id.ID

    @OptIn(ExperimentalSimbotApi::class)
    override val joinTime: Timestamp
        get() = source.joinedAt.toTimestamp()


    override suspend fun guild(): QGGuildImpl =
        sourceGuild ?: bot.guild(guildId) ?: throw NoSuchElementException("guild(id=$guildId)")

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
                return CreateDmsApi.create(user.id, guildId.literal).requestBy(bot).also {
                    dms = it
                }
            }
        }
    }

    @ExperimentalSimbotApi
    override val roles: Items<QGMemberRoleImpl>
        get() {
            return effectedFlowItems {
                guild().roles.collect {
                    if (it.id.literal in roleIdSet) {
                        emit(
                            QGMemberRoleImpl(
                                guildRole = it,
                                memberId = this@QGMemberImpl.id,
                                sourceMember = bot.checkIfTransmitCacheable(this@QGMemberImpl)
                            )
                        )
                    }
                }
            }
        }


    @JvmSynthetic
    override suspend fun send(message: Message): QGMessageReceipt {
        val builder = MessageParsers.parse(message)
        return send0(builder)
    }

    override suspend fun send(text: String): QGMessageReceipt {
        return bot.sendMessage(dms.guildId, text)
    }


    override suspend fun send(message: MessageContent): QGMessageReceipt {
        if (message is QGReceiveMessageContent) {
            val body = MessageSendApi.Body {
                fromMessage(message.sourceMessage)
            }
            return send0(body)
        }

        return send(message.messages)
    }

    private suspend fun send0(body: MessageSendApi.Body): QGMessageReceipt {
        val dms = getDms()
        return DmsSendApi.create(dms.guildId, body).requestBy(bot).asReceipt()
    }

    private suspend fun send0(bodyList: List<MessageSendApi.Body.Builder>): QGMessageReceipt {
        val dms = getDms()

        if (bodyList.size == 1) {
            val body = bodyList[0].build()
            return DmsSendApi.create(dms.guildId, body).requestBy(bot).asReceipt()
        }

        return bodyList.map {
            DmsSendApi.create(dms.guildId, it.build()).requestBy(bot)
        }.asReceipt()
    }

    override fun toString(): String {
        return "QGMemberImpl(id=$id, username=$username, nickname=$nickname, guildId=$guildId)"
    }
}

