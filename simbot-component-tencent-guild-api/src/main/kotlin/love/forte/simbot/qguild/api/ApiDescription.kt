/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.api

import io.ktor.http.*

/**
 * 对一个 API 类型的描述，属性格式与
 * [接口权限对象](https://bot.q.qq.com/wiki/develop/api/openapi/api_permissions/model.html)
 * 中的相关信息保持一致。
 *
 * [ApiDescription] 主要实现于 [TencentApi] 实现类型的**伴生对象**上。
 *
 * 可以通过 [eq] 来判断两个 [ApiDescription] 中的基本属性是否相同。
 *
 * @author ForteScarlet
 */
public interface ApiDescription {

    /**
     * API 接口名，例如 `/guilds/{guild_id}/members/{user_id}`
     */
    public val path: String

    /**
     * 请求方法，例如 `GET`（全大写）
     */
    public val method: String
}

/**
 * 用于判断两个 [ApiDescription] 中的两个属性是否相同。
 */
public infix fun ApiDescription.eq(other: ApiDescription): Boolean {
    if (path != other.path) return false
    if (method != other.method) return false
    return true
}

/**
 * [ApiDescription] 的基本抽象实现类，直接通过构造进行继承。
 */
public abstract class SimpleApiDescription(
    method: HttpMethod, override val path: String
) : ApiDescription {
    override val method: String = method.value
    override fun toString(): String {
        return "SimpleApiDescription(path='$path', method='$method')"
    }
}


/**
 * [SimpleApiDescription] 的基本抽象实现，[method] 提供为 [`GET`][HttpMethod.Get]
 */
public abstract class SimpleGetApiDescription(path: String) :
    SimpleApiDescription(HttpMethod.Get, path)

/**
 * [SimpleApiDescription] 的基本抽象实现，[method] 提供为 [`POST`][HttpMethod.Post]
 */
public abstract class SimplePostApiDescription(path: String) :
    SimpleApiDescription(HttpMethod.Post, path)

