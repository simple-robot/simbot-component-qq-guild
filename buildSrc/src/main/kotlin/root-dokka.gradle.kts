/*
 * Copyright (c) 2025. ForteScarlet.
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

import gradle.kotlin.dsl.accessors._970e5eb52742fb615d1693cfc4f6fb41.dokka
import gradle.kotlin.dsl.accessors._970e5eb52742fb615d1693cfc4f6fb41.html
import org.jetbrains.dokka.DokkaDefaults.moduleName
import org.jetbrains.dokka.DokkaDefaults.offlineMode
import org.jetbrains.dokka.DokkaDefaults.pluginsConfiguration
import org.jetbrains.kotlin.com.intellij.openapi.util.text.HtmlChunk.p

/*
 * Copyright (c) 2025. ForteScarlet.
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

plugins {
    id("org.jetbrains.dokka")
}

// region Dokka

val rootP = project

subprojects {
    afterEvaluate {
        if (plugins.hasPlugin("org.jetbrains.dokka")) {
            val p = this
            dokka {
                configSourceSets(p)
                pluginsConfiguration.html {
                    configHtmlCustoms(p)
                }
            }
            rootP.dependencies.dokka(p)
        }
    }
}

dokka {
    moduleName = "Simple Robot 组件 | QQ"

    dokkaPublications.all {
        if (isSimbotLocal()) {
            logger.info("Is 'SIMBOT_LOCAL', offline")
            offlineMode = true
        }
    }

    configSourceSets(project)

    pluginsConfiguration.html {
        configHtmlCustoms(project)
    }
}
// endregion
