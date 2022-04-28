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

@file:Suppress("unused")
/*
*  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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


abstract class SimbotProject {
    abstract val group: String
    abstract val version: String
}


/**
 * Project versions.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
sealed class P : SimbotProject() {
    object Simbot {
        init {
            println("System.getProperty(\"isSnapshot\"): ${System.getProperty("isSnapshot")}")
        }

        const val GROUP = "love.forte.simbot"
        const val BOOT_GROUP = "love.forte.simbot.boot"

        // e.g. 3.0.0.preview.6.0
        val version = Version(
            "3", 0, 0,
            status = preview(8, 1),
            isSnapshot = System.getProperty("isSnapshot")?.equals("true", true) ?: false
        )

        val isSnapshot get() = version.isSnapshot

        val VERSION = version.fullVersion(true) // = if (SNAPSHOT) "$REAL_VERSION-SNAPSHOT" else REAL_VERSION

    }

    // object Simbot {
    //     val SNAPSHOT = System.getProperty("isSnapshot")?.equals("true", true) ?: false
    //     const val GROUP = "love.forte.simbot"
    //     const val BOOT_GROUP = "love.forte.simbot.boot"
    //     const val REAL_VERSION = "3.0.0.preview.5.0"
    //     val VERSION = if (SNAPSHOT) "$REAL_VERSION-SNAPSHOT" else REAL_VERSION
    // }

    object ComponentTencentGuild {
        val isSnapshot get() = Simbot.isSnapshot
        val version = Version(
            major = "${Simbot.version.major}.${Simbot.version.minor}",
            minor = 0, patch = 0,
            status = preview(3, 1),
            isSnapshot = Simbot.isSnapshot
        )
        const val GROUP = "${Simbot.GROUP}.component"

        val VERSION: String = version.fullVersion(true)

        const val apiPath = ":simbot-component-tencent-guild-api"
        const val apiStdlibPath = ":simbot-component-tencent-guild-stdlib"
        const val componentPath = ":simbot-component-tencent-guild-core"
        const val componentBootPath = ":simbot-component-tencent-guild-boot"
        const val componentBootAnnotationPath = ":simbot-component-tencent-guild-boot-annotation"
    }


}


/**
 * **P**roject **V**ersion。
 */
@Suppress("SpellCheckingInspection")
data class Version(
    /**
     * 主版号
     */
    val major: String,
    /**
     * 次版号
     */
    val minor: Int,
    /**
     * 修订号
     */
    val patch: Int,

    /**
     * 状态号。状态号会追加在 [major].[minor].[patch] 之后，由 `.` 拼接，
     * 变为 [major].[minor].[patch].[PVS.status].[PVS.minor].[PVS.patch].
     *
     * 例如：
     * ```
     * 3.0.0.preview.0.1
     * ```
     *
     */
    val status: PVS? = null,

    /**
     * 是否快照。如果是，将会在版本号结尾处拼接 `-SNAPSHOT`。
     */
    val isSnapshot: Boolean = false
) {
    companion object {
        const val SNAPSHOT_SUFFIX = "-SNAPSHOT"
    }


    /**
     * 完整的版本号。
     */
    fun fullVersion(checkSnapshot: Boolean): String {
        return buildString {
            append(major).append('.').append(minor).append('.').append(patch)
            if (status != null) {
                append('.').append(status.status).append('.').append(status.minor).append('.').append(status.patch)
            }
            if (checkSnapshot && isSnapshot) {
                append(SNAPSHOT_SUFFIX)
            }
        }
    }

}

/**
 * **P**roject **V**ersion **S**tatus.
 */
@Suppress("SpellCheckingInspection")
data class PVS(
    val status: String,
    /**
     * 次版号
     */
    val minor: Int,
    /**
     * 修订号
     */
    val patch: Int,
) {
    companion object {
        const val PREVIEW_STATUS = "preview"
        const val BETA_STATUS = "beta"
    }
}


internal fun preview(minor: Int, patch: Int) = PVS(PVS.PREVIEW_STATUS, minor, patch)





