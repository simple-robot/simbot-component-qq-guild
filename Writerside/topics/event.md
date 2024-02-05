# 事件定义列表

QQ频道组件中的**事件类型**包含两个层面：

1. **API 模块** 中，对 QQ频道 API 中官方定义的事件结构的基本封装与实现。
2. **核心模块** 中，基于 API 模块中的事件封装，对 simbot4 标准库中的 `Event` 事件类型的实现。


## API 模块事件封装

API 模块所有的事件封装类型都在包 `love.forte.simbot.qguild.event` 中，
并且基本上命名与官网API中的事件类型名称有一定关联。

所有事件封装类型均继承密封类 `love.forte.simbot.qguild.event.Signal.Dispatch`。 

<deflist>
<def title="Ready" id="love_forte_simbot_qguild_event_Ready">

`love.forte.simbot.qguild.event.Ready`

事件类型名: `"READY"`

鉴权成功之后，后台会下发的 Ready Event.

</def>
<def title="Resumed" id="love_forte_simbot_qguild_event_Resumed">

`love.forte.simbot.qguild.event.Resumed`

事件类型名: `"RESUMED"`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/reference.html">4.恢复连接</a>
恢复成功之后，就开始补发遗漏事件，所有事件补发完成之后，会下发一个 `Resumed Event`

</def>
<def title="ChannelDispatch" id="love_forte_simbot_qguild_event_ChannelDispatch">

`love.forte.simbot.qguild.event.ChannelDispatch`

channel相关的事件类型。 `data` 类型为 `EventChannel` 。

</def>
<def title="ChannelCreate" id="love_forte_simbot_qguild_event_ChannelCreate">

`love.forte.simbot.qguild.event.ChannelCreate`

事件类型名: `"CHANNEL_CREATE"`

子频道事件 <a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/channel.html#channel-create">CHANNEL_CREATE</a>

**发送时机**

- 子频道被创建

</def>
<def title="ChannelUpdate" id="love_forte_simbot_qguild_event_ChannelUpdate">

`love.forte.simbot.qguild.event.ChannelUpdate`

事件类型名: `"CHANNEL_UPDATE"`

子频道事件 <a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/channel.html#channel-update">CHANNEL_UPDATE</a>

**发送时机**

- 子频道信息变更

</def>
<def title="ChannelDelete" id="love_forte_simbot_qguild_event_ChannelDelete">

`love.forte.simbot.qguild.event.ChannelDelete`

事件类型名: `"CHANNEL_DELETE"`

子频道事件 <a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/channel.html#channel-delete">CHANNEL_DELETE</a>

**发送时机**

- 子频道被删除

</def>
<def title="ForumDispatch" id="love_forte_simbot_qguild_event_ForumDispatch">

`love.forte.simbot.qguild.event.ForumDispatch`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/forum.html#forum-event-intents-forum-event">论坛事件(ForumEvent)</a>

**发送时机**

用户在话题子频道内发帖、评论、回复评论时产生该事件

**主题事件**

- FORUM_THREAD_CREATE
- FORUM_THREAD_UPDATE
- FORUM_THREAD_DELETE
  事件内容为 `Thread` 对象

**帖子事件**

- FORUM_POST_CREATE
- FORUM_POST_DELETE
  事件内容为 `Post` 对象

**回复事件**

- FORUM_REPLY_CREATE
- FORUM_REPLY_DELETE
  事件内容为 `Reply` 对象

**帖子审核事件**

- FORUM_PUBLISH_AUDIT_RESULT
  事件内容为 `AuditResult` 对象

</def>
<def title="ForumThreadDispatch" id="love_forte_simbot_qguild_event_ForumThreadDispatch">

`love.forte.simbot.qguild.event.ForumThreadDispatch`

论坛事件：主题事件

</def>
<def title="ForumThreadCreate" id="love_forte_simbot_qguild_event_ForumThreadCreate">

