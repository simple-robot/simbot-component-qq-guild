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

import love.forte.simbot.BotManager
import love.forte.simbot.component.tencentguild.internal.TencentGuildBotManagerImpl
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.tencentguild.TencentBotConfiguration

private inline fun <reified K, reified V> Map<K, V>.find(vararg keys: K, nullMessage: () -> String): V {
    for (k in keys) {
        val v = this[k]
        if (v != null) return v
    }
    throw NullPointerException(nullMessage())
}


/**
 *
 * QQ频道BOT的bot管理器。
 *
 * [TencentGuildBotManager] 不允许注册相同 `appId` 的bot。
 *
 * @author ForteScarlet
 */
public abstract class TencentGuildBotManager : BotManager<TencentGuildBot>() {

    override fun register(properties: Map<String, String>): TencentGuildBot {
        val appId = properties.find("app_id", "appId", "appid", "id") {
            "Required property 'app_id'"
        }
        val appKey = properties.find("app_key", "app_secret", "appKey", "appSecret") {
            "Required property 'app_key'"
        }
        val token = properties["token"] ?: throw NullPointerException("Required property 'token'")

        // no config
        return register(appId,  appKey,  token) {}
    }

    public abstract fun register(
        appId: String,
        appKey: String,
        token: String,
        block: TencentBotConfiguration.() -> Unit = {}
    ): TencentGuildBot


    public abstract val configuration: TencentGuildBotManagerConfiguration

    public companion object {
        @JvmStatic
        public fun newInstance(configuration: TencentGuildBotManagerConfiguration): TencentGuildBotManager {
            return TencentGuildBotManagerImpl(configuration)
        }
    }
}

@DslMarker
@Retention(AnnotationRetention.BINARY)
internal annotation class TencentGuildBMDsl


/**
 * 得到一个BotManager.
 */
@TencentGuildBMDsl
public fun tencentGuildBotManager(block: TencentGuildBotManagerConfiguration.() -> Unit): TencentGuildBotManager {
    return TencentGuildBotManager.newInstance(TencentGuildBotManagerConfiguration().also(block))
}


/**
 * [TencentGuildBotManager] 使用的配置类。
 */
@Suppress("MemberVisibilityCanBePrivate")
public class TencentGuildBotManagerConfiguration {
    /**
     * 提供一个事件处理器。
     */
    public lateinit var eventProcessor: EventProcessor

    /**
     * 从此处对所有bot的配置信息进行统一处理。
     */
    public var botConfigure: TencentBotConfiguration.(appId: String, appKey: String, token: String) -> Unit =
        { _, _, _ -> }

}

