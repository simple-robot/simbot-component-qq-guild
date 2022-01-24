/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

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