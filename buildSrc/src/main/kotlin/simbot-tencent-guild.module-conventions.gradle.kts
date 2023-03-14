/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */
import love.forte.gradle.common.core.project.setup
import love.forte.gradle.common.core.repository.Repositories
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import java.net.URL


plugins {
    `java-library`
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    idea
}

setup(P.ComponentTencentGuild)
if (isSnapshot()) {
    version = P.ComponentTencentGuild.snapshotVersion.toString()
}

repositories {
    mavenCentral()
    maven {
        url = uri(Repositories.Snapshot.URL)
        mavenContent {
            snapshotsOnly()
        }
    }
}

dependencies {
    testImplementation(kotlin("test-junit5"))
}


tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        javaParameters = true
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + listOf("-Xjvm-default=all")
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.encoding = "UTF-8"
}

kotlin {
    explicitApi()
    sourceSets.configureEach {
        languageSettings {
            optIn("kotlin.RequiresOptIn")
        }
    }

    sourceSets.getByName("test").kotlin {
        srcDir("src/samples")
    }
}

configurations.all {
    // check for updates every build
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}


idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}


//// show project info
logger.info("========================================================")
logger.info("== project.group:   ${group}")
logger.info("== project.name:    ${name}")
logger.info("== project.version: ${version}")
logger.info("========================================================")

//// Dokka


// dokka config
tasks.withType<DokkaTaskPartial>().configureEach {
    dokkaSourceSets.configureEach {
        version = P.ComponentTencentGuild.version.toString()
        documentedVisibilities.set(listOf(DokkaConfiguration.Visibility.PUBLIC, DokkaConfiguration.Visibility.PROTECTED))
        fun checkModule(projectFileName: String): Boolean {
            val moduleMdFile = project.file(projectFileName)
            if (moduleMdFile.exists()) {
                val isModuleHead = moduleMdFile.useLines { lines ->
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
        samples.from(
            project.files(),
            project.files("src/samples"),
        )
        
        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            val relativeTo = projectDir.relativeTo(rootProject.projectDir)
            remoteUrl.set(URL("${P.ComponentTencentGuild.HOMEPAGE}/tree/main/$relativeTo/src"))
            remoteLineSuffix.set("#L")
        }
        
        perPackageOption {
            matchingRegex.set(".*internal.*") // will match all .internal packages and sub-packages
            suppress.set(true)
        }
        
        
        
        fun externalDocumentation(docUrl: URL) {
            externalDocumentationLink {
                url.set(docUrl)
                packageListUrl.set(URL(docUrl, "${docUrl.path}/package-list"))
            }
        }
        
        // kotlin-coroutines doc
        externalDocumentation(URL("https://kotlinlang.org/api/kotlinx.coroutines"))
        
        // kotlin-serialization doc
        externalDocumentation(URL("https://kotlinlang.org/api/kotlinx.serialization"))
    
        // ktor
        externalDocumentation(URL("https://api.ktor.io"))
        
        // simbot doc
        externalDocumentation(URL("https://docs.simbot.forte.love/main"))
        
    }
}
