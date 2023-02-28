package love.forte.simbot.tencentguild.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.tencentguild.ApiModel

/**
 * [子频道权限对象(ChannelPermissions)](https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/model.htm)
 *
 */
@ApiModel
@Serializable
public data class ChannelPermissions(
    /**
     * 子频道 id
     */
    @SerialName("channel_id") val channelId: String,
    /**
     * 用户 id 或 身份组 id，只会返回其中之一
     *
     * @see userIdOrRoleId
     */
    @SerialName("user_id") val userId: String? = null,
    /**
     * 用户 id 或 身份组 id，只会返回其中之一
     *
     * @see userIdOrRoleId
     */
    @SerialName("role_id") val roleId: String? = null,
    /**
     * 用户拥有的子频道权限
     */
    @get:JvmName("getPermissions") val permissions: Permissions
) {
    /**
     * 获取[userId]或[roleId]的值。
     * > 用户 id 或 身份组 id，只会返回其中之一。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val userIdOrRoleId: String get() = (userId ?: roleId)!!
}
