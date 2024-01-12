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

package love.forte.simbot.qguild.api.message

import io.ktor.client.request.forms.*
import love.forte.simbot.qguild.QGInternalApi

/**
 * native平台没有额外的类型支持
 *
 */
public actual fun FormBuilder.resolveOther(fileImage: Any?) {
}

/**
 * 提供一些需要由不同平台额外实现的基类。
 * 主要针对 `fileImage`。
 */
@QGInternalApi
public actual abstract class BaseMessageSendBodyBuilder actual constructor() {
    public actual open var fileImage: Any? = null
    protected set
}
