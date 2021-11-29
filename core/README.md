# Core

core模块是针对bot事件监听的简易实现，是一个半 **底层** 库，仅提供最基础的 DSL 事件注册，不提供过多的应用级功能整合（例如依赖注入、自动扫描等等）

如果你想使用更友好更高阶的使用，请关注 [simple-robot](https://github.com/ForteScarlet/simpler-robot) 框架的3.x版本发布情况。更友好的应用级实现将会通过 `simple-robot` 的组件进行实现与提供。


## 使用
## 使用
⚠ 尚未发布至中央仓库

本库将会 `simple-robot` 3.x发布时（或相对较其早的时刻）发布于Maven中央仓库。主要是因为 `simple-robot 3.x` 仍处于设计开发阶段，可能会出现一些接口变动。

如果你现在就想尝试，有两个办法：

1. issue进行留言，我会临时提供编译版本
2. 自行clone当前仓库以及 <https://github.com/ForteScarlet/simpler-robot/tree/dev-v3.0.0-preview>(注意观察，分支是 `v3.0.0-preview`), 然后先将 simple-robot 发布至个人本地仓库，然后编译本库。

### Maven

```xml
<dependency>
    <groupId>love.forte.simple-robot</groupId>
    <artifactId>tencent-guild-core</artifactId>
    <version>敬请期待</version>
</dependency>
```

### Gradle groovy

```groovy
implementation "love.forte.simple-robot:tencent-guild-core:$version"
```

### Gradle kotlin DSL

```kotlin
implementation("love.forte.simple-robot:tencent-guild-core:$version")
```

## 示例
bot注册

### Kotlin
```kotlin

suspend fun main() {
  val bot = tencentBot(
    appId = "app_id",
    appKey = "app_key",
    token = "token",
  )

  // start bot
  bot.start()

  // 添加事件1
  bot.processor { decoder ->
    val dispatch: Signal.Dispatch = this
    val jsonElement: JsonElement = dispatch.data
    if (dispatch.type == "AT_MESSAGE_CREATE") {
      val message: TencentMessage =
        decoder.decodeFromJsonElement(EventSignals.AtMessages.AtMessageCreate.decoder, jsonElement)

      println(message)
    }
    // do something..
  }

  // 指定监听事件名称1
  bot.processor("AT_MESSAGE_CREATE") { decoder ->
    val dispatch: Signal.Dispatch = this
    val message: TencentMessage =
      decoder.decodeFromJsonElement(EventSignals.AtMessages.AtMessageCreate.decoder, dispatch.data)

    println(message)
  }

  // 指定监听事件名称2
  bot.processor(EventSignals.AtMessages.AtMessageCreate.type) { decoder ->
    val dispatch: Signal.Dispatch = this
    val message: TencentMessage =
      decoder.decodeFromJsonElement(EventSignals.AtMessages.AtMessageCreate.decoder, dispatch.data)

    println(message)
  }

  // 指定监听事件类型1
  bot.processor(EventSignals.AtMessages.AtMessageCreate) { message ->
    println(message)
  }

  // 所有事件都存在于 EventSignals 下的子类型中。

  bot.launch {
    delay(10_000)
    // 模拟bot关闭
    bot.cancel()
  }

  // join bot
  // 挂起直到bot被关闭
  bot.join()

}
```

可以看到，上述示例中，对一个bot注册事件通过 `bot.processor { ... } ` 进行注册。

对于所有的事件类型（以及他们的数据类型定义）都囊括在 `EventSignals` 的子类中，你可以直接参考此类的源码定义。