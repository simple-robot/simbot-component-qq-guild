plugins {
    `java-library`
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("kapt")
}


repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api(project(P.TencentGuild.apiCorePath))
    api(V.Simbot.Core.notation)
    api(V.Ktor.Client.Jvm.Core.notation)
    api(V.Ktor.Client.Jvm.CIO.notation)
    api(V.Ktor.Client.Websockets.notation)
    api(V.Ktor.Client.Serialization.notation)
    api(V.Kotlinx.Serialization.Json.notation)
    api(V.Kotlinx.Serialization.Properties.notation)

    compileOnly(V.Kotlinx.Serialization.Yaml.notation)

    testImplementation(V.Kotlin.Test.Junit.notation)
    testImplementation(V.Log4j.Api.notation)
    testImplementation(V.Log4j.Core.notation)
    testImplementation(V.Log4j.Slf4jImpl.notation)
    testImplementation(V.Kotlinx.Serialization.Yaml.notation)

    compileOnly("com.google.auto.service:auto-service:1.0.1")
    kapt("com.google.auto.service:auto-service:1.0.1")
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