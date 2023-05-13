/*
 * Copyright (c) 2023. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.forum

import love.forte.simbot.component.qguild.forum.QGThreadCreator
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.qguild.api.forum.PublishThreadApi
import love.forte.simbot.qguild.api.forum.ThreadPublishFormat
import love.forte.simbot.qguild.api.forum.ThreadPublishResult


/**
 *
 * @author ForteScarlet
 */
internal class QGThreadCreatorImpl(private val channel: QGForumChannelImpl) : QGThreadCreator {
    override var title: String? = null
    override var content: String? = null
    override var format: Int? = null
    override var formatType: ThreadPublishFormat?
        get() = format?.let { ThreadPublishFormat.values().find { f -> f.value == it } }
        set(value) {
            format = value?.value
        }

    override suspend fun publish(): ThreadPublishResult {
        return PublishThreadApi.create(
            channelId = channel.source.id,
            title = title ?: throw IllegalArgumentException("Required property 'title' is null"),
            content = content ?: throw IllegalArgumentException("Required property 'content' is null"),
            format = format ?: throw IllegalArgumentException("Required property 'format' is null"),
        ).requestBy(channel.bot)
    }

    override fun toString(): String {
        return "QGThreadCreatorImpl(title=$title, content=$content, format=$format, formatType=$formatType)"
    }
}
