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

package love.forte.simbot.component.qguild.message

import io.ktor.client.request.forms.*
import io.ktor.utils.io.streams.*
import love.forte.simbot.message.Image
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.resources.ByteArrayResource
import love.forte.simbot.resources.FileResource
import love.forte.simbot.resources.PathResource
import love.forte.simbot.resources.URLResource


/**
 *
 * @author ForteScarlet
 */
public object ImageParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: Message.Element<*>,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        // TODO attachment?

        when (element) {
            is Image -> {
                when (val resource = element.resource()) {
                    is FileResource -> {
                        builderContext.builderOrNew { it.fileImage == null }.setFileImage(resource.file)
                    }

                    is PathResource -> {
                        builderContext.builderOrNew { it.fileImage == null }.setFileImage(resource.path)
                    }

                    is ByteArrayResource -> {
                        builderContext.builderOrNew { it.fileImage == null }.setFileImage(resource.bytes)
                    }

                    is URLResource -> {
                        builderContext.builderOrNew { it.image == null }.image = resource.url.toString()
                    }

                    else -> {
                        builderContext.builderOrNew { it.fileImage == null }.setFileImage(InputProvider { resource.openStream().asInput() })
                    }
                }
            }

            // TODO more image type support for file_image
        }
    }
}
