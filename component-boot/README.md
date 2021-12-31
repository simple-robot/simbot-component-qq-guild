<div align="center">
    <img src="../.simbot/logo.png" alt="logo" style="width:230px; height:230px; border-radius:50%; " />
    <h2>
        - simple-robot-component-boot -
    </h2>
    <h4>
        ~ tencent-guild ~
    </h4>
</div>

<br>

# 建设中。。


基于 [simple-robot](https://github.com/ForteScarlet/simpler-robot) 3.x版本API的 `simple-robot` 组件boot。


`component-boot-xxx` 相关组件用于与 `simple-robot-boot-core` 配合使用，实现模块化开发、多组件快速协同等功能。



## 使用
### Maven

```xml
<dependency>
    <groupId>love.forte.simbot</groupId>
    <artifactId>simbot-component-tencent-guild-boot</artifactId>
    <version>3.0.0.preview.0.5-0.5</version>
</dependency>
```

### Gradle groovy

```groovy
implementation "love.forte.simbot:simbot-component-tencent-guild-boot:$version"
```

### Gradle kotlin DSL

```kotlin
implementation("love.forte.simbot:simbot-component-tencent-guild-boot:$version")
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