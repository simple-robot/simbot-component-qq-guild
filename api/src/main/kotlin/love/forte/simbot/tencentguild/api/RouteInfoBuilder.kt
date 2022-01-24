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
    public var contentType: ContentType? = ContentType.Application.Json

}

public fun interface ParametersAppender {
    public fun append(key: String, value: Any)
}