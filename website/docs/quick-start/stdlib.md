---
title: 使用标准库
sidebar_position: 2
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';
import {version} from '@site/static/version.json';


stdlib(标准库) 模块在 `API` 模块的基础上提供简单而轻量级的事件订阅能力。

## 前提准备

首先你应当准备至少一个可用的 [QQ频道机器人](https://q.qq.com/bot) 。

## 安装

<Tabs groupId="use-dependency">
<TabItem value="Gradle Kotlin DSL">

<CodeBlock language='kotlin'>{`
// 不要忘记使用 Gradle 的 kotlin 插件来允许自动选择对应平台，比如JVM或JS等。
implementation("love.forte.simbot.component:simbot-component-qq-gulid-stdlib:${version}") // 或参考下文所述的 Releases
`.trim()}</CodeBlock>


</TabItem>

<TabItem value="Gradle Groovy">

<CodeBlock language='gradle'>{`
// 不要忘记使用 Gradle 的 kotlin 插件来允许自动选择对应平台，比如JVM或JS等。
implementation 'love.forte.simbot.component:simbot-component-qq-gulid-stdlib:${version}' // 版本参考下文所述的 Releases
`.trim()}</CodeBlock>

</TabItem>

<TabItem value="Maven">

<CodeBlock language='xml'>{`
<dependency>
    <groupId>love.forte.simbot.component</groupId>
    <!-- 在Maven中使用 '-jvm' 后缀来选择使用JVM平台库 -->
    <artifactId>simbot-component-qq-guild-stdlib-jvm</artifactId>
    <!-- 参考下文所述的 Releases -->
    <version>${version}</version>
</dependency>
`.trim()}</CodeBlock>

</TabItem>
</Tabs>


:::info 版本参考

版本可前往 [**Releases**](https://github.com/simple-robot/simbot-component-qq-guild/releases) 查阅。

:::

## BOT注册

环境准备完毕后，接下来我们注册一个bot。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val bot = BotFactory.create("APP ID", "secret", "token") {
    // config
    this.wsClientEngine = ...    // 进行事件订阅所使用的 Ktor client 引擎，默认情况下会**尝试**自动加载，或直接手动指定一个支持ws连接的引擎。
    this.apiClientEngine = ...   // 使用API时所使用的 Ktor client 引擎，默认情况下会**尝试**自动加载，或直接手动指定一个支持 HTTP API 的引擎。
    this.serverUrl = QQGuild.URL // 使用正式环境，默认即为正式环境
    this.useSandboxServerUrl()   // 使用沙箱环境
    // 其他配置...
}

bot.start() // 启动bot
bot.join()  // 挂起bot直到bot终止
```

</TabItem>
<TabItem value="Java" label="Java Blocking">

```java
Bot bot = BotFactory.create("APP ID", "secret", "token", (configuration) -> {
    configuration.setWsClientEngine(...);     // 进行事件订阅所使用的 Ktor client 引擎，默认情况下会**尝试**自动加载，或直接手动指定一个支持ws连接的引擎。
    configuration.setApiClientEngine(...);    // 使用API时所使用的 Ktor client 引擎，默认情况下会**尝试**自动加载，或直接手动指定一个支持 HTTP API 的引擎。
    configuration.setServerUrl(QQGuild.URL);  // 使用正式环境，默认即为正式环境
    configuration.useSandboxServerUrl();      // 使用沙箱环境
    // 其他配置...
        
    return Unit.INSTANCE; // 结束配置
});

bot.startBlocking(); // 启动bot
bot.joinBlocking();  // 阻塞t直到bot终止
```

</TabItem>
<TabItem value="Java Async">

```java
Bot bot = BotFactory.create("APP ID", "secret", "token", (configuration) -> {
    configuration.setWsClientEngine(...);     // 进行事件订阅所使用的 Ktor client 引擎，默认情况下会**尝试**自动加载，或直接手动指定一个支持ws连接的引擎。
    configuration.setApiClientEngine(...);    // 使用API时所使用的 Ktor client 引擎，默认情况下会**尝试**自动加载，或直接手动指定一个支持 HTTP API 的引擎。
    configuration.setServerUrl(QQGuild.URL);  // 使用正式环境，默认即为正式环境
    configuration.useSandboxServerUrl();      // 使用沙箱环境
    // 其他配置...
        
    return Unit.INSTANCE; // 结束配置
});

// 阻塞直到bot终止
bot.startAsync().thenCompose(__ -> bot.joinAsync()).join();
```

</TabItem>
</Tabs>

:::info 引擎选择

引擎的选择可参考 [**Ktor文档**](https://ktor.io/docs/http-client-engines.html#limitations)。

大多数情况下你需要手动指定一个具体的引擎
(例如使用 `mingwX64` 目标时可选用 [**WinHttp**](https://ktor.io/docs/http-client-engines.html#winhttp) 引擎 )，
或者至少保证程序的运行时环境中存在可用引擎(在JVM平台下的自动加载)。

:::

## 事件监听(订阅)

在启动创建bot、启动bot这个过程中间，你可以注册一些**事件处理器**来对订阅的事件进行处理。

<Tabs groupId="code">
<TabItem value="Kotlin">

```kotlin
val bot = BotFactory.create("APP ID", "secret", "token") {
    // ...
}

// 订阅所有类型的事件
// highlight-start
bot.registerProcessor { raw -> // this: Signal.Dispatch
    // ...
}
// highlight-end

// 订阅具体的事件类型
// highlight-start
bot.registerProcessor<GuildMemberAdd> { raw -> // this: GuildMemberAdd
    // ...
}
// highlight-end

bot.start() // 启动bot
bot.join()  // 挂起bot直到bot终止
```

</TabItem>
<TabItem value="Java" label="Java Blocking">

```java
Bot bot = BotFactory.create("APP ID", "secret", "token", (configuration) -> {
    // ...    
    return Unit.INSTANCE; // 结束配置
});

// 订阅所有事件类型
// highlight-start
bot.registerBlockingProcessor((Signal.Dispatch event, String raw) -> {
    // ...
});
// highlight-end

// 订阅具体的事件类型
// highlight-start
bot.registerBlockingProcessor(GuildMemberAdd.class, (GuildMemberAdd event, String raw) -> {
    // ...
});
// highlight-end

bot.startBlocking(); // 启动bot
bot.joinBlocking();  // 阻塞直到bot终止
```

</TabItem>
<TabItem value="Java Async">

```java
Bot bot = BotFactory.create("APP ID", "secret", "token", (configuration) -> {
    // ...    
    return Unit.INSTANCE; // 结束配置
});

// 订阅所有事件类型
// highlight-start
bot.registerAsyncProcessor((Signal.Dispatch event, String raw) -> {
    // ...
    return CompletableFuture.completedFuture(null); // 异步事件处理器要求返回 CompletionStage<Void?> 类型的结果
});
// highlight-end

// 订阅具体的事件类型
// highlight-start
bot.registerAsyncProcessor(GuildMemberAdd.class, (GuildMemberAdd event, String raw) -> {
    // ...
    return CompletableFuture.completedFuture(null); // 异步事件处理器要求返回 CompletionStage<Void?> 类型的结果
});
// highlight-end

// 阻塞直到bot终止
bot.startAsync().thenCompose(__ -> bot.joinAsync()).join();
```

</TabItem>
</Tabs>

:::danger ⚠

施工中

:::
