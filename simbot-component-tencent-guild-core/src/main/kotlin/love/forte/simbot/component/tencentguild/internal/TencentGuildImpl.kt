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

import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.TencentGuild
import love.forte.simbot.component.tencentguild.TencentGuildComponentGuildBot
import love.forte.simbot.component.tencentguild.internal.info.toInternal
import love.forte.simbot.component.tencentguild.util.requestBy
import love.forte.simbot.literal
import love.forte.simbot.tencentguild.TencentApiException
import love.forte.simbot.tencentguild.TencentGuildInfo
import love.forte.simbot.tencentguild.api.channel.GetGuildChannelListApi
import love.forte.simbot.tencentguild.api.member.GetMemberApi
import love.forte.simbot.tencentguild.api.role.GetGuildRoleListApi
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.asItems
import love.forte.simbot.utils.item.effectedFlowItems
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration


/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildImpl private constructor(
    private val baseBot: TencentGuildComponentBotImpl,
    @Volatile internal var guildInfo: TencentGuildInfo,
) : TencentGuild {
    override val maximumChannel: Int
        get() = guildInfo.maximumChannel
    override val createTime: Timestamp
        get() = guildInfo.createTime
    override val currentMember: Int
        get() = guildInfo.currentMember
    override val description: String
        get() = guildInfo.description
    override val icon: String
        get() = guildInfo.icon
    override val id: ID
        get() = guildInfo.id
    override val maximumMember: Int
        get() = guildInfo.maximumMember
    override val name: String
        get() = guildInfo.name
    override val ownerId: ID
        get() = guildInfo.ownerId
    
    @Suppress("MemberVisibilityCanBePrivate")
    internal val internalChannels = ConcurrentHashMap<String, TencentChannelImpl>()
    
    internal fun getInternalChannel(id: ID): TencentChannelImpl? = internalChannels[id.literal]
    
    override lateinit var bot: TencentGuildComponentGuildBot
        internal set
    
    override lateinit var owner: TencentMemberImpl
        internal set
    
    override fun toString(): String {
        return "TencentGuildImpl(bot=$baseBot, guildInfo=$guildInfo)"
    }
    
    /**
     * 同步数据，包括成员信息和频道列表信息, 以及 [bot] 和 [owner] 的初始化。
     * 必须在对象实例后执行一次进行初始化，否则内部属性信息将会为空。
     *
     * *Note: 成员列表的获取暂时不支持（只支持私域）*
     */
    internal suspend fun syncData() {
        syncMembers()
        syncChannels()
    }
    
    
    override suspend fun member(id: ID): TencentMemberImpl? {
        val member = kotlin.runCatching { GetMemberApi(guildInfo.id, id).requestBy(baseBot) }.getOrElse { e ->
            if (e !is TencentApiException) throw e
            
            if (e.value == 404) null else throw e
        }
        
        return member?.let { info -> TencentMemberImpl(baseBot, info.toInternal(), this) }
    }
    
    override val roles: Items<TencentRoleImpl>
        get() = bot.effectedFlowItems {
            GetGuildRoleListApi(guildInfo.id).requestBy(baseBot).roles.forEach { info ->
                val roleImpl = TencentRoleImpl(baseBot, info)
                emit(roleImpl)
            }
            // getRoleFlow(guildInfo.id).map { info ->
            //     TencentRoleImpl(baseBot, info)
            // }
        }
    
    
    override val currentChannel: Int get() = internalChannels.size
    
    override val children: Items<TencentChannelImpl>
        get() = internalChannels.values.asItems()
    //     bot.effectedItemsByFlow {
    //     getChildrenFlow(guildInfo.id).map { info ->
    //         TencentChannelImpl(baseBot, info, this)
    //     }
    // }
    
    
    override suspend fun mute(duration: Duration): Boolean = false
    
    /**
     * 同步成员列表、owner、bot的信息。
     */
    private suspend fun syncMembers() {
        // emm... 暂时不支持成员列表的获取. 反正挺神奇的
        
        syncBot()
        syncOwner()
    }
    
    private suspend fun syncOwner() {
        val ownerId = guildInfo.ownerId
        val ownerInfo = GetMemberApi(guildInfo.id, ownerId).requestBy(baseBot)
        
        owner = TencentMemberImpl(baseBot, ownerInfo, this)
        baseBot.logger.debug("Sync guild owner: {}", ownerInfo)
    
    }
    
    private suspend fun syncBot() {
        val member = member(baseBot.id)!!
        bot = TencentGuildComponentGuildBotImpl(baseBot, member)
        baseBot.logger.debug("Sync guild bot: {}", bot)
    }
    
    
    private suspend fun syncChannels() {
        val channelInfoList = GetGuildChannelListApi(guildInfo.id).requestBy(baseBot)
        baseBot.logger.debug(
            "Sync the channel list for guild(id={}, name={}), {} pieces of synchronized data",
            id,
            name,
            channelInfoList.size
        )
        for (info in channelInfoList) {
            val channel = TencentChannelImpl(baseBot, info, this)
            internalChannels[info.id.literal] = channel
        }
    }
    
    companion object {
        suspend fun tencentGuildImpl(
            baseBot: TencentGuildComponentBotImpl,
            guildInfo: TencentGuildInfo,
        ): TencentGuildImpl {
            return TencentGuildImpl(baseBot, guildInfo).also { it.syncData() }
        }
    }
    
}



