package love.forte.simbot.tencentguild

import io.ktor.http.*
import kotlinx.serialization.Serializable
import love.forte.simbot.SimbotRuntimeException


public class TencentApiException : SimbotRuntimeException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

public inline fun ErrInfo.err(codeBlock: () -> HttpStatusCode): Nothing {
    val code = codeBlock()
    val statusInfo = "${code.value}: ${code.description} ; response info: $this"
    throw TencentApiException(statusInfo)
}


@Serializable
public data class ErrInfo(val code: String, val message: String)


