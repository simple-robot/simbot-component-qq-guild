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
pluginManagement {
    plugins {
        id("org.jetbrains.dokka") version "1.6.10"
    }
}

rootProject.name = "tencent-guild"
val pathMap = mutableMapOf<String, String>()

includeAndSaveFilePath(":api", "simbot-component-tencent-guild-api")
includeAndSaveFilePath(":stdlib", "simbot-component-tencent-guild-stdlib")
includeAndSaveFilePath(":component", "simbot-component-tencent-guild-core")
includeAndSaveFilePath(":component-boot", "simbot-component-tencent-guild-boot")
includeAndSaveFilePath(":component-boot-annotation", "simbot-component-tencent-guild-boot-annotation")



fun includeAndSaveFilePath(path: String, setName: String) {
    include(path)
    project(path).name = setName
}