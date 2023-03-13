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

package love.forte.simbot.qguild

import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable

/**
 * QQ频道API请求过程中出现的异常
 *
 */
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

/**
 * 判断 [TencentApiException.value] 的值是否为 `404`
 */
public inline val TencentApiException.isNotFound: Boolean get() = value == 404

/**
 * 如果 [TencentApiException.isNotFound] 为 `true`, 得到null，否则抛出此异常
 */
public inline fun <reified T> TencentApiException.ifNotFoundThenNull(): T? = if (isNotFound) null else throw this

/**
 * 提供 [ErrInfo] 和 [HttpStatusCode] ，构建一个 [TencentApiException] 并抛出。
 *
 * @throws TencentApiException 由 [ErrInfo] 和 [HttpStatusCode] 构建而来的异常
 */
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

/**
 * 提供一个 [CloseReason]，构建为一个 [TencentApiException] 并抛出。
 *
 * @throws TencentApiException 由 [CloseReason] 构建而来的异常
 */
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
