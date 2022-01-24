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
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.internal.TencentAnnouncesImpl


/**
 *
 * [公告对象(Announces)](https://bot.q.qq.com/wiki/develop/api/openapi/announces/model.html#announces)
 *
 * @author ForteScarlet
 */
public interface TencentAnnounces {
    /**
     * 	频道 id
     */
    public val guildId: ID


    /**
     * 	子频道 id
     */
    public val channelId: ID

    /**
     * 	消息 id
     */
    public val messageId: ID

    public companion object {
        internal val serializer: KSerializer<out TencentAnnouncesImpl> = TencentAnnouncesImpl.serializer()
    }
}