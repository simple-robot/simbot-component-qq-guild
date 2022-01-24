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