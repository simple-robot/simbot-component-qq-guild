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

import love.forte.gradle.common.core.project.setup
import love.forte.gradle.common.core.repository.Repositories
import util.checkPublishConfigurable
import java.time.Duration

plugins {
    id("io.github.gradle-nexus.publish-plugin")
}

setup(P.ComponentTencentGuild)
if (isSnapshot()) {
    version = P.ComponentTencentGuild.snapshotVersion.toString()
}

val (isSnapshotOnly, isReleaseOnly, isPublishConfigurable) = checkPublishConfigurable()

logger.info("isSnapshotOnly: {}", isSnapshotOnly)
logger.info("isReleaseOnly: {}", isReleaseOnly)
logger.info("isPublishConfigurable: {}", isPublishConfigurable)


if (isPublishConfigurable) {
    val sonatypeUsername: String? = sonatypeUsername
    val sonatypePassword: String? = sonatypePassword

    if (sonatypeUsername == null || sonatypePassword == null) {
        logger.warn("sonatype.username or sonatype.password is null, cannot config nexus publishing.")
    }

    nexusPublishing {
        packageGroup.set(project.group.toString())

        useStaging.set(
            project.provider { !project.version.toString().endsWith("SNAPSHOT", ignoreCase = true) }
        )

        transitionCheckOptions {
            maxRetries.set(100)
            delayBetween.set(Duration.ofSeconds(5))
        }

        repositories {
            sonatype {
                snapshotRepositoryUrl.set(uri(Repositories.Snapshot.URL))
                username.set(sonatypeUsername)
                password.set(sonatypePassword)
            }
        }
    }


    logger.info("[publishing-configure] - [{}] configured.", name)
}




