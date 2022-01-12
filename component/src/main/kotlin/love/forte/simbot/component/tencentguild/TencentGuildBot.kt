/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
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
public abstract class TencentGuildBot : Bot, TencentBot {

    public abstract val sourceBot: TencentBot

    override val id: ID
        get() = sourceBot.ticket.appId.ID

    override val username: String
        get() = botInfo.username

    override val avatar: String
        get() = botInfo.avatar

    abstract override val manager: TencentGuildBotManager

    abstract override val eventProcessor: EventProcessor


    override val component: Component
        get() = ComponentTencentGuild.component

    override val status: UserStatus get() = BotStatus

    abstract override suspend fun guilds(grouping: Grouping, limiter: Limiter): Flow<TencentGuild>

    override suspend fun uploadImage(resource: Resource): Image<*> {
        // TODO fake remote image.
        throw NotSupportActionException("upload Image")
    }


    override val isActive: Boolean
        get() = sourceBot.isActive

    @Api4J
    override fun cancelBlocking(reason: Throwable?): Boolean {
        return super<TencentBot>.cancelBlocking(reason)
    }

    @Api4J
    override fun startBlocking(): Boolean {
        return super<TencentBot>.startBlocking()
    }


    @Deprecated("频道不支持群相关API", ReplaceWith("emptyFlow()", "kotlinx.coroutines.flow.emptyFlow"))
    override suspend fun groups(grouping: Grouping, limiter: Limiter): Flow<Group> {
        return emptyFlow()
    }

    @OptIn(Api4J::class)
    @Deprecated("频道不支持群相关API", ReplaceWith("Stream.empty()", "java.util.stream.Stream"))
    override fun getGroups(grouping: Grouping, limiter: Limiter): Stream<out Group> {
        return Stream.empty()
    }

    @Deprecated("频道不支持好友相关API", ReplaceWith("emptyFlow()", "kotlinx.coroutines.flow.emptyFlow"))
    override suspend fun friends(grouping: Grouping, limiter: Limiter): Flow<Friend> {
        return emptyFlow()
    }

    @OptIn(Api4J::class)
    @Deprecated("频道不支持群相关API", ReplaceWith("Stream.empty()", "java.util.stream.Stream"))
    override fun getFriends(grouping: Grouping, limiter: Limiter): Stream<out Friend> {
        return Stream.empty()
    }

}


private object BotStatus : UserStatus {
    override val isNormal: Boolean get() = false
    override val isOfficial: Boolean get() = false
    override val isFake: Boolean get() = true
    override val isAnonymous: Boolean get() = false
    override val isBot: Boolean get() = true

}