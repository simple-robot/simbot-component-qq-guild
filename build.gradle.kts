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

repositories {
    mavenLocal()
    mavenCentral()
}

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "java")
    apply(plugin = "signing")
    repositories {
        mavenLocal()
        mavenCentral()
    }
    println(name)
    configurePublishing(name)


}

