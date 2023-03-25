# api模块

先阅读 [模块说明](Module.md)

api模块中，所有的API请求封装均在 `love.forte.simbot.qguild.api` 中，它们通常以 `Api` 结尾，例如 `GetGuildApi`。

所有的API构造方法均被隐藏，它们会各自提供自身的工厂函数，绝大多数以 `create` 命名，例如

```kotlin
val api: GetGuildApi = GetGuildApi.create("123")
```

## 使用示例

```kotlin
// 用于请求的 client 实例
val client: HttpClient = HttpClient { ... }

val token: String = "你的请求用token" // token    

// 得到一个api请求对象，此处为 获取Guild列表 API
val api = GetBotGuildListApi.create(before = null, after = null, limit = 10)

// api.request 发起请求并得到结果
// 如果失败可以捕获 QQGuildApiException 获取详情
val guildList: List<SimpleGuild> = api.request(
    client = client,
    server = QQGuild.SANDBOX_URL, // 请求server地址. 你可以通过 QQGuild.URL 得到一个官方的正式环境地址，或者其他自定义地址。
    token = token,
    decoder = Json // 可以省略
)

// 使用结果
guildList.forEach { guild ->
    println(guild)
}
```
