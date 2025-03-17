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
import org.jetbrains.kotlin.js.translate.context.Namer.kotlin

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
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        jvmMain.dependencies {
            implementation(libs.i2p.crypto.eddsa)
            compileOnly(libs.bouncycastle.bcprov.jdk18on)
        }

        jvmTest.dependencies {
            implementation(libs.i2p.crypto.eddsa)
            implementation(libs.bouncycastle.bcprov.jdk18on)
        }

        jsMain.dependencies {
            implementation(libs.libsodium.bindings)
        }

        nativeMain.dependencies {
            implementation(libs.libsodium.bindings)
        }
    }
}
