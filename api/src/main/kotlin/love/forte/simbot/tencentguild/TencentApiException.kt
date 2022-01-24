/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.tencentguild

import io.ktor.http.*
import io.ktor.http.cio.websocket.*
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
        description: String
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