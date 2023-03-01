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

package love.forte.simbot.tencentguild

import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable


@Suppress("MemberVisibilityCanBePrivate")
public class TencentApiException : IllegalStateException {
    public val info: ErrInfo?
    public val value: Int
    public val description: String
    
    public constructor(value: Int, description: String) : super("$value: $description") {
        this.info = null
        this.value = value
        this.description = description
    }
    
    public constructor(
        info: ErrInfo,
        value: Int,
        description: String,
    ) : super("$value: $description; response info: $info") {
        this.info = info
        this.value = value
        this.description = description
    }
    
}

public inline fun ErrInfo.err(codeBlock: () -> HttpStatusCode): Nothing {
    val code = codeBlock()
    throw TencentApiException(this, code.value, code.description)
}

/**
 * 如果返回值不是成功的响应码，[ErrInfo] 则为统一的错误码响应格式。
 *
 */
@Suppress("MemberVisibilityCanBePrivate")
@Serializable
public data class ErrInfo(val code: Int, val message: String)


@Suppress("NOTHING_TO_INLINE")
public inline fun CloseReason?.err(e: Throwable? = null): Nothing {
    if (this == null) {
        if (e != null) {
            throw TencentApiException(-1, "No reason").initCause(e)
        } else {
            throw TencentApiException(-1, "No reason")
        }
    }
    val known = knownReason
    val message = message
    if (known != null) {
        if (e != null) {
            throw TencentApiException(
                known.code.toInt(),
                "${known.name}: $message"
            ).initCause(e)
        } else {
            throw TencentApiException(known.code.toInt(), "${known.name}: $message")
        }
    } else {
        if (e != null) {
            throw TencentApiException(code.toInt(), message).initCause(e)
        } else {
            throw TencentApiException(code.toInt(), message)
        }
    }
}
