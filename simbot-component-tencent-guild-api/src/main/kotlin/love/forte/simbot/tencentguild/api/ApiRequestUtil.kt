/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

@file:JvmName("ApiRequestUtil")

package love.forte.simbot.tencentguild.api

import io.ktor.client.*
import io.ktor.http.*
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import love.forte.simbot.Api4J
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.utils.runInNoScopeBlocking
import java.util.function.Consumer

internal val logger = LoggerFactory.getLogger("love.forte.simbot.tencentguild.api.request")

/**
 *
 * 通过提供的参数，对此api进行请求并得到最终结果。
 *
 * @param client 提供一个 http client
 * @param server 提供一个目标服务器路径
 * @param token 提供一个 token.
 * @param decoder 如果有，提供一个decoder用于通过 [TencentApi.resultDeserializer] 进行反序列化，而不是通过 [client] 进行。
 *
 * @throws love.forte.simbot.tencentguild.TencentApiException 如果响应码不在 200..300 范围内。
 */
@JvmSynthetic
public suspend fun <R> TencentApi<R>.request(
    client: HttpClient,
    server: Url,
    token: String,
    decoder: StringFormat = defaultJson,
): R {
    return doRequest(client, server, token, decoder)
}


/**
 * [request] for Java
 */
@Api4J
@JvmOverloads
public fun <R> doRequest(
    api: TencentApi<R>,
    client: HttpClient,
    server: String,
    token: String,
    decoder: StringFormat = defaultJson,
): R = runInNoScopeBlocking {
    api.request(client, Url(server), token, decoder)
}

@OptIn(Api4J::class)
private val defaultJson = newJson()

@Api4J
public fun newHttpClient(): HttpClient = HttpClient()

@Api4J
@JvmOverloads
public fun newJson(
    build: Consumer<JsonBuilder> = Consumer {
        it.apply {
            isLenient = true
            ignoreUnknownKeys = true
        }
    },
): Json = Json {
    build.accept(this)
}
