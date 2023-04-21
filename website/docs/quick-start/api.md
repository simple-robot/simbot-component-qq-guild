---
title: 使用API
sidebar_position: 1
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';
import {version} from '@site/static/version.json';

**API模块**是独立的、多平台的，你可以单独使用它作为 [QQ频道API](https://bot.q.qq.com/wiki/develop/api/) 的封装库。


## 安装

<Tabs groupId="use-dependency">
<TabItem value="Gradle Kotlin DSL">

<CodeBlock language='kotlin'>{`
// 不要忘记使用 Gradle 的 kotlin 插件来允许自动选择对应平台，比如JVM或JS等。
implementation("love.forte.simbot.component:simbot-component-qq-gulid-api:${version}") // 或参考下文所述的 Releases
`.trim()}</CodeBlock>

</TabItem>

<TabItem value="Gradle Groovy">

<CodeBlock language='gradle'>{`
// 不要忘记使用 Gradle 的 kotlin 插件来允许自动选择对应平台，比如JVM或JS等。
implementation 'love.forte.simbot.component:simbot-component-qq-gulid-api:${version}' // 版本参考下文所述的 Releases
`.trim()}</CodeBlock>

</TabItem>

<TabItem value="Maven">

<CodeBlock language='xml'>{`
<dependency>
    <groupId>love.forte.simbot.component</groupId>
    <!-- 在Maven中使用 '-jvm' 后缀来选择使用JVM平台库 -->
    <artifactId>simbot-component-qq-guild-api-jvm</artifactId>
    <!-- 参考下文所述的 Releases -->
    <version>${version}</version>
</dependency>
`.trim()}</CodeBlock>

</TabItem>
</Tabs>


:::info 版本参考

版本可前往 [**Releases**](https://github.com/simple-robot/simbot-component-qq-guild/releases) 查阅。

:::

## 使用

:::tip 太多了

我们不会在此处一一列举所有的API做演示，这不太现实。
所有的API都在包路径 `love.forte.simbot.qguild.api` 下，你可以通过 [API文档](https://docs.simbot.forte.love/) 或查阅源码的方式来寻找你所需要的API。

API包装类的命名也存在一定的规律，比如一个 `获取某列表` 的API通常会被命名为 `GetXxxListApi`。

下文会选择一小部分API来做示例。

:::

### 获取用户频道服务器列表

以 [获取用户（BOT）频道服务器列表](https://bot.q.qq.com/wiki/develop/api/openapi/user/guilds.html) 为例。

<Tabs groupId="code">

<TabItem value="Kotlin">

```kotlin
// 准备参数
// 用于请求的token
val token = "Bot 123456789.ABABABABABABABABABABABABABABABABAB"
// 用于请求的 Ktor HttpClient，如有必要则需要自行引入并选择需要使用的引擎。参考：https://ktor.io/docs/http-client-engines.html
val client = HttpClient()
// 需要请求的环境的服务器地址，比如正式环境或沙箱环境，亦或是某个自己定义代理的第三方环境
// 可以通过 QQGuild 得到一些预定义的常量信息
val server = QQGuild.SANDBOX_URL

// 使用 GetBotGuildListApi 获取频道列表
// 创建了一个参数 limit=100 的 GetBotGuildListApi，并使用上述准备好的参数进行请求。
val list: List<SimpleGuild> = GetBotGuildListApi.create(limit = 100).request(client, server, token)

list.forEach { ... }
```

也可以通过额外的扩展函数来获得一个**全量数据**的数据流。

```kotlin
// 准备参数
// 用于请求的token
val token = "Bot 123456789.ABABABABABABABABABABABABABABABABAB"
// 用于请求的 Ktor HttpClient，如有必要则需要自行引入并选择需要使用的引擎。参考：https://ktor.io/docs/http-client-engines.html
val client = HttpClient()
// 需要请求的环境的服务器地址，比如正式环境或沙箱环境，亦或是某个自己定义代理的第三方环境
// 可以通过 QQGuild 得到一些预定义的常量信息
val server = QQGuild.SANDBOX_URL

// 使用 GetBotGuildListApi 获取频道列表
// 创建了一个每页数据的数据量都为 100 的全量数据流，每一页都使用上述准备好的参数进行请求。
val guildFlow: Flow<SimpleGuild> = GetBotGuildListApi.createFlow(batch = 100) { request(client, QQGuild.SANDBOX_URL, token) }
guildFlow.collect { guild ->
    // ...
}
```

</TabItem>

<TabItem value="Java" label="Java Blocking">

```java
// 准备参数
// 用于请求的token
String token = "Bot 123456789.ABABABABABABABABABABABABABABABABAB";
// 用于请求的 Ktor HttpClient，如有必要则需要自行引入并选择需要使用的引擎。参考：https://ktor.io/docs/http-client-engines.html
HttpClient client = HttpClientJvmKt.HttpClient(($1) -> Unit.INSTANCE);
// 或者通过jvm平台库提供的工具类来构建一个默认的 client。（需要环境中存在一种引擎）
HttpClient newClient = ApiRequestUtil.newHttpClient();
// 需要请求的环境的服务器地址，比如正式环境或沙箱环境，亦或是某个自己定义代理的第三方环境
// 可以通过 QQGuild 得到一些预定义的常量信息
Url server = QQGuild.SANDBOX_URL;

// 使用 GetBotGuildListApi 获取频道列表,
// 创建了一个 limit = 100 的 GetBotGuildListApi
GetBotGuildListApi api = GetBotGuildListApi.create(100);

// 发起请求并得到结果
List<? extends SimpleGuild> guildList = api.doRequestBlocking(client, server, token);

for (SimpleGuild guild : guildList) {
    // ...
}
```

</TabItem>

<TabItem value="Java Async">

```java
// 准备参数
// 用于请求的token
String token = "Bot 123456789.ABABABABABABABABABABABABABABABABAB";
// 用于请求的 Ktor HttpClient，如有必要则需要自行引入并选择需要使用的引擎。参考：https://ktor.io/docs/http-client-engines.html
HttpClient client = HttpClientJvmKt.HttpClient(($1) -> Unit.INSTANCE);
// 或者通过jvm平台库提供的工具类来构建一个默认的 client。（需要环境中存在一种引擎）
HttpClient newClient = ApiRequestUtil.newHttpClient();
// 需要请求的环境的服务器地址，比如正式环境或沙箱环境，亦或是某个自己定义代理的第三方环境
// 可以通过 QQGuild 得到一些预定义的常量信息
Url server = QQGuild.SANDBOX_URL;

// 使用 GetBotGuildListApi 获取频道列表,
// 创建了一个 limit = 100 的 GetBotGuildListApi
GetBotGuildListApi api = GetBotGuildListApi.create(100);

// 发起请求并得到 Future 结果
api.doRequestAsync(client, server, token).thenAccept(guildList -> {
    for (SimpleGuild guild : guildList) {
        // ...
    }
});
```

</TabItem>

<TabItem value="Java Reactive">

```java
// 准备参数
// 用于请求的token
String token = "Bot 123456789.ABABABABABABABABABABABABABABABABAB";
// 用于请求的 Ktor HttpClient，如有必要则需要自行引入并选择需要使用的引擎。参考：https://ktor.io/docs/http-client-engines.html
HttpClient client = HttpClientJvmKt.HttpClient(($1) -> Unit.INSTANCE);
// 或者通过jvm平台库提供的工具类来构建一个默认的 client。（需要环境中存在一种引擎）
HttpClient newClient = ApiRequestUtil.newHttpClient();
// 需要请求的环境的服务器地址，比如正式环境或沙箱环境，亦或是某个自己定义代理的第三方环境
// 可以通过 QQGuild 得到一些预定义的常量信息
Url server = QQGuild.SANDBOX_URL;

// 使用 GetBotGuildListApi 获取频道列表,
// 创建了一个 limit = 100 的 GetBotGuildListApi
GetBotGuildListApi api = GetBotGuildListApi.create(100);

// 发起请求并得到响应式结果
Flux<? extends SimpleGuild> guildFlux = Mono.fromCompletionStage(api.doRequestAsync(client, server, token))
        .flatMapIterable(Function.identity());

return guildFlux;
```

</TabItem>

</Tabs>

