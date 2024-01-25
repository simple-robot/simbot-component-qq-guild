/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

import love.forte.gradle.common.core.Gpg
import love.forte.gradle.common.publication.configure.jvmConfigPublishing
import util.checkPublishConfigurable
import util.isCi
import util.isLinux


plugins {
    id("signing")
    id("maven-publish")
}

val (isSnapshotOnly, isReleaseOnly, isPublishConfigurable) = checkPublishConfigurable()


logger.info("isSnapshotOnly: {}", isSnapshotOnly)
logger.info("isReleaseOnly: {}", isReleaseOnly)
logger.info("isPublishConfigurable: {}", isPublishConfigurable)

if (!isCi || isLinux) {
    checkPublishConfigurable {
        jvmConfigPublishing {
            project = P.ComponentQQGuild
            publicationName = "QQGuildDist"
            isSnapshot = isSnapshot().also {
                logger.info("jvmConfigPublishing.isSnapshot: {}", it)
            }

            val jarSources by tasks.registering(Jar::class) {
                archiveClassifier.set("sources")
                from(sourceSets["main"].allSource)
            }

            val jarJavadoc by tasks.registering(Jar::class) {
                if (!(isSnapshot || isSnapshot())) {
                    dependsOn(tasks.dokkaHtml)
                    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
                }
                archiveClassifier.set("javadoc")
            }

            artifact(jarSources)
            artifact(jarJavadoc)

            releasesRepository = ReleaseRepository
            snapshotRepository = SnapshotRepository
            gpg = Gpg.ofSystemPropOrNull()
        }
    }

    show()
}


inline val Project.sourceSets: SourceSetContainer
    get() = extensions.getByName("sourceSets") as SourceSetContainer

internal val TaskContainer.dokkaHtml: TaskProvider<org.jetbrains.dokka.gradle.DokkaTask>
    get() = named<org.jetbrains.dokka.gradle.DokkaTask>("dokkaHtml")

fun Project.show() {
    //// show project info
    logger.info("========================================================")
    logger.info("== project.group:       $group")
    logger.info("== project.name:        $name")
    logger.info("== project.version:     $version")
    logger.info("== project.description: $description")
    logger.info("========================================================")
}


