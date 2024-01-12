/*
 * Copyright (c) 2023-2024. ForteScarlet.
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
    `qq-guild-dokka-partial-configure`
}

kotlin {
    sourceSets.configureEach {
        languageSettings {
            optIn("love.forte.simbot.InternalSimbotApi")
            optIn("love.forte.simbot.qguild.QGInternalApi")
        }
    }
}

dependencies {
    api(project(":simbot-component-qq-guild-stdlib")) {
        exclude(SIMBOT_GROUP, "simbot-logger-slf4j-impl")
    }

    compileOnly(simbotCore)
    api(libs.ktor.client.core)
    api(libs.ktor.client.cio)
    api(libs.ktor.client.ws)
    api(libs.ktor.client.contentNegotiation)
    api(libs.ktor.serialization.kotlinxJson)
    api(libs.kotlinx.serialization.json)

    compileOnly(libs.kotlinx.serialization.properties)
    compileOnly(libs.charleskorn.kaml)

    testImplementation(simbotCore)
    testImplementation(libs.charleskorn.kaml)
    testImplementation(simbotLoggerSlf4jImpl)

}

