先阅读 [模块说明](Module.md)

## 示例

### Kotlin

```kotlin
// 获取一个Bot
val bot = BotFactory.create(
    "APP ID",
    "SECRET",
    "TOKEN",
) {
    // 一些配置，例如订阅的事件、使用的服务器（比如切换到沙箱环境）
    this.intents = EventIntents.GuildMembers.intents + EventIntents.Guilds.intents // 订阅的事件
    useSandboxServerUrl() // 使用的服务器切换到沙箱环境
    // ...
}

// 订阅事件。事件类型参考 Signal.Dispatch 的实现类类型
// 监听Signal.Dispatch即代表监听所有事件
bot.registerProcessor<Signal.Dispatch> { raw ->
    // 当前函数 receiver 即为事件本体
    println(this) // -> this: 事件本体
    // 函数体参数为事件的原始JSON字符串
    println(raw)  // -> raw: JSON字符串
}

// 监听频道信息更新事件
bot.registerProcessor<GuildUpdate> { raw ->
    this.data.opUserId // 操作人ID
    // ...

    // 事件中请求API
    val member = GetMemberApi.create(this.data.id, this.data.opUserId).requestBy(bot)
    println(member)
}

val me = bot.me() // 通过 me 查询bot自身的用户信息

// 启动bot，连接到websocket服务器并开始接收事件
bot.start()
```
