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
import love.forte.gradle.common.core.repository.Repositories
import love.forte.plugin.suspendtrans.gradle.SuspendTransformPluginExtension
import love.forte.simbot.gradle.suspendtransforms.addSimbotJvmTransforms

plugins {
    idea
    `changelog-generator`
    `root-dokka`
    `nexus-publish`
    alias(libs.plugins.kotlinxBinaryCompatibilityValidator)
    id("love.forte.plugin.suspend-transform") apply false
}

setup(P.ComponentQQGuild)

buildscript {
    repositories {
        mavenCentral()
    }

//    dependencies {
//        classpath(libs.simbot.gradle)
//    }
}

logger.info("=== Current version: {} ===", version)

allprojects {
    setup(P.ComponentQQGuild)
    repositories {
        mavenCentral()
        maven {
            url = uri(Repositories.Snapshot.URL)
            mavenContent {
                snapshotsOnly()
            }
        }
        mavenLocal()
    }
}

subprojects {
    afterEvaluate {
        if (plugins.hasPlugin(libs.plugins.suspendTransform.get().pluginId)) {
            extensions.configure<SuspendTransformPluginExtension>("suspendTransformPlugin") {
                includeRuntime = false
                includeAnnotation = false
                addSimbotJvmTransforms()
            }
        }
    }
}

idea {
    module.apply {
        isDownloadSources = true
    }
    project {
        modules.forEach { module ->
            module.apply {
                isDownloadSources = true
            }
        }
    }
}

apiValidation {
    ignoredPackages.add("*.internal.*")

    this.ignoredProjects.addAll(
        listOf(
            "api-reader",
            "intents-processor",
            "dispatch-serializer-processor",
            "webhook-server-ktor",
            "webhook-server-spring",
            "webhook-server-spring-webflux",
        ),
    )

    // 实验性和内部API可能无法保证二进制兼容
    nonPublicMarkers.addAll(
        listOf(
            "love.forte.simbot.annotations.ExperimentalSimbotAPI",
            "love.forte.simbot.annotations.InternalSimbotAPI",
            "love.forte.simbot.qguild.QGInternalApi",
            "love.forte.simbot.component.qguild.ExperimentalQGApi",
            "love.forte.simbot.qguild.ExperimentalQGMediaApi",
            "love.forte.simbot.qguild.ed25519.annotations.InternalEd25519Api"
        ),
    )

    apiDumpDirectory = "api"
}

//subprojects {
//    afterEvaluate {
//        val p = this
//        if (plugins.hasPlugin("org.jetbrains.dokka")) {
//            dokka {
//                configSourceSets(p)
//                pluginsConfiguration.html {
//                    configHtmlCustoms(p)
//                }
//            }
//            rootProject.dependencies.dokka(p)
//        }
//    }
//}
//
//dokka {
//    moduleName = "Simple Robot 组件 | QQ"
//
//    dokkaPublications.all {
//        if (isSimbotLocal()) {
//            logger.info("Is 'SIMBOT_LOCAL', offline")
//            offlineMode = true
//        }
//    }
//
//    configSourceSets(project)
//
//    pluginsConfiguration.html {
//        configHtmlCustoms(project)
//    }
//}