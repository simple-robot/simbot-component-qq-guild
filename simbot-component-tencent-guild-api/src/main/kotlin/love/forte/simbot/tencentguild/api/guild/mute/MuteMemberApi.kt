/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.tencentguild.api.guild.mute


/**
 * [禁言指定成员](https://bot.q.qq.com/wiki/develop/api/openapi/guild/patch_guild_member_mute.html)
 *
 * 用于禁言频道 `guild_id` 下的成员 `user_id`。
 *
 * 需要使用的 `token` 对应的用户具备管理员权限。如果是机器人，要求被添加为管理员。
 * 该接口同样可用于解除禁言，将 `mute_end_timestamp` 或 `mute_seconds` 传值为字符串'0'即可。
 *
 * @author ForteScarlet
 */
public class MuteMemberApi

// TODO
