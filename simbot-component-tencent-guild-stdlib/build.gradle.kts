/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    `simbot-tcg-suspend-transform-configure`
    id("simbot-tencent-guild.module-conventions")
    id("simbot-tencent-guild.maven-publish")
    kotlin("plugin.serialization")

}

dependencies {
    api(project(":simbot-component-tencent-guild-api"))
    api(simbotLoggerSlf4jImpl)
    api(simbotUtilLoop)
    api(libs.ktor.client.ws)
}

