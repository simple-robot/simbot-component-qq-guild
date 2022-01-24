/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */


abstract class SimbotProject {
    abstract val group: String
    abstract val version: String
}


/**
 * Project versions.
 */
@Suppress("unused")
sealed class P : SimbotProject() {
    object Simbot {
        const val GROUP = "love.forte.simbot"
        const val BOOT_GROUP = "love.forte.simbot.boot"
        const val VERSION = "3.0.0.preview.1.1"
    }

    object TencentGuild {
        private const val CURRENT_VERSION = "0.6"
        const val GROUP = "${Simbot.GROUP}.component"
        const val VERSION = "${Simbot.VERSION}-$CURRENT_VERSION"

        const val apiPath = ":simbot-component-tencent-guild-api"
        const val apiStdlibPath = ":simbot-component-tencent-guild-stdlib"
        const val componentPath = ":simbot-component-tencent-guild-core"
        const val componentBootPath = ":simbot-component-tencent-guild-boot"
        const val componentBootAnnotationPath = ":simbot-component-tencent-guild-boot-annotation"
    }





}



