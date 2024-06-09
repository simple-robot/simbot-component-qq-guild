---
switcher-label: Java API 风格
---
<show-structure for="chapter,procedure" depth="3"/>
<var name="jr" value="Reactor"/>

# 频道成员 QGMember

频道成员，即 `Member`，存在于频道服务器(`Guild`)中。


## API中的频道成员 {id="api-members"}

在API模块中存在一些用来获取 `Member` 的API。

比如你可以通过 `GetMemberApi` 获取 `SimpleMember` 类型结果的成员信息、
`DeleteMemberApi` 删除(踢出)一个成员等。

> 详细的API列表请参考
> <a href="api-list.md" />
> 或 [API文档](%api-doc%)。

## Stdlib模块中的频道成员 {id="stdlib-members"}

当你直接使用标准库模块时，你可以在一些与频道成员相关的事件中得到它的信息。

比如当你处理 `GuildMemberAdd` 类型事件时，可以通过 `data` 获取到 `EventMember`。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
bot.subscribe<GuildMemberAdd> {
    val member: EventMember = data
}
```

</tab>
<tab title="Java" group-key="Java">

```Java
bot.subscribe(EventProcessors.async(GuildMemberAdd.class, (event, raw) -> {
    var member = event.getData();
    // ...
    return CompletableFuture.completedFuture(null);
}));
```
{switcher-key="%ja%"}

```Java
bot.subscribe(EventProcessors.block(GuildMemberAdd.class, (event, raw) -> {
    var member = event.getData();
}));
```
{switcher-key="%jb%"}

</tab>
</tabs>

> 详细的事件列表请参考
> <a href="event.md" />
> 或 [API文档](%api-doc%)。

## 组件库中的频道成员 {id="component-members"}

组件中 `QGMember` 类型实现了simbot标准API中的 `Member` 类型，并提供与频道成员相关的功能。
如果你想要获取一个 `QGChannel`，你可以在 `QGBot`、`QGGuild` 或一个与子频道相关的事件中获取。

在 `QGBot` 中获取子频道你可以前往参考
<a href="QGBot.md#qgbot-guild" />。

在 `QGGuild` 中获取频道成员你可以前往参考
<a href="QGGuild.md#get-members" />。

在事件中获取，那么这个事件应当与**频道成员有所关联**。
它们通常使用 `member` 属性获取。

> 详细的事件列表请参考
> <a href="event.md" />
> 或 [API文档](%api-doc%)。

以 `QGMemberAddEvent` 事件为例：

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val event: QGMemberAddEvent = ...
val member = event.member()
```

</tab>
<tab title="Java" group-key="Java">

```Java
QGMemberAddEvent event = ...
event.getMemberAsync()
    .thenAccept(member -> { ... })
```
{switcher-key='%ja%'}

```Java
QGMemberAddEvent event = ...
var member = event.getMemberBlocking() 
```
{switcher-key='%jb%'}

```Java
QGChannelCreateEvent event = ...
event.getMemberReserve()
        // 例如转为 Reactor 的 `Mono`
        .transform(SuspendReserves.mono())
        .subscribe(member -> { ... })
```
{switcher-key='%jr%'}

</tab>
</tabs>


## 私聊消息 {id='send-message'}

`QGMember` 实现simbot标准API中的 `Channel`，而它又支持 `SendSupport`，
也就是说 `QGMember` 支持向某个频道成员发送私聊消息。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val member: QGMember = ...
member.send("消息内容")
member.send("消息内容".toText() + At("user id".ID))
```

</tab>
<tab title="Java" group-key="Java" switcher-key="%ja%">

```Java
QGMember member = ...

var sendTask1 = member.sendAsync("消息内容");
var sendTask2 = member.sendAsync(Messages.of(
        Text.of("文本消息"),
        At.of(Identifies.of("user id"))
));
```

</tab>
<tab title="Java" group-key="Java" switcher-key="%jb%">

```Java
QGMember member = ...

member.sendBlocking("消息内容");
member.sendBlocking(Messages.of(
        Text.of("文本消息"),
        At.of(Identifies.of("user id"))
));
```

</tab>
<tab title="Java" group-key="Java" switcher-key="%jr%">

```Java
QGTextChannel member = ...

member.sendReserve("消息内容")
        .transform(SuspendReserves.mono())
        .subscribe(receipt -> { ... });

member.sendReserve(Messages.of(
        Text.of("文本消息"),
        At.of(Identifies.of("user id"))
    )).transform(SuspendReserves.mono())
        .subscribe(receipt -> { ... });
```

</tab>
</tabs>

> 有关消息元素、消息发送的更多内容前往参考
> <a href="messages.md" />


## 角色操作 {id='roles'}

`QGMember` 拥有一些获取或操作自身角色 (`QGMemberRole`) 的API。

> 有关角色的更多内容前往参考
> <a href="api_role.md" />。

### 获取角色 {id='get-roles'}

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val member: QGMember = ...
// 获取当前用户拥有的角色集合
val roles = member.roles
```

</tab>
<tab title="Java" group-key="Java">

```Java
QGmember member = ...

// 可以直接在异步中遍历
// 第一个参数 scope 可以选择 QGGuild、QGBot 等，
// 或者直接使用 GlobalScope
member.getRoles().collectAsync(GlobalScope.INSTANCE, member -> { });

// 可以使用 Collectables.toListAsync / collectAsync
var rolesCollectable = member.getRoles();
Collectables.toListAsync(rolesCollectable)
        .thenAccept(role -> {});
```
{switcher-key='%ja%'}

```Java
QGmember member = ...

// 可以使用 SuspendReserves.list 转为 List
var roleList = member.getRoles().transform(SuspendReserves.list());

// 可以使用 Collectables 转成 Stream 或 List
var rolesCollectable = member.getRoles();
Collectables.asStream(rolesCollectable)
        .forEach(role -> {});
```
{switcher-key='%jb%'}

```Java
QGmember member = ...
        
// 可以直接在异步中遍历
// 第一个参数 scope 可以选择 QGGuild、QGBot 等，
// 或者直接使用 GlobalScope
member.getRoles().collectAsync(GlobalScope.INSTANCE, role -> { });

var rolesCollectable = guild.getRoles();
// 可以使用 Collectables 转为 Flux
Collectables.asFlux(rolesCollectable)
        .subscribe(role -> {  });
```
{switcher-key='%jr%'}

</tab>
</tabs>

