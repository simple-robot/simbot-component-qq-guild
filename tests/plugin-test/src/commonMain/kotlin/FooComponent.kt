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
import love.forte.simbot.component.Component
import love.forte.simbot.component.ComponentConfigureContext
import love.forte.simbot.component.ComponentFactory
import love.forte.simbot.core.application.launchSimpleApplication
import love.forte.simbot.plugin.Plugin
import love.forte.simbot.plugin.PluginConfigureContext
import love.forte.simbot.plugin.PluginFactory

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
    override val id: String = "example.foo"
    override val serializersModule: SerializersModule = EmptySerializersModule()

    companion object Factory : ComponentFactory<FooComponent, Unit> {
        override val key: ComponentFactory.Key = object : ComponentFactory.Key {}
        override fun create(context: ComponentConfigureContext, configurer: ConfigurerFunction<Unit>): FooComponent {
            return FooComponent()
        }
    }
}

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
        install(FooPlugin)
    }

}
