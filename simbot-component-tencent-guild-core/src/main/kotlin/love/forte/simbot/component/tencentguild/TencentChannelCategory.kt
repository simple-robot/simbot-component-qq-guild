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

package love.forte.simbot.component.tencentguild

import love.forte.simbot.Api4J
import love.forte.simbot.definition.BotContainer
import love.forte.simbot.definition.Category
import love.forte.simbot.definition.GuildInfoContainer
import love.forte.simbot.tencentguild.CHANNEL_TYPE_GROUPING
import love.forte.simbot.tencentguild.TencentChannelInfo

/**
 *
 * 当一个频道的 [TencentChannel.channelType] 的值等于 [CHANNEL_TYPE_GROUPING] 时，
 * 此频道代表为一个分组。
 *
 * @author ForteScarlet
 */
public interface TencentChannelCategory : Category, TencentChannelInfo, BotContainer, GuildInfoContainer {
    override val bot: TencentGuildComponentGuildBot
    
    /**
     * 获取此分类所属的频道服务器。
     */
    @OptIn(Api4J::class)
    override val guild: TencentGuild
    
    /**
     * 获取此分类所属的频道服务器。
     */
    @JvmSynthetic
    override suspend fun guild(): TencentGuild = guild
}