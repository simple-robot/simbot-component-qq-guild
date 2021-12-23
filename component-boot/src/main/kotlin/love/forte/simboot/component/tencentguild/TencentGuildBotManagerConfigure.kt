package love.forte.simboot.component.tencentguild

import love.forte.di.annotation.Configurable
import love.forte.di.annotation.SpareBean
import love.forte.simbot.LoggerFactory
import love.forte.simbot.component.tencentguild.TencentGuildBotManagerConfiguration
import javax.inject.Named


/**
 * [TencentGuildBotManagerConfiguration] 配置类。
 * @author ForteScarlet
 */
public interface TencentGuildBotManagerConfigure {

    /**
     * 得到配置文件并进行配置。
     */
    public fun config(configuration: TencentGuildBotManagerConfiguration)

}


@Named("defaultTencentGuildBotManagerConfigure")
@SpareBean(TencentGuildBotManagerConfigure::class)
@Configurable(prefix = "simbot.component.tencentguild")
public class DefaultTencentGuildBotManagerConfigure : TencentGuildBotManagerConfigure {
    override fun config(configuration: TencentGuildBotManagerConfiguration) {
        logger.debug("Do tencent guild bot manager config by Default.")
        // do nothing?
    }

    public companion object {
        private val logger = LoggerFactory.getLogger(DefaultTencentGuildBotManagerConfigure::class)
    }
}