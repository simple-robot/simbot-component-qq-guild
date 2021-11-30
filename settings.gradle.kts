rootProject.name = "tencent-guild"

include(":api")
include(":core")
include(":component")
project(":api").name = "tencent-guild-api"
project(":core").name = "tencent-guild-core"
project(":component").name = "tencent-guild-component"

