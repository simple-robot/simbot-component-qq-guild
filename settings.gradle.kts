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

rootProject.name = "qq-guild"


//include(":builder-generator")
include(":simbot-component-qq-guild-api")
include(":simbot-component-qq-guild-stdlib")
include(":simbot-component-qq-guild-core")
//include(":simbot-component-qq-guild-core")
//include(":simbot-component-qq-guild-benchmark")

// tests
//if (!System.getenv("IS_CI").toBoolean()) {
//    include(":tests:application-test")
//    include(":tests:spring-boot-test")
//    include(":tests:plugin-test")
//}

