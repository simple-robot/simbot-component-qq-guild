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
import love.forte.simbot.tencentguild.TencentChannelInfo


@Serializable
internal data class TencentChannelInfoImpl(
    override val id: CharSequenceID,
    @SerialName("guild_id")
    override val guildId: CharSequenceID,

    override val name: String,
    @SerialName("type")
    override val channelTypeValue: Int,
    @SerialName("sub_type")
    override val channelSubTypeValue: Int,

    override val position: Int,
    @SerialName("parent_id")
    override val parentId: String,

    @SerialName("owner_id")
    override val ownerId: CharSequenceID,
) : TencentChannelInfo