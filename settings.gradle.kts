rootProject.name = "tencent-guild"
val pathMap = mutableMapOf<String, String>()

includeAndSaveFilePath(":api", "tencent-guild-api")
includeAndSaveFilePath(":core", "tencent-guild-core")
includeAndSaveFilePath(":component", "component-tencent-guild")



// project(":api").name = "tencent-guild-api"

// project(":core").name = "tencent-guild-core"
// project(":component").name = "tencent-guild-component"

fun includeAndSaveFilePath(path: String, setName: String) {
    include(path)
    project(path).name = setName
}