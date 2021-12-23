package love.forte.simboot.component.tencentguild

import love.forte.di.annotation.Depend
import love.forte.simboot.factory.BotRegistrarFactory
import love.forte.simbot.BotRegistrar
import love.forte.simbot.component.tencentguild.tencentGuildBotManager
import love.forte.simbot.event.EventProcessor
import javax.inject.Named


/**
 *
 * @author ForteScarlet
 */
@Named("tencentGuildBotRegistrarFactory")
public class TencentGuildBotRegistrarFactory @Depend(required = false) constructor(
    @Depend(required = false) private val configure: TencentGuildBotManagerConfigure? = null
) : BotRegistrarFactory {
    override fun invoke(processor: EventProcessor): BotRegistrar {
        return tencentGuildBotManager(processor) {
            configure?.config(this)
        }
    }
}