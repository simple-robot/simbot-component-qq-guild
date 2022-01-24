/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

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