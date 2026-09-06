/*
 * Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.panel

import kotlinx.coroutines.flow.flow
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.panel.QGCommandPanelHandle
import love.forte.simbot.component.qguild.panel.QGCommandPanelManager
import love.forte.simbot.qguild.api.panel.CreateCommandPanelApi
import love.forte.simbot.qguild.api.panel.GetCommandPanelApi
import love.forte.simbot.qguild.api.panel.GetCommandPanelListApi
import love.forte.simbot.qguild.model.panel.CommandPanelCreate
import love.forte.simbot.qguild.model.panel.CommandPanelRecord

@OptIn(ExperimentalQGApi::class)
internal class QGCommandPanelManagerImpl(
    private val bot: QGBot,
) : QGCommandPanelManager {
    override fun handle(panelId: ID): QGCommandPanelHandle = QGCommandPanelHandleOperator(bot, panelId)

    override suspend fun create(input: CommandPanelCreate): QGCommandPanelHandle {
        val source = bot.executeData(CreateCommandPanelApi.create(input))
        return handle(source.panelId.ID)
    }

    override suspend fun get(panelId: ID): QGCommandPanelRecordImpl {
        val source = bot.executeData(GetCommandPanelApi.create(panelId.literal))
        return source.toSnapshot(bot)
    }

    override fun list(
        scope: String,
        startCursor: String?,
        limit: Int?,
        autoPagination: Boolean
    ): Collectable<QGCommandPanelRecordImpl> {
        return flow {
            var cursor: String? = startCursor
            do {
                val page = bot.executeData(
                    GetCommandPanelListApi.create(
                        scope = scope,
                        cursor = cursor,
                        limit = limit,
                    )
                )

                page.records.forEach { emit(it.toSnapshot(bot)) }

                if (page.isEnd || page.nextCursor.isEmpty()) {
                    break
                }

                cursor = page.nextCursor
            } while (autoPagination)
        }.asCollectable()
    }
}

private fun CommandPanelRecord.toSnapshot(bot: QGBot): QGCommandPanelRecordImpl = QGCommandPanelRecordImpl(bot, this)
