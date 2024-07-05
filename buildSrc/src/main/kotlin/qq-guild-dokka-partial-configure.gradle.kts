/*
 * Copyright (c) 2023-2024. ForteScarlet.
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

import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import java.net.URI

plugins {
    id("org.jetbrains.dokka")
}


// dokka config
tasks.withType<DokkaTaskPartial>().configureEach {
    dokkaSourceSets.configureEach {
        version = P.ComponentQQGuild.version
        documentedVisibilities.set(listOf(DokkaConfiguration.Visibility.PUBLIC, DokkaConfiguration.Visibility.PROTECTED))
        fun checkModule(projectFileName: String): Boolean {
            val moduleMdFile = project.file(projectFileName)
            if (moduleMdFile.exists()) {
                moduleMdFile.useLines { lines ->
                    val head = lines.first { it.isNotBlank() }.trim()
                    if (head == "# Module ${project.name}") {
                        includes.from(projectFileName)
                        return true
                    }
                }
            }

            return false
        }

        if (!checkModule("Module.md")) {
            checkModule("README.md")
        }

        // samples
//        samples.from(
//            project.files(),
//            project.files("src/samples"),
//        )

        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            val relativeTo = projectDir.relativeTo(rootProject.projectDir).path.replace('\\', '/')
            remoteUrl.set(URI.create("${P.ComponentQQGuild.HOMEPAGE}/tree/main/$relativeTo/src/").toURL())
            remoteLineSuffix.set("#L")
        }

        perPackageOption {
            matchingRegex.set(".*internal.*") // will match all .internal packages and sub-packages
            suppress.set(true)
        }


        fun externalDocumentation(docUri: URI) {
            externalDocumentationLink {
                url.set(docUri.toURL())
                packageListUrl.set(docUri.resolve("package-list").toURL())
            }
        }

        // kotlin-coroutines doc
        externalDocumentation(URI.create("https://kotlinlang.org/api/kotlinx.coroutines/"))

        // kotlin-serialization doc
        externalDocumentation(URI.create("https://kotlinlang.org/api/kotlinx.serialization/"))

        // ktor
        externalDocumentation(URI.create("https://api.ktor.io/"))

        // simbot doc
        externalDocumentation(URI.create("https://docs.simbot.forte.love/main/"))

    }
}
