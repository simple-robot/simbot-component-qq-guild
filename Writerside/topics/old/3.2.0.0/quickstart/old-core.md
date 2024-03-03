# 使用simbot核心库

通过**core模块**配合**simbot核心库**来使用强大而简单的事件调度能力。

<note title="这才是组件库">

core模块亦可以称之为simbot3的**组件库**，因为它事实上实现了simbot所提供的各种API和对simbot事件类型的封装。

</note>

## 前提准备

首先你应当准备至少一个可用的 [QQ频道机器人](https://q.qq.com/bot) 。

<tip title="Java或许更喜欢注解">

simbot核心库中的API大多是以**DSL风格**为主的。尽管其依旧是对 Java "友好", 但以 Java 的代码风格而言依旧会 _略显臃肿_。

如果你不介意，我们更建议 Java 开发者直接使用 [SpringBoot](old-spring-boot.md)，因为这更符合大多数 Java 开发者的习惯。

你也可以观察后续的代码示例，来体会DSL风格的API在 Kotlin 和 Java 之间的差异。

</tip>

## 安装

<tabs group="use-dependency">
<tab title="Gradle Kotlin DSL" group-key="Kts">

```kotlin
// simbot core starter  
implementation("love.forte.simbot:simbot-core:3.3.0") // 版本请参考下文的参考链接
// QQ频道组件  
implementation("love.forte.simbot.component:simbot-component-qq-guild-core:3.2.0.0") // 或参考下文的参考链接
```

</tab>
<tab title="Gradle Groovy" group-key="Gradle">

```gradle
// simbot core starter  
implementation 'love.forte.simbot:simbot-core:3.3.0' // 版本请参考下文的参考链接
// QQ频道组件  
implementation 'love.forte.simbot.component:simbot-component-qq-guild-core:3.2.0.0' // 或参考下文的参考链接
```

</tab>
<tab title="Maven" group-key="Maven">

```xml
<!-- simbot core -->
<dependency>
    <groupId>love.forte.simbot</groupId>
    <artifactId>simbot-core</artifactId>
    <version>\${SIMBOT_VERSION}</version><!-- 版本请参考下文的参考链接 -->
</dependency>
        <!-- QQ频道组件 -->
<dependency>
<groupId>love.forte.simbot.component</groupId>
<artifactId>simbot-component-qq-guild-core</artifactId>
<version>3.2.0.0</version><!-- 或参考下文的参考链接 -->
</dependency>

```

</tab>
</Tabs>




<tip title="版本参考">

simbot核心库(`simbot-core`)的版本可前往 [**simbot Releases**](https://github.com/simple-robot/simpler-robot/releases)
查阅。

QQ频道组件版本可前往 [**Releases**](https://github.com/simple-robot/simbot-component-qq-guild/releases) 查阅。


</tip>

## 构建Application

首先构建一个 simbot 的 `Application`，这里以 `SimpleApplication` (也就是最基础的实现) 为例:

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```kotlin title='com.example.App'
val application = createSimpleApplication {
    // 安装QQ频道相关的组件和Bot管理器，并暂且忽略配置
    install(QQGuildComponent)
    install(QQGuildBotManager)
}

application.join() // 挂起直到被关闭
```

或使用扩展函数来简化上述代码:

```kotlin title='com.example.App'
val application = createSimpleApplication {
    // 安装QQ频道相关的组件和Bot管理器，并暂且忽略配置
    useQQGuild()
}

application.join() // 挂起直到被关闭
```

</tab>
<tab title="Java" group-key="Java">

```java title='com.example.App'
final ApplicationLauncher<SimpleApplication> launcher = Applications.simbotApplication(Simple.INSTANCE, c -> { /* Application配置 */ }, (builder, configuration) -> {
    // 安装QQ频道相关的组件和Bot管理器，并暂且忽略配置
    builder.install(QQGuildComponent.Factory, (__, ___) -> Unit.INSTANCE);
    builder.install(QQGuildBotManager.Factory, (__, ___) -> Unit.INSTANCE);
});

// 启动 application
SimpleApplication application = launcher.launchBlocking();
application.

joinBlocking(); // 阻塞直到被关闭
```

</tab>
<tab title="Java Async" group-key="Java">

```java title='com.example.App'
final ApplicationLauncher<SimpleApplication> launcher = Applications.simbotApplication(Simple.INSTANCE, c -> { /* Application配置 */ }, (builder, configuration) -> {
    // 安装QQ频道相关的组件和Bot管理器，并暂且忽略配置
    builder.install(QQGuildComponent.Factory, (__, ___) -> Unit.INSTANCE);
    builder.install(QQGuildBotManager.Factory, (__, ___) -> Unit.INSTANCE);
});

// 启动 application, 结束后转化为 Future 并阻塞直到完成(application被关闭)
launcher.

launchAsync().

thenCompose(SimpleApplication::asFuture).

join();
```

</tab>

</Tabs>

## 监听函数

完成对Application的构建之后，我们便可以开始注册监听函数用以处理事件了。

下面我们实现如此功能: 当收到一个**(公域)子频道消息**，且内容为 "你好"，则**引用回复**一句 "你也好"。
流程大致为:

```text
用户: 
@BOT 你好

BOT:
> 用户: @BOT 你好
你也好
```

> 在公域中的BOT要想收到消息必须被 `@` 。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```kotlin title='com.example.App'
val application = createSimpleApplication {
    // 安装QQ频道相关的组件和Bot管理器，并暂且忽略配置
    install(QQGuildComponent)
    install(QQGuildBotManager)
}

// 获取 eventListenerManager 并注册监听函数
application.eventListenerManager.listeners {
    // highlight-start
    ChannelMessageEvent { event ->
        // 当下述条件满足，则此处才被执行，并引用回复一句 '你也好'
        event.reply("你也好")
    } onMatch { event ->
        // 匹配判断: 当消息中存在任何 AT 消息，且at的目标是bot，且消息文本为 '你好' 时
        val bot = event.bot
        event.messageContent.messages.any { it is At && bot.isMe(it.target) }
                && event.messageContent.plainText.trim() == "你好"
    }
    // highlight-end
}

application.join() // 挂起直到被关闭
```

</tab>
<tab title="Java" group-key="Java">

```java title='com.example.App'
final ApplicationLauncher<SimpleApplication> launcher = Applications.simbotApplication(Simple.INSTANCE, c -> { /* Application配置 */ }, (builder, configuration) -> {
    // 安装QQ频道相关的组件和Bot管理器，并暂且忽略配置
    builder.install(QQGuildComponent.Factory, (__, ___) -> Unit.INSTANCE);
    builder.install(QQGuildBotManager.Factory, (__, ___) -> Unit.INSTANCE);
});

// 启动 application
SimpleApplication application = launcher.launchBlocking();

// 获取 EventListenerManager 并用于注册监听函数
EventListenerManager eventListenerManager = application.getEventListenerManager();

// 注册一个 EventListener 实例，此实例通过 SimpleListeners.listener(...) 构建而得。
// highlight-start
eventListenerManager.

register(SimpleListeners.listener(ChannelMessageEvent.Key, (context, event) ->{
// 匹配判断: 当消息中存在任何 AT 消息，且at的目标是bot，且消息文本为 '你好' 时
Bot bot = event.getBot();
boolean atBot = false;
MessageContent messageContent = event.getMessageContent();
    for(
Message.Element<?> message :messageContent.

getMessages()){
        if(message instanceof
At at &&bot.

isMe(at.getTarget())){
atBot =true;
        break;
        }
        }

        return atBot &&"你好".

equals(messageContent.getPlainText().

trim());
        },(context,event)->{
        // 当上面的匹配通过，则此处才被执行，并引用回复一句 '你也好'
        event.

replyBlocking("你也好");
}));
// highlight-end

        application.

joinBlocking(); // 阻塞直到被关闭
```

</tab>
<tab title="Java Async" group-key="Java">

```java title='com.example.App'
final ApplicationLauncher<SimpleApplication> launcher = Applications.simbotApplication(Simple.INSTANCE, c -> { /* Application配置 */ }, (builder, configuration) -> {
    // 安装QQ频道相关的组件和Bot管理器，并暂且忽略配置
    builder.install(QQGuildComponent.Factory, (__, ___) -> Unit.INSTANCE);
    builder.install(QQGuildBotManager.Factory, (__, ___) -> Unit.INSTANCE);
});

// 启动 application 结束后转化为 Future 并阻塞直到完成(application被关闭)
launcher.

launchAsync().

thenCompose(application ->{
SimpleEventListenerManager eventListenerManager = application.getEventListenerManager();
// highlight-start
    eventListenerManager.

register(SimpleListeners.listener(ChannelMessageEvent.Key, (context, event) ->{
// 匹配判断: 当消息中存在任何 AT 消息，且at的目标是bot，且消息文本为 '你好' 时
Bot bot = event.getBot();
boolean atBot = false;
MessageContent messageContent = event.getMessageContent();
        for(
Message.Element<?> message :messageContent.

getMessages()){
        if(message instanceof
At at &&bot.

isMe(at.getTarget())){
atBot =true;
        break;
        }
        }

        return atBot &&"你好".

equals(messageContent.getPlainText().

trim());
        },(context,event)->{
// 当上面的匹配通过，则此处才被执行，并引用回复一句 '你也好'
CompletableFuture<? extends MessageReceipt> replyAsync = event.replyAsync("你也好");
        return EventResult.

of(replyAsync); // 通过 EventResult.of(future) 将你的异步结果返回，事件处理器会挂起并处理此异步结果
    }));
            // highlight-end

            return application.

asFuture();
}).

join();
```

</tab>
</Tabs>


> 其实上述示例中，在BOT为公域的情况下也可以不去判断 `At` 消息类型，直接判断 `plainText` 是否为 `"你好"` 即可。

## 注册bot

当监听函数注册完了之后，我们就需要注册bot了。当bot被注册并启动后，它们收到的事件就会流入到Application中的事件处理器中被统一处理。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```kotlin title='com.example.App'
val application = createSimpleApplication {
    // 安装QQ频道相关的组件和Bot管理器，并暂且忽略配置
    install(QQGuildComponent)
    install(QQGuildBotManager)
}

application.eventListenerManager.listeners {
    // 注册事件...
}

// 寻找到一个 QQGuildBotManager 并注册bot
// highlight-start
val qqGuildBotManager = application.botManagers.first { it is QQGuildBotManager } as QQGuildBotManager
val bot: QGBot = qqGuildBotManager.register("APP ID", "secret", "token") { // this: QGBotComponentConfiguration
    // **组件BOT**的额外配置信息
    this.cacheConfig = CacheConfig(enable = true, TransmitCacheConfig(enable = true)) // 启用 '传递性缓存', 此缓存配置默认启用
    // 其他额外配置...

    botConfig {
        // **此处为对应 stdlib bot 的配置信息
    }
}
bot.start() // 启用bot
// highlight-end

application.join() // 挂起直到被关闭
```

或者通过扩展函数来简化上述代码:

```kotlin
val application = createSimpleApplication {
    // 安装QQ频道相关的组件和Bot管理器，并暂且忽略配置
    install(QQGuildComponent)
    install(QQGuildBotManager)
}

application.eventListenerManager.listeners {
    // 注册监听函数...
}

// 寻找到一个 QQGuildBotManager 并注册bot
// highlight-start
application.qgGuildBots {
    val bot: QGBot = register("APP ID", "secret", "token") { // this: QGBotComponentConfiguration
        // **组件BOT**的额外配置信息
        this.cacheConfig = CacheConfig(enable = true, TransmitCacheConfig(enable = true)) // 启用 '传递性缓存', 此缓存配置默认启用
        // 其他额外配置...

        botConfig {
            // **此处为对应 stdlib bot 的配置信息
        }
    }
    bot.start() // 启用bot
}
// highlight-end

application.join() // 挂起直到被关闭
```

</tab>
<tab title="Java" group-key="Java">

```java title='com.example.App'
final ApplicationLauncher<SimpleApplication> launcher = Applications.simbotApplication(Simple.INSTANCE, c -> { /* Application配置 */ }, (builder, configuration) -> {
    // 安装QQ频道相关的组件和Bot管理器，并暂且忽略配置
    builder.install(QQGuildComponent.Factory, (__, ___) -> Unit.INSTANCE);
    builder.install(QQGuildBotManager.Factory, (__, ___) -> Unit.INSTANCE);
});

// 启动 application
SimpleApplication application = launcher.launchBlocking();
// 注册监听函数...

// highlight-start
for(
BotManager<?> botManager :application.

getBotManagers()){
        // 找到第一个 QQGuildBotManager 并对其进行操作
        if(botManager instanceof
QQGuildBotManager qqGuildBotManager){
QGBot bot = qqGuildBotManager.register("APP ID", "secret", "token", configuration -> {
    // **组件BOT**的额外配置信息
    configuration.setCacheConfig(...);
    configuration.botConfig(botConfig -> {
        // **此处为对应 stdlib bot 的配置信息
        return Unit.INSTANCE; // 结束配置
    });

    return Unit.INSTANCE; // 结束配置
});

        bot.

startBlocking(); // 阻塞启动

        break;
                }
                }
// highlight-end

                application.

joinBlocking(); // 阻塞直到被关闭
```

</tab>
<tab title="Java Async" group-key="Java">

```java title='com.example.App'
final ApplicationLauncher<SimpleApplication> launcher = Applications.simbotApplication(Simple.INSTANCE, c -> { /* Application配置 */ }, (builder, configuration) -> {
    // 安装QQ频道相关的组件和Bot管理器，并暂且忽略配置
    builder.install(QQGuildComponent.Factory, (__, ___) -> Unit.INSTANCE);
    builder.install(QQGuildBotManager.Factory, (__, ___) -> Unit.INSTANCE);
});

// 启动 application 结束后转化为 Future 并阻塞直到完成(application被关闭)
launcher.

launchAsync().

thenCompose(application ->{
        // 注册监听函数...
        // highlight-start
        for(
BotManager<?> botManager :application.

getBotManagers()){
        // 找到第一个 QQGuildBotManager 并操作
        if(botManager instanceof
QQGuildBotManager qqGuildBotManager){
QGBot bot = qqGuildBotManager.register("APP ID", "secret", "token", configuration -> {
    // **组件BOT**的额外配置信息
    configuration.setCacheConfig(...);
    configuration.botConfig(botConfig -> {
        // **此处为对应 stdlib bot 的配置信息
        return Unit.INSTANCE; // 结束配置
    });

    return Unit.INSTANCE; // 结束配置
});

            bot.

startAsync(); // 异步启动。此处异步直接放养，如果需要处理异常等则需要进行操作。

            break;
                    }
                    }
                    // highlight-end

                    return application.

asFuture();
}).

join();
```

</tab>
</Tabs>

## 启动

接下来，启动程序并在你的沙箱频道中@它试试看吧。

当然，如果遇到了预期外的问题也不要慌，积极反馈问题才能使我们变得更好，可以前往 [Issues](https://github.com/simple-robot/simpler-robot/issues)
反馈问题、[社区](https://github.com/orgs/simple-robot/discussions) 提出疑问。
