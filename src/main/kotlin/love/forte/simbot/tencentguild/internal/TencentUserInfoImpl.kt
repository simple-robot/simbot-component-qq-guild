package love.forte.simbot.tencentguild.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.LongID
import love.forte.simbot.tencentguild.TencentUserInfo

/**
 * [User](https://bot.q.qq.com/wiki/develop/api/openapi/user/model.html)
 *
 */
@Serializable
internal data class TencentUserInfoImpl(
    override val id: LongID,
    override val username: String,
    override val avatar: String,
    @SerialName("bot")
    override val isBot: Boolean,
    @SerialName("union_openid")
    override val unionOpenid: String? = null,
    @SerialName("union_user_account")
    override val unionUserAccount: String? = null
) : TencentUserInfo {
}