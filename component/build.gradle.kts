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
    // api(project(":tencent-guild-core"))
    api(project(P.TencentGuild.corePath))
    api(V.Simbot.Core.notation)
    api(V.Ktor.Client.Jvm.Core.notation)
    api(V.Ktor.Client.Jvm.CIO.notation)
    api(V.Ktor.Client.Auth.notation)
    api(V.Ktor.Client.Websockets.notation)
    api(V.Ktor.Client.Serialization.notation)
    api(V.Kotlinx.Serialization.Json.notation)
    api(V.Kotlinx.Serialization.Properties.notation)
    api(V.Kotlinx.Serialization.Yaml.notation)



    testImplementation(V.Kotlin.Test.Junit.notation)
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