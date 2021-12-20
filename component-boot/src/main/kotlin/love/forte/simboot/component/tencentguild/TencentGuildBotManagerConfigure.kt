package love.forte.simboot.component.tencentguild

import love.forte.di.annotation.Configurable
import love.forte.di.annotation.SpareBean
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
@SpareBean
@Configurable(prefix = "simbot.component.tencentguild")
public class DefaultTencentGuildBotManagerConfigure : TencentGuildBotManagerConfigure {
    override fun config(configuration: TencentGuildBotManagerConfiguration) {
        // do nothing?
    }
}