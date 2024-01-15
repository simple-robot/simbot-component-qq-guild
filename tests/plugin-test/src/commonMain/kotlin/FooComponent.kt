/*
 * Copyright (c) 2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import love.forte.simbot.component.*
import love.forte.simbot.core.application.launchSimpleApplication
import love.forte.simbot.plugin.Plugin
import love.forte.simbot.plugin.PluginConfigureContext
import love.forte.simbot.plugin.PluginFactory
import kotlin.jvm.JvmField

/*
 * Copyright (c) 2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

class FooComponent : Component {
    override val id: String get() = ID_VALUE
    override val serializersModule: SerializersModule get() = SerializersModule

    /** 伴生对象实现的工厂实现 */
    companion object Factory : ComponentFactory<FooComponent, FooComponentConfiguration> {
        /** id 常量化 */
        const val ID_VALUE: String = "com.example.foo"
        /** serializersModule "静态化" */
        @JvmField
        val SerializersModule: SerializersModule = EmptySerializersModule()

        override val key: ComponentFactory.Key = object : ComponentFactory.Key {}
        override fun create(context: ComponentConfigureContext, configurer: ConfigurerFunction<FooComponentConfiguration>): FooComponent {
            FooComponentConfiguration().invokeBy(configurer)
            return FooComponent()
        }
    }
}

class FooComponentFactoryProvider : ComponentFactoryProvider<FooComponentConfiguration> {
    override fun loadConfigurers(): Sequence<ComponentFactoryConfigurerProvider<FooComponentConfiguration>>? {
        return null
    }

    override fun provide(): ComponentFactory<*, FooComponentConfiguration> {
        return FooComponent.Factory
    }
}

class FooComponentConfiguration

class FooPlugin : Plugin {
    companion object Factory : PluginFactory<FooPlugin, Unit> {
        override val key: PluginFactory.Key = object : PluginFactory.Key {}

        override fun create(context: PluginConfigureContext, configurer: ConfigurerFunction<Unit>): FooPlugin {
            return FooPlugin()
        }
    }
}

suspend fun main() {
    val app = launchSimpleApplication {
        install(FooComponent)
    }

    // Kotlin 可以直接通过扩展函数根据类型寻找目标
    val fooComponent = app.components.find<FooComponent>()
    println(fooComponent)
}
