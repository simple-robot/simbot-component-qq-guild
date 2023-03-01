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
plugins {
    idea
}

idea {
    module.apply {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
    project {
        modules.forEach { module ->
            module.apply {
                isDownloadSources = true
                isDownloadJavadoc = true
            }
        }
    }
}

configurations.all {
    // check for updates every build
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}
