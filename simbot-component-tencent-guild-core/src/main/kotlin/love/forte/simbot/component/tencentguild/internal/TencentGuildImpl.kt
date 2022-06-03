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

import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentGuildComponentGuildMemberBot
import love.forte.simbot.component.tencentguild.util.requestBy
import love.forte.simbot.tencentguild.TencentApiException
import love.forte.simbot.tencentguild.TencentChannelInfo
import love.forte.simbot.tencentguild.TencentGuildInfo
import love.forte.simbot.tencentguild.TencentRoleInfo
import love.forte.simbot.tencentguild.api.channel.GetGuildChannelListApi
import love.forte.simbot.tencentguild.api.member.GetMemberApi
import love.forte.simbot.tencentguild.api.role.GetGuildRoleListApi
import love.forte.simbot.utils.LazyValue
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.effectOn
import love.forte.simbot.utils.item.effectedItemsByFlow
import love.forte.simbot.utils.item.itemsByFlow
import love.forte.simbot.utils.lazyValue
import love.forte.simbot.utils.runInBlocking
import kotlin.time.Duration

/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildImpl(
    private val baseBot: TencentGuildComponentBotImpl,
    private val guildInfo: TencentGuildInfo,
) : TencentGuild, TencentGuildInfo by guildInfo {
    
    override val bot: TencentGuildComponentGuildMemberBot by lazy {
        runInBlocking { baseBot.asMember(member(baseBot.sourceBot.botInfo.id)!!) }
    }
    
    
    override suspend fun member(id: ID): TencentMemberImpl? {
        val member = kotlin.runCatching { GetMemberApi(guildInfo.id, id).requestBy(baseBot) }.getOrElse { e ->
            if (e !is TencentApiException) throw e
            
            if (e.value == 404) null else throw e
        }
        
        return member?.let { m -> TencentMemberImpl(baseBot, m, this) }
    }
    
    override val roles: Items<TencentRoleImpl>
        get() = bot.itemsByFlow { prop ->
            val flow = getRoleFlow(guildInfo.id).map { info ->
                TencentRoleImpl(baseBot, info)
            }
            prop.effectOn(flow)
        }
    
    private fun getRoleFlow(guildId: ID): Flow<TencentRoleInfo> = flow {
        GetGuildRoleListApi(guildId).requestBy(baseBot).roles.forEach {
            emit(it)
        }
    }
    
    override val currentChannel: Int by lazy {
        baseBot.async { getChildrenFlow(guildInfo.id).count() }
            .asCompletableFuture().join()
    }
    
    override val children: Items<TencentChannelImpl>
        get() = bot.effectedItemsByFlow {
            getChildrenFlow(guildInfo.id).map { info ->
                TencentChannelImpl(baseBot, info, this)
            }
        }
    
    
    override suspend fun mute(duration: Duration): Boolean = false
    
    
    private var _owner: LazyValue<TencentMemberImpl> = lazyValue {
        member(guildInfo.ownerId)!!
    }
    
    override suspend fun owner(): TencentMemberImpl = _owner()
    
    @Api4J
    override val owner: TencentMemberImpl
        get() = runBlocking { owner() }
    
    private fun getChildrenFlow(guildId: ID): Flow<TencentChannelInfo> = flow {
        val list = GetGuildChannelListApi(guildId = guildId).requestBy(baseBot)
        list.forEach { emit(it) }
    }
    
    
}