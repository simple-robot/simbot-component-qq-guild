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

package love.forte.simbot.qguild.api.member

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.TencentApi


/**
 *
 * [删除频道成员](https://bot.q.qq.com/wiki/develop/api/openapi/member/delete_member.html)
 *
 * 用于删除 guild_id 指定的频道下的成员 user_id。
 *
 * - 需要使用的 token 对应的用户具备踢人权限。如果是机器人，要求被添加为管理员。
 * - 操作成功后，会触发频道成员删除事件。
 * - 无法移除身份为管理员的成员
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class DeleteMemberApi private constructor(
    guildId: String,
    userId: String,
    private val _body: Body
) : TencentApi<Unit>() {
    public companion object Factory {

        /**
         * 构造 [DeleteMemberApi].
         *
         * @param addBlacklist 删除成员的同时，是否将该用户添加到频道黑名单中。
         * @param deleteHistoryMsgDays 删除成员的同时，撤回该成员的消息，可以指定撤回消息的时间范围。
         * 注：消息撤回时间范围仅支持固定的天数：`3`，`7`，`15`，`30`。 特殊的时间范围：`-1`: 撤回全部消息。默认值为 `0` 不撤回任何消息。
         *
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            guildId: String,
            userId: String,
            addBlacklist: Boolean = false,
            deleteHistoryMsgDays: Int = 0
        ): DeleteMemberApi = DeleteMemberApi(guildId, userId, Body(addBlacklist, deleteHistoryMsgDays))

    }

    // DELETE /guilds/{guild_id}/members/{user_id}
    private val path = arrayOf("guilds", guildId, "members", userId)

    override val resultDeserializer: DeserializationStrategy<Unit>
        get() = Unit.serializer()

    override val method: HttpMethod
        get() = HttpMethod.Delete

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any
        get() = _body

    /**
     * [DeleteMemberApi] 请求用的参数体，由内部构建并使用。
     */
    @Serializable
    private data class Body(
        @SerialName("add_blacklist") val addBlacklist: Boolean,
        /**
         * 删除成员的同时，撤回该成员的消息，可以指定撤回消息的时间范围
         */
        @SerialName("delete_history_msg_days") val deleteHistoryMsgDays: Int
    ) {
        init {
            require(deleteHistoryMsgDays == -1 || deleteHistoryMsgDaysRangeContains(deleteHistoryMsgDays)) {
                "deleteHistoryMsgDays must be `-1` or in [0, 3, 7, 15, 30], but $deleteHistoryMsgDays"
            }
        }

        companion object {
            private const val deleteHistoryMsgDaysRange =
                0 or (1 shl 3) or (1 shl 7) or (1 shl 15) or (1 shl 30) or (1 shl 0)

            private fun deleteHistoryMsgDaysRangeContains(value: Int): Boolean {
                return value <= 30 && (deleteHistoryMsgDaysRange and (1 shl value)) != 0
            }

        }
    }
}
