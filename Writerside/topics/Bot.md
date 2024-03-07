---
switcher-label: Java API 风格
toc_max_heading_level: 3
---

# Bot

作为一个QQ频道的 Bot 库，有一个用于描述机器人的 `Bot` 想必肯定是很正常的。


## API?

在 API 模块 (`simbot-component-qq-guild-api`) 中，你可能找不到太多有关 `Bot` 的身影。
毕竟 API 模块仅是针对QQ频道中的 API 的封装与实现，本身是不包括对 `Bot` 的描述的。

## 标准库 Bot {id='stdlib-bot'}

在标准库模块 (`simbot-component-qq-guild-stdlib`) 中，
你可以发现一个类型 `love.forte.simbot.qguild.Bot`，它便是对一个**QQ频道机器人**的描述，
可以用来订阅并处理事件等。

> 标准库模块**不是**针对 _simbot标准库_ 的实现，而是一个可以作为独立的SDK库存在的模块，
> 并为下文的 _组件库_ 打下基础。

### 创建 Bot {id='create-bot'}

使用工厂类 `BotFactory.create(..)` 可创建一个尚未启动的 `Bot` 实例。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

**使用 Ticket**

```Kotlin
// 准备 bot 的必要信息
val botId = "xxxx"
val botSecret = "" // secret 如果用不到可使用空字符串
val botToken = "xxxx"
// 用于注册 bot 的 “票据” 信息。
val ticket = Bot.Ticket(botId, botSecret, botToken)
// 构建一个 Bot，并可选的进行一些配置。
val bot = BotFactory.create(ticket) {
    // 各种配置...
    // 比如切换服务地址为沙箱频道的服务地址
    useSandboxServerUrl()

    // 指定需要订阅的事件的 intents，默认会订阅：
    // - 频道相关事件
    // - 频道成员相关事件
    // - 公域消息相关事件
    intents // = xxx

    // 自定义一个 shard，默认是 Shard.FULL
    shard = Shard.FULL

    // 其他各种配置...
}
```

**直接作为参数**

也可以直接将这三个必要信息作为参数使用。

```Kotlin
// 准备 bot 的必要信息
val botId = "xxxx"
val botSecret = "" // secret 如果用不到可使用空字符串
val botToken = "xxxx"
// 构建一个 Bot，并可选的进行一些配置。
val bot = BotFactory.create(botId, botSecret, botToken) {
    ...
}
```

**直接提供配置类**

作为 DSL 的配置类也可以独立构建后直接提供。

```Kotlin
// 准备 bot 的必要信息
val botId = "xxxx"
val botSecret = "" // secret 如果用不到可使用空字符串
val botToken = "xxxx"
// 构建配置类
val configuration = ConfigurableBotConfiguration()
// config...

// 构建一个 Bot，并提供配置。
val bot = BotFactory.create(botId, botSecret, botToken, configuration)
```


</tab>
<tab title="Java" group-key="Java">

```Java
// 准备 bot 的必要信息
var botId = "xxxx";
var botSecret = ""; // secret 如果用不到可使用空字符串
var botToken = "xxxx";
// 用于注册 bot 的 “票据” 信息。
var ticket = new Bot.Ticket(botId, botSecret, botToken);

// 构建一个 Bot，并可选的进行一些配置。
var bot = BotFactory.create(ticket, config -> {
    // 各种配置...
    // 比如切换服务地址为沙箱频道的服务地址
    config.useSandboxServerUrl();

    // 指定需要订阅的事件的 intents，默认会订阅：
    // - 频道相关事件
    // - 频道成员相关事件
    // - 公域消息相关事件
    // config.setIntentsValue(...);

    // 自定义一个 shard，默认是 Shard.FULL
    config.setShard(Shard.FULL);

    // 其他各种配置...
});
```

**直接作为参数**

也可以直接将这三个必要信息作为参数使用。

```Java
// 准备 bot 的必要信息
var botId = "xxxx";
var botSecret = ""; // secret 如果用不到可使用空字符串
var botToken = "xxxx";

// 构建一个 Bot，并可选的进行一些配置。
var bot = BotFactory.create(botId, botSecret, botToken, config -> {
    ...
});
```

**直接提供配置类**

作为 DSL 的配置类也可以独立构建后直接提供。

```Java
// 准备 bot 的必要信息
var botId = "xxxx";
var botSecret = ""; // secret 如果用不到可使用空字符串
var botToken = "xxxx";

var configuration = new ConfigurableBotConfiguration();

// 构建一个 Bot，并可选的进行一些配置。
var bot = BotFactory.create(botId, botSecret, botToken, configuration);
```

</tab>
</tabs>

### 订阅事件 {id='bot-subscribe-event'}

你可以通过 `registerProcessor` 来订阅全部或指定类型的事件。

<tip>

标准库模块中可使用的事件列表可以在
<a href="event.md" />
中或API文档中找到。
当然，借助IDE的智能提示也是不错的选择。

</tip>

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

**订阅全部事件**

使用 `registerProcessor` 注册一个普通的事件处理器，此处理器会接收并处理所有类型的事件。

```Kotlin
bot.registerProcessor { raw ->
    // raw 代表事件的原始JSON字符串
    // this: Signal.Dispatch, 也就是解析出来的事件结构体
    println("event: $this")
    println("event.data: $data")
    println("raw: $raw")
}
```

**订阅指定类型的事件**

使用扩展函数 `registerProcessor` 注册一个针对具体 `Signal.Dispatch` 事件类型的事件处理器，
它只有在接收到的 `Signal.Dispatch` 与目标类型一致时才会处理。

此示例展示处理 `AtMessageCreate` 也就公域是消息事件，
并在对方发送了包含 `"stop"` 的文本时终止 bot。

