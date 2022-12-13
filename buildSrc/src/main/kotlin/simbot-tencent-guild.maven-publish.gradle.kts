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
//                    url.set("https://github.com/simple-robot/simbot-component-tencent-guild")
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
//                        url.set("https://github.com/simple-robot/simbot-component-tencent-guild")
//                        connection.set("scm:git:https://github.com/simple-robot/simbot-component-tencent-guild.git")
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


