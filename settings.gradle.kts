rootProject.name = "tencent-guild"
val pathMap = mutableMapOf<String, String>()

includeAndSaveFilePath(":api", "simbot-component-tencent-guild-api")
includeAndSaveFilePath(":core", "simbot-component-tencent-guild-api-core")
includeAndSaveFilePath(":component", "simbot-component-tencent-guild-core")
includeAndSaveFilePath(":component-boot", "simbot-component-tencent-guild-boot")



// project(":api").name = "tencent-guild-api"

// project(":core").name = "tencent-guild-core"
// project(":component").name = "tencent-guild-component"

fun includeAndSaveFilePath(path: String, setName: String) {
    include(path)
    project(path).name = setName
}