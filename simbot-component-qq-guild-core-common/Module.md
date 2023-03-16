# Module simbot-component-tencent-guild-core-common

基于 `stdlib` 和 `api` 模块，使用[simbot3核心库](https://github.com/simple-robot/simpler-robot/tree/v3-main) 对 [QQ频道API](https://bot.q.qq.com/wiki/develop/api/) 功能的完整性实现。
这是此仓库作为simbot组件库的主要 **"证据"** ，也是实际意义上的组件模块。

`core-common` 是 `core` 模块中绝大部分的公开定义类型，此模块被 `core` 模块依赖，不能直接使用。如果需要使用，更多应用参考 `simbot-component-tencent-guild-core` 模块。

有关simbot3的内容，可以前往其[**官方网站**](https://simbot.forte.love)或[**仓库首页**](https://github.com/simple-robot/simpler-robot/tree/v3-main)了解更多。

## 命名说明

在QQ频道组件中，所有相关的类型（包括但不限于事件、频道、子频道等相关对象）均会以 `QG` 为前缀命名 (例如 `QGGuild` 、`QGBot`)，代表 `QQ-Guild`。
其中除了 `Component` 的实现 `QQGuildComponent`。组件的实现使用全名 `QQGuild` 作为前缀。

## 使用

<details open><summary><b>Gradle Kotlin DSL</b></summary>

```kotlin
implementation("love.forte.simbot:simbot-core:$SIMBOT_VERSION") // 必须显式指定simbot相关依赖比如此核心库或spring-boot-starter
implementation("love.forte.simbot.component:simbot-component-qq-guild-core:$VERSION")
```

</details>


<details><summary><b>Gradle Groovy</b></summary>

```groovy
implementation 'love.forte.simbot:simbot-core:$SIMBOT_VERSION' // 必须显式指定simbot相关依赖，如此核心库或spring-boot-starter
implementation 'love.forte.simbot.component:simbot-component-qq-guild-core:$VERSION'
```

</details>

<details><summary><b>Maven</b></summary>

```xml
<!-- 必须显式指定simbot相关依赖，如此核心库或spring-boot-starter -->
<dependency>
    <groupId>love.forte.simbot</groupId>
    <artifactId>simbot-core</artifactId>
    <version>${SIMBOT_VERSION}</version>
</dependency>
<dependency>
    <groupId>love.forte.simbot.component</groupId>
    <artifactId>simbot-component-tencent-guild-core</artifactId>
    <version>${VERSION}</version>
</dependency>
```

</details>

或者你可以在借助 `installAll` 的情况下，将 `core` 模块隐藏起来，仅使用 `core-common`。这样可以避免被一些内部类型打扰。

<details open><summary><b>Gradle Kotlin DSL</b></summary>

```kotlin
implementation("love.forte.simbot:simbot-core:$SIMBOT_VERSION") // 必须显式指定simbot相关依赖比如此核心库或spring-boot-starter
implementation("love.forte.simbot.component:simbot-component-qq-guild-core-common:$VERSION")
runtimeOnly("love.forte.simbot.component:simbot-component-qq-guild-core:$VERSION") // 仅运行时
```

</details>


<details><summary><b>Gradle Groovy</b></summary>

```groovy
implementation 'love.forte.simbot:simbot-core:$SIMBOT_VERSION' // 必须显式指定simbot相关依赖，如此核心库或spring-boot-starter
implementation 'love.forte.simbot.component:simbot-component-qq-guild-core-common:$VERSION'
runtimeOnly 'love.forte.simbot.component:simbot-component-qq-guild-core:$VERSION' // 仅运行时
```

</details>

<details><summary><b>Maven</b></summary>

```xml
<!-- 必须显式指定simbot相关依赖，如此核心库或spring-boot-starter -->
<dependency>
    <groupId>love.forte.simbot</groupId>
    <artifactId>simbot-core</artifactId>
    <version>${SIMBOT_VERSION}</version>
</dependency>
<dependency>
    <groupId>love.forte.simbot.component</groupId>
    <artifactId>simbot-component-tencent-guild-core-common</artifactId>
    <version>${VERSION}</version>
</dependency>
<dependency>
    <groupId>love.forte.simbot.component</groupId>
    <artifactId>simbot-component-tencent-guild-core</artifactId>
    <version>${VERSION}</version>
    <scope>runtime</scope> <!-- 仅运行时 -->
</dependency>
```

</details>

此时，如果希望从 Application 中主动寻找 `BotManager`, 您应当使用 `BaseQGBotManager` 。

