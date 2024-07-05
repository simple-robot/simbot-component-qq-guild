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

import love.forte.gradle.common.core.project.setup
import love.forte.gradle.common.core.repository.Repositories
import java.time.Duration

plugins {
    id("io.github.gradle-nexus.publish-plugin")
}

setup(P.ComponentQQGuild)

val userInfo = love.forte.gradle.common.publication.sonatypeUserInfoOrNull

if (userInfo == null) {
    logger.warn("sonatype.username or sonatype.password is null, cannot config nexus publishing.")
}

nexusPublishing {
    packageGroup.set(P.ComponentQQGuild.group)
    useStaging = project.provider { !project.version.toString().endsWith("SNAPSHOT", ignoreCase = true) }

    transitionCheckOptions {
        maxRetries = 1000
        delayBetween = Duration.ofSeconds(2)
    }

    repositories {
        sonatype {
            snapshotRepositoryUrl.set(uri(Repositories.Snapshot.URL))
            username.set(userInfo?.username)
            password.set(userInfo?.password)
        }
    }
}
