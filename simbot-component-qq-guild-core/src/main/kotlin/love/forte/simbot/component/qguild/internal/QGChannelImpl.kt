/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.internal

import kotlinx.coroutines.currentCoroutineContext
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.QGChannel
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QGMember
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.message.Message
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.emptyItems
import love.forte.simbot.qguild.model.Channel as QGSourceChannel

internal abstract class BaseQGChannel : QGChannel {
    internal abstract val baseBot: QGBotImpl
    internal abstract val guildInternal: QGGuildImpl? // TODO id only


    override val createTime: Timestamp get() = Timestamp.notSupport()
    override val currentMember: Int get() = -1
    override val description: String get() = ""
    override val icon: String get() = ""
    override val maximumMember: Int get() = -1
    override val roles: Items<QGRoleImpl> get() = emptyItems()

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
    override val bot: QGGuildBotImpl,
    override val source: QGSourceChannel,
    override val guildInternal: QGGuildImpl?,
    override val category: QGChannelCategoryIdImpl,
    override val id: ID,
    override val ownerId: ID,
    override val guildId: ID,
) : BaseQGChannel() {

    internal constructor(
        bot: QGGuildBotImpl,
        source: QGSourceChannel,
        guildInternal: QGGuildImpl?,
        category: QGChannelCategoryIdImpl,
    ) : this(
        bot, source, guildInternal, category,
        source.id.ID,
        source.ownerId.ID,
        source.guildId.ID,
    )

    override val baseBot: QGBotImpl
        get() = bot.bot

    override val name: String get() = source.name

    override suspend fun guild(): QGGuild =
        guildInternal ?: bot.guild(guildId) ?: throw NoSuchElementException("guild(id=$guildId)")

    override suspend fun owner(): QGMember = guild().owner()

    override fun toString(): String {
        return "QGChannelImpl(id=$id, name=$name, bot=$baseBot, source=$source, guild=$guildInternal)"
    }

    internal fun update(newSource: QGSourceChannel): QGChannelImpl =
        QGChannelImpl(bot, newSource, guildInternal, category, id, ownerId, guildId)
}

