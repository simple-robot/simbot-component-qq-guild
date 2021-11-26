package love.forte.simbot.tencentguild

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import love.forte.simbot.BotManager
import love.forte.simbot.tencentguild.internal.TencentGuildBotManagerImpl
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 *
 * QQ频道BOT的bot管理器。
 *
 * @author ForteScarlet
 */
public abstract class TencentGuildBotManager : BotManager<TencentGuildBot>() {


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
     * 为Manager提供一个可选的父级[Job].
     */
    @TencentGuildBMDsl
    public var parentJob: Job?
        get() = coroutineContext[Job]
        set(value) {
            if (value != null) {
                coroutineContext += value
            } else {
                coroutineContext.minusKey(Job)
            }
        }

    @OptIn(ExperimentalStdlibApi::class)
    public var dispatcher: CoroutineDispatcher
        get() = coroutineContext[CoroutineDispatcher] ?: Dispatchers.Main.also {
            dispatcher = it
        }
        set(value) {
            coroutineContext += value
        }

    /**
     * 提供一个上下文. 其中的 [Job] 会作为manager的父级Job。
     */
    @TencentGuildBMDsl
    public var coroutineContext: CoroutineContext = EmptyCoroutineContext


    // 得到bot票据信息，提供bot的构建config
    public var botConfigurationFactory: (TencentGuildBot.Ticket) -> TencentGuildBotConfiguration = TODO()


}

