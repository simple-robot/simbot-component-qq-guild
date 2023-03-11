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

package love.forte.simbot.qguild.api.guild.mute

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.SimpleApiDescription
import love.forte.simbot.qguild.api.TencentApi
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit


/**
 * [禁言批量成员](https://bot.q.qq.com/wiki/develop/api/openapi/guild/patch_guild_mute_multi_member.html)
 *
 * 用于将频道的指定批量成员（非管理员）禁言。
 *
 * 需要使用的 `token` 对应的用户具备管理员权限。如果是机器人，要求被添加为管理员。
 * 该接口同样可用于批量解除禁言，将 `mute_end_timestamp` 或 `mute_seconds` 传值为字符串'0'即可，及需要批量解除禁言的成员的user_id列表user_ids'。
 *
 * @author ForteScarlet
 */
public class MuteMultiMemberApi private constructor(guildId: String, body: MuteBody) : TencentApi<MultiMuteResult>() {
    public companion object Factory : SimpleApiDescription(
        HttpMethod.Patch, "/guilds/{guild_id}/mute"
    ) {

        /**
         * 使用 `mute_end_timestamp` 的方式构建 [MuteMultiMemberApi]
         * @param muteEndTimestamp 禁言到期时间戳，绝对时间戳。
         */
        @JvmStatic
        public fun create(guildId: String, muteEndTimestamp: Instant): MuteMultiMemberApi =
            when (val second = muteEndTimestamp.epochSecond) {  // 单位：秒
                0L -> MuteMultiMemberApi(guildId, MuteBody.Unmute) // 单位：秒
                else -> MuteMultiMemberApi(guildId, MuteBody(muteEndTimestamp = second.toString()))
            }


        /**
         * 使用 `mute_seconds` 的方式构建 [MuteMultiMemberApi]
         * @param muteSeconds 禁言多少秒
         *
         * @throws IllegalArgumentException [muteSeconds] 小于0
         */
        @JvmStatic
        public fun create(guildId: String, muteSeconds: Duration): MuteMultiMemberApi =
            createBySeconds(guildId, muteSeconds.seconds)


        /**
         * 使用 `mute_seconds` 的方式构建 [MuteMultiMemberApi]
         * @param muteTime 禁言时长
         * @param timeUnit 时间单位
         *
         * @throws IllegalArgumentException [muteTime] 转为秒值后小于0
         */
        @JvmStatic
        public fun create(guildId: String, muteTime: Long, timeUnit: TimeUnit): MuteMultiMemberApi =
            createBySeconds(guildId, timeUnit.toSeconds(muteTime))

        /**
         * 构建 `mute_seconds` 为 `"0"` 的 [MuteMultiMemberApi]
         */
        @JvmStatic
        public fun createUnmute(guildId: String): MuteMultiMemberApi = MuteMultiMemberApi(guildId, MuteBody.Unmute)


        private fun createBySeconds(guildId: String, seconds: Long): MuteMultiMemberApi {
            return when {
                seconds == 0L -> MuteMultiMemberApi(guildId, MuteBody.Unmute)
                seconds < 0L -> MuteMultiMemberApi(guildId, MuteBody(muteSeconds = seconds.toString()))
                else -> throw IllegalStateException("mute seconds must >= 0, but $seconds")
            }
        }
    }

    private val path = arrayOf("guilds", guildId, "mute")

    override val resultDeserializer: DeserializationStrategy<MultiMuteResult>
        get() = MultiMuteResult.serializer()

    override val method: HttpMethod
        get() = HttpMethod.Patch

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
    }

    override val body: Any = body
}

/**
 * [MuteMultiMemberApi] 请求成功后的返回值。
 *
 * @property userIds 设置禁言成功的用户ID列表
 *
 */
@ApiModel
@Serializable
public data class MultiMuteResult(@SerialName("user_ids") val userIds: List<String>)
