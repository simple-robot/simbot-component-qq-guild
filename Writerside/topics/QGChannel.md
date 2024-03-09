---
switcher-label: Java API 风格
---
<show-structure for="chapter,procedure" depth="3"/>
<var name="jr" value="Reactor"/>

# 子频道 QGChannel

子频道，即 `Channel`，存在于频道服务器(`Guild`)中，
有多种类型，例如文字子频道、论坛子频道等。

> 有关**论坛子频道**可前往参考
> <a href="api_forum.md" /> ，
> 本章节不过多讨论。

## API中的子频道 {id="api-channels"}

万物起源于API。你在API模块中会遇到一些用来获取、操作子频道相关的API。

比如你可以通过 `GetGuildChannelListApi` 获取频道服务器的子频道列表、
`GetChannelApi` 获取某个频道的详情。它们分别返回 `List<SimpleChannel>`
和 `SimpleChannel`。

> 详细的API列表请参考
> <a href="api-list.md" />
> 或 [API文档](%api-doc%)。

## Stdlib模块中的子频道 {id="stdlib-channels"}

当你直接使用标准库模块时，你可以在一些与子频道相关的事件中得到它的信息。

比如当你处理 `ChannelDispatch` 或其子类型的事件时，可以通过 `data` 获取到 `EventChannel`。
以 `ChannelCreate` 为例：

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
bot.process<ChannelCreate> {
    val channel: EventChannel = data
}
```

</tab>
<tab title="Java" group-key="Java">

```Java
bot.subscribe(EventProcessors.async(ChannelCreate.class, (event, raw) -> {
    var channel = event.getData();
    // ...
    return CompletableFuture.completedFuture(null);
}));
```
{switcher-key="%ja%"}

```Java
bot.subscribe(EventProcessors.block(ChannelCreate.class, (event, raw) -> {
    var channel = event.getData();
}));
```
{switcher-key="%jb%"}

</tab>
</tabs>


## 组件库中的子频道 {id="component-channels"}

组件库模块中，`QGChannel` 类型即为实现了simbot标准API中 `Channel` 类型的实现类型。
它基于stdlib模块的 `Channel` (这个不是指simbot标准API中的 `Channel`)
提供更进一步的功能。

## 获取子频道 {id="get-channels"}

如果你想要获取一个 `QGChannel`，你可以在 `QGBot`、`QGGuild` 或一个与子频道相关的事件中获取。

在 `QGBot` 中获取子频道你可以前往参考
<a href="QGBot.md#qgbot-guild" />。

在 `QGGuild` 中获取子频道你可以前往参考 
<a href="QGGuild.md#get-channels" />。

在事件中获取，那么这个事件应当与**子频道有所关联**。
以 `QGChannelCreateEvent` 事件为例：

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val event: QGChannelCreateEvent = ...
val channel = event.content()
```

</tab>
<tab title="Java" group-key="Java">

```Java
QGChannelCreateEvent event = ...
event.getContentAsync()
    .thenAccept(channel -> { ... })
```
{switcher-key='%ja%'}

```Java
QGChannelCreateEvent event = ...
var channel = event.getContentBlocking() 
```
{switcher-key='%jb%'}

```Java
QGChannelCreateEvent event = ...
event.getContentReserve()
        // 例如转为 Reactor 的 `Mono`
        .transform(SuspendReserves.mono())
        .subscribe(channel -> { ... })
```
{switcher-key='%jr%'}

</tab>
</tabs>

## 子频道类型 {id='channel-types'}

在QQ频道中，一个子频道可能有多个不同的类型，例如文字、论坛、分组等等。
而在这里面只有使用**文字子频道**才能够发送消息。

因此 `QGChannl` 进一步差分为了两个子类型：

- `QGTextChannel`
- `QGNonTextChannel`

顾名思义，它们分别表示自己是否为一个文字子频道。

### QGTextChannel

如果子频道是文字子频道，那么它在实现simbot标准API的 `Channel` 之上，
额外实现了 `ChatChannel` 接口，即表示一个聊天子频道，可用于发送消息。

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

### QGForumChannel

有关论坛子频道的内容可前往参考
<a href="api_forum.md" />。

### QGCategoryChannel

用来表示一个类型为“分类”的子频道。

### QGNonTextChannel

其他没有特殊实现类型的子频道 (比如语音子频道等) 均会使用 `QGNonTextChannel`。
