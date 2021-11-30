/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.component.tencentguild

import love.forte.simbot.*

/**
 *
 * https://bot.q.qq.com/wiki/develop/api/openapi/guild/model.html
 */
public object TencentGuildComponent {
    public val COMPONENT_ID: CharSequenceID = "simbot.tencent.guild".ID
    @Suppress("ObjectPropertyName")
    internal lateinit var _component: Component
    public val component: Component get() = if (::_component.isInitialized) _component else Components[COMPONENT_ID]
}


public class TencentGuildComponentInformation : ComponentInformation {
    override val id: ID
        get() = TencentGuildComponent.COMPONENT_ID
    override val name: String
        get() = TencentGuildComponent.COMPONENT_ID.toString()

    override fun configAttributes(attributes: MutableAttributeMap) {
        // nothing now.
    }

    override fun setComponent(component: Component) {
        TencentGuildComponent._component = component
    }

}



