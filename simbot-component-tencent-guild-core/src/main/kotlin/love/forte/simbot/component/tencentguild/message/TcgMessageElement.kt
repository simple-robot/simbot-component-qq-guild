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

package love.forte.simbot.component.tencentguild.message

import love.forte.simbot.message.Message


/**
 *
 * 腾讯QQ频道下所有的组件特殊消息的统一类型。
 *
 * 所有腾讯QQ频道的组件消息类型，也就是 [TcgMessageElement] 的实现类型，其开头都会以 `Tcg` 开头，例如 [TcgReplyTo].
 *
 * @author ForteScarlet
 */
public interface TcgMessageElement<out E : TcgMessageElement<E>> : Message.Element<E>
