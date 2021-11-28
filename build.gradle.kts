plugins {
    kotlin("jvm") version "1.6.0" apply false
    kotlin("plugin.serialization") version "1.6.0" apply false
    id("org.jetbrains.dokka") version "1.5.30" apply false

}

group = "love.forte.simple-robot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

subprojects {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

