plugins {
    `java-library`
    kotlin("jvm") // version "1.6.0" // apply false
    kotlin("plugin.serialization") // version "1.6.0" // apply false
    id("org.jetbrains.dokka") // version "1.5.30" // apply false

}




dependencies {
    api(V.Simbot.Api.notation)

    api(V.Kotlin.Reflect.notation)

    api(V.Ktor.Client.Jvm.Core.notation)
    api(V.Ktor.Client.Jvm.CIO.notation)
    // api(V.Ktor.Client.Auth.notation)
    // api(V.Ktor.Client.Websockets.notation)
    api(V.Ktor.Client.Serialization.notation)

    api(V.Kotlinx.Serialization.Json.notation)

    testImplementation(V.Kotlin.Test.Junit5.notation)
}
repositories {
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        name = "ktor-eap"
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        javaParameters = true
        jvmTarget = "1.8"
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    outputDirectory.set(File(rootProject.projectDir, "api-doc/api"))
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
