package love.forte.simbot.tencentguild.api

import io.ktor.http.*


public class RouteInfoBuilder(public val parametersAppender: ParametersAppender) {
    /**
     * 可以设置api路径
     */
    public var apiPath: List<String> = emptyList()

    /**
     * 请求头中的 [ContentType], 绝大多数情况下，此参数默认为 [ContentType.Application.Json].
     */
    public var contentType: ContentType = ContentType.Application.Json

}

public fun interface ParametersAppender {
    public fun append(key: String, value: Any)
}