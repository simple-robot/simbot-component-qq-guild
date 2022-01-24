/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
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