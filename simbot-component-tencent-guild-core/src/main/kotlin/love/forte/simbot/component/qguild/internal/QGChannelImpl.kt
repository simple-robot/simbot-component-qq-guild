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
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.QGChannel
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QGGuildBot
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.message.Message
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.model.Channel
import love.forte.simbot.utils.item.Items
import love.forte.simbot.qguild.event.EventChannel as QGEventChannel
import love.forte.simbot.qguild.model.Channel as QGSourceChannel

internal abstract class BaseQGChannel : QGChannel {
    internal abstract val baseBot: QGBotImpl
    internal abstract val guildInternal: QGGuildImpl


    override val createTime: Timestamp get() = Timestamp.notSupport()
    override val currentMember: Int get() = guildInternal.currentMember
    override val description: String get() = ""
    override val icon: String get() = guildInternal.icon
    override val maximumMember: Int get() = guildInternal.maximumMember
    override val roles: Items<QGRoleImpl> get() = guildInternal.roles

    override suspend fun send(message: Message): QGMessageReceipt {
        val currentCoroutineContext = currentCoroutineContext()

        val builder = MessageParsers.parse(message) {
            if (this.msgId == null) {
                // TODO
//                val currentEvent =
//                    currentCoroutineContext[EventProcessingContext]?.event?.takeIf { it is QGChannelAtMessageEvent } as? QGChannelAtMessageEvent

//                val msgId = currentEvent?.sourceEventEntity?.id
//                if (msgId != null) {
//                    this.msgId = msgId
//                }
            }
        }

        return MessageSendApi.create(source.id, builder.build()).requestBy(baseBot).asReceipt()
    }
}


/**
 *
 * @author ForteScarlet
 */
internal class QGChannelImpl private constructor(
    override val baseBot: QGBotImpl,
    override val source: QGSourceChannel,
    override val guildInternal: QGGuildImpl,
    override val category: QGChannelCategoryIdImpl,
    override val id: ID,
    override val ownerId: ID,
    override val guildId: ID,
) : BaseQGChannel() {

    internal constructor(
        baseBot: QGBotImpl,
        source: QGSourceChannel,
        guildInternal: QGGuildImpl,
        category: QGChannelCategoryIdImpl,
    ) : this(
        baseBot, source, guildInternal, category,
        source.id.ID,
        source.ownerId.ID,
        source.guildId.ID,
    )

    override val name: String get() = source.name
    override val bot: QGGuildBot get() = guildInternal.bot

    override suspend fun guild(): QGGuild = guildInternal
    override suspend fun owner(): QGMember = guildInternal.owner()

    override fun toString(): String {
        return "QGChannelImpl(id=$id, name=$name, bot=$baseBot, source=$source, guild=$guildInternal)"
    }

    internal fun update(newSource: QGSourceChannel): QGChannelImpl =
        QGChannelImpl(baseBot, newSource, guildInternal, category, id, ownerId, guildId)
}

/**
 * 通过事件推送而得到的 [QGChannel] 实现。
 *
 */
internal class QGEventChannelImpl internal constructor(
    override val baseBot: QGBotImpl,
    private val eventChannel: QGEventChannel,
    override val guildInternal: QGGuildImpl,
    override val category: QGChannelCategoryImpl,
) : BaseQGChannel() {
    override val id: ID = eventChannel.id.ID
    override val guildId: ID = eventChannel.guildId.ID
    override val ownerId: ID = eventChannel.ownerId.ID

    override val bot: QGGuildBot get() = guildInternal.bot
    override val name: String get() = eventChannel.name

    override suspend fun guild(): QGGuild {
        TODO("Not yet implemented")
    }

    override suspend fun owner(): QGMember {
        TODO("Not yet implemented")
    }

    override val source: Channel
        get() = TODO("Not yet implemented")
}
