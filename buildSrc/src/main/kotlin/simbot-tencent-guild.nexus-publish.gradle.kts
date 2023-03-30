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

import love.forte.gradle.common.core.project.setup
import love.forte.gradle.common.core.repository.Repositories
import love.forte.gradle.common.publication.configure.nexusPublishConfig
import util.checkPublishConfigurable
import java.time.Duration

plugins {
    id("io.github.gradle-nexus.publish-plugin")
}

setup(P.ComponentQQGuild)

if (isSnapshot()) {
    version = P.ComponentQQGuild.snapshotVersion.toString()
}

//val (isSnapshotOnly, isReleaseOnly, isPublishConfigurable) = checkPublishConfigurable()
//
//logger.info("isSnapshotOnly: {}", isSnapshotOnly)
//logger.info("isReleaseOnly: {}", isReleaseOnly)
//logger.info("isPublishConfigurable: {}", isPublishConfigurable)

val userInfo = love.forte.gradle.common.publication.sonatypeUserInfoOrNull

if (userInfo == null) {
    logger.warn("sonatype.username or sonatype.password is null, cannot config nexus publishing.")
}

nexusPublishConfig {
    projectDetail = P.ComponentQQGuild
    useStaging = project.provider { !project.version.toString().endsWith("SNAPSHOT", ignoreCase = true) }
    repositoriesConfig = {
        sonatype {
            snapshotRepositoryUrl.set(uri(Repositories.Snapshot.URL))
            username.set(userInfo?.username)
            password.set(userInfo?.password)
        }
    }
}

//
//if (isPublishConfigurable) {
//    val sonatypeUsername: String? = sonatypeUsername
//    val sonatypePassword: String? = sonatypePassword
//
//    if (sonatypeUsername == null || sonatypePassword == null) {
//        logger.warn("sonatype.username or sonatype.password is null, cannot config nexus publishing.")
//    }
//
//    nexusPublishing {
//        packageGroup.set(project.group.toString())
//
//        useStaging.set(
//            project.provider { !project.version.toString().endsWith("SNAPSHOT", ignoreCase = true).also {
//                logger.info("UseStaging: {} (project version: {})", it, project.version.toString())
//            } }
//        )
//
//        transitionCheckOptions {
//            maxRetries.set(100)
//            delayBetween.set(Duration.ofSeconds(30))
//        }
//
//        repositories {
//            sonatype {
//                snapshotRepositoryUrl.set(uri(Repositories.Snapshot.URL))
//                username.set(sonatypeUsername)
//                password.set(sonatypePassword)
//            }
//        }
//    }
//
//
//    logger.info("[publishing-configure] - [{}] configured.", name)
//}




