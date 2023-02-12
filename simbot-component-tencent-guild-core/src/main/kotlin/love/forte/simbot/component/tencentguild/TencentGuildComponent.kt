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

import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.message.*
import love.forte.simbot.message.Message


/**
 * QQ频道实现simbot相关组件的基本组件信息，可以用来获取组件ID、组件所用序列化器等等。
 *
 * @author ForteScarlet
 */
public class TencentGuildComponent : Component {
    override val id: String
        get() = ID_VALUE
    
    override val componentSerializersModule: SerializersModule
        get() = messageSerializersModule
    
    
    override fun toString(): String = TO_STRING_VALUE
    
    override fun equals(other: Any?): Boolean {
        return when {
            other === this -> true
            other !is TencentGuildComponent -> false
            else -> other.id == id
        }
    }
    
    override fun hashCode(): Int {
        return id.hashCode()
    }
    
    /**
     * 组件 [TencentGuildComponent] 的注册器。
     */
    public companion object Factory : ComponentFactory<TencentGuildComponent, TencentGuildComponentConfiguration> {
        /**
         * 组件的ID标识常量。
         */
        @Suppress("MemberVisibilityCanBePrivate")
        public const val ID_VALUE: String = "simbot.tencentguild"
        
        internal const val TO_STRING_VALUE: String = "TencentGuildComponent(id=$ID_VALUE)"
        
        /**
         * [ID_VALUE] 的 [ID] 类型。
         */
        @Deprecated("Unused")
        public val componentID: CharSequenceID = ID_VALUE.ID
        
        /**
         * 作为注册器时的标识。
         */
        override val key: Attribute<TencentGuildComponent> = attribute(ID_VALUE)
        
        /**
         * 注册配置函数。
         */
        override suspend fun create(configurator: TencentGuildComponentConfiguration.() -> Unit): TencentGuildComponent {
            TencentGuildComponentConfiguration.configurator()
            
            return TencentGuildComponent()
        }
        
        /**
         * QQ频道组件所使用到的特殊消息类型序列化信息。
         */
        @JvmStatic
        public val messageSerializersModule: SerializersModule = SerializersModule {
            fun PolymorphicModuleBuilder<TcgMessageElement<*>>.subclass0() {
                subclass(TcgArk.serializer())
                subclass(TcgMentionChannel.serializer())
                subclass(TcgAttachmentMessage.serializer())
                subclass(TcgReplyTo.serializer())
            }
            
            polymorphic(TcgMessageElement::class) {
                subclass0()
            }
            
            polymorphic(Message.Element::class) {
                subclass0()
            }
        }
        
        
    }
    
}


/**
 * 用于 [TencentGuildComponent] 组件注册时的配置类信息。
 *
 * _Note: 目前 [TencentGuildComponent] 暂无可配置内容。_
 *
 */
public object TencentGuildComponentConfiguration

/**
 * [TencentGuildComponent] 的注册器工厂，用于支持组件的自动加载。
 *
 */
public class TencentGuildComponentRegistrarFactory :
    ComponentAutoRegistrarFactory<TencentGuildComponent, TencentGuildComponentConfiguration> {
    override val registrar: TencentGuildComponent.Factory
        get() = TencentGuildComponent
}






