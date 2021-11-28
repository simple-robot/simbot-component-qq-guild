package love.forte.simbot.tencentguild

import io.ktor.http.*
import kotlinx.serialization.Serializable


public class TencentApiException : IllegalStateException {
    @Suppress("MemberVisibilityCanBePrivate")
    public val info: ErrInfo?

    public constructor() : super() {
        this.info = null
    }
    public constructor(message: String?) : super(message) {
        this.info = null
    }
    public constructor(message: String?, cause: Throwable?) : super(message, cause) {
        this.info = null
    }
    public constructor(cause: Throwable?) : super(cause) {
        this.info = null
    }

    public constructor(info: ErrInfo) : super() {
        this.info = info
    }
    public constructor(info: ErrInfo, message: String?) : super(message) {
        this.info = info
    }
    public constructor(info: ErrInfo, message: String?, cause: Throwable?) : super(message, cause) {
        this.info = info
    }
    public constructor(info: ErrInfo, cause: Throwable?) : super(cause) {
        this.info = info
    }
}

public inline fun ErrInfo.err(codeBlock: () -> HttpStatusCode): Nothing {
    val code = codeBlock()
    val statusInfo = "${code.value}: ${code.description} ; response info: $this"
    throw TencentApiException(statusInfo)
}


@Suppress("MemberVisibilityCanBePrivate")
@Serializable
public data class ErrInfo(val code: Int, val message: String)


