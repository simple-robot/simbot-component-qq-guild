/*
 * Copyright (c) 2022-2025. ForteScarlet.
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

import love.forte.gradle.common.core.project.setup
import love.forte.gradle.common.kotlin.multiplatform.applyTier1
import love.forte.gradle.common.kotlin.multiplatform.applyTier2
import love.forte.gradle.common.kotlin.multiplatform.applyTier3

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    id("love.forte.plugin.suspend-transform")
}

setup(P.ComponentQQGuild)

apply(plugin = "qq-guild-multiplatform-maven-publish")

configJsTestTasks()

kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()

    sourceSets.configureEach {
        languageSettings {
            optIn("love.forte.simbot.qguild.QGInternalApi")
        }
    }

    configKotlinJvm()

    js(IR) {
        configJs()
    }

    applyTier1()
    applyTier2(
        // stdlib 不再支持
        watchosX64 = false
    )
    applyTier3(supportKtorClient = true)

    sourceSets {
        commonMain.dependencies {
            // api (compile only for JVM)
            implementation(libs.simbot.api)

            api(project(":simbot-component-qq-guild-stdlib"))
            api(libs.simbot.common.annotations)
            // ktor
            api(libs.ktor.client.contentNegotiation)
            api(libs.ktor.serialization.kotlinxJson)
            api(libs.ktor.client.ws)
            // datetime
            api(libs.kotlinx.datetime)
            // io (runtime scope)
            implementation(libs.kotlinx.io.core)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("reflect"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)
            api(libs.simbot.core)
            api(libs.simbot.common.core)
        }

        jvmTest.dependencies {
            compileOnly(libs.simbot.api)
            implementation(libs.ktor.client.java)

            implementation(libs.log4j.api)
            implementation(libs.log4j.core)
            implementation(libs.log4j.slf4j2)
        }

        mingwTest.dependencies {
            implementation(libs.ktor.client.winhttp)
        }
    }
}

configJavaCompileWithModule("simbot.component.qqguild.core")
