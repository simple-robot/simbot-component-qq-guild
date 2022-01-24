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
import love.forte.simbot.CharSequenceID
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.tencentguild.TencentGuildInfo
import love.forte.simbot.tencentguild.TimestampISO8601Serializer


/**
 *
 *  [频道对象](https://bot.q.qq.com/wiki/develop/api/openapi/guild/model.html)
 *
 * @author ForteScarlet
 */
@Serializable
internal data class TencentGuildInfoImpl(
    override val id: CharSequenceID,
    override val name: String,
    override val icon: String = "", // TODO 缺失
    @SerialName("owner_id")
    override val ownerId: CharSequenceID = "".ID, // TODO 缺失
    @SerialName("owner")
    override val isBotOwner: Boolean,
    @SerialName("member_count")
    override val memberCount: Int = -1,     // TODO 缺失
    @SerialName("max_members")
    override val maxMembers: Int = -1,      // TODO 缺失
    override val description: String = "",  // TODO 缺失
    @SerialName("joined_at")
    @Serializable(TimestampISO8601Serializer::class)
    override val joinedAt: Timestamp = Timestamp.NotSupport, // TODO 缺失
    @SerialName("union_world_id")
    override val unionWorldId: String? = null,
    @SerialName("union_org_id")
    override val unionOrgId: String? = null,
) : TencentGuildInfo