`love.forte.simbot.qguild.event.ForumThreadCreate`

事件类型名: `"FORUM_THREAD_CREATE"`

主题创建事件。

</def>
<def title="ForumThreadUpdate" id="love_forte_simbot_qguild_event_ForumThreadUpdate">

`love.forte.simbot.qguild.event.ForumThreadUpdate`

事件类型名: `"FORUM_THREAD_UPDATE"`

主题更新事件。

</def>
<def title="ForumThreadDelete" id="love_forte_simbot_qguild_event_ForumThreadDelete">

`love.forte.simbot.qguild.event.ForumThreadDelete`

事件类型名: `"FORUM_THREAD_DELETE"`

主题删除事件。

</def>
<def title="ForumPostDispatch" id="love_forte_simbot_qguild_event_ForumPostDispatch">

`love.forte.simbot.qguild.event.ForumPostDispatch`

论坛事件：帖子事件

</def>
<def title="ForumPostCreate" id="love_forte_simbot_qguild_event_ForumPostCreate">

`love.forte.simbot.qguild.event.ForumPostCreate`

事件类型名: `"FORUM_POST_CREATE"`

帖子创建事件

</def>
<def title="ForumPostDelete" id="love_forte_simbot_qguild_event_ForumPostDelete">

`love.forte.simbot.qguild.event.ForumPostDelete`

事件类型名: `"FORUM_POST_DELETE"`

帖子删除事件

</def>
<def title="ForumReplyDispatch" id="love_forte_simbot_qguild_event_ForumReplyDispatch">

`love.forte.simbot.qguild.event.ForumReplyDispatch`

论坛事件：回复事件

</def>
<def title="ForumReplyCreate" id="love_forte_simbot_qguild_event_ForumReplyCreate">

`love.forte.simbot.qguild.event.ForumReplyCreate`

事件类型名: `"FORUM_REPLY_CREATE"`

回复创建事件

</def>
<def title="ForumReplyDelete" id="love_forte_simbot_qguild_event_ForumReplyDelete">

`love.forte.simbot.qguild.event.ForumReplyDelete`

事件类型名: `"FORUM_REPLY_DELETE"`

回复删除事件

</def>
<def title="ForumPublishAuditResult" id="love_forte_simbot_qguild_event_ForumPublishAuditResult">

`love.forte.simbot.qguild.event.ForumPublishAuditResult`

事件类型名: `"FORUM_PUBLISH_AUDIT_RESULT"`

帖子审核事件

</def>
<def title="EventGuildDispatch" id="love_forte_simbot_qguild_event_EventGuildDispatch">

`love.forte.simbot.qguild.event.EventGuildDispatch`

Guild相关事件类型。 `data` 类型为 `EventGuild` 。

</def>
<def title="GuildCreate" id="love_forte_simbot_qguild_event_GuildCreate">

`love.forte.simbot.qguild.event.GuildCreate`

事件类型名: `"GUILD_CREATE"`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/guild.html#guild-create">GUILD_CREATE</a>

**发送时机**

- 机器人被加入到某个频道的时候

</def>
<def title="GuildUpdate" id="love_forte_simbot_qguild_event_GuildUpdate">

`love.forte.simbot.qguild.event.GuildUpdate`

事件类型名: `"GUILD_UPDATE"`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/guild.html#guild-update">GUILD_UPDATE</a>

**发送时机**

- 频道信息变更
- 事件内容为变更后的数据

</def>
<def title="GuildDelete" id="love_forte_simbot_qguild_event_GuildDelete">

`love.forte.simbot.qguild.event.GuildDelete`

事件类型名: `"GUILD_DELETE"`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/guild.html#guild-delete">GUILD_DELETE</a>

**发送时机**

- 频道被解散
- 机器人被移除
- 事件内容为变更前的数据

</def>
<def title="GuildMemberAdd" id="love_forte_simbot_qguild_event_GuildMemberAdd">

