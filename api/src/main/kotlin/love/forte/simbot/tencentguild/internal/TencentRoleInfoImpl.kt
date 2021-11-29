package love.forte.simbot.tencentguild.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.CharSequenceID
import love.forte.simbot.tencentguild.TencentRoleInfo

/**
 * [身份组](https://bot.q.qq.com/wiki/develop/api/openapi/guild/role_model.html)
 */
@Serializable
internal data class TencentRoleInfoImpl(
    override val id: CharSequenceID,
    override val name: String,
    override val color: Int,
    /** 0-否, 1-是 */
    @SerialName("hoist")
    val hoistValue: Int,
    override val number: Int,
    @SerialName("member_limit")
    override val memberLimit: Int
) : TencentRoleInfo {

    override val isHoist: Boolean get() = hoistValue == 1

}

//

