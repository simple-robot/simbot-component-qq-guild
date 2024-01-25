---
switcher-label: Java API 风格
---

# 论坛 Forum

QQ频道中有一些针对 `论坛子频道` 的API。([参考文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread))


## API

首先是 `API` 模块中对相关API的封装类型，它们在 `love.forte.simbot.qguild.api.forum` 中：

- `DeleteThreadApi`
- `GetThreadApi`
- `GetThreadListApi`
- `PublishThreadApi`

使用它们的方式都差不多，我们选其中一个 `GetThreadListApi` 作为示例：

<tabs group="Code">
<tab title="Kotlin" group-key="Kotlin">

```kotlin
// QQ频道API请求用的 token
val token = "Bot xxx"

// Ktor 的 HttpClient
// 在不同平台下请注意选择可用的引擎，比如在JS平台下使用 `JS` 引擎，windows系统平台下使用 `WinHttp` 等。
val client = HttpClient()

// 请求的服务器地址
// 此处为沙箱地址，也可选择正式地址或其他第三方代理地址
val server = QQGuild.SANDBOX_URL

val api = GetThreadListApi.create("channel ID")
val result = api.requestData(client, server, token)

result.threads.forEach { thread ->
    // 遍历结果...
}
```

</tab>
<tab title="Java" group-key="Java">


```java
// QQ频道API请求用的 token
String token = "Bot xxx";

// Ktor 的 HttpClient
// 此处选择使用 HttpClientJvmKt.HttpClient 自动加载环境中存在的 HttpClient 引擎
// 请保证classpath中存在一个可用的 HttpClient JVM 引擎
HttpClient client = HttpClientJvmKt.HttpClient(config -> Unit.INSTANCE);

// 请求的服务器地址
// 此处为沙箱地址，也可选择正式地址或其他第三方代理地址
Url server = QQGuild.SANDBOX_URL;

GetThreadListApi api = GetThreadListApi.create("channel ID");
CompletableFuture<? extends ThreadListResult> result = ApiRequests.requestDataAsync(api, client, token, server);

result.thenApply(ThreadListResult::getThreads)
        .thenAccept(threads -> {
            for (Thread thread : threads) {
                // 遍历结果
            }
        });
```
{switcher-key="%ja%"}

```java
// QQ频道API请求用的 token
String token = "Bot xxx";

// Ktor 的 HttpClient
// 此处选择使用 HttpClientJvmKt.HttpClient 自动加载环境中存在的 HttpClient 引擎
// 请保证classpath中存在一个可用的 HttpClient JVM 引擎
HttpClient client = HttpClientJvmKt.HttpClient(config -> Unit.INSTANCE);

// 请求的服务器地址
// 此处为沙箱地址，也可选择正式地址或其他第三方代理地址
Url server = QQGuild.SANDBOX_URL;

GetThreadListApi api = GetThreadListApi.create("channel ID");
ThreadListResult result =.requestDataBlocking(api, client, token, server);

for (Thread thread : result.getThreads()) {
// 遍历结果
}
```
{switcher-key="%jb%"}

</tab>
</tabs>

## 组件能力

在组件模块 `core` 中，也同样针对论坛子频道的相关内容提供了API。
在组件模块中提供了一些新的类型：

- `QGForumChannel` : 表示论坛子频道的 `Channel` 实现
- `QGForums` : 表示一个 `QGGuild` 针对帖子的相关操作 
- `QGThread` : 表示一个主题帖
- `QGThreadCreator` : 一个用于构造并发布帖子的构造器


> 这些操作大多从 `QGGuild` 作为入口提供。


<tabs group="Code">
<tab title="Kotlin" group-key="Kotlin">

```kotlin
val guild: QGGuild = ....
        
// 在所有的子频道中筛选出 论坛子频道
guild.channels.asFlow()
    // highlight-next-line
    .filterIsInstance<QGForumChannel>()
    .collect { channel: QGForumChannel ->
    // ...
    channel.threads.collect {
        // 获取所有的主题帖
    }
    val thread: QGThread? = channel.thread("123".ID) // 获取指定ID的主题帖
    
    // 构造并发布一个主题贴
    channel.createThread {
        title = ...
        content = ...
        format = ...
    }
    
    // 假设其不为null
    // 删除某个主题帖
    thread!!.delete()
}
```

