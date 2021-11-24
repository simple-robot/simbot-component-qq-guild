package love.forte.simbot.api.tencentguild

import love.forte.simbot.*

/**
 *
 * https://bot.q.qq.com/wiki/develop/api/openapi/guild/model.html
 */
public object TencentGuildApi {
    public val COMPONENT_ID: CharSequenceID = "simbot-tencent-guild".ID
    public val component: Component get() = Components[COMPONENT_ID]
}


public class TencentGuildComponent : ComponentRegistrar {
    override fun registerComponent(configuration: ComponentConfiguration) {
        configuration.id = TencentGuildApi.COMPONENT_ID
        configuration.name = "simbot-tencent-guild"
    }

    override fun setComponent(component: Component) {
        println(component)
    }

}


public const val TENCENT_GUILD_BOT_COMPONENT_ID: String = "simbot-tencent-guild-bot"
public inline val tgbComponent: Component get() = Components[TENCENT_GUILD_BOT_COMPONENT_ID]

