---
switcher-label: Java API
---

<var name="jr" value="Reactor"/>

# 使用 API

<tldr>
<p>本章节介绍如何使用 <control>API 模块</control> 来构建、请求一个QQ频道的API。</p>
</tldr>

## 安装

<tabs group="build">
<tab title="Gradle(Kotlin DSL)" group-key="kts">

```Kotlin
implementation("love.forte.simbot.component:simbot-component-qq-guild-api:%version%")
```

<tip>

如果你使用 Java 而不配合使用 Gradle 的 `kotlin` 插件,
那么你需要指定依赖的后缀为 `-jvm`。

```Kotlin
implementation("love.forte.simbot.component:simbot-component-qq-guild-api-jvm:%version%")
```

</tip>

</tab>
<tab title="Gradle(Groovy)" group-key="groovy">

```Groovy
implementation 'love.forte.simbot.component:simbot-component-qq-guild-api:%version%'
```

<tip>

如果你使用 Java 而不配合使用 Gradle 的 `kotlin` 插件, 
那么你需要指定依赖的后缀为 `-jvm`。

```Groovy
implementation 'love.forte.simbot.component:simbot-component-qq-guild-api-jvm:%version%'
```

</tip>

</tab>
<tab title="Maven" group-key="maven">

```xml
<dependency>
    <groupId>love.forte.simbot.component</groupId>
    <!-- Maven 需要添加 `-jvm` 后缀来选择使用 JVM 平台 -->
    <artifactId>simbot-component-qq-guild-api-jvm</artifactId>
    <version>%version%</version>
</dependency>
```

</tab>
</tabs>

### 引擎选择

<include from="snippets.md" element-id="engine-choose" />

## 使用

