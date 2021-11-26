package love.forte.simbot.tencentguild

import io.ktor.http.*
import love.forte.simbot.*

/**
 *
 * https://bot.q.qq.com/wiki/develop/api/openapi/guild/model.html
 */
public object TencentGuildApi {
    public val COMPONENT_ID: CharSequenceID = "simbot.tencent.guild".ID
    public val component: Component get() = Components[COMPONENT_ID]

    public val URL: Url = Url("https://api.sgroup.qq.com/")
    public val SANDBOX_URL: Url = Url("https://sandbox.api.sgroup.qq.com")
}


public class TencentGuildComponentInformation : ComponentInformation {
    override val id: ID
        get() = TencentGuildApi.COMPONENT_ID
    override val name: String
        get() = TencentGuildApi.COMPONENT_ID.toString()

    override fun configAttributes(attributes: MutableAttributeMap) {
        // nothing now.
    }

    override fun setComponent(component: Component) {

    }

}



