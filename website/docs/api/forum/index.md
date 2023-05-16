---
title: 帖子API
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

QQ频道中有一些针对 `论坛子频道` 的API。( [参考文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread) )


## API

首先是 `API` 模块中对相关API的封装类型，它们在 `love.forte.simbot.qguild.api.forum` 中：

- `DeleteThreadApi`
- `GetThreadApi`
- `GetThreadListApi`
- `PublishThreadApi`

使用它们的方式都差不多，我们选其中一个 `GetThreadListApi` 作为示例：

<Tabs groupId="code">
<TabItem value="Kotlin" attributes={{'data-value': `Kotlin`}}>

```kotlin
// QQ频道API请求用的 token
val token = "Bot xxx"

// Ktor 的 HttpClient
// 在不同平台下请注意选择可用的引擎，比如在JS平台下使用 `JS` 引擎，windows系统平天下使用 `WinHttp` 等。
val client = HttpClient()

// 请求的服务器地址
// 此处为沙箱地址，也可选择正式地址或其他第三方代理地址
val server = QQGuild.SANDBOX_URL

val api = GetThreadListApi.create("channel ID")
val result = api.request(client, server, token)

result.threads.forEach { thread ->
    // 遍历结果...
}
```

</TabItem>
<TabItem value="Java" attributes={{'data-value': `Java`}}>

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
ThreadListResult result = api.doRequestBlocking(client, server, token);

for (Thread thread : result.getThreads()) {
// 遍历结果
}
```

</TabItem>
<TabItem value="Java Async" attributes={{'data-value': `Java`}}>

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
CompletableFuture<? extends ThreadListResult> result = api.doRequestAsync(client, server, token);

result.thenApply(ThreadListResult::getThreads)
        .thenAccept(threads -> {
            for (Thread thread : threads) {
                // 遍历结果
            }
        });
```

</TabItem>
</Tabs>

## 组件应用

在组件模块 `core` 中，也同样针对论坛子频道的相关内容提供了API。
在组件模块中提供了一些新的类型：

- `QGForumChannel` : 表示论坛子频道的 `Channel` 实现
- `QGForums` : 表示一个 `QGGuild` 针对帖子的相关操作 
- `QGThread` : 表示一个主题帖
- `QGThreadCreator` : 一个用于构造并发布帖子的构造器

:::note 

这些操作大多从 `QGGuild` 作为入口提供。

:::



<Tabs groupId="code">
<TabItem value="Kotlin" attributes={{'data-value': `Kotlin`}}>

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

</TabItem>
<TabItem value="Java" attributes={{'data-value': `Java`}}>

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

除了在 `channels` 中通过类型筛选以外，也可以通过 `QGGuild.forums` 来进行操作：

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

</TabItem>
<TabItem value="Java Async" attributes={{'data-value': `Java`}}>

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

</TabItem>
</Tabs>
