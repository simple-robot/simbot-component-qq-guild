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
    kotlin("jvm") version "1.6.0" apply false
    kotlin("plugin.serialization") version "1.6.0" apply false
    id("org.jetbrains.dokka") version "1.5.30" apply false
    `maven-publish`
    signing
    // see https://github.com/gradle-nexus/publish-plugin
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

group = P.TencentGuild.GROUP
version = P.TencentGuild.VERSION

println("=== Current version: $version ===")

repositories {
    mavenLocal()
    mavenCentral()
}


subprojects {
    group = P.TencentGuild.GROUP
    version = P.TencentGuild.VERSION

    apply(plugin = "maven-publish")
    apply(plugin = "java")
    apply(plugin = "signing")
    repositories {
        mavenLocal()
        mavenCentral()
    }
    println(name)

    configurePublishing(name)

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + listOf("-Xjvm-default=all")
        }
    }
    println("[publishing-configure] - [$name] configured.")
    // set gpg file path to root
    val secretKeyRingFileKey = "signing.secretKeyRingFile"
    // val secretKeyRingFile = local().getProperty(secretKeyRingFileKey) ?: throw kotlin.NullPointerException(secretKeyRingFileKey)
    val secretRingFile = File(project.rootDir, "ForteScarlet.gpg")
    extra[secretKeyRingFileKey] = secretRingFile
    setProperty(secretKeyRingFileKey, secretRingFile)

    signing {
        // val key = local().getProperty("signing.keyId")
        // val password = local().getProperty("signing.password")
        // this.useInMemoryPgpKeys(key, password)
        sign(publishing.publications)
    }


}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}


val credentialsUsername: String = local().getProperty("credentials.username")!!
val credentialsPassword: String = local().getProperty("credentials.password")!!


nexusPublishing {
    packageGroup.set(P.Simbot.GROUP)

    repositories {
        sonatype {
            username.set(credentialsUsername)
            password.set(credentialsPassword)
        }

    }
}