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

import com.google.auto.service.AutoService
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.ComponentTencentGuild.component
import love.forte.simbot.component.tencentguild.message.Ark
import love.forte.simbot.component.tencentguild.message.AttachmentMessage
import love.forte.simbot.component.tencentguild.message.MentionChannel
import love.forte.simbot.component.tencentguild.message.ReplyTo
import love.forte.simbot.message.Message


/**
 * 腾讯频道实现simbot相关组件的基本组件信息，可以用来获取组件ID以及组件实例（实例化之后）。
 *
 * 其内部的 [component] 会在当前组件被加载后初始化。
 *
 * @author ForteScarlet
 */
public object ComponentTencentGuild {

    /**
     * 腾讯频道组件的ID标识。
     */
    @JvmField
    public val COMPONENT_ID: CharSequenceID = "simbot.tencentguild".ID

    @Suppress("ObjectPropertyName")
    internal lateinit var _component: Component

    /**
     * 腾讯频道组件的 [组件][Component] 对象实例。会在被simbot加载后初始化。
     */
    @JvmStatic
    public val component: Component get() = if (::_component.isInitialized) _component else Components[COMPONENT_ID]
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

/**
 * 用于注册腾讯频道组件的Service interface.
 */
@AutoService(ComponentInformationRegistrar::class)
public class TencentGuildComponentInformationRegistrar : ComponentInformationRegistrar {
    override fun informations(): ComponentInformationRegistrar.Result {
        return ComponentInformationRegistrar.Result.ok(listOf(TencentGuildComponentInformation()))
    }

}


private class TencentGuildComponentInformation : ComponentInformation {
    override val id: ID
        get() = ComponentTencentGuild.COMPONENT_ID
    override val name: String
        get() = ComponentTencentGuild.COMPONENT_ID.toString()

    override val messageSerializersModule: SerializersModule = SerializersModule {
        polymorphic(Message.Element::class) {
            subclass(Ark::class, Ark.serializer())
            subclass(MentionChannel::class, MentionChannel.serializer())
            subclass(AttachmentMessage::class, AttachmentMessage.serializer())
            subclass(ReplyTo::class, ReplyTo.serializer())
        }
    }

    override fun configAttributes(attributes: MutableAttributeMap) {
        // nothing now.
    }

    override fun setComponent(component: Component) {
        ComponentTencentGuild._component = component
    }

}



