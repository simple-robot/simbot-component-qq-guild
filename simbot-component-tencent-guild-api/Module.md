# Module simbot-component-tencent-guild-api

对[QQ频道API](https://bot.q.qq.com/wiki/develop/api/)的最基本封装，不包含额外的功能性实现（比如bot、事件等）。

如果希望使用一个仅仅是针对API的原始封装模块而不需要基础/高级的事件流程控制，那么请考虑使用api模块。

> _api模块不再实现 `simbot-api`，它将会作为一个纯净的模块提供（仅包含 `ktor-client` 所需依赖、`kotlinx-serialization-json` 所需依赖 ）。_

## 使用

**Gradle Kotlin DSL**

```kotlin
implementation("love.forte.simbot.component:simbot-component-tencent-guild-api:$VERSION")
```

**Gradle Groovy**

```groovy
implementation 'love.forte.simbot.component:simbot-component-tencent-guild-api:$VERSION'
```

**Maven**

```xml
<dependency>
    <groupId>love.forte.simbot.component</groupId>
    <artifactId>simbot-component-tencent-guild-api</artifactId>
    <version>${VERSION}</version>
</dependency>
```


# Package love.forte.simbot.qguild.api

与[QQ频道开发者文档](https://bot.q.qq.com/wiki/develop/api/)中定义的API进行对照实现的模块。


# Package love.forte.simbot.qguild.model

用于存放QQ频道中各API的**响应数据模型**定义。

数据模型与文档中定义的类型基本对应，以不可变数据类 (`immutable data class`) 的形式定义实现，并通过API模块下的各API响应结果对外提供。 
