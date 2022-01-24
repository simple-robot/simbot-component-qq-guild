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

package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.component.tencentguild.TencentMember
import love.forte.simbot.component.tencentguild.TencentRole
import love.forte.simbot.definition.UserStatus
import love.forte.simbot.tencentguild.TencentMemberInfo
import java.util.stream.Stream

/**
 *
 * @author ForteScarlet
 */
internal class TencentMemberImpl internal constructor(
    override val bot: TencentGuildBotImpl,
    private val info: TencentMemberInfo,
    private val guildFactory: suspend () -> TencentGuildImpl
) : TencentMember, TencentMemberInfo by info {

    internal constructor(
        bot: TencentGuildBotImpl,
        info: TencentMemberInfo,
        guild: TencentGuildImpl
    ) : this(bot, info, { guild })

    override suspend fun guild(): TencentGuildImpl = guildFactory()

    override suspend fun organization(): TencentGuildImpl = guild()

    @Api4J
    override val organization: TencentGuildImpl
        get() = runBlocking { organization() }

    override val status: UserStatus =
        if (info.id == bot.id) {
            bot.status
        } else {
            if (info.user.isBot) botStatus else normalStatus
        }


    override suspend fun roles(): Flow<TencentRole> {
        val roleIds = info.roleIds.mapTo(mutableSetOf()) { it.toString() }
        return guild().roles().filter { it.id.toString() in roleIds }
    }

    @Api4J
    override val roles: Stream<out TencentRole>
        get() {
            val roleIds = info.roleIds.mapTo(mutableSetOf()) { it.toString() }
            return runBlocking { guild() }.getRoles().filter { it.id.toString() in roleIds }
        }


}

private val botStatus = UserStatus.builder().bot().fakeUser().build()
private val normalStatus = UserStatus.builder().normal().build()