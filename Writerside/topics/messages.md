---
switcher-label: Java API 风格
---
<var name="jr" value="Reactor"/>

# 消息

<tldr>

- 对那些 **核心库** 中、实现了 simbot4 标准库的 `Message.Element` 消息元素类型的说明，
- 对一些与 **核心库** 中“消息”相关的内容的补充说明。

</tldr>

## 消息元素实现 {id='message-impl'}

所有的 `Message.Element` 特殊实现类型均定义在包 `love.forte.simbot.component.qguild.message` 中。

它们都继承了 `love.forte.simbot.component.qguild.message.QGMessageElement` 。

<deflist>
<def title="QGArk">对 API 模块中 Ark 消息的包装体，可用来发送 <code>Ark</code> 消息。</def>
<def title="QGContentText"></def>
<def title="QGEmbed">对 API 模块中 Embed 消息的包装体，可用来发送 <code>Embed</code> 消息。</def>
<def title="QGReference">

发送消息时，QQ频道的消息引用。与官方发送消息API中的 `reference` 对应。

</def>
<def title="QGReplyTo">

<tip>仅用于发送。</tip>

发送消息时，指定一个需要回复的目标消息ID。

</def>
</deflist>

## 发送消息 {id='message-usage'}

在simbot中，使用组件的消息元素与使用其他消息元素别无二致，
通常使用 `SendSupport` 和 `ReplySupport` 的实现类中提供的 `send(...)` 和 `reply(..)` API 发送消息。

前者多由
<a href="actors.md">行为对象</a>
中的一些类型实现(例如`QGMember`、`QGTextChannel`)，
而后者则通常由与消息相关的事件实现(例如 `QGAtMessageCreateEvent`)。

此处以 `QGTextChannel` 为例，`send` 可以使用拼接后的消息链、字符串或单独的消息元素作为参数。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val channel: QGTextChannel = ...
channel.send("消息内容")
channel.send("消息内容".toText() + At("user id".ID))
```

</tab>
<tab title="Java" group-key="Java" switcher-key="%ja%">

```Java
QGTextChannel channel = ...

var sendTask1 = channel.sendAsync("消息内容");
var sendTask2 = channel.sendAsync(Messages.of(
        Text.of("文本消息"),
        At.of(Identifies.of("user id"))
));
```

</tab>
<tab title="Java" group-key="Java" switcher-key="%jb%">

```Java
QGTextChannel channel = ...

channel.sendBlocking("消息内容");
channel.sendBlocking(Messages.of(
        Text.of("文本消息"),
        At.of(Identifies.of("user id"))
));
```

</tab>
<tab title="Java" group-key="Java" switcher-key="%jr%">

```Java
QGTextChannel channel = ...

channel.sendReserve("消息内容")
        .transform(SuspendReserves.mono())
        .subscribe(receipt -> { ... });

channel.sendReserve(Messages.of(
        Text.of("文本消息"),
        At.of(Identifies.of("user id"))
    )).transform(SuspendReserves.mono())
        .subscribe(receipt -> { ... });
```

</tab>
</tabs>


<note title="标准API">

你可以前往 [Simple Robot 应用手册](https://simbot.forte.love/basic-messages.html) 了解更多。

</note>
