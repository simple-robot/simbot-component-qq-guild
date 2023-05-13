/*
 * Copyright (c) 2022-2023. ForteScarlet.
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


tasks.create("createChangelog") {
    group = "documentation"
    doFirst {
        val realVersion = P.ComponentQQGuild.version.toString()
        val version = "v$realVersion"
        logger.info("Generate change log for {} ...", version)
        // configurations.runtimeClasspath
        val changelogDir = rootProject.file(".changelog").also {
            it.mkdirs()
        }
        val file = File(changelogDir, "$version.md")
        if (!file.exists()) {
            file.createNewFile()
            val coreVersion = simbotVersion
            val autoGenerateText = """
                > 对应核心版本: [**v$coreVersion**](https://github.com/simple-robot/simpler-robot/releases/tag/v$coreVersion)
                
                > **Warning**
                > **目前版本仍处于 `ALPHA` 阶段，代表仍然可能存在大量[已知问题](https://github.com/simple-robot/simbot-component-qq-guild/issues)或未知问题，
                以及未完善的内容和落后于官方更新的内容。**
                
                我们欢迎并期望着您的的[反馈](https://github.com/simple-robot/simbot-component-qq-guild/issues)或[协助](https://github.com/simple-robot/simbot-component-qq-guild/pulls)，
                感谢您的贡献与支持！
                
            """.trimIndent()


            file.writeText(autoGenerateText)
        }
    }
}

tasks.create("updateWebsiteVersionJson") {
    group = "documentation"
    doFirst {
        val version = P.ComponentQQGuild.version.toString()

        val websiteVersionJsonDir = rootProject.file("website/static")
        if (!websiteVersionJsonDir.exists()) {
            websiteVersionJsonDir.mkdirs()
        }
        val websiteVersionJsonFile = File(websiteVersionJsonDir, "version.json")
        if (!websiteVersionJsonFile.exists()) {
            websiteVersionJsonFile.createNewFile()
        }

        websiteVersionJsonFile.writeText(
            """
            {
              "version": "$version"
            }
        """.trimIndent()
        )
    }
}


fun repoRow(moduleName: String, group: String, id: String, version: String): String {
    return "| $moduleName | [$moduleName: v$version](https://repo1.maven.org/maven2/${group.replace(".", "/")}/${id.replace(".", "/")}/$version) | [$moduleName: v$version](https://search.maven.org/artifact/$group/$id/$version/jar)  |"
}
