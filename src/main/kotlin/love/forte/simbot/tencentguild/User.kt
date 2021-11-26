package love.forte.simbot.tencentguild

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.LongID

/**
 * [User](https://bot.q.qq.com/wiki/develop/api/openapi/user/model.html)
 *
 */
@Serializable
public data class TencentUser(
    public val id: LongID,
    public val username: String,
    public val avatar: String,
    @SerialName("bot")
    public val isBot: Boolean,
    @SerialName("union_openid")
    public val unionOpenid: String? = null,
    public val union_user_account: String? = null
)