`love.forte.simbot.qguild.event.GuildMemberAdd`

事件类型名: `"GUILD_MEMBER_ADD"`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/guild_member.html#guild-member-add">`GUILD_MEMBER_ADD`</a>

**发送时机**

- 新用户加入频道

</def>
<def title="GuildMemberUpdate" id="love_forte_simbot_qguild_event_GuildMemberUpdate">

`love.forte.simbot.qguild.event.GuildMemberUpdate`

事件类型名: `"GUILD_MEMBER_UPDATE"`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/guild_member.html#guild-member-update">`GUILD_MEMBER_UPDATE`</a>

**发送时机**

- 用户的频道属性发生变化，如频道昵称，或者身份组

</def>
<def title="GuildMemberRemove" id="love_forte_simbot_qguild_event_GuildMemberRemove">

`love.forte.simbot.qguild.event.GuildMemberRemove`

事件类型名: `"GUILD_MEMBER_REMOVE"`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/guild_member.html#guild-member-remove">`GUILD_MEMBER_REMOVE`</a>

**发送时机**

- 用户离开频道

</def>
<def title="MessageDispatch" id="love_forte_simbot_qguild_event_MessageDispatch">

`love.forte.simbot.qguild.event.MessageDispatch`

与 `message` 相关的事件类型。 `data` 类型为 `Message`

</def>
<def title="AtMessageCreate" id="love_forte_simbot_qguild_event_AtMessageCreate">

`love.forte.simbot.qguild.event.AtMessageCreate`

事件类型名: `"AT_MESSAGE_CREATE"`

消息事件
<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/message.html#at-message-create-intents-public-guild-messages">`AT_MESSAGE_CREATE（intents PUBLIC_GUILD_MESSAGES）`</a>

**发送时机**

- 用户发送消息，@当前机器人或回复机器人消息时
- 为保障消息投递的速度，消息顺序我们虽然会尽量有序，但是并不保证是严格有序的，
  如开发者对消息顺序有严格有序的需求，可以自行缓冲消息事件之后，基于 `seq` 进行排序

</def>
<def title="PublicMessageDeleteCreate" id="love_forte_simbot_qguild_event_PublicMessageDeleteCreate">

`love.forte.simbot.qguild.event.PublicMessageDeleteCreate`

事件类型名: `"PUBLIC_MESSAGE_DELETE"`

消息事件
`PUBLIC_MESSAGE_DELETE_TYPE`

</def>
<def title="DirectMessageCreate" id="love_forte_simbot_qguild_event_DirectMessageCreate">

`love.forte.simbot.qguild.event.DirectMessageCreate`

事件类型名: `"DIRECT_MESSAGE_CREATE"`

私信消息事件
<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/direct_message.html#direct-message-create-intents-direct-message">`DIRECT_MESSAGE_CREATE (intents DIRECT_MESSAGE)`</a>

**发送时机**

- 用户通过私信发消息给机器人时

</def>
<def title="MessageAuditedDispatch" id="love_forte_simbot_qguild_event_MessageAuditedDispatch">

`love.forte.simbot.qguild.event.MessageAuditedDispatch`

与 `MessageAudited` 相关的事件类型。 `data` 类型为 `MessageAudited` 。

</def>
<def title="MessageCreate" id="love_forte_simbot_qguild_event_MessageCreate">

`love.forte.simbot.qguild.event.MessageCreate`

事件类型名: `"MESSAGE_CREATE"`

发送消息事件，代表频道内的全部消息，而不只是 at 机器人的消息。内容与 AT_MESSAGE_CREATE 相同

</def>
<def title="MessageDelete" id="love_forte_simbot_qguild_event_MessageDelete">

`love.forte.simbot.qguild.event.MessageDelete`

事件类型名: `"MESSAGE_DELETE"`

删除（撤回）消息事件

