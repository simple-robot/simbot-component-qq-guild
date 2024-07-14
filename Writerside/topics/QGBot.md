---
switcher-label: Java API 风格
---
<show-structure for="chapter,procedure" depth="3"/>


# QGBot

<include from="snippets.md" element-id="to-main-doc" />

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
// 构建一个 Bot，并可选地进行一些配置。
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
// 构建一个 Bot，并可选地进行一些配置。
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

// 构建一个 Bot，并可选地进行一些配置。
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

// 构建一个 Bot，并可选地进行一些配置。
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

// 构建一个 Bot，并可选地进行一些配置。
var bot = BotFactory.create(botId, botSecret, botToken, configuration);
```

</tab>
</tabs>

### 订阅事件 {id='bot-subscribe-event'}

你可以通过 `subscribe` 来订阅全部或指定类型的事件。

<tip>

标准库模块中可使用的事件列表可以在
<a href="event.md" />
中或API文档中找到。
当然，借助IDE的智能提示也是不错的选择。

</tip>

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

**订阅全部事件**

使用 `subscribe` 注册一个普通的事件处理器，此处理器会接收并处理所有类型的事件。

```Kotlin
bot.subscribe { raw ->
    // raw 代表事件的原始JSON字符串
    // this: Signal.Dispatch, 也就是解析出来的事件结构体
    println("event: $this")
    println("event.data: $data")
    println("raw: $raw")
}
```

**订阅指定类型的事件**

使用扩展函数 `subscribe` 注册一个针对具体 `Signal.Dispatch` 事件类型的事件处理器，
它只有在接收到的 `Signal.Dispatch` 与目标类型一致时才会处理。

此示例展示处理 `AtMessageCreate` 也就公域是消息事件，
并在对方发送了包含 `"stop"` 的文本时终止 bot。

```Kotlin
// 注册一个普通的事件处理器，此处理器会接收并处理所有类型的事件
bot.subscribe<AtMessageCreate> {
    if ("stop" in data.content) {
        // 终止 bot
        bot.cancel()
    }
}
```

</tab>
<tab title="Java" group-key="Java">

**订阅全部事件**

使用 `subscribe` 注册一个普通的事件处理器，此处理器会接收并处理所有类型的事件。

```Java
bot.subscribe(EventProcessors.async((event, raw) -> {
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
bot.subscribe(EventProcessors.block((event, raw) -> {
    // raw 代表事件的原始JSON字符串
    // event: Signal.Dispatch, 也就是解析出来的事件结构体
    System.out.println("event: " + event);
    System.out.println("event.data: " + event.getData());
    System.out.println("raw: " + raw);
}));
```
{switcher-key="%jb%"}

**订阅指定类型的事件**

使用 `subscribe` 注册一个指定了具体类型的事件处理器，
它只有在接收到的 `Signal.Dispatch` 与目标类型一致时才会处理。

此示例展示处理 `AtMessageCreate` 也就公域是消息事件，
并在对方发送了包含 `"stop"` 的文本时终止 bot。

```Java
bot.subscribe(EventProcessors.async(AtMessageCreate.class, (event, raw) -> {
    if (event.getData().getContent().contains("stop")) {
        // 终止 bot
        bot.cancel();
    }
    
    return CompletableFuture.completedFuture(null);
}));
```
{switcher-key="%ja%"}

```Java
bot.subscribe(EventProcessors.block(AtMessageCreate.class, (event, raw) -> {
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

在 `Application` 中安装 `QQGuildBotManager` 后即可使用其注册、启动一个 `QGBot` 了。 

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

**安装 QQGuildBotManager**

> 此处我们以使用 `Simple` Application 实现为例。

```Kotlin
val app = launchSimpleApplication {
    install(QQGuildComponent) // 别忘了安装 Component 标识
    install(QQGuildBotManager) {
        // 可选地。进行一些配置，比如一个所有 Bot 共享的父级 CoroutineContext
    }
}
```

Kotlin 中可以选择使用扩展函数 `useQQGuild` 来简化代码：

```Kotlin
val app = launchSimpleApplication {
        // 同时安装组件标识和BotManager
        useQQGuild()
        // 或..
        // 可选地进行一些配置
        useQQGuild {

            // 可选地对 BotManager 进行一些配置
            botManager {
                // ...
            }
        }
    }
```

</tab>
<tab title="Java" group-key="Java">

```Java
var appAsync = Applications.launchApplicationAsync(Simple.INSTANCE, builder -> {
    builder.install(QQGuildComponent.Factory);
    builder.install(QQGuildBotManager.Factory);
});

// 可以转化为 CompletableFuture 然后进行一些操作
var appFuture = appAsync.asFuture();
```
{switcher-key='%ja%'}

```Java
var app = Applications.launchApplicationBlocking(Simple.INSTANCE, builder -> {
    builder.install(QQGuildComponent.Factory);
    builder.install(QQGuildBotManager.Factory);
});
```
{switcher-key='%jb%'}

你可以可选地进行一些配置。

```Java
var appAsync = Applications.launchApplicationAsync(Simple.INSTANCE, builder -> {
    builder.install(QQGuildComponent.Factory);
    builder.install(QQGuildBotManager.Factory, config -> {
        // 比如让所有的 Bot 都默认的使用一个 4 守护线程的线程池
        // 这里仅供示例，真实配置请按照项目实际情况来。
        config.setCoroutineContext(
                ExecutorsKt.from(Executors.newFixedThreadPool(4, r -> {
                    final var t = new Thread(r);
                    t.setDaemon(true);
                    return t;
                }))
        );
    });
});

// 可以转化为 CompletableFuture 然后进行一些操作
var appFuture = appAsync.asFuture();
```
{switcher-key='%ja%'}

```Java
var app = Applications.launchApplicationBlocking(Simple.INSTANCE, builder -> {
    builder.install(QQGuildComponent.Factory);
    builder.install(QQGuildBotManager.Factory, config -> {
        // 比如让所有的 Bot 都默认使用虚拟线程线程池，
        // 这样就可以在事件处理器中相对更安全的使用 **阻塞API** 了。
        // 这里仅供示例，真实配置请按照项目实际情况来。
        config.setCoroutineContext(
                ExecutorsKt.from(Executors.newVirtualThreadPerTaskExecutor())
        );
    });
});
```
{switcher-key='%jb%'}

</tab>
</tabs>

构建完 `Application` 后，我们寻找 `QQGuildBotManager`，并注册、启动一个 bot。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val app = launchSimpleApplication {
    useQQGuild()
}

val bot = app.botManagers.get<QQGuildBotManager>()
    .register("APP ID", "SECRET", "TOKEN") {
        // 可选地进行一些配置
    }

// 启动它
bot.start()

// 挂起它
bot.join()

// 或者直接挂起 app
app.join()
```

Kotlin 中也可以使用扩展函数 `qgGuildBots` 来简化操作。

```Kotlin
val app = launchSimpleApplication {
    useQQGuild()
}

app.qgGuildBots {
    val bot = register("APP ID", "SECRET", "TOKEN") {
        // 可选地进行一些配置
    }
    // 启动 bot
    bot.start()
}

// 挂起 app
app.join()
```

</tab>
<tab title="Java" group-key="Java">

```Java
var appAsync = Applications.launchApplicationAsync(Simple.INSTANCE, builder -> {
    builder.install(QQGuildComponent.Factory);
    builder.install(QQGuildBotManager.Factory);
});

var future = appAsync.asFuture()
        .thenCompose(app -> {
            // 找到 QQGuildBotManager
            var botManager = app.getBotManagers().stream().filter(it -> it instanceof QQGuildBotManager)
                    .map(it -> (QQGuildBotManager) it)
                    .findFirst()
                    .orElseThrow();

            // 注册 bot
            var bot = botManager.register("APP ID", "SECRET", "TOKEN", config -> {
                // 可选地进行一些配置
            });

            // 启动 bot,
            // 启动完 bot 后，返回 app 作为 future，并在外部直接 join app.
            return bot.startAsync()
                    .thenCompose(($) -> app.asFuture());
        });

future.join();
```
{switcher-key='%ja%'}


```Java
var app = Applications.launchApplicationBlocking(Simple.INSTANCE, builder -> {
    builder.install(QQGuildComponent.Factory);
    builder.install(QQGuildBotManager.Factory);
});

// 找到 QQGuildBotManager
var botManager = app.getBotManagers().stream().filter(it -> it instanceof QQGuildBotManager)
        .map(it -> (QQGuildBotManager) it)
        .findFirst()
        .orElseThrow();

// 注册 bot
var bot = botManager.register("APP ID", "SECRET", "TOKEN", config -> {
    // 可选地进行一些配置
});

// 启动 bot
bot.startBlocking();

// 阻塞bot
bot.joinBlocking();

// 或直接阻塞 app
app.joinBlocking();
```
{switcher-key='%jb%'}

</tab>
</tabs>


### 监听事件 {id='qgbot-listen-event'}

实际上，在组件库中监听事件与某个具体的 Bot 无关。
你可以前往参考 Simple Robot 应用手册:
<a href="https://simbot.forte.love/basic-event-listener.html">事件监听与处理</a>。

你可以在
<a href="event.md#component-events" />
或API文档中找到所有可用于 simbot 中的事件类型。它们大多与 API 模块中定义的事件类型有一些对应规则。

此处使用 `公域消息事件 QGAtMessageCreateEvent` 作为例子:

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val app = launchSimpleApplication {
    useQQGuild()
}

// 注册一个事件处理器，处理 QGAtMessageCreateEvent 类型的事件
app.eventDispatcher.listen<QGAtMessageCreateEvent> { atMessageEvent -> // QGAtMessageCreateEvent
    // this: EventListenerContext
    println("Event: $atMessageEvent")
    println("Context: $this")

    // result.
    EventResult.empty()
}

// 注册 bot...
app.qgGuildBots { ... }

app.join()
```

</tab>
<tab title="Java" group-key="Java">

```Java
var appAsync = Applications.launchApplicationAsync(Simple.INSTANCE, builder -> {
    builder.install(QQGuildComponent.Factory);
    builder.install(QQGuildBotManager.Factory);
});

var future = appAsync.asFuture()
    .thenCompose(app -> {
        app.getEventDispatcher().register(
                EventListeners.async(
                        QGAtMessageCreateEvent.class,
                        (context, event) -> {
                            System.out.println("Event: " + event);
                            System.out.println("Context: " + context);
                            // 返回异步结果
                            return CompletableFuture.completedFuture(EventResult.empty());
                        }
                )
        );
        // 注册 bot
        var bot = ...

        // 启动 bot, 或者之类的各种操作
        return ...
    });

future.join();

```
{switcher-key='%ja%'}

```Java
var app = Applications.launchApplicationBlocking(Simple.INSTANCE, builder -> {
    builder.install(QQGuildComponent.Factory);
    builder.install(QQGuildBotManager.Factory, config -> {
        // 如果使用完全的阻塞API，建议配置调度器为虚拟线程调度器，来更好的避免线程匮乏的问题。
        // 这里仅供示例，真实配置请按照项目实际情况来。
        config.setCoroutineContext(
                ExecutorsKt.from(Executors.newVirtualThreadPerTaskExecutor())
        );
    });
});

// 注册一个事件处理器，处理 QGAtMessageCreateEvent 类型的事件
app.getEventDispatcher().register(EventListeners.block(
        QGAtMessageCreateEvent.class,
        (context, event) -> {
            System.out.println("Event: " + event);
            System.out.println("Context: " + context);
            return EventResult.empty();
        }
));
// 注册bot...
app.getBotManagers()...

var bot = ...
bot.startBlocking();

// 阻塞 app
app.joinBlocking();
```
{switcher-key='%jb%'}

</tab>
</tabs>

### 频道操作 {id='qgbot-guild'}

对频道 (`QGGuild`) 以及之下的子频道(`QGChannel`)、频道成员(`QGMember`) 的操作，
都是在 `QGBot` 的 `guildRelation` 中开始的。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val bot: QGBot = ...
val guildRelation = bot.guildRelation
```

</tab>
<tab title="Java" group-key="Java">

```Java
QGBot bot = ...
var guildRelation = bot.getGuildRelation()
```

</tab>
</tabs>

通过获取到的 `QGGuildRelation`，可以用来获取 `QGGuild`、`QGChannel` 等信息。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val guildRelation = bot.guildRelation

// 寻找指定ID的频道服务器
val guild = guildRelation.guild(1234L.ID)

// 获取全部 QGGuild 的流
guildRelation.guilds
    .asFlow() // 可以转成 Flow
    .collect { ... }

// 额外提供可以指定批次数量和 lastId 的API
guildRelation.guilds(lastId = null, batch = 100)
    .asFlow() // 可以转成 Flow
    .collect { ... }

// 额外提供了一些直接查询子频道的API
// 寻找指定ID的子频道
val channel = guildRelation.channel(1234L.ID)
// 寻找指定ID的聊天子频道，可用来发消息的那种
val chatChannel = guildRelation.chatChannel(1234L.ID)
// 寻找指定ID的帖子子频道
val forumChannel = guildRelation.forumChannel(1234L.ID)
```

</tab>
<tab title="Java" group-key="Java">

```Java
final var guildRelation = bot.getGuildRelation();

// 寻找指定ID的频道服务器
guildRelation.getGuildAsync(Identifies.of(1234L))
        .thenAccept(guild -> { ... });
// 获取全部 QGGuild 的流
guildRelation.getGuilds()
        // 可以转化成 Flux
        .transform(SuspendReserves.flux())
        .subscribe(guild -> { ... });

// 额外提供可以指定批次数量和 lastId 的API
guildRelation.guilds(null, 100)
        // 可以转化成 Flux
        .transform(SuspendReserves.flux())
        .subscribe(guild -> { ... });

// 额外提供了一些直接查询子频道的API
// 寻找指定ID的子频道
guildRelation.getChannelAsync(Identifies.of(1234L))
        .thenAccept(channel -> { ... });
// 寻找指定ID的聊天子频道，可用来发消息的那种
guildRelation.getChatChannelAsync(Identifies.of(1234L))
        .thenAccept(chatChannel -> { ... });
// 寻找指定ID的帖子子频道
guildRelation.getForumChannelAsync(Identifies.of(1234L))
        .thenAccept(forumChannel -> { ... });
```
{switcher-key='%ja%'}


```Java
        var guildRelation = bot.getGuildRelation();

// 寻找指定ID的频道服务器
var guildValue = guildRelation.getGuild(Identifies.of(1234L));

// 获取全部 QGGuild 的流
var guilds = guildRelation.getGuilds();
// 可以转成 stream 或者 list
        Collectables.asStream(guilds)
                .forEach(guild -> { ... });

// 额外提供可以指定批次数量和 lastId 的API
var guildList = guildRelation.guilds(null, 100)
        // 可以转化成 list
        .transform(SuspendReserves.list());

// 额外提供了一些直接查询子频道的API
// 寻找指定ID的子频道
var channel = guildRelation.getChannel(Identifies.of(1234L))
// 寻找指定ID的聊天子频道，可用来发消息的那种
var chatChannel = guildRelation.getChatChannel(Identifies.of(1234L))
// 寻找指定ID的帖子子频道
var forumChannel = guildRelation.getForumChannel(Identifies.of(1234L))
```
{switcher-key='%jb%'}

</tab>
</tabs>

### 消息发送 {id='qgbot-send-to'}

除了使用频道中获取到的子频道对象 `QGChannel` 的 `send` 直接发送、
使用消息事件中的 `reply` 发送消息等方式以外，
`QGBot` 本身提供了一些可以跳过获取频道这一环节、直接根据 `ID` 发送消息的 API 
`QGBot.sendTo`。

> 这也是为了避免能够发送消息、但是没有查看频道信息权限的情况。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val bot: QGBot = ...
bot.sendTo("channel id".ID, "消息内容")
bot.sendTo("channel id".ID, "消息内容".toText() + At("user id".ID))
```

</tab>
<tab title="Java" group-key="Java">

<if switcher-key="%ja%">

```Java
QGBot bot = ...

var sendTask1 = bot.sendToAsync(Identifies.of("channel id"), "消息内容");
var sendTask2 = bot.sendToAsync(Identifies.of("channel id"), Messages.of(
        Text.of("文本消息"),
        At.of(Identifies.of("user id"))
));

// 如果有需要，记得异常处理
sendTask1.exceptionally(err -> ...);
sendTask2.exceptionally(err -> ...);
```

或者有顺序地执行这两个任务：

```Java
QGBot bot = ...

var sendTask = bot.sendToAsync(Identifies.of("channel id"), "消息内容")
        .thenCompose(($) -> bot.sendToAsync(Identifies.of("channel id"), Messages.of(
                Text.of("文本消息"),
                At.of(Identifies.of("user id"))
        )));
```

如果希望在事件处理器中，处理器需要等待所有异步任务完成后再进行下一个处理器，则返回这个 future，
否则会不会等待。

```Java
QGBot bot = ...

var sendTask1 = bot.sendToAsync(...);
var sendTask2 = bot.sendToAsync(...);

return CompletableFuture.allOf(sendTask1, sendTask2)
        .thenApply($ -> EventResult.empty()); // 任务全部完成后，返回事件结果
```

</if>

<if switcher-key="%jb%">

```Java
QGBot bot = ...

bot.sendToBlocking(Identifies.of("channel id"), "消息内容");
bot.sendToBlocking(Identifies.of("channel id"), Messages.of(
        Text.of("文本消息"),
        At.of(Identifies.of("user id"))
));
```

</if>

</tab>
</tabs>

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
