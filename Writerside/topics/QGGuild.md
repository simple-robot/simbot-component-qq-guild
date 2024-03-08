---
switcher-label: Java API 风格
---
<show-structure for="chapter,procedure" depth="3"/>
<var name="jr" value="Reactor"/>

# 频道 QGGuild

## API模块中的Guild {id='guild-in-api'}

API模块中部分用于获取频道信息的API中会返回一些与 `Guild` 相关的数据类型。

当你使用 `GetGuildApi` 或 `GetBotGuildListApi` 时，
返回值类型分别为 `SimpleGuild` 和 `List<SimpleGuild>`。

## Stdlib模块中的Guild {id='guild-in-stdlib'}

除了使用API主动查询、获取以外，
你还可能在stdlib模块下订阅与Guild相关的事件时获取到它。

当你订阅 `EventGuildDispatch` 或它的子类型事件 
`GuildCreate`、`GuildUpdate`、`GuildDelete`
时，可以通过 `data` 获取到 `EventGuild`。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
bot.process<GuildCreate> {
    val guild: EventGuild = data
}
```

</tab>
<tab title="Java" group-key="Java">

```Java
bot.subscribe(EventProcessors.async(GuildCreate.class, (event, raw) -> {
    EventGuild guild = event.getData();
    // ...
    return CompletableFuture.completedFuture(null);
}));
```
{switcher-key="%ja%"}

```Java
bot.subscribe(EventProcessors.block(GuildCreate.class, (event, raw) -> {
    EventGuild guild = event.getData();
}));
```
{switcher-key="%jb%"}

</tab>
</tabs>

## 组件库中的QGGuild {id="guild-in-core"}

在组件库模块中，QQ频道组件提供了一个针对 simbot标准API中的 `Guild` 类型的实现：
`QGGuild`，它基于 stdlib模块的 `Guild` (这个不是指simbot标准API中的 `Guild`)
提供更进一步的功能。

### 得到QGGuild {id='get-guild'}

你可以在 `QGBot` 中得到 `QGGuild`。
这部分可以前往参考
<a href="QGBot.md#qgbot-guild" />。

除了直接在 `QGBot` 中获取，你也可以在与频道相关的事件中获取到它。

如果一个频道是这个事件中的主体(例如 `QGGuildCreateEvent`)，
那么你可以通过 `content` 得到 `QGGuild`。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val event: QGGuildCreateEvent = ...
val guild = event.content()
```

</tab>
<tab title="Java" group-key="Java">

```Java
QGGuildCreateEvent event = ...
event.getContentAsync()
    .thenAccept(guild -> { ... })
```
{switcher-key='%ja%'}

```Java
QGGuildCreateEvent event = ...
var guild = event.getContentBlocking() 
```
{switcher-key='%jb%'}

```Java
QGGuildCreateEvent event = ...
var guild = event.getContentReserve()
        // 例如转为 Reactor 的 `Mono`
        .transform(SuspendReserves.mono())
        .subscribe(guild -> { ... })
```
{switcher-key='%jr%'}

</tab>
</tabs>

### 获取基本信息 {id="get-info"}

`QGGuild` 理所当然得可以获取到一些频道服务器的基本属性，
比如id、名称、图标等。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
guild.id
guild.name
guild.ownerId
guild.joinTime
guild.description
guild.icon
guild.memberCount
guild.maxMembers
```

</tab>
<tab title="Java" group-key="Java">

```Java
guild.getId();
guild.getName();
guild.getOwnerId();
guild.getJoinTime();
guild.getDescription();
guild.getIcon();
guild.getMemberCount();
guild.getMaxMembers();
```

</tab>
</tabs>

### 获取子频道 {id="get-channels"}

你可以在 `QGGuild` 中获取到子频道类型 `QGChannel`。

> 更多有关子频道的信息可前往参考
> <a href="QGChannel.md" />。
> 
> 其中，更多有关论坛子类型(`Forum`)的信息可前往参考
> <a href="api_forum.md" />。


<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

**寻找指定ID的子频道**

```Kotlin
val guild: QGGuild = ...
// 假设这是某个子频道的ID
val id = 1234L.ID