</def>
<def title="MessageAuditPass" id="love_forte_simbot_qguild_event_MessageAuditPass">

`love.forte.simbot.qguild.event.MessageAuditPass`

事件类型名: `"MESSAGE_AUDIT_PASS"`

消息审核事件
<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/message.html#message-audit-pass-intents-message-audit">`MESSAGE_AUDIT_PASS（intents MESSAGE_AUDIT）`</a>

**发送时机**

- 消息审核通过

</def>
<def title="MessageAuditReject" id="love_forte_simbot_qguild_event_MessageAuditReject">

`love.forte.simbot.qguild.event.MessageAuditReject`

事件类型名: `"MESSAGE_AUDIT_REJECT"`

消息审核事件
<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/message.html#message-audit-reject-intents-message-audit">`MESSAGE_AUDIT_REJECT（intents MESSAGE_AUDIT）`</a>

**发送时机**

- 消息审核不通过

</def>
<def title="OpenForumDispatch" id="love_forte_simbot_qguild_event_OpenForumDispatch">

`love.forte.simbot.qguild.event.OpenForumDispatch`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/open_forum.html">开放论坛事件(OpenForumEvent)</a> 相关的事件父类。

**发送时机**

用户在话题子频道内发帖、评论、回复评论时产生该事件

**主题事件**

- OPEN_FORUM_THREAD_CREATE
- OPEN_FORUM_THREAD_UPDATE
- OPEN_FORUM_THREAD_DELETE
  参考 `OpenForumThreadDispatch`

**帖子（评论）事件**

- OPEN_FORUM_POST_CREATE
- OPEN_FORUM_POST_DELETE
  参考 `OpenForumPostDispatch`

**回复事件**

- OPEN_FORUM_REPLY_CREATE
- OPEN_FORUM_REPLY_DELETE
  参考 `OpenForumReplyDispatch`

</def>
<def title="OpenForumThreadDispatch" id="love_forte_simbot_qguild_event_OpenForumThreadDispatch">

`love.forte.simbot.qguild.event.OpenForumThreadDispatch`

开放论坛事件的 **_主题事件_**。

</def>
<def title="OpenForumThreadCreate" id="love_forte_simbot_qguild_event_OpenForumThreadCreate">

`love.forte.simbot.qguild.event.OpenForumThreadCreate`

事件类型名: `"OPEN_FORUM_THREAD_CREATE"`

主题事件：创建主题

</def>
<def title="OpenForumThreadUpdate" id="love_forte_simbot_qguild_event_OpenForumThreadUpdate">

`love.forte.simbot.qguild.event.OpenForumThreadUpdate`

事件类型名: `"OPEN_FORUM_THREAD_UPDATE"`

主题事件：更新主题

</def>
<def title="OpenForumThreadDelete" id="love_forte_simbot_qguild_event_OpenForumThreadDelete">

`love.forte.simbot.qguild.event.OpenForumThreadDelete`

事件类型名: `"OPEN_FORUM_THREAD_DELETE"`

主题事件：删除主题

</def>
<def title="OpenForumPostDispatch" id="love_forte_simbot_qguild_event_OpenForumPostDispatch">

`love.forte.simbot.qguild.event.OpenForumPostDispatch`

开放论坛事件的 **_帖子（评论）事件_**。

</def>
<def title="OpenForumPostCreate" id="love_forte_simbot_qguild_event_OpenForumPostCreate">

`love.forte.simbot.qguild.event.OpenForumPostCreate`

事件类型名: `"OPEN_FORUM_POST_CREATE"`

帖子事件：创建帖子（评论）

</def>
<def title="OpenForumPostDelete" id="love_forte_simbot_qguild_event_OpenForumPostDelete">

`love.forte.simbot.qguild.event.OpenForumPostDelete`

事件类型名: `"OPEN_FORUM_POST_DELETE"`

帖子事件：删除帖子（评论）

