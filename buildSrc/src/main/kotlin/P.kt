import love.forte.gradle.common.core.project.ProjectDetail
import love.forte.gradle.common.core.project.minus
import love.forte.gradle.common.core.project.version as v

/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

val simbotVersion = v(3, 0, 0) - v("M5")

fun simbot(name: String, version: String = simbotVersion.toString()): String = "love.forte.simbot:simbot-$name:$version"
fun simboot(name: String, version: String = simbotVersion.toString()): String = "love.forte.simbot.boot:simboot-$name:$version"

val simbotApi = simbot("api")
val simbotCore = simbot("core")
val simbootApi = simboot("api")
val simbootCore = simboot("core")
val simbotUtilLoop = "love.forte.simbot.util:simbot-util-stage-loop:$simbotVersion"

/**
 * Project versions.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object P {
    object Simbot {
        const val GROUP = "love.forte.simbot"
    }

    object ComponentTencentGuild : ProjectDetail() {
        const val GROUP = "love.forte.simbot.component"
        const val DESCRIPTION = "Simple Robot框架下针对QQ频道的组件实现"
        const val HOMEPAGE = "https://github.com/simple-robot/simbot-component-tencent-guild"

        override val group: String get() = GROUP
        override val description: String get() = DESCRIPTION
        override val homepage: String get() = HOMEPAGE


        private val baseVersion = v(
            "${simbotVersion.major}.${simbotVersion.minor}",
            0, 0
        )

        private val alphaSuffix = v("alpha", 3)

        override val version = baseVersion - alphaSuffix
        val snapshotVersion = baseVersion - (alphaSuffix - love.forte.gradle.common.core.project.Version.SNAPSHOT)

        val versionIfSnap get() = (if (isSnapshot()) snapshotVersion else version).toString()

        val VERSION: String get() = versionIfSnap.toString()

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
            developerConnection = "scm:git:ssh://git@github.com/simple-robot/simbot-component-tencent-guild.git"
        }
    }


}

fun isSnapshot(): Boolean {
    val property = System.getProperty("simbot.snapshot")?.toBoolean() ?: false
    val env = System.getenv(Env.IS_SNAPSHOT)?.toBoolean() ?: false
    println("Is snapshot from System.property:  $property")
    println("Is snapshot from System.env:       $env")

    return property || env
}
