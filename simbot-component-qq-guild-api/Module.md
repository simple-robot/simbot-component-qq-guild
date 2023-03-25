# Module simbot-component-tencent-guild-api

对[QQ频道API](https://bot.q.qq.com/wiki/develop/api/)的最基本封装，不包含额外的功能性实现（比如bot、事件调度等）。

如果希望使用一个仅仅是针对API的原始封装模块而不需要基础/高级的事件流程控制，那么请考虑使用api模块。

api模块仅包含以下依赖：

- kotlinx.coroutines
- ktor
  - client-core
  - client-content-negotiation
  - serialization-kotlinx-json
- kotlinx.serialization-json
- kotlinx.datetime
- SLF4J API (JVM Only, provided by simbot-logger)

## 使用

**Gradle Kotlin DSL**

`api` 模块是多平台模块，在Gradle中需要使用 `kotlin` 插件来引用正确的平台。这并不代表你需要使用Kotlin语言编写。

```kotlin
plugin {
  java // 你仍然可以使用Java
  kotlin("jvm") version "$KOTLIN_VERSION" // 比如 1.8.10
  // 或者使用其他平台，例如 kotlin("js")
}

dependencies {
    implementation("love.forte.simbot.component:simbot-component-qq-guild-api:$VERSION")

    // 你需要自行选择一个想要使用的 ktor-http-client, 例如 cio 或 okhttp 等
    // 更多选择参考 https://ktor.io/docs/http-client-engines.html
    runtimeOnly("io.ktor:ktor-client-cio:$KTOR_VERSION")
}
```

**Gradle Groovy**

`api` 模块是多平台模块，在Gradle中需要使用 `kotlin` 插件来引用正确的平台。这并不代表你需要使用Kotlin语言编写。

```groovy
plugin {
  java // 你仍然可以使用Java
  id "org.jetbrains.kotlin.jvm" version "$KOTLIN_VERSION" // 比如 1.8.10
  // 或者使用其他平台，例如 kotlin("js")
}

dependencies {
    implementation 'love.forte.simbot.component:simbot-component-qq-guild-api:$VERSION'

    // 你需要自行选择一个想要使用的 ktor-http-client, 例如 cio 或 okhttp 等
    // 更多选择参考 https://ktor.io/docs/http-client-engines.html
    runtimeOnly 'io.ktor:ktor-client-cio:$KTOR_VERSION' 
}
```

**Maven**

`api` 模块是多平台模块，在Maven中需要增加 `-jvm` 后缀来使用JVM平台库。

```xml
<dependency>
    <groupId>love.forte.simbot.component</groupId>
    <artifactId>simbot-component-qq-guild-api-jvm</artifactId>
    <version>${VERSION}</version>
</dependency>

<!--
  你需要自行选择一个想要使用的 ktor-http-client, 例如 cio 或 okhttp 等
  更多选择参考 https://ktor.io/docs/http-client-engines.html
-->
<dependency>
  <groupId>io.ktor</groupId>
  <artifactId>ktor-client-cio</artifactId>
  <version>${KTOR_VERSION}</version>
  <scope>runtime</scope>
</dependency>
```


# Package love.forte.simbot.qguild

api模块下的部分顶层内容，例如部分标记性注解、模块定义的异常类型、模块使用的常量类等。

# Package love.forte.simbot.qguild.api

与[QQ频道开发者文档](https://bot.q.qq.com/wiki/develop/api/)中定义的API进行对照实现的模块。

# Package love.forte.simbot.qguild.event

QQ频道的事件相关内容，包括 [事件订阅](https://bot.q.qq.com/wiki/develop/api/gateway/intents.html) 、
[opcode](https://bot.q.qq.com/wiki/develop/api/gateway/opcode.html)
以及推送的各种事件消息体等。

# Package love.forte.simbot.qguild.message

一些与消息相关的内容，例如针对 [内嵌格式](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html) 的高效编码/解码器。

# Package love.forte.simbot.qguild.model

用于存放QQ频道中各API的**响应数据模型**。

数据模型与文档中定义的类型基本对应，以不可变数据类 (`immutable data class`) 的形式定义实现，并通过API模块下的各API响应结果对外提供。 

# Package love.forte.simbot.qguild.time

用于兼容多平台的部分**时间相关**类型。
