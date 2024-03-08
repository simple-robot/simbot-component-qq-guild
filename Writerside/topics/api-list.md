# API定义列表

此处会列举 `API 模块` 中、`love.forte.simbot.qguild.api` 包下定义的所有 `QQGuildApi` 实现。

> 对于一个具体的API的详细说明，我们建议你前往 [API 文档](https://docs.simbot.forte.love/) 或源码注释查阅，
> 因为那是最贴合真实情况且最全面的。

<deflist>
<def title="GatewayApis" id="love_forte_simbot_qguild_api_GatewayApis">

`love.forte.simbot.qguild.api.GatewayApis`

获取网关信息。

通过 `Normal` 或 `Shared` 的形式根据bot信息获取使用 Websocket 接入时间通知的链接。

> <a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/reference.html">参考文档</a>

<deflist>
<def title="Normal" id="love_forte_simbot_qguild_api_GatewayApis_Normal">

`love.forte.simbot.qguild.api.Normal`

获取通用 WSS 接入点

> <a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/wss/url_get.html">参考文档</a>


</def>
<def title="Shared" id="love_forte_simbot_qguild_api_GatewayApis_Shared">

`love.forte.simbot.qguild.api.Shared`

获取带分片 WSS 接入点

> <a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/wss/shard_url_get.html">参考文档</a>


</def>

</deflist>
</def>
<def title="CreateAnnouncesApi" id="love_forte_simbot_qguild_api_announces_CreateAnnouncesApi">

`love.forte.simbot.qguild.api.announces.CreateAnnouncesApi`

机器人设置消息为指定子频道公告。

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/announces/post_channel_announces.html">创建子频道公告</a>




</def>
<def title="DeleteAnnouncesApi" id="love_forte_simbot_qguild_api_announces_DeleteAnnouncesApi">

`love.forte.simbot.qguild.api.announces.DeleteAnnouncesApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/announces/delete_channel_announces.html">删除子频道公告</a>

机器人删除指定子频道公告



</def>
<def title="DemandApiPermissionApi" id="love_forte_simbot_qguild_api_apipermission_DemandApiPermissionApi">

`love.forte.simbot.qguild.api.apipermission.DemandApiPermissionApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/api_permissions/post_api_permission_demand.html">创建频道 API 接口权限授权链接</a>

用于创建 API 接口权限授权链接，该链接指向 `guild_id` 对应的频道 。

需要注意，私信场景中，当需要查询私信来源频道的权限时，应使用 `src_guild_id` ，即 <a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#message">message</a>
中的 `src_guild_id`

每天只能在一个频道内发 `3` 条（默认值）频道权限授权链接。



</def>
<def title="GetApiPermissionListApi" id="love_forte_simbot_qguild_api_apipermission_GetApiPermissionListApi">

`love.forte.simbot.qguild.api.apipermission.GetApiPermissionListApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/api_permissions/get_guild_api_permission.html">获取频道可用权限列表</a>

用于获取机器人在频道 `guild_id` 内可以使用的权限列表。



</def>
<def title="CreateChannelApi" id="love_forte_simbot_qguild_api_channel_CreateChannelApi">

`love.forte.simbot.qguild.api.channel.CreateChannelApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/channel/post_channels.html">创建子频道</a>

用于在 `guild_id` 指定的频道下创建一个子频道。

- 要求操作人具有管理频道的权限，如果是机器人，则需要将机器人设置为管理员。
- 创建成功后，返回创建成功的子频道对象，同时会触发一个频道创建的事件通知。



</def>
<def title="DeleteChannelApi" id="love_forte_simbot_qguild_api_channel_DeleteChannelApi">

`love.forte.simbot.qguild.api.channel.DeleteChannelApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/channel/delete_channel.html">删除子频道</a>

用于删除 `channel_id` 指定的子频道。

- 要求操作人具有 `管理子频道` 的权限，如果是机器人，则需要将机器人设置为管理员。
- 修改成功后，会触发子频道删除事件。


**注意**

子频道的删除是无法撤回的，一旦删除，将无法恢复。



</def>
<def title="GetChannelApi" id="love_forte_simbot_qguild_api_channel_GetChannelApi">

`love.forte.simbot.qguild.api.channel.GetChannelApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/channel/get_channel.html">获取子频道信息</a>



</def>
<def title="GetChannelOnlineNumsApi" id="love_forte_simbot_qguild_api_channel_GetChannelOnlineNumsApi">

`love.forte.simbot.qguild.api.channel.GetChannelOnlineNumsApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/channel/get_online_nums.html">获取在线成员数</a>

用于查询音视频/直播子频道 channel_id 的在线成员数。



</def>
<def title="GetGuildChannelListApi" id="love_forte_simbot_qguild_api_channel_GetGuildChannelListApi">

`love.forte.simbot.qguild.api.channel.GetGuildChannelListApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/channel/get_channels.html">获取子频道列表</a>

用于获取 `guild_id` 指定的频道下的子频道列表。



</def>
<def title="ModifyChannelApi" id="love_forte_simbot_qguild_api_channel_ModifyChannelApi">

`love.forte.simbot.qguild.api.channel.ModifyChannelApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/channel/patch_channel.html">修改子频道</a>

用于修改 `channel_id` 指定的子频道的信息。

- 要求操作人具有 `管理子频道` 的权限，如果是机器人，则需要将机器人设置为管理员。
- 修改成功后，会触发子频道更新事件。



</def>
<def title="GetChannelMemberPermissionsApi" id="love_forte_simbot_qguild_api_channel_permissions_GetChannelMemberPermissionsApi">

`love.forte.simbot.qguild.api.channel.permissions.GetChannelMemberPermissionsApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/get_channel_permissions.html">获取指定子频道的权限</a>

用于获取 子频道 `channel_id` 下用户 `user_id` 的权限。

- 获取子频道用户权限。
- 要求操作人具有管理子频道的权限，如果是机器人，则需要将机器人设置为管理员。



</def>
<def title="GetChannelRolePermissionsApi" id="love_forte_simbot_qguild_api_channel_permissions_GetChannelRolePermissionsApi">

`love.forte.simbot.qguild.api.channel.permissions.GetChannelRolePermissionsApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/get_channel_roles_permissions.html">获取子频道身份组权限</a>

用于获取子频道 `channel_id` 下身份组 `role_id` 的权限。

- 要求操作人具有管理子频道的权限，如果是机器人，则需要将机器人设置为管理员。



</def>
<def title="ModifyChannelMemberPermissionsApi" id="love_forte_simbot_qguild_api_channel_permissions_ModifyChannelMemberPermissionsApi">

`love.forte.simbot.qguild.api.channel.permissions.ModifyChannelMemberPermissionsApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/put_channel_permissions.html">修改子频道权限</a>

用于修改子频道 `channel_id` 下用户 `user_id` 的权限。

- 要求操作人具有 `管理子频道` 的权限，如果是机器人，则需要将机器人设置为管理员。
- 参数包括 `add` 和 `remove` 两个字段，分别表示授予的权限以及删除的权限。
  要授予用户权限即把 `add` 对应位置 1，删除用户权限即把 `remove` 对应位置 1。当两个字段同一位都为 1，表现为删除权限。
- 本接口不支持修改 `可管理子频道` 权限。



</def>
<def title="ModifyChannelRolePermissionsApi" id="love_forte_simbot_qguild_api_channel_permissions_ModifyChannelRolePermissionsApi">

`love.forte.simbot.qguild.api.channel.permissions.ModifyChannelRolePermissionsApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/put_channel_roles_permissions.html">修改子频道身份组权限</a>

用于修改子频道 `channel_id` 下身份组 `role_id` 的权限。

- 要求操作人具有管理子频道的权限，如果是机器人，则需要将机器人设置为管理员。
- 参数包括 `add` 和 `remove` 两个字段，分别表示授予的权限以及删除的权限。
  要授予身份组权限即把 `add` 对应位置 1，删除身份组权限即把 `remove` 对应位置 1。当两个字段同一位都为 1，表现为删除权限。
- 本接口不支持修改 `可管理子频道` 权限。



</def>
<def title="AddPinsMessageApi" id="love_forte_simbot_qguild_api_channel_pins_AddPinsMessageApi">

`love.forte.simbot.qguild.api.channel.pins.AddPinsMessageApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/pins/put_pins_message.html">添加精华消息</a>

用于添加子频道 `channel_id` 内的精华消息。

- 精华消息在一个子频道内最多只能创建 `20` 条。
- 只有可见的消息才能被设置为精华消息。
- 接口返回对象中 `message_ids` 为当前请求后子频道内所有精华消息 `message_id` 数组。



</def>
<def title="DeletePinsMessageApi" id="love_forte_simbot_qguild_api_channel_pins_DeletePinsMessageApi">

`love.forte.simbot.qguild.api.channel.pins.DeletePinsMessageApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/pins/delete_pins_message.html">删除精华消息</a>

用于删除子频道 `channel_id` 下指定 `message_id` 的精华消息。

- 删除子频道内全部精华消息，请将 `message_id` 设置为 [`all`] `DELETE_ALL_MESSAGE_ID` 。



</def>
<def title="GetPinsMessageApi" id="love_forte_simbot_qguild_api_channel_pins_GetPinsMessageApi">

`love.forte.simbot.qguild.api.channel.pins.GetPinsMessageApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/pins/get_pins_message.html">获取精华消息</a>

用于获取子频道 `channel_id` 内的精华消息。



</def>
<def title="CreateScheduleApi" id="love_forte_simbot_qguild_api_channel_schedules_CreateScheduleApi">

`love.forte.simbot.qguild.api.channel.schedules.CreateScheduleApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/schedule/post_schedule.html">创建日程</a>

用于在 `channel_id` 指定的 `日程子频道` 下创建一个日程。

- 要求操作人具有 `管理频道` 的权限，如果是机器人，则需要将机器人设置为管理员。
- 创建成功后，返回创建成功的日程对象。
- 创建操作频次限制
- 单个管理员每天限 `10` 次。
- 单个频道每天 `100` 次。



</def>
<def title="DeleteScheduleApi" id="love_forte_simbot_qguild_api_channel_schedules_DeleteScheduleApi">

`love.forte.simbot.qguild.api.channel.schedules.DeleteScheduleApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/schedule/patch_schedule.html">修改日程</a>

用于修改日程子频道 `channel_id` 下 `schedule_id` 指定的日程的详情。

- 要求操作人具有 `管理频道` 的权限，如果是机器人，则需要将机器人设置为管理员。



</def>
<def title="GetScheduleApi" id="love_forte_simbot_qguild_api_channel_schedules_GetScheduleApi">

`love.forte.simbot.qguild.api.channel.schedules.GetScheduleApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/schedule/get_schedule.html">获取日程详情</a>

获取日程子频道 `channel_id` 下 `schedule_id` 指定的的日程的详情。



</def>
<def title="GetScheduleListApi" id="love_forte_simbot_qguild_api_channel_schedules_GetScheduleListApi">

`love.forte.simbot.qguild.api.channel.schedules.GetScheduleListApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/schedule/get_schedules.html">获取频道日程列表</a>

用于获取 `channel_id` 指定的子频道中当天的日程列表。

- 若带了参数 `since`，则返回在 `since` 对应当天的日程列表；若未带参数 `since`，则默认返回今天的日程列表。



</def>
<def title="ModifyScheduleApi" id="love_forte_simbot_qguild_api_channel_schedules_ModifyScheduleApi">

`love.forte.simbot.qguild.api.channel.schedules.ModifyScheduleApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/schedule/patch_schedule.html">修改日程</a>

用于修改日程子频道 `channel_id` 下 `schedule_id` 指定的日程的详情。

- 要求操作人具有 `管理频道` 的权限，如果是机器人，则需要将机器人设置为管理员。



</def>
<def title="DeleteThreadApi" id="love_forte_simbot_qguild_api_forum_DeleteThreadApi">

`love.forte.simbot.qguild.api.forum.DeleteThreadApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/forum/delete_thread.html">删除帖子</a>

该接口用于删除指定子频道下的某个帖子。



</def>
<def title="GetThreadApi" id="love_forte_simbot_qguild_api_forum_GetThreadApi">

`love.forte.simbot.qguild.api.forum.GetThreadApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/forum/get_thread.html">获取帖子详情</a>

该接口用于获取子频道下的帖子详情。




</def>
<def title="GetThreadListApi" id="love_forte_simbot_qguild_api_forum_GetThreadListApi">

`love.forte.simbot.qguild.api.forum.GetThreadListApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/forum/get_threads_list.html">获取帖子列表</a>

该接口用于获取子频道下的帖子列表。



</def>
<def title="PublishThreadApi" id="love_forte_simbot_qguild_api_forum_PublishThreadApi">

`love.forte.simbot.qguild.api.forum.PublishThreadApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/forum/put_thread.html">发表帖子</a>




</def>
<def title="GetGuildApi" id="love_forte_simbot_qguild_api_guild_GetGuildApi">

`love.forte.simbot.qguild.api.guild.GetGuildApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/guild/get_guild.html">获取频道详情</a>

用于获取 `guildId` 指定的频道的详情。


</def>
<def title="MuteAllApi" id="love_forte_simbot_qguild_api_guild_mute_MuteAllApi">

`love.forte.simbot.qguild.api.guild.mute.MuteAllApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/guild/patch_guild_mute.html">禁言全员</a>

用于将频道的全体成员（非管理员）禁言。

需要使用的 `token` 对应的用户具备管理员权限。如果是机器人，要求被添加为管理员。
该接口同样可用于解除禁言，将 `mute_end_timestamp` 或 `mute_seconds` 传值为字符串'0'即可。





</def>
<def title="MuteMemberApi" id="love_forte_simbot_qguild_api_guild_mute_MuteMemberApi">

`love.forte.simbot.qguild.api.guild.mute.MuteMemberApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/guild/patch_guild_member_mute.html">禁言指定成员</a>

用于禁言频道 `guild_id` 下的成员 `user_id`。

需要使用的 `token` 对应的用户具备管理员权限。如果是机器人，要求被添加为管理员。
该接口同样可用于解除禁言，将 `mute_end_timestamp` 或 `mute_seconds` 传值为字符串'0'即可。



</def>
<def title="MuteMultiMemberApi" id="love_forte_simbot_qguild_api_guild_mute_MuteMultiMemberApi">

`love.forte.simbot.qguild.api.guild.mute.MuteMultiMemberApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/guild/patch_guild_mute_multi_member.html">禁言批量成员</a>

用于将频道的指定批量成员（非管理员）禁言。

需要使用的 `token` 对应的用户具备管理员权限。如果是机器人，要求被添加为管理员。
该接口同样可用于批量解除禁言，将 `mute_end_timestamp` 或 `mute_seconds` 传值为字符串'0'即可，及需要批量解除禁言的成员的user_id列表user_ids'。



</def>
<def title="DeleteMemberApi" id="love_forte_simbot_qguild_api_member_DeleteMemberApi">

`love.forte.simbot.qguild.api.member.DeleteMemberApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/member/delete_member.html">删除频道成员</a>

用于删除 guild_id 指定的频道下的成员 user_id。

- 需要使用的 token 对应的用户具备踢人权限。如果是机器人，要求被添加为管理员。
- 操作成功后，会触发频道成员删除事件。
- 无法移除身份为管理员的成员



</def>
<def title="GetGuildMemberListApi" id="love_forte_simbot_qguild_api_member_GetGuildMemberListApi">

`love.forte.simbot.qguild.api.member.GetGuildMemberListApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/member/get_members.html">获取频道成员列表</a>

用于获取 guild_id 指定的频道中所有成员的详情列表，支持分页。


**有关返回结果的说明**

1. 在每次翻页的过程中，可能会返回上一次请求已经返回过的 `member` 信息，需要调用方自己根据 `user id` 来进行去重。
2. 每次返回的 `member` 数量与 `limit` 不一定完全相等。翻页请使用最后一个 `member` 的 `user id` 作为下一次请求的 `after` 参数，直到回包为空，拉取结束。




</def>
<def title="GetGuildRoleMemberListApi" id="love_forte_simbot_qguild_api_member_GetGuildRoleMemberListApi">

`love.forte.simbot.qguild.api.member.GetGuildRoleMemberListApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/member/get_role_members.html">获取频道身份组成员列表</a>

用于获取 `guild_id` 频道中指定 `role_id` 身份组下所有成员的详情列表，支持分页。


**有关返回结果的说明**

1. 每次返回的member数量与limit不一定完全相等。特定管理身份组下的成员可能存在一次性返回全部的情况



</def>
<def title="GetMemberApi" id="love_forte_simbot_qguild_api_member_GetMemberApi">

`love.forte.simbot.qguild.api.member.GetMemberApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/member/get_member.html">获取某个成员信息</a>

用于获取 `guild_id` 指定的频道中 `user_id` 对应成员的详细信息。



</def>
<def title="DeleteMessageApi" id="love_forte_simbot_qguild_api_message_DeleteMessageApi">

`love.forte.simbot.qguild.api.message.DeleteMessageApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/message/delete_message.html">撤回消息</a>

用于撤回子频道 `channel_id` 下的消息 `message_id`。

- 管理员可以撤回普通成员的消息。
- 频道主可以撤回所有人的消息。



</def>
<def title="GetMessageApi" id="love_forte_simbot_qguild_api_message_GetMessageApi">

`love.forte.simbot.qguild.api.message.GetMessageApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/message/get_message_of_id.html">获取指定消息</a>

用于获取子频道 `channel_id` 下的消息 `message_id` 的详情。



</def>
<def title="MessageSendApi" id="love_forte_simbot_qguild_api_message_MessageSendApi">

`love.forte.simbot.qguild.api.message.MessageSendApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages.html">发送消息</a>

用于向 `channel_id` 指定的子频道发送消息。

- 要求操作人在该子频道具有 `发送消息` 的权限。
- 主动消息在频道主或管理设置了情况下，按设置的数量进行限频。在未设置的情况遵循如下限制:
- 主动推送消息，默认每天往每个子频道可推送的消息数是 `20` 条，超过会被限制。
- 主动推送消息在每个频道中，每天可以往 `2` 个子频道推送消息。超过后会被限制。
- 不论主动消息还是被动消息，在一个子频道中，每 `1s` 只能发送 `5` 条消息。
- 被动回复消息有效期为 `5` 分钟。超时会报错。
- 发送消息接口要求机器人接口需要连接到 websocket 上保持在线状态
- 有关主动消息审核，可以通过 <a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/gateway/intents.html">Intents</a>
  中审核事件 `MESSAGE_AUDIT` 返回 <a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/message/model.html#messageaudited">MessageAudited</a> 对象获取结果。

<hr />


**主动消息与被动消息**

- 主动消息：发送消息时，未填充 `msg_id/event_id` 字段的消息。
- 被动消息：发送消息时，填充了 `msg_id/event_id` 字段的消息。`msg_id` 和 `event_id` 两个字段任意填一个即为被动消息。
  接口使用此 `msg_id/event_id` 拉取用户的消息或事件，同时判断用户消息或事件的发送时间，如果超过被动消息回复时效，将会不允许发送该消息。

更多参考 <a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages.html#%E4%B8%BB%E5%8A%A8%E6%B6%88%E6%81%AF%E4%B8%8E%E8%A2%AB%E5%8A%A8%E6%B6%88%E6%81%AF">文档</a>


**发送 ARK 模板消息**

通过指定 `ark` 字段发送模板消息。

- 要求操作人在该子频道具有发送消息和 对应 `ARK 模板` 的权限。
- 调用前需要先申请消息模板，这一步会得到一个模板 `id`，在请求时填在 `ark.template_id` 上。
- 发送成功之后，会触发一个创建消息的事件。
- 可用模板参考<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/message/message_template.html">可用模板</a>。

更多参考 <a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/message/post_ark_messages.html">文档</a>


**发送引用消息**


- 只支持引用机器人自己发送到的消息以及用户@机器人产生的消息。
- 发送成功之后，会触发一个创建消息的事件。

不能单独发送引用消息，引用消息需要和其他消息类型组合发送，参数请见<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages.html">发送消息</a>。

更多参考 <a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages_reference.html">文档</a>


**发送含有消息按钮组件的消息**


通过指定 `keyboard` 字段发送带按钮的消息，支持 `keyboard 模版` 和 `自定义 keyboard` 两种请求格式。

- 要求操作人在该子频道具有 `发送消息` 和 `对应消息按钮组件` 的权限。
- 请求参数 `keyboard 模版` 和 `自定义 keyboard` 只能单一传值。
- `keyboard 模版`
- 调用前需要先申请消息按钮组件模板，这一步会得到一个模板 id，在请求时填在 `keyboard` 字段上。
- 申请消息按钮组件模板需要提供响应的 json，具体格式参考 <a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/message/message_keyboard.html#inlinekeyboard">InlineKeyboard</a>。
- 仅 `markdown` 消息支持消息按钮。

更多参考 <a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/message/post_keyboard_messages.html">文档</a>


**内嵌格式**

利用 `content` 字段发送内嵌格式的消息。

- 内嵌格式仅在 `content` 中会生效，在 `Ark` 和 `Embed` 中不生效。
- 为了区分是文本还是内嵌格式，消息抄送和发送会对消息内容进行相关的转义。


**转义内容**


| **源字符** | **转义后** |
|----------|----------|
| `&` | `&amp;` |
| `<` | `&lt;` |
| `>` | `&gt;` |

可参考使用 `ContentTextDecoder` 和 `ContentTextEncoder`


**消息审核**


> 其中推送、回复消息的 code 错误码 `304023`、`304024` 会在 响应数据包 `data` 中返回 `MessageAudit` 审核消息的信息

当响应结果为上述错误码时，请求实体对象结果的API时会抛出 `MessageAuditedException` 异常并携带相关的对象信息。

详见文档 <a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages.html">发送消息</a> 中的相关描述以及
`MessageAuditedException` 的文档描述。

<hr />

更多参考 <a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html">文档</a>




</def>
<def title="CreateDmsApi" id="love_forte_simbot_qguild_api_message_direct_CreateDmsApi">

`love.forte.simbot.qguild.api.message.direct.CreateDmsApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/dms/post_dms.html">创建私信会话</a>

用于机器人和在同一个频道内的成员创建私信会话。

机器人和用户存在共同频道才能创建私信会话。
创建成功后，返回创建成功的频道 `id` ，子频道 `id` 和创建时间。


**参数**

| 字段名 | 类型 | 描述 |
|-----|-----|-----|
| `recipient_id` | `string` | 接收者 id |
| `source_guild_id` | `string` | 源频道 id |




</def>
<def title="DeleteDmsApi" id="love_forte_simbot_qguild_api_message_direct_DeleteDmsApi">

`love.forte.simbot.qguild.api.message.direct.DeleteDmsApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/dms/delete_dms.html">撤回私信</a>

用于撤回私信频道 `guild_id` 中 `message_id` 指定的私信消息。只能用于撤回机器人自己发送的私信。




</def>
<def title="DmsSendApi" id="love_forte_simbot_qguild_api_message_direct_DmsSendApi">

`love.forte.simbot.qguild.api.message.direct.DmsSendApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/dms/post_dms_messages.html">发送私信</a>

**接口**

`POST /dms/{guild_id}/messages`


**功能描述**

用于发送私信消息，前提是已经创建了私信会话。

* 私信的 `guild_id` 在创建私信会话时以及私信消息事件中获取。
* 私信场景下，每个机器人每天可以对一个用户发 `2` 条主动消息。
* 私信场景下，每个机器人每天累计可以发 `200` 条主动消息。
* 私信场景下，被动消息没有条数限制。


**参数**

和 [发送消息] `MessageSendApi` 参数一致。


**返回**

和 [发送消息] `MessageSendApi` 返回一致。





</def>
<def title="GetMessageSettingApi" id="love_forte_simbot_qguild_api_message_setting_GetMessageSettingApi">

`love.forte.simbot.qguild.api.message.setting.GetMessageSettingApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/setting/message_setting.html">获取频道消息频率设置</a>

用于获取机器人在频道 `guild_id` 内的消息频率设置。


</def>
<def title="AddMemberRoleApi" id="love_forte_simbot_qguild_api_role_AddMemberRoleApi">

`love.forte.simbot.qguild.api.role.AddMemberRoleApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/guild/put_guild_member_role.html">增加频道身份组成员</a>

用于将频道 `guild_id` 下的用户 `user_id` 添加到身份组 `role_id` 。

- 需要使用的 `token` 对应的用户具备增加身份组成员权限。如果是机器人，要求被添加为管理员。
- 如果要增加的身份组 `ID` 是 [`5-子频道管理员`] `love.forte.simbot.qguild.model.Role.DEFAULT_ID_CHANNEL_ADMIN` ，
  需要增加 `channel` 对象来指定具体是哪个子频道。



</def>
<def title="CreateGuildRoleApi" id="love_forte_simbot_qguild_api_role_CreateGuildRoleApi">

`love.forte.simbot.qguild.api.role.CreateGuildRoleApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/guild/post_guild_role.html">创建频道身份组</a>

用于在 `guild_id` 指定的频道下创建一个身份组。

- 需要使用的 `token` 对应的用户具备创建身份组权限。如果是机器人，要求被添加为管理员。
- 参数为非必填，但至少需要传其中之一，默认为空或 0。



</def>
<def title="DeleteGuildRoleApi" id="love_forte_simbot_qguild_api_role_DeleteGuildRoleApi">

`love.forte.simbot.qguild.api.role.DeleteGuildRoleApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/guild/delete_guild_role.html#%E5%88%A0%E9%99%A4%E9%A2%91%E9%81%93%E8%BA%AB%E4%BB%BD%E7%BB%84">删除频道身份组</a>

用于删除频道 `guild_id` 下 `role_id` 对应的身份组。

需要使用的 `token` 对应的用户具备删除身份组权限。如果是机器人，要求被添加为管理员。


</def>
<def title="GetGuildRoleListApi" id="love_forte_simbot_qguild_api_role_GetGuildRoleListApi">

`love.forte.simbot.qguild.api.role.GetGuildRoleListApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/guild/get_guild_roles.html#%E8%8E%B7%E5%8F%96%E9%A2%91%E9%81%93%E8%BA%AB%E4%BB%BD%E7%BB%84%E5%88%97%E8%A1%A8">获取频道身份组列表</a>

用于获取 `guild_id` 指定的频道下的身份组列表。



</def>
<def title="ModifyGuildRoleApi" id="love_forte_simbot_qguild_api_role_ModifyGuildRoleApi">

`love.forte.simbot.qguild.api.role.ModifyGuildRoleApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/guild/patch_guild_role.html#%E4%BF%AE%E6%94%B9%E9%A2%91%E9%81%93%E8%BA%AB%E4%BB%BD%E7%BB%84">修改频道身份组</a>

用于修改频道 `guild_id` 下 `role_id` 指定的身份组。

- 需要使用的 `token` 对应的用户具备修改身份组权限。如果是机器人，要求被添加为管理员。
- 接口会修改传入的字段，不传入的默认不会修改，至少要传入一个参数。



</def>
<def title="RemoveMemberRoleApi" id="love_forte_simbot_qguild_api_role_RemoveMemberRoleApi">

`love.forte.simbot.qguild.api.role.RemoveMemberRoleApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/guild/delete_guild_member_role.html">删除频道身份组成员</a>

用于将 用户 `user_id` 从 频道 `guild_id` 的 `role_id` 身份组中移除。

- 需要使用的 `token` 对应的用户具备删除身份组成员权限。如果是机器人，要求被添加为管理员。
- 如果要删除的身份组 `ID` 是 [`5-子频道管理员`] `love.forte.simbot.qguild.model.Role.DEFAULT_ID_CHANNEL_ADMIN` ，
  需要增加 `channel` 对象来指定具体是哪个子频道。



</def>
<def title="GetBotGuildListApi" id="love_forte_simbot_qguild_api_user_GetBotGuildListApi">

`love.forte.simbot.qguild.api.user.GetBotGuildListApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/user/guilds.html">获取用户频道列表</a>

用于获取当前用户（机器人）所加入的频道列表，支持分页。

当 `HTTP Authorization` 中填入 `Bot Token` 是获取机器人的数据，填入 `Bearer Token` 则获取用户的数据。


</def>
<def title="GetBotInfoApi" id="love_forte_simbot_qguild_api_user_GetBotInfoApi">

`love.forte.simbot.qguild.api.user.GetBotInfoApi`

<a ignore-vars="true" href="https://bot.q.qq.com/wiki/develop/api/openapi/user/me.html">获取用户详情</a>

用于获取当前用户（机器人）详情。

由于 `GetBotInfoApi` 本身为 `object` 类型, 因此 `ApiDescription` 由内部对象 `Description` 提供而不是伴生对象。

`GetBotInfoApi` 得到的 `User` 中， `User.isBot` 始终为 `true`。



</def>

</deflist>



