package love.forte.simboot.component.tencentguild

import love.forte.di.annotation.SpareBean
import love.forte.simboot.factory.BotRegistrarFactory
import love.forte.simbot.BotRegistrar
import javax.inject.Named


/**
 *
 * @author ForteScarlet
 */
@Named("tencentGuildBotRegistrarFactory")
@SpareBean
public class TencentGuildBotRegistrarFactory : BotRegistrarFactory {
    override fun invoke(): BotRegistrar {
        TODO("Not yet implemented")
    }
}