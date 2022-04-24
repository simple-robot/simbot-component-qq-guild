/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.isActive
import love.forte.simbot.*
import love.forte.simbot.action.UnsupportedActionException
import love.forte.simbot.definition.Friend
import love.forte.simbot.definition.Group
import love.forte.simbot.definition.UserStatus
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.message.Image
import love.forte.simbot.resources.Resource
import love.forte.simbot.tencentguild.TencentGuildBot
import java.util.stream.Stream

/**
 * 一个tencent频道BOT的接口实例。
 * @author ForteScarlet
 */
public abstract class TencentGuildComponentBot : Bot {
    /**
     * 腾讯频道的 [组件][TencentGuildComponent] 对象实例。
     */
    abstract override val component: TencentGuildComponent

    /**
     * 在 stdlib 模块下的原始Bot类型。
     */
    public abstract val sourceBot: TencentGuildBot

    override val id: ID
        get() = sourceBot.ticket.appId.ID

    /**
     * 腾讯机器人有两个可能的唯一标识：作为bot的clientId以及在系统中作为用户的 user id.
     *
     * 需要注意的是，作为普通用户时的id必须在 [start] 之后才能被检测。在 [start] 之前，将会只通过 [id] 进行检测。
     *
     */
    abstract override fun isMe(id: ID): Boolean

    /**
     * 启动当前bot, 并且初始化此bot的信息。 启动时会通过 [TencentGuildBot.me] 查询当前bot的信息，并为 [isMe] 提供额外id的匹配支持。
     *
     */
    abstract override suspend fun start(): Boolean

    /**
     * bot的用户名
     */
    override val username: String
        get() = sourceBot.botInfo.username

    /**
     * bot的头像
     */
    override val avatar: String
        get() = sourceBot.botInfo.avatar

    /**
     * bot所属的bot管理器
     */
    abstract override val manager: TencentGuildBotManager

    /**
     * bot所属的事件处理器。
     */
    abstract override val eventProcessor: EventProcessor


    /**
     * bot的用户属性。
     */
    override val status: UserStatus get() = BotStatus

    @JvmSynthetic
    abstract override suspend fun guilds(grouping: Grouping, limiter: Limiter): Flow<TencentGuild>

    //// Impl

    @JvmSynthetic
    override suspend fun uploadImage(resource: Resource): Image<*> {
        // TODO fake remote image.
        throw UnsupportedActionException("uploadImage(Resource)")
    }

    override suspend fun resolveImage(id: ID): Image<*> {
        // TODO fake remote image.
        throw UnsupportedActionException("resolveImage(ID)")
    }


    override val isActive: Boolean
        get() = sourceBot.isActive

    @Api4J
    override fun cancelBlocking(reason: Throwable?): Boolean {
        return sourceBot.cancelBlocking(reason)
    }

    @Api4J
    override fun startBlocking(): Boolean {
        return sourceBot.startBlocking()
    }


    @Deprecated("频道不支持群相关API", ReplaceWith("emptyFlow()", "kotlinx.coroutines.flow.emptyFlow"))
    @JvmSynthetic
    override suspend fun groups(grouping: Grouping, limiter: Limiter): Flow<Group> {
        return emptyFlow()
    }

    @OptIn(Api4J::class)
    @Deprecated("频道不支持群相关API", ReplaceWith("Stream.empty()", "java.util.stream.Stream"))
    override fun getGroups(grouping: Grouping, limiter: Limiter): Stream<out Group> {
        return Stream.empty()
    }

    @Deprecated("频道不支持好友相关API", ReplaceWith("emptyFlow()", "kotlinx.coroutines.flow.emptyFlow"))
    @JvmSynthetic
    override suspend fun friends(grouping: Grouping, limiter: Limiter): Flow<Friend> {
        return emptyFlow()
    }

    @OptIn(Api4J::class)
    @Deprecated("频道不支持群相关API", ReplaceWith("Stream.empty()", "java.util.stream.Stream"))
    override fun getFriends(grouping: Grouping, limiter: Limiter): Stream<out Friend> {
        return Stream.empty()
    }

    @Api4J
    abstract override fun getGuilds(grouping: Grouping, limiter: Limiter): Stream<out TencentGuild>

    @Api4J
    abstract override fun getGuild(id: ID): TencentGuild?

    @JvmSynthetic
    abstract override suspend fun guild(id: ID): TencentGuild?

    @JvmSynthetic
    override suspend fun friend(id: ID): Friend? = null

    @Api4J
    override fun getFriend(id: ID): Friend? = null

    @Api4J
    override fun getFriends(): Stream<out Friend> = Stream.empty()

    @Api4J
    override fun getFriends(limiter: Limiter): Stream<out Friend> = Stream.empty()

    @JvmSynthetic
    override suspend fun group(id: ID): Group? = null

    @OptIn(Api4J::class)
    override fun getGroup(id: ID): Group? = null
}


private object BotStatus : UserStatus {
    override val isNormal: Boolean get() = false
    override val isOfficial: Boolean get() = false
    override val isFake: Boolean get() = true
    override val isAnonymous: Boolean get() = false
    override val isBot: Boolean get() = true

}