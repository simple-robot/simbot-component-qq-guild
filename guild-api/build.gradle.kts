plugins {
    `java-library`
    kotlin("jvm")
    kotlin("plugin.serialization")
}


repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api(V.Simbot.Api.notation)
    api(V.Ktor.Client.Jvm.Core.notation)
    api(V.Ktor.Client.Jvm.CIO.notation)
    api(V.Ktor.Client.Auth.notation)
    api(V.Ktor.Client.Websockets.notation)

    testImplementation(V.Kotlin.Test.Junit.notation)
    testImplementation(V.Kotlinx.Serialization.Json.notation)
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