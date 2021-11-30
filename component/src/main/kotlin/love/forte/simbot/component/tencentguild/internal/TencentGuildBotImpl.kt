/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import love.forte.simbot.Grouping
import love.forte.simbot.Limiter
import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.component.tencentguild.TencentGuildBotManager
import love.forte.simbot.definition.Friend
import love.forte.simbot.definition.Group
import love.forte.simbot.definition.Guild
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.tencentguild.TencentBot
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildBotImpl(
    override val sourceBot: TencentBot,
    override val manager: TencentGuildBotManager,
    override val eventProcessor: EventProcessor,
) : TencentGuildBot(), TencentBot by sourceBot {
    // 0 init 1 start 2 cancel
    private val activeStatus = AtomicInteger(0)


    override suspend fun guilds(grouping: Grouping, limiter: Limiter): Flow<Guild> {
        // TODO
        // GetGuildApi
        TODO("Not yet implemented")
    }


    override suspend fun start(): Boolean = sourceBot.start().also {
        activeStatus.compareAndSet(0, 1)
    }


    override suspend fun join() {
        sourceBot.join()
    }

    override suspend fun cancel(): Boolean = sourceBot.cancel().also {
        activeStatus.set(2)
    }

    override val isStarted: Boolean
        get() = activeStatus.get() >= 1



    override val isCancelled: Boolean
        get() = activeStatus.get() == 2


    override suspend fun groups(grouping: Grouping, limiter: Limiter): Flow<Group> {
        return emptyFlow()
    }

    override fun getGroups(grouping: Grouping, limiter: Limiter): List<Group> {
        return emptyList()
    }

    override suspend fun friends(grouping: Grouping, limiter: Limiter): Flow<Friend> {
        return emptyFlow()
    }

    override fun getFriends(grouping: Grouping, limiter: Limiter): List<Friend> {
        return emptyList()
    }
}