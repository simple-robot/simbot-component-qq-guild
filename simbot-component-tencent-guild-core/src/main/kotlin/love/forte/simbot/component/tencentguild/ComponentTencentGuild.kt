/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simbot.component.tencentguild

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.message.TcgArk
import love.forte.simbot.component.tencentguild.message.TcgAttachmentMessage
import love.forte.simbot.component.tencentguild.message.TcgMentionChannel
import love.forte.simbot.component.tencentguild.message.TcgReplyTo
import love.forte.simbot.message.Message


/**
 * 腾讯频道实现simbot相关组件的基本组件信息，可以用来获取组件ID、组件所用序列化器等等。
 *
 * @author ForteScarlet
 */
public class TencentGuildComponent : Component {
    override val id: ID
        get() = componentID

    override val componentSerializersModule: SerializersModule
        get() = messageSerializersModule


    /**
     * 组件 [TencentGuildComponent] 的注册器。
     */
    public companion object Registrar : ComponentRegistrar<TencentGuildComponent, TencentGuildComponentConfiguration> {

        @Suppress("MemberVisibilityCanBePrivate")
        public const val ID_VALUE: String = "simbot.tencentguild"
        public val componentID: CharSequenceID = ID_VALUE.ID

        /**
         * 作为注册器时的标识。
         */
        override val key: Attribute<TencentGuildComponent> = attribute(ID_VALUE)

        /**
         * 注册配置函数。
         */
        override fun register(block: TencentGuildComponentConfiguration.() -> Unit): TencentGuildComponent {
            // nothing config now.

            return TencentGuildComponent()
        }

        /**
         * 腾讯频道组件所使用到的特殊消息类型序列化信息。
         */
        @JvmStatic
        public val messageSerializersModule: SerializersModule = SerializersModule {
            polymorphic(Message.Element::class) {
                subclass(TcgArk::class, TcgArk.serializer())
                subclass(TcgMentionChannel::class, TcgMentionChannel.serializer())
                subclass(TcgAttachmentMessage::class, TcgAttachmentMessage.serializer())
                subclass(TcgReplyTo::class, TcgReplyTo.serializer())
            }
        }


    }

}


/**
 * 用于 [TencentGuildComponent] 组件注册时的配置类信息。
 */
public class TencentGuildComponentConfiguration

/**
 * [TencentGuildComponent] 的注册器工厂，用于支持组件的自动加载。
 *
 */
public class TencentGuildComponentRegistrarFactory : ComponentRegistrarFactory<TencentGuildComponent, TencentGuildComponentConfiguration> {
    override val registrar: ComponentRegistrar<TencentGuildComponent, TencentGuildComponentConfiguration>
        get() = TencentGuildComponent
}


/**
 * @see TencentGuildComponent
 * @author ForteScarlet
 */
@Deprecated("Use 'TencentGuildComponent'")
public object ComponentTencentGuild {

    /**
     */
    @JvmField
    @Deprecated("Use 'TencentGuildComponent'")
    public val COMPONENT_ID: CharSequenceID = TencentGuildComponent.componentID


    @JvmStatic
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Use 'TencentGuildComponent'")
    public val component: Component
        get() = throw UnsupportedOperationException("See 'TencentGuildComponent'")
}

//region botManager 扩展
/**
 * 从 [OriginBotManager] 中过滤取出所有 [TencentGuildBotManager] 实例.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun tencentGuildBotManagers(): List<TencentGuildBotManager> =
    OriginBotManager.filterIsInstance<TencentGuildBotManager>()

/**
 * 从中过滤取出所有 [TencentGuildBotManager] 实例.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Iterable<BotManager<*>>.tencentGuildBotManagers(): List<TencentGuildBotManager> =
    filterIsInstance<TencentGuildBotManager>()

/**
 * 从序列中过滤出 [TencentGuildBotManager] 实例.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun Sequence<BotManager<*>>.tencentGuildBotManagers(): Sequence<TencentGuildBotManager> =
    filterIsInstance<TencentGuildBotManager>()


//endregion




