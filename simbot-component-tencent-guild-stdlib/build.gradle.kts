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
    id("com.google.devtools.ksp") version "1.8.0-1.0.8"
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":simbot-component-tencent-guild-api"))
    api(simbotLoggerSlf4jImpl)
    api(simbotUtilLoop)
    api(libs.ktor.client.ws)
//    implementation(project(":builder-generator"))
//    ksp(project(":builder-generator"))
    testCompileOnly(project(":builder-generator"))
    kspTest(project(":builder-generator"))
//    implementation("io.ktor:ktor-client-content-negotiation:2.2.3")
//    implementation("io.ktor:ktor-serialization-jackson:2.2.3")
}

