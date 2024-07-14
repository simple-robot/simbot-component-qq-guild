---
switcher-label: Java API 风格
---

<var name="jr" value="Reactor"/>

<include from="snippets.md" element-id="to-main-doc" />

# API

## API模块 {id='api-module'}

所有的 [**QQ频道API**](https://bot.q.qq.com/wiki/develop/api/) 封装与实现都在
API 模块中，
这些实现均在包路径 `love.forte.simbot.qguild.api` 下，
并实现接口 `love.forte.simbot.qguild.api.QQGuildApi`。

你可以前往 
<a href="modules.md#module-api" />
了解 API 模块的概述。

## API定义 {id='api-list'}

你可以前往
<a href="api-list.md" />
或 [API文档](%api-doc%) 
来寻找所有的 API 实现。

## 使用API {id='api-usage'}

在 API模块、stdlib模块和核心组件模块中都可以 '使用' API。

首先何为 '使用API'? 即提供一些所需参数，对此API发起请求并得到预期的结果或错误。

### API模块中使用 {id='use-api-in-api-module'}

在API模块中直接使用API，通常需要提供如下参数：

- `HttpClient`: 用于发起请求的 Ktor `HttpClient` 对象。
- `token`: QQ频道API中用于鉴权的客户端token。可以在它们[文档](https://bot.q.qq.com/wiki/develop/api/)中找到，比如 `Bot 100000.aaaabbbbccccdddd`。
- `server`: _可选_ 。QQ频道API有正式频道和沙箱频道之分，可通过此参数选择不同的服务器地址。在一些特殊需求下，也可以通过此方式自定义一个第三方服务器地址。

对 API 的请求是以扩展函数提供的(Java 中可以使用 `ApiRequests` 提供的静态方法)：

- `request`: 直接返回原始的 `HttpResponse` 结果，几乎不做校验
- `requestText`: 返回请求到的原始JSON字符串，会校验HTTP响应状态是否为 `2xx`。
- `requestData`: 会解析响应值为对应的实体对象后返回。会校验是否成功。

以 `GetGuildApi` (获取频道服务器详情) 为例：

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
// 准备必要信息
val token = "..."
val client = HttpClient()
// 准备API对象
val api = GetGuildApi.create("频道ID")
// 发起请求
val response = api.request(client, token)
val text = api.requestText(client, token)
val data = api.requestData(client, token)

```

</tab>
<tab title="Java" group-key="Java">

```Java
// 准备必要信息
var token = "...";
var client = HttpClientJvmKt.HttpClient($ -> Unit.INSTANCE);
// 准备API对象
final var api = GetGuildApi.create("频道ID");
// 发起请求
ApiRequests.requestAsync(api, client, token)
                .thenAccept(response -> { ... });

ApiRequests.requestTextAsync(api, client, token)
                .thenAccept(text -> { ... });

ApiRequests.requestDataAsync(api, client, token)
        .thenAccept(data -> { ... });
```
{switcher-key="%ja%"}

```Java
var token = "...";
var client = HttpClientJvmKt.HttpClient($ -> Unit.INSTANCE);
// 准备API对象
final var api = GetGuildApi.create("频道ID");
// 发起请求
var response = ApiRequests.requestBlocking(api, client, token);
var text = ApiRequests.requestTextBlocking(api, client, token);
var data = ApiRequests.requestDataBlocking(api, client, token);
```
{switcher-key="%jb%"}


```Java
// 准备必要信息
var token = "...";
var client = HttpClientJvmKt.HttpClient($ -> Unit.INSTANCE);
// 准备API对象
final var api = GetGuildApi.create("频道ID");
// 发起请求
ApiRequests.requestReserve(api, client, token)
        // 假设转化为 reactor 的 `Mono`
        .transform(SuspendReserves.mono())
        .subscribe(response -> { ... });

ApiRequests.requestTextReserve(api, client, token)
        // 假设转化为 reactor 的 `Mono`
        .transform(SuspendReserves.mono())
        .subscribe(text -> { ... });

ApiRequests.requestDataReserve(api, client, token)
        // 假设转化为 reactor 的 `Mono`
        .transform(SuspendReserves.mono())
        .subscribe(data -> { ... });
```
{switcher-key="%jr%"}

</tab>
</tabs>

### Stdlib模块中使用 {id='use-api-in-stdlib-module'}

在stdlib模块下，一个 `Bot` 类型中已经包括了上面我们提到的那些必要信息，
因此你可以使用 `Bot.requestXxx(api)` 或 `api.requestXxxBy(bot)` 来简化你的请求
(Java中可以使用 `BotRequests` 提供的静态方法)。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
// 准备必要信息
val bot: Bot = ...
// 准备API对象
val api = GetGuildApi.create("频道ID")
// 发起请求
// bot为主
val response = bot.request(api)
val text = bot.requestText(api)
val data = bot.requestData(api)
// api为主
val response1 = api.requestBy(bot)
val text1 = api.requestTextBy(bot)
val data1 = api.requestDataBy(bot)

```

</tab>
<tab title="Java" group-key="Java">

```Java
// 准备必要信息
Bot bot = ...
// 准备API对象
final var api = GetGuildApi.create("频道ID");
// 发起请求
BotRequests.requestAsync(bot, api)
                .thenAccept(response -> { ... });

BotRequests.requestTextAsync(bot, api)
                .thenAccept(text -> { ... });

BotRequests.requestDataAsync(bot, api)
        .thenAccept(data -> { ... });
```
{switcher-key="%ja%"}

```Java
// 准备必要信息
Bot bot = ...
// 准备API对象
final var api = GetGuildApi.create("频道ID");
// 发起请求
var response = BotRequests.requestBlocking(bot, api);
var text = BotRequests.requestTextBlocking(bot, api);
var data = BotRequests.requestDataBlocking(bot, api);
```
{switcher-key="%jb%"}


```Java
// 准备必要信息
Bot bot = ...
// 准备API对象
final var api = GetGuildApi.create("频道ID");
// 发起请求
BotRequests.requestReserve(bot, api)
        // 假设转化为 reactor 的 `Mono`
        .transform(SuspendReserves.mono())
        .subscribe(response -> { ... });

BotRequests.requestTextReserve(bot, api)
        // 假设转化为 reactor 的 `Mono`
        .transform(SuspendReserves.mono())
        .subscribe(text -> { ... });

BotRequests.requestDataReserve(bot, api)
        // 假设转化为 reactor 的 `Mono`
        .transform(SuspendReserves.mono())
        .subscribe(data -> { ... });
```
{switcher-key="%jr%"}

</tab>
</tabs>


### 核心模块中使用 {id='use-api-in-core-module'}

核心模块提供的 `QGBot` 可以直接使用属性 `source` 获取到stdlib模块中的 `Bot`，
因此获取后如同
<a href="#use-api-in-stdlib-module">Stdlib模块中使用</a>
中的方式一致即可。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val bot: QGBot = ...
val sourceBot = bot.source
// 使用 sourceBot
```

</tab>
<tab title="Java" group-key="Java">

```Java
QGBot bot = ...
var sourceBot = bot.getSource();
// 使用 sourceBot
```

</tab>
</tabs>
