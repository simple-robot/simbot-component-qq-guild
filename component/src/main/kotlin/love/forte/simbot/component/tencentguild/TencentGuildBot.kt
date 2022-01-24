/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.component.tencentguild

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.isActive
import love.forte.simbot.*
import love.forte.simbot.action.NotSupportActionException
import love.forte.simbot.definition.Friend
import love.forte.simbot.definition.Group
import love.forte.simbot.definition.UserStatus
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.message.Image
import love.forte.simbot.resources.Resource
import love.forte.simbot.tencentguild.TencentBot
import java.util.stream.Stream

/**
 * 一个tencent频道BOT的接口实例。
 * @author ForteScarlet
 */
public abstract class TencentGuildBot : Bot {

    public abstract val sourceBot: TencentBot

    override val id: ID
        get() = sourceBot.ticket.appId.ID

    override val username: String
        get() = sourceBot.botInfo.username

    override val avatar: String
        get() = sourceBot.botInfo.avatar

    abstract override val manager: TencentGuildBotManager

    abstract override val eventProcessor: EventProcessor


    override val component: Component
        get() = ComponentTencentGuild.component

    override val status: UserStatus get() = BotStatus

    @JvmSynthetic
    abstract override suspend fun guilds(grouping: Grouping, limiter: Limiter): Flow<TencentGuild>

    //// Impl

    @JvmSynthetic
    override suspend fun uploadImage(resource: Resource): Image<*> {
        // TODO fake remote image.
        throw NotSupportActionException("upload Image")
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