/*
 * Copyright (c) 2024. ForteScarlet.
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

import love.forte.simbot.message.Messages
import love.forte.simbot.message.OfflineImage
import love.forte.simbot.message.OfflineResourceImage
import love.forte.simbot.resource.FileResource
import love.forte.simbot.resource.PathResource
import love.forte.simbot.resource.URIResource

internal actual fun processOfflineImage0(
    index: Int,
    element: OfflineImage,
    messages: Messages?,
    builderContext: SendingMessageParser.BuilderContext
): Boolean {
    if (element is OfflineResourceImage) {
        when (val resource = element.resource) {
            is FileResource -> {
                builderContext.builderOrNew { it.fileImage == null }.fileImage = resource.file
                return true
            }

            is PathResource -> {
                builderContext.builderOrNew { it.fileImage == null }.fileImage = resource.path
                return true
            }

            is URIResource -> {
                builderContext.builderOrNew { it.fileImage == null }.fileImage = resource.uri
                return true
            }
        }
    }

    return false
}
