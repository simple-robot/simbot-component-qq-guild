/*
 * Copyright (c) 2023-2025. ForteScarlet.
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

import love.forte.gradle.common.core.project.ProjectDetail
import love.forte.gradle.common.core.property.systemProp

//val simbotVersion = v(3, 2, 0)
////- v("RC", 3)
//
//fun simbot(name: String, version: String = simbotVersion.toString()): String = "love.forte.simbot:simbot-$name:$version"
//fun simboot(name: String, version: String = simbotVersion.toString()): String =
//    "love.forte.simbot.boot:simboot-$name:$version"
//
//val simbotApi = simbot("api")
//val simbotCore = simbot("core")
//val simbotLogger = simbot("logger")
//val simbotLoggerJvm = simbot("logger-jvm")
//val simbotLoggerSlf4jImpl = simbot("logger-slf4j-impl")
//
//val simbotUtilLoop = "love.forte.simbot.util:simbot-util-stage-loop:$simbotVersion"
//val simbotUtilSuspendTransformer = "love.forte.simbot.util:simbot-util-suspend-transformer:$simbotVersion"
//val simbotUtilAnnotations = "love.forte.simbot.util:simbot-annotations:$simbotVersion"

const val SIMBOT_GROUP = "love.forte.simbot"

/**
 * Project versions.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object P {
    object Simbot {
        const val GROUP = "love.forte.simbot"
    }

    object ComponentQQGuild : ProjectDetail() {
        const val GROUP = "love.forte.simbot.component"
        const val DESCRIPTION = "Simple Robot框架下针对QQ频道的组件实现"
        const val HOMEPAGE = "https://github.com/simple-robot/simbot-component-qq-guild"

        override val group: String get() = GROUP
        override val description: String get() = DESCRIPTION
        override val homepage: String get() = HOMEPAGE


        const val VERSION = "4.1.3"
        const val NEXT_VERSION = "4.1.4"

        override val snapshotVersion = "$NEXT_VERSION-SNAPSHOT"
        override val version = if (isSnapshot()) snapshotVersion else VERSION

        override val developers: List<Developer> = developers {
            developer {
                id = "forte"
                name = "ForteScarlet"
                email = "ForteScarlet@163.com"
                url = "https://github.com/ForteScarlet"
            }
            developer {
                id = "forliy"
                name = "ForliyScarlet"
                email = "ForliyScarlet@163.com"
                url = "https://github.com/ForliyScarlet"
            }
        }

        override val licenses: List<License> = licenses {
            license {
                name = "GNU GENERAL PUBLIC LICENSE, Version 3"
                url = "https://www.gnu.org/licenses/gpl-3.0-standalone.html"
            }
            license {
                name = "GNU LESSER GENERAL PUBLIC LICENSE, Version 3"
                url = "https://www.gnu.org/licenses/lgpl-3.0-standalone.html"
            }
        }

        override val scm: Scm = scm {
            url = HOMEPAGE
            connection = "scm:git:$HOMEPAGE.git"
            developerConnection = "scm:git:ssh://git@github.com/simple-robot/simbot-component-qq-guild.git"
        }
    }


}

private val _isSnapshot by lazy { initIsSnapshot() }

private fun initIsSnapshot(): Boolean {
    val property = System.getProperty("simbot.snapshot").toBoolean()
    val env = System.getenv(Env.IS_SNAPSHOT).toBoolean()

    return property || env
}

fun isSnapshot(): Boolean = _isSnapshot

fun isSimbotLocal(): Boolean = systemProp("SIMBOT_LOCAL").toBoolean()
