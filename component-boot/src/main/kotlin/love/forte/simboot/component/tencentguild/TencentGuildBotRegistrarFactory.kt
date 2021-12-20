package love.forte.simboot.component.tencentguild

import love.forte.di.annotation.SpareBean
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
@SpareBean
public class TencentGuildBotRegistrarFactory(
    private val configure: TencentGuildBotManagerConfigure
) : BotRegistrarFactory {
    override fun invoke(processor: EventProcessor): BotRegistrar {
        return tencentGuildBotManager(processor) {
            configure.config(this)
        }
    }
}