除了在 `channels` 中通过类型筛选以外，也可以通过 `QGGuild.forums` 来进行操作：

```kotlin
val guild: QGGuild = ....
        
// 在所有的子频道中筛选出 论坛子频道
// highlight-next-line
guild.forums.forumChannels
    .collect { channel: QGForumChannel ->
    // ...
    channel.threads.collect {
        // 获取所有的主题帖
    }
    val thread: QGThread? = channel.thread("123".ID) // 获取指定ID的主题帖
    // 构造并发布一个主题贴
    channel.createThread {
        title = ...
        content = ...
        format = ...
    }
    // 假设其不为null
    // 删除某个主题帖
    thread!!.delete()
}
// 根据ID获取指定的 论坛子频道 实例
val forumChannel: QGForumChannel? = guild.forums.forumChannel("666".ID)

```

</tab>
<tab title="Java" group-key="Java">

```java
QGGuild guild = ...;

// 直接遍历
// 你也可以选择转为列表后再操作
guild.getChannels().collectAsync(channel -> {
    // 遍历所有的论坛子频道
    // highlight-next-line
    if (channel instanceof QGForumChannel forumChannel) {
        // 获取所有的主题帖并遍历
        forumChannel.getThreads().collectAsync(thread -> {
            // ...
        }); // thenXxx?
        // 获取指定ID的主题帖
        CompletableFuture<? extends QGThread> threadAsync = forumChannel.getThreadAsync(Identifies.ID("123"));
        // 构造并发布一个主题贴
        forumChannel.threadCreator()
                .title(...)
                .content(...)
                .format(...)
                .publishAsync();
        
        threadAsync.thenAccept(thread -> { // or use thenCompose
            // 假设其不为null
            assert thread != null;
            // 删除某个主题帖
            thread.deleteAsync();
        });
    }
});
```
{switcher-key="%ja%"}

```java
QGGuild guild = ...;

// 直接遍历
// 你也可以选择转为列表后再操作
guild.getChannels().collect(channel -> {
    // 遍历所有的论坛子频道
    // highlight-next-line
    if (channel instanceof QGForumChannel forumChannel) {
        // 获取所有的主题帖并遍历
        forumChannel.getThreads().collect(thread -> {
            // ...
        });
        // 获取指定ID的主题帖
        QGThread thread = forumChannel.getThread(Identifies.ID("123")); // nullable
        // 构造并发布一个主题贴
        forumChannel.threadCreator()
                .title(...)
                .content(...)
                .format(...)
                .publishBlocking();
        
        // 假设其不为null
        assert thread != null;
        // 删除某个主题帖
        thread.deleteBlocking();
    }
});
```
{switcher-key="%jb%"}

除了在 `channels` 中通过类型筛选以外，也可以通过 `QGGuild.forums` 来进行操作：

```java
QGGuild guild = ...;

// 直接遍历
// 你也可以选择转为列表后再操作
guild.getForums()
        // highlight-next-line
        .getForumChannels()
        .collectAsync(forumChannel -> {
            // 获取所有的主题帖并遍历
            forumChannel.getThreads().collectAsync(thread -> {
                // ...
            }); // thenXxx?
            // 获取指定ID的主题帖
            CompletableFuture<? extends QGThread> threadAsync = forumChannel.getThreadAsync(Identifies.ID("123"));
            // 构造并发布一个主题贴
            forumChannel.threadCreator()
                    .title(...)
                    .content(...)
                    .format(...)
                    .publishAsync();
            threadAsync.thenAccept(thread -> { // or use thenCompose
                // 假设其不为null
                assert thread != null;
                // 删除某个主题帖
                thread.deleteAsync();
            });
        });
```
{switcher-key="%ja%"}

