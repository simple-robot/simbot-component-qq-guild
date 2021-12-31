<div align="center">
    <img src="../.simbot/logo.png" alt="logo" style="width:230px; height:230px; border-radius:50%; " />
    <h2>
        - simple-robot-component-boot -
    </h2>
    <h4>
        ~ tencent-guild ~
    </h4>
    <br />
<a href="https://repo1.maven.org/maven2/love/forte/simbot/component/simbot-component-tencent-guild-boot" target="_blank">
  <img alt="release" src="https://img.shields.io/maven-central/v/love.forte.simbot.component/simbot-component-tencent-guild-boot" /></a>
<a href="https://www.yuque.com/simpler-robot/simpler-robot-doc" target="_blank">
  <img alt="doc" src="https://img.shields.io/badge/doc-yuque-brightgreen" /></a>
<hr />
</div>

<br>

# 建设中。。


基于 [simple-robot](https://github.com/ForteScarlet/simpler-robot) 3.x版本API的 `simbot` 组件的boot支持。


`component-boot-xxx` 相关组件用于与 `simboot-core` 配合使用，实现模块化开发、多组件快速协同等功能。



## 使用
### Maven

```xml
<dependency>
    <groupId>love.forte.simbot.component</groupId>
    <artifactId>simbot-component-tencent-guild-boot</artifactId>
    <version>3.0.0.preview.0.5-0.5</version>
</dependency>
<!-- simbot boot核心 -->
<dependency>
    <groupId>love.forte.simbot.boot</groupId>
    <artifactId>simboot-core</artifactId>
    <version>3.0.0.preview.0.5</version>
</dependency>
```

### Gradle groovy

```groovy
implementation "love.forte.simbot.component:simbot-component-tencent-guild-boot:$version"
implementation "love.forte.simbot.boot:simboot-core:3.0.0.preview.0.5"
```

### Gradle kotlin DSL

```kotlin
implementation("love.forte.simbot.component:simbot-component-tencent-guild-boot:$version")
implementation("love.forte.simbot.boot:simboot-core:3.0.0.preview.0.5")
```


## 示例

### 启动类
首先，你需要写一个启动类

```kotlin
@SimbootApplication
class MyAppClass

/** 启动入口 */
suspend fun main(args: Array<String>) {
    val context = SimbootApp.run(MyAppClass::class, *args)
    context.join() // 挂起，直到被cancel
}

```

### 监听函数 - 基础用法
在上述启动入口所在包（或子包）中定义任意类，并编写监听函数，比如：

```kotlin
@Listener // 必须，标记为一个监听函数
suspend fun ChannelMessageEvent.myListener1() {
    // 如果标记了 @Listener，且未标记任何 @Listen，
    // 则参数中（可以是receiver）中必须有一个Event（事件）的子类。
    
    val channel = channel() 
    replyIfSupport(Text { "hi!" })
}

@Listener
@ContentTrim // 使得被匹配的纯文本消息在匹配前进行 trim() 操作
@Filter("HI!", matchType = MatchType.TEXT_EQUALS) // 匹配事件中的纯文本消息。
suspend fun ChannelMessageEvent.myListener2() {
    replyIfSupport(Text { "hi!" })
}
```

### 监听函数 - 持续会话
有时候，你可能会有需要进行连续对话的情况：

> 注意，下述逻辑经过简化，仅供参考

```kotlin
@Listener(async = true)
@ContentTrim
@Filter("记录", matchType = MatchType.TEXT_EQUALS)
suspend fun ChannelMessageEvent.roulette(session: ContinuousSessionContext): EventResult {
    
    val userId = author.id
    val channelId = channel().id
    
    replyIfSupport(Text { "请输入你的名称" })
    // session.waitingFor 会挂起，直到超时，或者监听函数内调用了 provider.push / provider.pushException
    val name: String = session.waitingFor(id = randomID(), timeout = 1.minutes) { event: ChannelMessageEvent, context, provider ->
        // session构建的临时监听器暂时无法整合例如 @Filter 等便捷过滤的方法，你需要手动匹配事件是否是你所需要的
        if (channel().id == channelId && author.id == userId) {
            val value = event.messageContent.plainText.trim()
            provider.push(value) // 当得到的需要的值，推送结果以结束外层挂起
        }
    }
    
    replyIfSupport(Text { "你的名称：$name" })
}
```
