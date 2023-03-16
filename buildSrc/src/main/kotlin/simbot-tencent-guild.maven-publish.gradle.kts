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

import love.forte.gradle.common.core.Gpg
import love.forte.gradle.common.publication.configure.jvmConfigPublishing
import util.checkPublishConfigurable
import util.systemProp


plugins {
    id("signing")
    id("maven-publish")
}

val (isSnapshotOnly, isReleaseOnly, isPublishConfigurable) = checkPublishConfigurable()


logger.info("isSnapshotOnly: $isSnapshotOnly")
logger.info("isReleaseOnly: $isReleaseOnly")
logger.info("isPublishConfigurable: $isPublishConfigurable")

checkPublishConfigurable {
    jvmConfigPublishing {
        project = P.ComponentTencentGuild
        publicationName = "tencentGuildDist"
        val jarSources by tasks.registering(Jar::class) {
            archiveClassifier.set("sources")
            from(sourceSets["main"].allSource)
        }

        val jarJavadoc by tasks.registering(Jar::class) {
            archiveClassifier.set("javadoc")
        }

        artifact(jarSources)
        artifact(jarJavadoc)

        isSnapshot = isSnapshot().also {
            logger.info("jvmConfigPublishing.isSnapshot: {}", it)
        }
        releasesRepository = ReleaseRepository
        snapshotRepository = SnapshotRepository
        gpg = if (isSnapshot()) null else Gpg.ofSystemPropOrNull()
    }

    if (isSnapshot()) {
        publishing {
            publications.withType<MavenPublication> {
                version = P.ComponentTencentGuild.snapshotVersion.toString()
            }
        }
    }

    publishing {
        publications.withType<MavenPublication> {
            show()
        }
    }


}

//
//if (isPublishConfigurable) {
//    val sonatypeUsername: String? = systemProp("OSSRH_USER")
//    val sonatypePassword: String? = systemProp("OSSRH_PASSWORD")
//
//    if (sonatypeUsername == null || sonatypePassword == null) {
//        println("[WARN] - sonatype.username or sonatype.password is null, cannot config nexus publishing.")
//    }
//
//    val jarSources by tasks.registering(Jar::class) {
//        archiveClassifier.set("sources")
//        from(sourceSets["main"].allSource)
//    }
//
//    val jarJavadoc by tasks.registering(Jar::class) {
//        archiveClassifier.set("javadoc")
//    }
//
//    publishing {
//        publications {
//            create<MavenPublication>("tencentGuildDist") {
//                from(components["java"])
//                artifact(jarSources)
//                artifact(jarJavadoc)
//
//                groupId = project.group.toString()
//                artifactId = project.name
//                version = project.version.toString()
//                description = project.description?.toString() ?: P.ComponentTencentGuild.DESCRIPTION
//
//                pom {
//                    show()
//
//                    name.set("${project.group}:${project.name}")
//                    description.set(project.description?.toString() ?: P.ComponentTencentGuild.DESCRIPTION)
//                    url.set("https://github.com/simple-robot/simbot-component-qq-guild")
//                    licenses {
//                        license {
//                            name.set("GNU GENERAL PUBLIC LICENSE, Version 3")
//                            url.set("https://www.gnu.org/licenses/gpl-3.0-standalone.html")
//                        }
//                        license {
//                            name.set("GNU LESSER GENERAL PUBLIC LICENSE, Version 3")
//                            url.set("https://www.gnu.org/licenses/lgpl-3.0-standalone.html")
//                        }
//                    }
//                    scm {
//                        url.set("https://github.com/simple-robot/simbot-component-qq-guild")
//                        connection.set("scm:git:https://github.com/simple-robot/simbot-component-qq-guild.git")
//                        developerConnection.set("scm:git:ssh://git@github.com/simple-robot/simbot-component-tencent-guild.git")
//                    }
//
//                    setupDevelopers()
//                }
//            }
//
//
//
//            repositories {
//                configMaven(Sonatype.Central, sonatypeUsername, sonatypePassword)
//                configMaven(Sonatype.Snapshot, sonatypeUsername, sonatypePassword)
//            }
//        }
//    }
//
//
//    signing {
//        val keyId = System.getenv("GPG_KEY_ID")
//        val secretKey = System.getenv("GPG_SECRET_KEY")
//        val password = System.getenv("GPG_PASSWORD")
//
//        setRequired {
//            !project.version.toString().endsWith("SNAPSHOT")
//        }
//
//        useInMemoryPgpKeys(keyId, secretKey, password)
//
//        sign(publishing.publications["tencentGuildDist"])
//    }
//
//
//    println("[publishing-configure] - [$name] configured.")
//}



inline val Project.sourceSets: SourceSetContainer
    get() = extensions.getByName("sourceSets") as SourceSetContainer


fun Project.show() {
    //// show project info
    logger.info("========================================================")
    logger.info("== project.group:       $group")
    logger.info("== project.name:        $name")
    logger.info("== project.version:     $version")
    logger.info("== project.description: $description")
    logger.info("========================================================")
}