```java
QGGuild guild = ...;

// 直接遍历
// 你也可以选择转为列表后再操作
guild.getForums()
        // highlight-next-line
        .getForumChannels()
        .collect(forumChannel -> {
            // 遍历所有的论坛子频道
            // 获取所有的主题帖并遍历
            forumChannel.getThreads().collect(thread -> {
                // ...
            });
            // 获取指定ID的主题帖
            QGThread thread = forumChannel.getThread(Identifies.ID("123")); // nullable
            // 构造并发布一个主题贴
            forumChannel.threadCreator()
                    .title(...)
                    .content(...)
                    .format(...)
                    .publishBlocking();
            
            // 假设其不为null
            assert thread != null;
            // 删除某个主题帖
            thread.deleteBlocking();
        });
```
{switcher-key="%jb%"}

</tab>
</tabs>


## API事件 {id="api_dispatch"}

API模块实现了与论坛相关的事件类型，它们的类型（与继承关系）如下：

- `OpenForumDispatch` : **开放论坛事件**
  - `OpenForumThreadDispatch` : 开放论坛事件 - **主题贴事件**
    - `OpenForumThreadCreate` : 主题贴事件: **主题贴创建**
    - `OpenForumThreadUpdate` : 主题贴事件: **主题贴更新**
    - `OpenForumThreadDelete` : 主题贴事件: **主题贴删除**
  - `OpenForumPostDispatch` : 开放论坛事件 - **评论事件**
    - `OpenForumPostCreate` : 评论事件 - **评论创建**
    - `OpenForumPostDelete` : 评论事件 - **评论删除**
  - `OpenForumReplyDispatch` : 开放论坛事件 - **回复事件**
    - `OpenForumReplyCreate` : 回复事件 - **回复创建**
    - `OpenForumReplyDelete` : 回复事件 - **回复删除**

:::note 开放论坛事件

对应的 `instents` 为 `EventIntents.OpenForumsEvent.intents`

