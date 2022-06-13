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

package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.flow.filter
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.action.UnsupportedActionException
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.component.tencentguild.TencentRole
import love.forte.simbot.definition.UserStatus
import love.forte.simbot.literal
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.tencentguild.TencentMemberInfo
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.effectedItemsByFlow

/**
 *
 * @author ForteScarlet
 */
internal class TencentMemberImpl(
    override val bot: TencentGuildComponentBotImpl,
    private val info: TencentMemberInfo,
    override val guild: TencentGuildImpl,
) : TencentMember, TencentMemberInfo by info {
    private val roleIdSet = info.roleIds.mapTo(mutableSetOf()) { it.literal }
    
    @ExperimentalSimbotApi
    override val status: UserStatus =
        if (info.id == bot.id) {
            bot.status
        } else {
            if (info.user.isBot) botStatus else normalStatus
        }
    
    override val roles: Items<TencentRole>
        get() {
            
            return bot.effectedItemsByFlow {
                guild.roles.asFlow().filter { it.id.literal in roleIdSet }
            }
            
        }
    
    
    @Deprecated("Not yet implemented")
    @JvmSynthetic
    override suspend fun send(message: Message): MessageReceipt {
        throw UnsupportedActionException("send(message)")
        // TODO
    }
    
    override fun toString(): String {
        return "TencentMemberImpl(bot=$bot, info=$info, guild=$guild)"
    }
}

@ExperimentalSimbotApi
private val botStatus = UserStatus.builder().bot().fakeUser().build()

@ExperimentalSimbotApi
private val normalStatus = UserStatus.builder().normal().build()