package love.forte.simbot.tencentguild

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.LongID

/**
 * [身份组](https://bot.q.qq.com/wiki/develop/api/openapi/guild/role_model.html)
 */
@Serializable
public data class TencentRole(
    public val id: LongID,
    public val name: String,
    public val color: Int,
    /**
     * 是否在成员列表中单独展示: 0-否, 1-是
     */
    @SerialName("hoist")
    public val hoistValue: Int,

    public val number: Int,

    public val memberLimit: Int
) {

    public val isHoist: Boolean get() = hoistValue == 1
    public val isDefaultRole: Boolean get() = id in defaultRoleIds

    public companion object {
        public val defaultRoleIds: Map<LongID, String> = mapOf(
            0L.ID to "普通成员",
            1L.ID to "管理员",
            2L.ID to "群主",
            3L.ID to "机器人",
            5L.ID to "子频道管理员"
        )


    }
}

//

