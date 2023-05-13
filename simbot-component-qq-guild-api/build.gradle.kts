/*
 * Copyright (c) 2023. ForteScarlet.
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

import love.forte.gradle.common.kotlin.multiplatform.NativeTargets

/*
 * Copyright (c) 2023. ForteScarlet.
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
    kotlin("multiplatform")
    `qq-guild-multiplatform-maven-publish`
//    id("simbot-tencent-guild.module-conventions")
//    id("simbot-tencent-guild.maven-publish")
    kotlin("plugin.serialization")
    `qq-guild-dokka-partial-configure`
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    sourceSets.configureEach {
        languageSettings {
            optIn("love.forte.simbot.qguild.InternalApi")
        }
    }

    jvm {
        withJava()
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
                javaParameters = true
                freeCompilerArgs = freeCompilerArgs + listOf("-Xjvm-default=all")
            }
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        nodejs()
        binaries.library()
    }


    val mainPresets = mutableSetOf<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>()
    val testPresets = mutableSetOf<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>()

    // https://kotlinlang.org/docs/native-target-support.html
    // 针对 kotlin target support 中的列表结合 ktor-client 平台的支持提供的平台能力。
    // 注释掉的内容是Ktor尚不支持的平台
//    val supportTargets = setOf(
//        // Tier 1
//        "linuxX64",
//        "macosX64",
//        "macosArm64",
//        "iosSimulatorArm64",
//        "iosX64",
//
//        // Tier 2
////        "linuxArm64",
//        "watchosSimulatorArm64",
//        "watchosX64",
//        "watchosArm32",
//        "watchosArm64",
//        "tvosSimulatorArm64",
//        "tvosX64",
//        "tvosArm64",
//        "iosArm64",
//
//        // Tier 3
////        "androidNativeArm32",
////        "androidNativeArm64",
////        "androidNativeX86",
////        "androidNativeX64",
//        "mingwX64",
////        "watchosDeviceArm64",
//    )

    val targets = NativeTargets.Official.all.intersect(NativeTargets.Ktor.all) + setOf("mingwX64")

    targets {
        presets.filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset<*>>()
            .filter { it.name in targets }
            .forEach { presets ->
                val target = fromPreset(presets, presets.name)
                val mainSourceSet = target.compilations["main"].kotlinSourceSets.first()
                val testSourceSet = target.compilations["test"].kotlinSourceSets.first()

                val tn = target.name
                when {
                    // just for test
                    // main中只使用HttpClient但用不到引擎，没必要指定

                    // win
                    tn.startsWith("mingw") -> {
                        testSourceSet.dependencies {
                            implementation(libs.ktor.client.winhttp)
                        }
                    }
                    // linux: CIO..?
                    tn.startsWith("linux") -> {
                        testSourceSet.dependencies {
                            implementation(libs.ktor.client.cio)
                        }
                    }

                    // darwin based
                    tn.startsWith("macos")
                            || tn.startsWith("ios")
                            || tn.startsWith("watchos")
                            || tn.startsWith("tvos") -> {
                        testSourceSet.dependencies {
                            implementation(libs.ktor.client.darwin)
                        }
                    }
                }

                mainPresets.add(mainSourceSet)
                testPresets.add(testSourceSet)
            }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.ktor.client.core)
                api(libs.ktor.client.contentNegotiation)
                api(libs.ktor.serialization.kotlinxJson)
                api(libs.kotlinx.serialization.json)
                api(simbotLogger)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        getByName("jvmMain") {
            dependencies {
                compileOnly(simbotApi) // use @Api4J annotation
            }
        }

        getByName("jvmTest") {
            dependencies {
                implementation(libs.ktor.client.cio)
                implementation(simbotApi) // use @Api4J annotation
                implementation(libs.log4j.api)
                implementation(libs.log4j.core)
                implementation(libs.log4j.slf4j2)
            }
        }

        getByName("jsMain") {
            dependencies {
                api(libs.ktor.client.js)
            }
        }

        val nativeMain by creating {
            dependsOn(commonMain)
        }

        val nativeTest by creating {
            dependsOn(commonTest)
        }

        configure(mainPresets) { dependsOn(nativeMain) }
        configure(testPresets) { dependsOn(nativeTest) }

    }

}

// suppress all?
//tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
//    dokkaSourceSets.configureEach {
//        suppress.set(true)
//        perPackageOption {
//            suppress.set(true)
//        }
//    }
//}


