/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.guild

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.newSupervisorCoroutineContext
import love.forte.simbot.component.qguild.internal.message.asReceipt
import love.forte.simbot.component.qguild.message.MessageParsers
import love.forte.simbot.component.qguild.message.QGMessageContent
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.sendMessage
import love.forte.simbot.component.qguild.role.QGMemberRole
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.addStackTrace
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.api.message.direct.CreateDmsApi
import love.forte.simbot.qguild.api.message.direct.DmsSendApi
import love.forte.simbot.qguild.model.DirectMessageSession
import love.forte.simbot.qguild.stdlib.requestDataBy
import kotlin.concurrent.Volatile
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmSynthetic
import love.forte.simbot.qguild.model.Member as QGSourceMember

/**
 *
 * @author ForteScarlet
 */
internal class QGMemberImpl(
    private val bot: QGBotImpl,
    override val source: QGSourceMember,
    private val guildId: ID,
    private val sourceGuild: QGGuildImpl? = null
) : QGMember {
    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()

    private val user get() = source.user

    override val id: ID = user.id.ID

//    override suspend fun guild(): QGGuildImpl =
//        sourceGuild ?: bot.guild(guildId) ?: throw NoSuchElementException("guild(id=$guildId)")

    // TODO
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
                return CreateDmsApi.create(user.id, guildId.literal).requestDataBy(bot.source).also {
                    dms = it
                }
            }
        }
    }

    // TODO

    @ExperimentalQGApi
    override val roles: Collectable<QGMemberRole>
        get() = TODO("Not yet implemented")

    // TODO
//    @ExperimentalSimbotApi
//    override val roles: Collectable<QGMemberRoleImpl>
//        get() {
//            return effectedFlowItems {
//                guild().roles.collect {
//                    if (it.id.literal in roleIdSet) {
//                        emit(
//                            QGMemberRoleImpl(
//                                guildRole = it,
//                                memberId = this@QGMemberImpl.id,
//                                sourceMember = bot.checkIfTransmitCacheable(this@QGMemberImpl)
//                            )
//                        )
//                    }
//                }
//            }
//        }


    @JvmSynthetic
    override suspend fun send(message: Message): QGMessageReceipt {
        val builder = MessageParsers.parse(message)
        return send0(builder)
    }

    override suspend fun send(text: String): QGMessageReceipt {
        return bot.sendMessage(dms.guildId, text)
    }


    override suspend fun send(messageContent: MessageContent): QGMessageReceipt {
        if (messageContent is QGMessageContent) {
            val body = MessageSendApi.Body {
                fromMessage(messageContent.sourceMessage)
            }
            return send0(body)
        }

        return send(messageContent.messages)
    }

    private suspend fun send0(body: MessageSendApi.Body): QGMessageReceipt {
        val dms = getDms()
        return DmsSendApi.create(dms.guildId, body).requestDataBy(bot.source).asReceipt()
    }

    private suspend fun send0(bodyList: List<MessageSendApi.Body.Builder>): QGMessageReceipt {
        val dms = getDms()

        if (bodyList.size == 1) {
            val body = bodyList[0].build()
            return DmsSendApi.create(dms.guildId, body).requestDataBy(bot.source).asReceipt()
        }

        return try {
            bodyList.map {
                DmsSendApi.create(dms.guildId, it.build()).requestDataBy(bot.source)
            }.asReceipt()
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "member.send" }
        }
    }

    override fun toString(): String {
        return "QGMember(id=$id, name=$name, nick=$nick, guildId=$guildId)"
    }
}