QQ频道组件的API模块提供了针对
[QQ频道API](https://bot.q.qq.com/wiki/develop/api/) 
的基本对应封装。

API封装的命名与API具有一定关联，例如 [`获取用户（BOT）频道服务器列表`](https://bot.q.qq.com/wiki/develop/api/openapi/user/guilds.html)：

<compare first-title="API" second-title="API封装" type="top-bottom">

```HTTP
GET /users/@me/guilds
```

```
love.forte.simbot.qguild.api.user.GetBotGuildListApi
```
</compare>

> 所有的API实现均在包路径 `love.forte.simbot.miyoushe.api` 中。

API的应用大差不差，因此此处仅使用部分类型作为示例，
不会演示所有API。
如果想浏览或寻找需要的 API，可前往 [APIDoc引导](https://docs.simbot.forte.love)
中进入QQ频道组件的 KDoc 查阅，或可以简单的借助IDE的智能提示进行寻找。

以 [获取用户（BOT）频道服务器列表](https://bot.q.qq.com/wiki/develop/api/openapi/user/guilds.html) 为例。

<tabs group="Code">
<tab title="Kotlin" group-key="Kotlin">

```kotlin
    // 准备参数
// 用于请求的token
val token = "Bot 123456789.ABABABABABABABABABABABABABABABABAB"
// 用于请求的 Ktor HttpClient，如有必要则需要自行引入并选择需要使用的引擎。
// 参考：[[[Ktor Engines|https://ktor.io/docs/http-client-engines.html]]]
val client = HttpClient()
// 需要请求的环境的服务器地址，比如正式环境或沙箱环境，亦或是某个自己定义代理的第三方环境
// 可以通过 QQGuild 得到一些预定义的常量信息
val server = QQGuild.SANDBOX_URL

// 使用 GetBotGuildListApi 获取频道列表
// 创建了一个参数 limit=100 的 GetBotGuildListApi，并使用上述准备好的参数进行请求。
val resultList = GetBotGuildListApi.create(limit = 100).requestData(client, token, server)

resultList.forEach { // it: SimpleGuild
    ...
}
```

也可以通过额外的扩展函数来获得一个**全量数据**的数据流。

```kotlin
// 准备参数
// 用于请求的token
val token = "Bot 123456789.ABABABABABABABABABABABABABABABABAB"
// 用于请求的 Ktor HttpClient，如有必要则需要自行引入并选择需要使用的引擎。
// 参考：[[[Ktor Engines|https://ktor.io/docs/http-client-engines.html]]]
val client = HttpClient()
// 需要请求的环境的服务器地址，比如正式环境或沙箱环境，亦或是某个自己定义代理的第三方环境
// 可以通过 QQGuild 得到一些预定义的常量信息
val server = QQGuild.SANDBOX_URL

// 使用 GetBotGuildListApi 获取频道列表
// 创建了一个基于 GetBotGuildListApi 获取全量数据的流，并使用上述准备好的参数进行请求。
val resultFlow = GetBotGuildListApi.createFlow { requestData(client, token, server) }

resultFlow.collect { // it: SimpleGuild
    println(it)
}
```

</tab>
<tab title="Java" group-key="Java">

```java
        // 准备参数
// 用于请求的token
var token = "Bot 123456789.ABABABABABABABABABABABABABABABABAB";
// 用于请求的 Ktor HttpClient，如有必要则需要自行引入并选择需要使用的引擎。
// 参考：https://ktor.io/docs/http-client-engines.html
var client = HttpClientJvmKt.HttpClient(($1) -> Unit.INSTANCE);
// 或者通过jvm平台库提供的工具类来构建一个默认的 client。（需要环境中存在一种引擎）
var newClient = ApiRequests.newHttpClient();

// 需要请求的环境的服务器地址，比如正式环境或沙箱环境，亦或是某个自己定义代理的第三方环境
// 可以通过 QQGuild 得到一些预定义的常量信息
var server = QQGuild.SANDBOX_URL;

// 使用 GetBotGuildListApi 获取频道列表,
// 创建了一个 limit = 100 的 GetBotGuildListApi
var api = GetBotGuildListApi.create(100);

ApiRequests.requestDataAsync(api, client, token, server)
        .thenAccept(guildList -> {
            for (var guild : guildList) {
                // ...
            }
        });
```
{switcher-key="%ja%"}

```java
// 准备参数
// 用于请求的token
var token = "Bot 123456789.ABABABABABABABABABABABABABABABABAB";
// 用于请求的 Ktor HttpClient，如有必要则需要自行引入并选择需要使用的引擎。
// 参考：https://ktor.io/docs/http-client-engines.html
var client = HttpClientJvmKt.HttpClient(($1) -> Unit.INSTANCE);
// 或者通过jvm平台库提供的工具类来构建一个默认的 client。（需要环境中存在一种引擎）
var newClient = ApiRequests.newHttpClient();

// 需要请求的环境的服务器地址，比如正式环境或沙箱环境，亦或是某个自己定义代理的第三方环境
// 可以通过 QQGuild 得到一些预定义的常量信息
var server = QQGuild.SANDBOX_URL;

// 使用 GetBotGuildListApi 获取频道列表,
// 创建了一个 limit = 100 的 GetBotGuildListApi
var api = GetBotGuildListApi.create(100);

// 发起请求并得到结果
var guildList = ApiRequests.requestDataBlocking(api, client, token, server);

for (var guild : guildList) {
    // ...
}
```
{switcher-key="%jb%"}


```java
        // 准备参数
// 用于请求的token
var token = "Bot 123456789.ABABABABABABABABABABABABABABABABAB";
// 用于请求的 Ktor HttpClient，如有必要则需要自行引入并选择需要使用的引擎。
// 参考：https://ktor.io/docs/http-client-engines.html
var client = HttpClientJvmKt.HttpClient(($1) -> Unit.INSTANCE);
// 或者通过jvm平台库提供的工具类来构建一个默认的 client。（需要环境中存在一种引擎）
var newClient = ApiRequests.newHttpClient();

// 需要请求的环境的服务器地址，比如正式环境或沙箱环境，亦或是某个自己定义代理的第三方环境
// 可以通过 QQGuild 得到一些预定义的常量信息
var server = QQGuild.SANDBOX_URL;

// 使用 GetBotGuildListApi 获取频道列表,
// 创建了一个 limit = 100 的 GetBotGuildListApi
var api = GetBotGuildListApi.create(100);

// 发起请求并得到结果
ApiRequests.requestDataReserve(api, client, token, server)
        .transform(SuspendReserves.mono())
        .subscribe(guildList -> {
            for (var guild : guildList) {
                // ...
            }
        });
```
{switcher-key="%jr%"}

</tab>
</Tabs>
