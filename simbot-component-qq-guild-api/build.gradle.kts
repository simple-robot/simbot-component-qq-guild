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
    `qq-guild-dokka-partial-configure`
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

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
        js(IR) {
            nodejs()
        }


        val mainPresets = mutableSetOf<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>()
        val testPresets = mutableSetOf<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>()

        // https://github.com/Kotlin/kotlinx-datetime/blob/cd6dcd1abb547bc90455471e644e1e8fe2a49e5c/core/build.gradle.kts#L39-L57
        val supportTargets = setOf(
            "linuxX64",
            "mingwX64",
            "macosX64",
            "macosArm64",
            "iosX64",
            "iosArm64",
            "iosArm32",
            "iosSimulatorArm64",
            "watchosArm32",
            "watchosArm64",
            "watchosX86",
            "watchosX64",
            "watchosSimulatorArm64",
            "tvosArm64",
            "tvosX64",
            "tvosSimulatorArm64",
        )

        targets {
            presets.filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset<*>>()
                .filter { it.name in supportTargets }
                .forEach { presets ->
                    val target = fromPreset(presets, presets.name)
                    mainPresets.add(target.compilations["main"].kotlinSourceSets.first())
                    testPresets.add(target.compilations["test"].kotlinSourceSets.first())
                }
        }

        sourceSets {
            val commonMain by getting {
                dependencies {
                    api(libs.ktor.client.core)
                    api(libs.ktor.client.contentNegotiation)
                    api(libs.ktor.serialization.kotlinxJson)
                    api(libs.kotlinx.serialization.json)
                    api(libs.kotlinx.datetime)
                    api(simbotLogger)
                }
            }

            val commonTest by getting {
                dependencies {
                    implementation(kotlin("test"))
                    implementation(libs.kotlinx.coroutines.test)
                }
            }

            val jvmMain by getting {
                dependencies {
                    compileOnly(simbotApi) // use @Api4J annotation
                }
            }

            val jvmTest by getting {
                dependencies {
                    runtimeOnly(libs.ktor.client.cio)
                    compileOnly(simbotApi) // use @Api4J annotation
                    implementation(libs.log4j.api)
                    implementation(libs.log4j.core)
                    implementation(libs.log4j.slf4jImpl)
                }
            }

            val jsTest by getting {
                dependencies {
                    implementation(libs.ktor.client.js)
                }
            }

            val mingwX64Main by getting {
                dependencies {
                    implementation(libs.ktor.client.winhttp)
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