```Kotlin
// 注册一个普通的事件处理器，此处理器会接收并处理所有类型的事件
bot.process<AtMessageCreate> {
    if ("stop" in data.content) {
        // 终止 bot
        bot.cancel()
    }
}
```

</tab>
<tab title="Java" group-key="Java">

**订阅全部事件**

使用 `registerProcessor` 注册一个普通的事件处理器，此处理器会接收并处理所有类型的事件。

```Java
bot.registerProcessor(EventProcessors.async((event, raw) -> {
    // raw 代表事件的原始JSON字符串
    // event: Signal.Dispatch, 也就是解析出来的事件结构体
    System.out.println("event: " + event);
    System.out.println("event.data: " + event.getData());
    System.out.println("raw: " + raw);

    // 异步处理器必须返回 CompletableFuture
    return CompletableFuture.completedFuture(null);
}));
```
{switcher-key="%ja%"}

```Java
bot.registerProcessor(EventProcessors.block((event, raw) -> {
    // raw 代表事件的原始JSON字符串
    // event: Signal.Dispatch, 也就是解析出来的事件结构体
    System.out.println("event: " + event);
    System.out.println("event.data: " + event.getData());
    System.out.println("raw: " + raw);
}));
```
{switcher-key="%jb%"}

**订阅指定类型的事件**

使用 `registerProcessor` 注册一个指定了具体类型的事件处理器，
它只有在接收到的 `Signal.Dispatch` 与目标类型一致时才会处理。

此示例展示处理 `AtMessageCreate` 也就公域是消息事件，
并在对方发送了包含 `"stop"` 的文本时终止 bot。

```Java
bot.registerProcessor(EventProcessors.async(AtMessageCreate.class, (event, raw) -> {
    if (event.getData().getContent().contains("stop")) {
        // 终止 bot
        bot.cancel();
    }
    
    return CompletableFuture.completedFuture(null);
}));
```
{switcher-key="%ja%"}

```Java
bot.registerProcessor(EventProcessors.block(AtMessageCreate.class, (event, raw) -> {
    if (event.getData().getContent().contains("stop")) {
        // 终止 bot
        bot.cancel();
    }
}));
```
{switcher-key="%jb%"}


</tab>
</tabs>

### 启动 Bot {id='bot-start'}

在 `Bot` 被启动之前，它不会与服务器建立连接，也不会收到并处理事件。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
bot.start() // 启动bot
bot.join()  // 挂起并直到bot被关闭
```

</tab>
<tab title="Java" group-key="Java">

```Java
var future = bot.startAsync() // // 启动bot
        .thenCompose(r -> bot.asFuture()); // 转为直到bot被终止后结束的 future

// 阻塞线程或者怎么样都行
future.join();
```
{switcher-key="%ja%"}

```Java
// 启动bot
bot.startBlocking();
// 阻塞当前线程，直到bot被终止。
bot.joinBlocking();
```
{switcher-key="%jb%"}

</tab>
</tabs>

### 关闭 Bot {id='bot-cancel'}

使用 `cancel` 即可关闭一个 Bot。被关闭的 Bot 不能再次启动。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
bot.cancel()
bot.cancel(reason) // 或可以提供一个 reason: Throwable
```

</tab>
<tab title="Java" group-key="Java">

```Java
bot.cancel();
bot.cancel(reason); // 或可以提供一个 Throwable reason
```

</tab>
</tabs>

## 组件库 QGBot

当你在配合 simbot 使用组件库(`simbot-component-qq-guild-core`)的时候，
你可能会更需要了解 `love.forte.simbot.component.qguild.bot.QGBot`。
它是作为一个 simbot 组件库的 `Bot` 的实现
(simbot标准API中的 `Bot` 接口，不是上面提到的 stdlib bot)。

<tip>

有关 simbot 标准API的更多信息，
可前往 [Simple Robot 应用手册](https://simbot.forte.love) ，
此处不会过多赘述。

</tip>

### 源 stdlib Bot {id='qgbot-source-bot'}

组件库的 `QGBot` 是在上文提到过的
<a href="#stdlib-bot" />
的基础上进行构建与扩展的。

你可以在 `QGBot` 中通过属性 `source` 获取到其对应的源 `Bot`。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val sourceBot = bot.source
```

</tab>
<tab title="Java" group-key="Java">

```Java
var sourceBot = bot.getSource();
```

</tab>
</tabs>

### 构建 QGBot {id='create-qgbot'}

TODO



### 监听事件 {id='qgbot-listen-event'}

实际上，在组件库中监听事件与某个具体的 Bot 无关。
你可以前往参考 Simple Robot 应用手册:
<a href="https://simbot.forte.love/basic-event-listener.html">事件监听与处理</a>。

### 频道操作 {id='qgbot-guild'}

TODO

### 消息发送 {id='qgbot-send-to'}

除了使用频道中获取到的子频道对象 `QGChannel` 以外，
`QGBot` 本身提供了一些可以跳过获取频道这一环节、直接根据 `ID` 发送消息的 API 
`QGBot.sendTo`。

> 这也是为了避免能够发送消息、但是没有查看频道信息权限的情况。

TODO

### Spring Boot

如果你使用 simbot 配合 Spring Boot 来使用QQ频道组件，
那么你可以前往：

<list>
<li>
<a href="use-spring-boot.md"/>: 了解如何开始一个项目
</li>
<li>
<a href="bot-config.md"/>: 了解如何配置 Bot 信息
</li>
<li>
<a href="https://simbot.forte.love/start-use-spring-boot-3.html">Simple Robot 应用手册</a>
了解使用 Spring Boot 的更多信息。
</li>
</list>