val channel = guild.channel(id)
val chatChannel = guild.chatChannel(id)
val forumChannel = guild.forum(id)
```

**批量获取子频道**

```Kotlin
val guild: QGGuild = ...

guild.channels
    // .asFlow() // 也可以转成 Flow
    .collect { channel ->  }
    
val chatChannelList = guild.chatChannels
    .asFlow().toList()

guild.forums.collect { forum ->  }
```

</tab>
<tab title="Java" group-key="Java">

**寻找指定ID的子频道**

```Java
QGGuild guild = ...
// 假设这是某个子频道的ID
var id = Identifies.of(1234L);

final var channel = guild.getChannel(id);
final var chatChannel = guild.getChatChannel(id);
final var forum = guild.getForum(id);
```
{switcher-key='%ja%'}

```Java
QGGuild guild = ...
// 假设这是某个子频道的ID
var id = Identifies.of(1234L);

guild.getChannelAsync(id)
        .thenAccept(channel -> {...});

guild.getChatChannelAsync(id)
        .thenAccept(channel -> {...});

guild.getForumAsync(id)
        .thenAccept(channel -> {...});
```
{switcher-key='%jb%'}

```Java
QGGuild guild = ...
// 假设这是某个子频道的ID
var id = Identifies.of(1234L);

guild.getChannelReserve(id)
        // 假设转化为 Reactor 的 `Mono`
        .transform(SuspendReserves.mono())
        .subscribe(channel -> {});

guild.getChatChannelReserve(id)
        // 假设转化为 Reactor 的 `Mono`
        .transform(SuspendReserves.mono())
        .subscribe(channel -> {});

guild.getForumReserve(id)
        // 假设转化为 Reactor 的 `Mono`
        .transform(SuspendReserves.mono())
        .subscribe(channel -> {});
```
{switcher-key='%jr%'}

**批量获取子频道**

```Java
QGGuild guild = ...

// 可以使用 SuspendReserves.list 转为 List
var channelList = guild.getChannels().transform(SuspendReserves.list());

var chatChannelsCollectable = guild.getChatChannels();
// 可以使用 Collectables 转成 Stream 或 List
Collectables.asStream(chatChannelsCollectable)
        .forEach(chatChannel -> {});

var forumsCollectable = guild.getForums();
var forumList = Collectables.toList(forumsCollectable);
```
{switcher-key='%ja%'}

```Java
QGGuild guild = ...

```
{switcher-key='%jb%'}

```Java
QGGuild guild = ...
        
// 可以直接在异步中遍历
// CoroutineScope 可以选择 QGBot、QGGuild等
// 也可以选择 GlobalScope
guild.getChannels()
        .collectAsync(guild, channel -> {  });

final var chatChannelsCollectable = guild.getChatChannels();
// 可以使用 Collectables 在异步中转为 List
// CoroutineScope 如果不填默认 GlobalScope.INSTANCE
        Collectables.toListAsync(chatChannelsCollectable, GlobalScope.INSTANCE)
                .thenAccept(chatChannelList -> {  });

final var forumsCollectable = guild.getForums();
        Collectables.toListAsync(forumsCollectable)
                .thenAccept(forumList -> {  });
```
{switcher-key='%jr%'}

</tab>
</tabs>

### 获取成员 {id="get-members"}

你可以在 `QGGuild` 中获取到成员类型 `QGMember`。

> 更多有关子频道的信息可前往参考
<a href="QGMember.md" />。

### 获取权限 {id="get-permissions"}

你可以在 `QGGuild` 中获取到当前频道对 bot 的权限限制信息 `ApiPermissions`。

> 更多有关频道权限的信息可前往参考
<a href="ApiPermission.md" />。

### 获取角色 {id="get-roles"}

你可以在 `QGGuild` 中获取到角色类型 `QGRole`。

> 更多有关子频道的信息可前往参考
<a href="api_role.md" />。
