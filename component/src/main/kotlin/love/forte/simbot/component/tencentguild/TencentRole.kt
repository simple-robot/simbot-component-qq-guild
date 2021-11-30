package love.forte.simbot.component.tencentguild

import kotlinx.coroutines.flow.Flow
import love.forte.simbot.ID
import love.forte.simbot.definition.Permission
import love.forte.simbot.definition.Role
import love.forte.simbot.tencentguild.TencentRoleInfo

/**
 *
 * @author ForteScarlet
 */
public interface TencentRole : Role, TencentRoleInfo {
    override val id: ID
    override val name: String
    override suspend fun permissions(): Flow<Permission>
    override val color: Int
    override val isHoist: Boolean
    override val number: Int
    override val memberLimit: Int
}