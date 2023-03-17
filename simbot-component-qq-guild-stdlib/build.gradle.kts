/*
 * Copyright (c) 2022-2023. ForteScarlet.
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

plugins {
    `simbot-tcg-suspend-transform-configure`
    id("simbot-tencent-guild.module-conventions")
    id("simbot-tencent-guild.maven-publish")
    kotlin("plugin.serialization")
//    id("com.google.devtools.ksp") version "1.8.10-1.0.9"
    `qq-guild-dokka-partial-configure`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(simbotApi) // use @Api4J annotation
    api(project(":simbot-component-qq-guild-api"))
    api(simbotLoggerSlf4jImpl)
    api(simbotUtilLoop)
    api(libs.ktor.client.ws)
//    implementation(project(":builder-generator"))
//    ksp(project(":builder-generator"))
//    testCompileOnly(project(":builder-generator"))
//    kspTest(project(":builder-generator"))
//    implementation("io.ktor:ktor-client-content-negotiation:2.2.3")
//    implementation("io.ktor:ktor-serialization-jackson:2.2.3")
}

