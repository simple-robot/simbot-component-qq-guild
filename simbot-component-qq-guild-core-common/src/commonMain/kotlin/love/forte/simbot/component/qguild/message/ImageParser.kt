/*
 * Copyright (c) 2023-2024. ForteScarlet.
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

import io.ktor.utils.io.core.*
import love.forte.simbot.message.*
import love.forte.simbot.resource.ByteArrayResource


/**
 *
 * @author ForteScarlet
 */
public object ImageParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        // TODO attachment?

        when (element) {
            is Image -> {
                if (element is OfflineImage) {
                    processOfflineImage(index, element, messages, builderContext)
                }

            }

            // TODO more image type support for file_image
        }
    }
}

internal fun processOfflineImage(
    index: Int,
    element: OfflineImage,
    messages: Messages?,
    builderContext: SendingMessageParser.BuilderContext
) {
    if (processOfflineImage0(index, element, messages, builderContext)) {
        return
    }
    if (element is OfflineResourceImage) {
        when (val resource = element.resource) {
            is ByteArrayResource -> {
                builderContext.builderOrNew { it.fileImage == null }.setFileImage(resource.data())
            }
            else -> {
                builderContext.builderOrNew { it.fileImage == null }.setFileImage(ByteReadPacket(resource.data()))
            }
        }
    } else {
        val dataBytes = element.data()
        builderContext.builderOrNew { it.fileImage == null }.setFileImage(ByteReadPacket(dataBytes))
    }
}

internal expect fun processOfflineImage0(
    index: Int,
    element: OfflineImage,
    messages: Messages?,
    builderContext: SendingMessageParser.BuilderContext
): Boolean
