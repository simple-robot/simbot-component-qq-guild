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

package love.forte.simbot.tencentguild.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import love.forte.simbot.CharSequenceID
import love.forte.simbot.Timestamp
import love.forte.simbot.tencentguild.TencentMemberInfo
import love.forte.simbot.tencentguild.TimestampISO8601Serializer

/**
 *
 * @author ForteScarlet
 */
@Serializable
internal data class TencentMemberInfoImpl(
    @SerialName("guild_id")
    override val guildId: CharSequenceID? = null,
    override val user: TencentUserInfoImpl,
    override val nick: String = "",
    @SerialName("roles")
    override val roleIds: List<CharSequenceID>,
    @SerialName("joined_at")
    @Serializable(TimestampISO8601Serializer::class)
    override val joinedAt: Timestamp
) : TencentMemberInfo


@Serializable
internal data class TencentMemberInfoImplForMessage(
    @SerialName("guild_id")
    override var guildId: CharSequenceID? = null,
    override val nick: String = "",
    @SerialName("roles")
    override val roleIds: List<CharSequenceID>,
    @SerialName("joined_at")
    @Serializable(TimestampISO8601Serializer::class)
    override val joinedAt: Timestamp
) : TencentMemberInfo {
    @Transient
    override lateinit var user: TencentUserInfoImpl
}