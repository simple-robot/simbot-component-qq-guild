package love.forte.simbot.component.tencentguild

import love.forte.simbot.tencentguild.TencentGuildObjective


/**
 *
 * 代表为一个包含一个 [TencentGuildObjective] 类型的[源][source]。
 *
 * @author ForteScarlet
 */
public interface TencentGuildObjectiveContainer<T : TencentGuildObjective> {
    // TODO 尚未完善
    
    /**
     * 得到此容器中保留的原始的 [TencentGuildObjective] 对象。
     */
    public val source: T
    
}