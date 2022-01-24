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

package love.forte.simbot.tencentguild

import kotlinx.serialization.KSerializer
import love.forte.simbot.ID
import love.forte.simbot.definition.UserInfo
import love.forte.simbot.tencentguild.internal.TencentUserInfoImpl

/**
 *
 * @author ForteScarlet
 */
public interface TencentUserInfo : UserInfo {
    /**
     * 用户 id
     */
    override val id: ID

    /**
     * 用户名
     */
    override val username: String

    /**
     * 用户头像地址
     */
    override val avatar: String

    /**
     * 是否是机器人
     */
    public val isBot: Boolean

    /**
     * 特殊关联应用的 openid，需要特殊申请并配置后才会返回。如需申请，请联系平台运营人员。
     */
    public val unionOpenid: String?

    /**
     * 机器人关联的互联应用的用户信息，与union_openid关联的应用是同一个。如需申请，请联系平台运营人员。
     */
    public val unionUserAccount: String?

    public companion object {
        public val serializer: KSerializer<out TencentUserInfo> = TencentUserInfoImpl.serializer()
    }
}