/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild

/**
 * 标记一个类型为作为QQ频道中定义的对象模型。
 *
 * 这些类型对于使用者来讲应当仅作为API响应的**反序列化**结果使用，
 * 避免直接构造它们。
 *
 * [ApiModel] 标记的类型可能会随着官方文档的更新而更新，**不保证**未来的更新不会出现破坏性。
 *
 * _此注解目前的作用仅用于源码标记_
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
public annotation class ApiModel


/**
 * 标记一个API类型，代表它是一个在QQ频道API文档中被标记 **仅支持私域机器人** 的API。
 *
 * 文档原文警告如下：
 *
 * **注意**
 * - 公域机器人暂不支持申请，仅私域机器人可用，选择私域机器人后默认开通。
 * - 注意: 开通后需要先将机器人从频道移除，然后重新添加，方可生效。
 *
 * _此注解仅用作标记用，且不保证准确。API是否真的是**私域机器人专属**请以官方API文档为准。_
 *
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
public annotation class PrivateDomainOnly

/**
 * 部的simbot-qq-guild-API，不面向外界使用，可能会随时变更。
 */
@RequiresOptIn("internal qq guild api", level = RequiresOptIn.Level.WARNING)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
public annotation class InternalSrTcgApi
