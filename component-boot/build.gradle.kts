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
    `java-library`
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
}


repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        name = "ktor-eap"
    }
}

dependencies {
    api(project(P.ComponentTencentGuild.componentPath))
    api(V.Simbot.BootApi.notation)
    testImplementation(V.Simbot.BootCore.notation)
    // api("love.forte.simbot.boot:simboot-api:3.0.0.preview.0.5")


    // api(V.Ktor.Client.Jvm.Core.notation)
    // api(V.Ktor.Client.Jvm.CIO.notation)
    // api(V.Ktor.Client.Auth.notation)
    // api(V.Ktor.Client.Websockets.notation)
    // api(V.Ktor.Client.Serialization.notation)
    // api(V.Kotlinx.Serialization.Json.notation)
    // compileOnly(V.Kotlinx.Serialization.Properties.notation)
    // compileOnly(V.Kotlinx.Serialization.Yaml.notation)


    testImplementation(V.Kotlin.Test.Junit.notation)
    testImplementation(V.Log4j.Api.notation)
    testImplementation(V.Log4j.Core.notation)
    testImplementation(V.Log4j.Slf4jImpl.notation)
    testImplementation(V.Kotlinx.Serialization.Yaml.notation)

    // implementation("love.forte.simple-robot:api:3.0.0-PREVIEW")
}

tasks.getByName<Test>("test") {
    useJUnit()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        javaParameters = true
        jvmTarget = "1.8"
    }
}

kotlin {
    // 严格模式
    explicitApiWarning()


    sourceSets.all {
        languageSettings {
            optIn("kotlin.RequiresOptIn")
        }
    }
}