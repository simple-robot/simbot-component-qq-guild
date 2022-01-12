plugins {
    `java-library`
    kotlin("jvm") // version "1.6.0" // apply false
    kotlin("plugin.serialization") // version "1.6.0" // apply false
    id("org.jetbrains.dokka") // version "1.5.30" // apply false

}

println(path)

dependencies {
    // api(project(":tencent-guild-api"))
    api(project(P.TencentGuild.apiPath))
    api(V.Ktor.Client.Websockets.notation)

    // api(V.Kotlin.Reflect.notation)
    // api(V.Ktor.Client.Jvm.Core.notation)
    // api(V.Ktor.Client.Auth.notation)
    // api(V.Ktor.Client.Serialization.notation)
    // api(V.Kotlinx.Serialization.Json.notation)

    testImplementation(V.Kotlin.Test.Junit5.notation)
    testImplementation(V.Log4j.Api.notation)
    testImplementation(V.Log4j.Core.notation)
    testImplementation(V.Log4j.Slf4jImpl.notation)
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
    outputDirectory.set(File(rootProject.projectDir, "api-doc/core"))
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
