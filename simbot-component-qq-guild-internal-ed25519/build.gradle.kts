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
    `qq-guild-dokka-partial-configure`
    `qq-guild-module-config`
}

setup(P.ComponentQQGuild)

configJavaCompileWithModule("simbot.component.qqguild.internal.ed25519")
apply(plugin = "qq-guild-multiplatform-maven-publish")

configJsTestTasks()

kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()

    sourceSets.configureEach {
        languageSettings {
        }
    }

    configKotlinJvm()

    js(IR) {
        configJs()
    }

    applyTier1()
    applyTier2(
        // multiplatform-crypto-libsodium 不支持 watchosX64 target.
        watchosX64 = false,

        )
    applyTier3(
        androidNativeArm32 = false,
        androidNativeArm64 = false,
        androidNativeX64 = false,
        androidNativeX86 = false,
        watchosDeviceArm64 = false,
    )

    sourceSets {
        commonMain.dependencies {

            // https://github.com/andreypfau/curve25519-kotlin
//            implementation("io.github.andreypfau:curve25519-kotlin:0.0.8")

            // https://github.com/ionspin/kotlin-multiplatform-libsodium
//            implementation("com.ionspin.kotlin:multiplatform-crypto-libsodium-bindings:0.9.2")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        jvmTest.dependencies {
            implementation("org.bouncycastle:bcprov-jdk18on:1.80")
            implementation("net.i2p.crypto:eddsa:0.3.0")
            // TODO libsodium?
        }

        jsMain.dependencies {
            implementation("com.ionspin.kotlin:multiplatform-crypto-libsodium-bindings:0.9.2")
        }

        nativeMain.dependencies {
            implementation("com.ionspin.kotlin:multiplatform-crypto-libsodium-bindings:0.9.2")
        }
    }
}
