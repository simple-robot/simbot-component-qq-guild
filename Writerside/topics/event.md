# 事件类型

QQ频道组件中的**事件类型**包含两个层面：

1. **API 模块** 中，对 QQ频道 API 中官方定义的事件结构的基本封装与实现。
2. **核心模块** 中，基于 API 模块中的事件封装，对 simbot4 标准库中的 `Event` 事件类型的实现。


## API 模块事件封装

API 模块所有的事件封装类型都在包 `love.forte.simbot.qguild.event` 中，
并且基本上命名与官网API中的事件类型名称有一定关联。

所有事件封装类型均继承密封类 `love.forte.simbot.qguild.event.Signal.Dispatch`。 

<deflist type="wide">
<def title="ChannelCreate">子频道创建事件 <code>CHANNEL_CREATE</code> </def>
<def title="ChannelUpdate">子频道修改事件 <code>CHANNEL_UPDATE</code> </def>
<def title="ChannelDelete">子频道删除事件 <code>CHANNEL_DELETE</code> </def>
<def title="ForumThreadCreate">主题创建事件 <code>FORUM_THREAD_CREATE</code></def>
<def title="ForumThreadUpdate">主题更新事件 <code>FORUM_THREAD_UPDATE</code></def>
<def title="ForumThreadDelete">主题删除事件 <code>FORUM_THREAD_DELETE</code></def>
<def title="ForumPostCreate">帖子创建事件 <code>FORUM_POST_CREATE</code></def>
<def title="ForumPostDelete">帖子删除事件 <code>FORUM_POST_DELETE</code></def>
<def title="ForumReplyCreate">回复创建事件 <code>FORUM_REPLY_CREATE</code> </def>
<def title="ForumReplyDelete">回复删除事件 <code>FORUM_REPLY_DELETE</code> </def>
<def title="ForumPublishAuditResult">帖子审核事件 <code>FORUM_PUBLISH_AUDIT_RESULT</code> </def>
<def title="OpenForumThreadCreate">"开放"创建主题事件 <code>OPEN_FORUM_THREAD_CREATE</code> </def>
<def title="OpenForumThreadUpdate">"开放"更新主题事件 <code>OPEN_FORUM_THREAD_UPDATE</code> </def>
<def title="OpenForumThreadDelete">"开放"删除主题事件 <code>OPEN_FORUM_THREAD_DELETE</code> </def>
<def title="OpenForumPostCreate">"开放"帖子创建(评论)事件 <code>OPEN_FORUM_POST_CREATE</code> </def>
<def title="OpenForumPostDelete">"开放"帖子删除(评论)事件 <code>OPEN_FORUM_POST_DELETE</code> </def>
<def title="OpenForumReplyCreate">"开放"回复创建事件 <code>OPEN_FORUM_REPLY_CREATE</code> </def>
<def title="OpenForumReplyDelete">"开放"回复删除事件 <code>OPEN_FORUM_REPLY_DELETE</code> </def>
<def title="GuildCreate">Bot加入频道事件 <code>GUILD_CREATE</code></def>
<def title="GuildUpdate">频道信息变更事件 <code>GUILD_UPDATE</code></def>
<def title="GuildDelete">Bot因各种原因退出频道事件 <code>GUILD_DELETE</code></def>
<def title="GuildMemberAdd">新用户加入频道事件 <code>GUILD_MEMBER_ADD</code></def>
<def title="GuildMemberUpdate">用户的频道属性发生变化事件 <code>GUILD_MEMBER_UPDATE</code></def>
<def title="GuildMemberRemove">用户离开频道事件 <code>GUILD_MEMBER_REMOVE</code></def>
<def title="AtMessageCreate">收到公域at消息事件 <code>AT_MESSAGE_CREATE</code></def>
<def title="PublicMessageDeleteCreate">
<code>PUBLIC_MESSAGE_DELETE</code>
<warning>此事件官网文档似乎没有详细说明，请慎重使用</warning>
</def>
<def title="DirectMessageCreate">私信消息事件 <code>DIRECT_MESSAGE_CREATE</code></def>
<def title="MessageCreate">(私域)发送消息事件 <code>MESSAGE_CREATE</code></def>
<def title="MessageDelete">
<code>MESSAGE_DELETE</code>
<warning>此事件官网文档似乎没有详细说明，请慎重使用</warning>
</def>
<def title="MessageAuditPass">消息审核通过事件 <code>MESSAGE_AUDIT_PASS</code></def>
<def title="MessageAuditReject">消息审核不通过事件 <code>MESSAGE_AUDIT_REJECT</code></def>
</deflist>

API 模块事件封装可以使用在 **标准库模块 (stdlib)** 中，使用 `Bot` 类型对他们进行监听与处理。


## simbot Event 实现

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
