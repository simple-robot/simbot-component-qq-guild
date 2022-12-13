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


tasks.create("createChangelog") {
    group = "documentation"
    doFirst {
        val realVersion = P.ComponentTencentGuild.version.toString()
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
                > 对应核心版本: [**v$coreVersion**](https://github.com/ForteScarlet/simpler-robot/releases/tag/v$coreVersion)
                
                **⚠ 目前版本仍处于 `ALPHA` 阶段，代表仍然可能存在大量[已知问题](https://github.com/simple-robot/simbot-component-tencent-guild/issues)或未知问题，
                以及未完善的内容和落后于官方更新的内容。**
                
                我们欢迎并期望着积极的[反馈](https://github.com/simple-robot/simbot-component-tencent-guild/issues)或[协助](https://github.com/simple-robot/simbot-component-tencent-guild/pulls)，
                感谢您的贡献与支持！
                
                **仓库参考:**
                
                | **模块** | **repo1.maven** | **search.maven** |
                |---------|-----------------|------------------|
                ${repoRow("simbot-tencent-guild-api", "love.forte.simbot.component", "simbot-component-tencent-guild-api", realVersion)}
                ${repoRow("simbot-tencent-guild-stdlib", "love.forte.simbot.component", "simbot-component-tencent-guild-stdlib", realVersion)}
                ${repoRow("simbot-tencent-guild-core", "love.forte.simbot.component", "simbot-component-tencent-guild-core", realVersion)}
                ${repoRow("simbot-tencent-guild-boot", "love.forte.simbot.component", "simbot-component-tencent-guild-boot", realVersion)}
                
            """.trimIndent()


            file.writeText(autoGenerateText)
        }
    }
}


fun repoRow(moduleName: String, group: String, id: String, version: String): String {
    return "| $moduleName | [$moduleName: v$version](https://repo1.maven.org/maven2/${group.replace(".", "/")}/${id.replace(".", "/")}/$version) | [$moduleName: v$version](https://search.maven.org/artifact/$group/$id/$version/jar)  |"
}
