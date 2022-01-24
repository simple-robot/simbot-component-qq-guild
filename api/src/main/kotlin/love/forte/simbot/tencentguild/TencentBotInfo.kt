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

package love.forte.simbot.tencentguild

import kotlinx.serialization.KSerializer
import love.forte.simbot.BotInfo
import love.forte.simbot.ID


/**
 * Bot信息
 *
 * @author ForteScarlet
 */
public interface TencentBotInfo : TencentUserInfo, BotInfo {
    override val id: ID
    override val username: String
    override val avatar: String
    override val isBot: Boolean
    override val unionOpenid: String?
    override val unionUserAccount: String?
    public companion object {
        internal val serializer: KSerializer<out TencentBotInfo> = TencentBotInfoImpl.serializer()
    }
}