更多可参考 [官方文档](https://bot.q.qq.com/wiki/develop/api/gateway/open_forum.html#oepn-forum-event-intents-open-forum-event)

:::

- `ForumDispatch` : **论坛事件**
  - `ForumThreadDispatch` : 论坛事件 - **主题贴事件**
    - `ForumThreadCreate` : 主题贴事件: **主题贴创建**
    - `ForumThreadUpdate` : 主题贴事件: **主题贴更新**
    - `ForumThreadDelete` : 主题贴事件: **主题贴删除**
  - `ForumPostDispatch` : 论坛事件 - **评论事件**
    - `ForumPostCreate` : 评论事件 - **评论创建**
    - `ForumPostDelete` : 评论事件 - **评论删除**
  - `ForumReplyDispatch` : 论坛事件 - **回复事件**
    - `ForumReplyCreate` : 回复事件 - **回复创建**
    - `ForumReplyDelete` : 回复事件 - **回复删除**
  - `ForumPublishAuditResult` : 论坛事件 - **帖子审核事件**


:::note 论坛事件

对应的 `instents` 为 `EventIntents.ForumsEvent.intents`

更多可参考 [官方文档](https://bot.q.qq.com/wiki/develop/api/gateway/forum.html)

:::

:::info 仅私域

非开放的论坛事件是仅支持**私域BOT**的。

:::

### 标准库应用

在使用 `stdlib` 标准库时可以对它们进行监听，以 `OpenForumThreadCreate` 为例：

<tabs group="Code">
<tab title="Kotlin" group-key="Kotlin">

```kotlin
// 配置并创建bot
val bot = BotFactory.create("app id", "sec", "token") {
    useSandboxServerUrl()
    // 为了示例，增加对 OpenForumsEvent 事件的支持
    intents += EventIntents.OpenForumsEvent.intents
}

bot.process<OpenForumThreadCreate> { raw ->
    println("OpenForumThreadCreate:     $this")
    println("OpenForumThreadCreate.raw: $raw")
}

bot.start()
bot.join()
```

</tab>
<tab title="Java" group-key="Java">

```java
Bot bot = BotFactory.create("appid", "sec", "token", (config) -> {
        config.useSandboxServerUrl();
        // 追加对 OpenForumsEvent 事件的订阅：OpenForumsEvent 与默认订阅合并
        config.setIntentsValue(
                config.getIntentsValue() | EventIntents.OpenForumsEvent.getIntents()
        );
    });

bot.registerProcessor(EventProcessors.async(OpenForumThreadCreate.class, (event, raw) -> {
        System.out.println("OpenForumThreadCreate:     " + event);
        System.out.println("OpenForumThreadCreate.raw: " + raw);
        return CompletableFuture.completedFuture(null); // Void?
    }));

bot.startAsync().thenCompose((v) -> bot.joinAsync()).join();
```
{switcher-key="%ja%"}

```java
Bot bot = BotFactory.create("appid", "sec", "token", (config) -> {
        config.useSandboxServerUrl();
        // 追加对 OpenForumsEvent 事件的订阅：OpenForumsEvent 与默认订阅合并
        config.setIntentsValue(
                config.getIntentsValue() | EventIntents.OpenForumsEvent.getIntents()
        );
        return Unit.INSTANCE;
    });

bot.registerBlockingProcessor(EventProcessors.block(OpenForumThreadCreate.class, (event, raw) -> {
        System.out.println("OpenForumThreadCreate:     " + event);
        System.out.println("OpenForumThreadCreate.raw: " + raw);
    }));

bot.startBlocking();
bot.joinBlocking();
```
{switcher-key="%jb%"}

</tab>
</tabs>

### 组件模块应用

#### core 组件模块

core 组件模块基于 simbot api 针对上述事件提供了进一步的封装实现：

- `QGOpenForumEvent` : **开放论坛事件**
  - `QGOpenForumThreadEvent` : 开放论坛事件 - **主题贴事件**
    - `QGOpenForumThreadCreateEvent` : 主题贴事件: **主题贴创建**
    - `QGOpenForumThreadUpdateEvent` : 主题贴事件: **主题贴更新**
    - `QGOpenForumThreadDeleteEvent` : 主题贴事件: **主题贴删除**
  - `QGOpenForumPostEvent` : 开放论坛事件 - **评论事件**
    - `QGOpenForumPostCreateEvent` : 评论事件 - **评论创建**
    - `QGOpenForumPostDeleteEvent` : 评论事件 - **评论删除**
  - `QGOpenForumReplyEvent` : 开放论坛事件 - **回复事件**
    - `QGOpenForumReplyCreateEvent` : 回复事件 - **回复创建**
    - `QGOpenForumReplyDeleteEvent` : 回复事件 - **回复删除**


- `QGForumEvent` : **论坛事件**
  - `QGForumThreadEvent` : 论坛事件 - **主题贴事件**
    - `QGForumThreadCreateEvent` : 主题贴事件: **主题贴创建**
    - `QGForumThreadUpdateEvent` : 主题贴事件: **主题贴更新**
    - `QGForumThreadDeleteEvent` : 主题贴事件: **主题贴删除**
  - `QGForumPostEvent` : 论坛事件 - **评论事件**
    - `QGForumPostCreateEvent` : 评论事件 - **评论创建**
    - `QGForumPostDeleteEvent` : 评论事件 - **评论删除**
  - `QGForumReplyEvent` : 论坛事件 - **回复事件**
    - `QGForumReplyCreateEvent` : 回复事件 - **回复创建**
    - `QGForumReplyDeleteEvent` : 回复事件 - **回复删除**
  - `QGForumPublishAuditResultEvent` : 论坛事件 - **帖子审核事件**

它们基本上与 API 模块中的基础实现类型一一对应。

在使用 simbot 核心库时：

<tabs group="Code">
<tab title="Kotlin" group-key="Kotlin">

```kotlin
val app = launchSimpleApplication {
  useQQGuild()
}

app.eventDispatcher.apply {
  // 所有开放论坛事件
  listen<QGOpenForumEvent> {
    println("Open forum event: $it")

    EventResult.empty()
  }

  // 所有论坛事件
  listen<QGForumEvent> {
    println("Forum event: $it")

    EventResult.empty()
  }
}

app.qqGuildBots {
  val bot = register("appid", "sec", "token") {
    botConfig {
      useSandboxServerUrl()
      // 追加事件订阅
      intents += EventIntents.OpenForumsEvent.intents + EventIntents.ForumsEvent.intents
    }
  }

  bot.start()
}

app.join()
```

</tab>
<tab title="Java" group-key="Java">


```java
var future = Applications.launchApplicationAsync(Simple.INSTANCE, configurer -> {
    configurer.install(QQGuildBotManager.Factory);
    configurer.install(QQGuildComponent.Factory);
}).asFuture().thenCompose(application -> {
    // 所有开放论坛事件
    application.getEventDispatcher().register(EventListeners.async(QGOpenForumEvent.class, (context, event) -> {
      System.out.println("QG open forum event: " + event);
      return CompletableFuture.completedFuture(EventResult.empty());
    }));
    // 所有论坛事件
    application.getEventDispatcher().register(EventListeners.async(QGForumEvent.class, (context, event) -> {
      System.out.println("QG forum event: " + event);
      return CompletableFuture.completedFuture(EventResult.empty());
    }));
    // 寻找并注册 bot
    for (var botManager : application.getBotManagers()) {
      if (botManager instanceof QQGuildBotManager qqGuildBotManager) {
        var bot = qqGuildBotManager.register("appid", "sec", "token", (qgBotConfig) -> {
          qgBotConfig.botConfig(botConfig -> {
            botConfig.useSandboxServerUrl();
            // 追加对 OpenForumsEvent 事件的订阅：OpenForumsEvent 与默认订阅合并
            botConfig.setIntentsValue(
                    botConfig.getIntentsValue() | EventIntents.OpenForumsEvent.getIntents()
            );
          });
        });
        bot.startAsync();
        break;
      }
    }
  
    return application.asFuture();
});

future.join();
```
{switcher-key="%ja%"}

```java
final var application = Applications.launchApplicationBlocking(Simple.INSTANCE, configurer -> {
    configurer.install(QQGuildBotManager.Factory);
    configurer.install(QQGuildComponent.Factory);
});

// 所有开放论坛事件
application.getEventDispatcher().register(EventListeners.block(QGOpenForumEvent.class, (context, event) -> {
    System.out.println("QG open forum event: " + event);
        return EventResult.empty();
    }));

// 所有论坛事件
application.getEventDispatcher().register(EventListeners.block(QGForumEvent.class, (context, event) -> {
    System.out.println("QG forum event: " + event);
        return EventResult.empty();
    }));

// 寻找并注册 bot
for (var botManager : application.getBotManagers()) {
    if (botManager instanceof QQGuildBotManager qqGuildBotManager) {
        var bot = qqGuildBotManager.register("appid", "sec", "token", (qgBotConfig) -> {
            qgBotConfig.botConfig(botConfig -> {
                botConfig.useSandboxServerUrl();
                // 追加对 OpenForumsEvent 事件的订阅：OpenForumsEvent 与默认订阅合并
                botConfig.setIntentsValue(
                        botConfig.getIntentsValue() | EventIntents.OpenForumsEvent.getIntents()
                );
            });
        });
        bot.startBlocking();
        break;
    }
}

application.joinBlocking();
```
{switcher-key="%jb%"}

</tab>
</tabs>

#### SpringBoot

或在 SpringBoot 中：

> _省略掉有关SpringBoot项目本身的配置说明_

<tabs group="Code">
<tab title="Kotlin" group-key="Kotlin">

```kotlin
@Listener
suspend fun onForumEvent(event: QGForumEvent) {
    println("Event: $event")
}
```

</tab>
<tab title="Java" group-key="Java">

```java
@Listener
public CompletableFuture<Void> onForumEvent(QGForumEvent event) {
    System.out.println("Event: " + event);
    return CompletableFuture.completedFuture(null);
}
```
{switcher-key="%ja%"}

```java
@Listener
public void onForumEvent(QGForumEvent event) {
    System.out.println("Event: " + event);
}
```
{switcher-key="%jb%"}

</tab>
</tabs>
