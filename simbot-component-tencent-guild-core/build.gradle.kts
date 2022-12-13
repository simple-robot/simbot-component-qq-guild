/*
 *  Copyright (c) 2022 ForteScarlet <ForteScarlet@163.com>
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

plugins {
    `simbot-tcg-suspend-transform-configure`
    id("simbot-tencent-guild.module-conventions")
    id("simbot-tencent-guild.maven-publish")
    kotlin("plugin.serialization")
}


dependencies {
    api(project(":simbot-component-tencent-guild-stdlib")) {
        exclude(SIMBOT_GROUP, "simbot-logger-slf4j-impl")
    }

    implementation(simbotCore)
    api(libs.ktor.client.core)
    api(libs.ktor.client.cio)
    api(libs.ktor.client.ws)
    api(libs.ktor.client.contentNegotiation)
    api(libs.ktor.serialization.kotlinxJson)
    api(libs.kotlinx.serialization.json)

    compileOnly(libs.kotlinx.serialization.properties)
    compileOnly(libs.charleskorn.kaml)

    testImplementation(libs.charleskorn.kaml)
    testImplementation("love.forte.simbot:simbot-logger-slf4j-impl:3.0.0-M5")

}