</def>
<def title="OpenForumReplyDispatch" id="love_forte_simbot_qguild_event_OpenForumReplyDispatch">

`love.forte.simbot.qguild.event.OpenForumReplyDispatch`

开放论坛事件的 **_回复事件_**。

</def>
<def title="OpenForumReplyCreate" id="love_forte_simbot_qguild_event_OpenForumReplyCreate">

`love.forte.simbot.qguild.event.OpenForumReplyCreate`

事件类型名: `"OPEN_FORUM_REPLY_CREATE"`

回复事件：创建回复

</def>
<def title="OpenForumReplyDelete" id="love_forte_simbot_qguild_event_OpenForumReplyDelete">

`love.forte.simbot.qguild.event.OpenForumReplyDelete`

事件类型名: `"OPEN_FORUM_REPLY_DELETE"`

回复事件：删除回复

</def>

</deflist>

API 模块事件封装可以使用在 **标准库模块 (stdlib)** 中，使用 `Bot` 类型对他们进行监听与处理。


## Simbot 标准库 Event 实现

使用核心库，可以在 simbot4 的 `Application` 或 Spring Boot 中使用这些事件类型实现。

核心模块所有的 simbot Event 实现类型定义都在包 `love.forte.simbot.component.qguild.event` 中。

所有实现类型均继承 `love.forte.simbot.qguild.component.event.QGEvent`。

> QQ频道的 simbot 事件实现会根据含义，选择性的实现一些特定的类型。
> 举个例子，`QGAtMessageCreateEvent` 可以代表“子频道消息事件”，
> 因此它实现了 `ChatChannelMessageEvent`。

<tip>
仔细观察可以发现，大部分 simbot Event 实现类型都可以与 API 模块的事件封装类型相对应。
</tip>

<deflist type="wide">
<def title="QGChannelCreateEvent">子频道创建事件</def>
<def title="QGChannelUpdateEvent">子频道修改事件</def>
<def title="QGChannelDeleteEvent">子频道删除事件</def>
<def title="QGForumThreadCreateEvent">主题创建事件</def>
<def title="QGForumThreadUpdateEvent">主题更新事件</def>
<def title="QGForumThreadDeleteEvent">主题删除事件</def>
<def title="QGForumPostCreateEvent">帖子创建事件</def>
<def title="QGForumPostDeleteEvent">帖子删除事件</def>
<def title="QGForumReplyCreateEvent">回复创建事件</def>
<def title="QGForumReplyDeleteEvent">回复删除事件</def>
<def title="QGForumPublishAuditResultEvent">帖子审核事件</def>
<def title="QGGuildCreateEvent">新用户加入频道事件</def>
<def title="QGGuildUpdateEvent">用户的频道属性发生变化事件</def>
<def title="QGGuildDeleteEvent">用户离开频道事件</def>
<def title="QGMemberAddEvent">新用户加入频道事件</def>
<def title="QGMemberUpdateEvent">用户的频道属性发生变化事件</def>
<def title="QGMemberRemoveEvent">用户离开频道事件</def>
<def title="QGAtMessageCreateEvent">收到公域at消息事件</def>
<def title="QGOpenForumThreadCreateEvent">"开放"创建主题事件</def>
<def title="QGOpenForumThreadUpdateEvent">"开放"更新主题事件</def>
<def title="QGOpenForumThreadDeleteEvent">"开放"删除主题事件</def>
<def title="QGOpenForumPostCreateEvent">"开放"帖子创建(评论)事件</def>
<def title="QGOpenForumPostDeleteEvent">"开放"帖子删除(评论)事件</def>
<def title="QGOpenForumReplyCreateEvent">"开放"回复创建事件</def>
<def title="QGOpenForumReplyDeleteEvent">"开放"回复删除事件</def>
<def title="QGUnsupportedEvent">
特殊的事件类型，用于包装兼容那些尚未被封装支持的 API 模块的事件封装类型。
</def>
</deflist>
