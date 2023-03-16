# Module simbot-component-tencent-guild-core

基于core模块，基于[simbot3核心库](https://github.com/simple-robot/simpler-robot/tree/v3-main) 对[QQ频道API](https://bot.q.qq.com/wiki/develop/api/)功能的完整性实现。
这是此仓库作为simbot组件库的主要 **"证据"** ，也是实际意义上的组件模块。

core模块尽可能大限度的以QQ频道的角度去实现simbot核心库中的API，是一个**较高封装程度**的模块。同样的，使用此模块可以使用绝大多数的simbot特性，
包括它的API风格以及Spring Boot Starter等特性。

有关simbot3的内容，可以前往其[**官方网站**](https://simbot.forte.love)或[**仓库首页**](https://github.com/simple-robot/simpler-robot/tree/v3-main)了解更多。

## 说明

在QQ频道组件中，所有相关的类型（包括但不限于事件、频道、子频道等相关对象）均会以 `QG` 为前缀命名 (例如 `QGGuild` 、`QGBot`)，代表 `QQ-Guild`。
其中除了 `Component` 的实现 `QQGuildComponent`。组件的实现使用全名 `QQGuild` 作为前缀。

## 使用

**Gradle Kotlin DSL**

```kotlin
implementation("love.forte.simbot:simbot-core:$SIMBOT_VERSION") // 必须显式指定simbot相关依赖比如此核心库或spring-boot-starter
implementation("love.forte.simbot.component:simbot-component-qq-guild-core:$VERSION")
```

**Gradle Groovy**

```groovy
implementation 'love.forte.simbot:simbot-core:$SIMBOT_VERSION' // 必须显式指定simbot相关依赖，如此核心库或spring-boot-starter
implementation 'love.forte.simbot.component:simbot-component-qq-guild-stdlib:$VERSION'
```

**Maven**

```xml
<!-- 必须显式指定simbot相关依赖，如此核心库或spring-boot-starter -->
<dependency>
    <groupId>love.forte.simbot</groupId>
    <artifactId>simbot-core</artifactId>
    <version>${SIMBOT_VERSION}</version>
</dependency>
<dependency>
    <groupId>love.forte.simbot.component</groupId>
    <artifactId>simbot-component-tencent-guild-stdlib</artifactId>
    <version>${VERSION}</version>
</dependency>
```
