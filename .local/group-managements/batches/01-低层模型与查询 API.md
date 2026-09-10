# 批次 1：低层模型与查询 API

## 目标与前置

本批只建立可独立使用的 KMP 模型和 8 个 GET 请求对象，不改 core 的 `QGGroup` 行为，不提交写操作 API。完成后，调用方可按现有 `QQGuildApi.requestData(...)` 直接查询所有官方已列出的读资源；下批复用同一套模型实现写操作。

前置：先阅读 [官方资料](../reference/官方接口资料与原始示例.md) 和 [总体决策](../总体架构与一致性决策.md)。本批不得把官方 GET 页中误标的“请求体”实现为 HTTP body。

## 1. 新增文件与精确职责

| 文件 | 声明 | 职责 |
| --- | --- | --- |
| `api/src/commonMain/.../model/group/GroupInfo.kt` | `GroupInfo`、`GroupBotState` | `/info`、`/bot_state` 的返回体。 |
| `.../model/group/GroupMember.kt` | `GroupMember`、`GroupMemberPage`、`GroupBlacklistUser`、`GroupBlacklistPage`、移除/黑名单请求与回执模型 | 成员、成员列表、黑名单的公共结构。 |
| `.../model/group/GroupJoinRequest.kt` | `GroupJoinRequestPage`、`GroupJoinRequest`、`GroupJoinRequestVerifyInfo`、`GroupJoinRequestReviewQuestion`、`GroupJoinRequestApproval` | 入群申请列表与审批 body。 |
| `.../model/group/GroupMute.kt` | `GroupRestrictChatState`、`GroupGlobalMuteRule`、`GroupMuteScheduleRule`、`GroupMuteRecurringRule`、`GroupMemberMuteState`、`GroupMemberMuteUpdate`、`GroupMemberMuteUpdateRequest` | 查询和设置成员禁言。 |
| `.../model/group/JoinApprovalStrategy.kt` | 策略列表/记录、创建/更新/白名单请求和三种部分回执 | 全局入群自动审批策略的全部模型。 |
| `.../api/group/GetGroupInfoApi.kt` | `GetGroupInfoApi` | `GET /v2/groups/{group_openid}/info`。 |
| `.../api/group/GetGroupBotStateApi.kt` | `GetGroupBotStateApi` | `GET /v2/groups/{group_openid}/bot_state`。 |
| `.../api/group/GetGroupJoinRequestListApi.kt` | `GetGroupJoinRequestListApi` | `GET .../join_request_list?cursor&limit`。 |
| `.../api/group/GetGroupRestrictChatSettingApi.kt` | `GetGroupRestrictChatSettingApi` | `GET .../restrict_chat_setting`。 |
| `.../api/group/approval/GetJoinApprovalStrategyListApi.kt` | `GetJoinApprovalStrategyListApi` | `GET /v2/groups/join_approval_strategy?cursor&limit`。 |
| `.../api/group/member/GetGroupMemberListApi.kt` | `GetGroupMemberListApi` | `GET .../members?cursor`。 |
| `.../api/group/member/GetGroupMemberApi.kt` | `GetGroupMemberApi` | `GET .../members/{member_openid}`。 |
| `.../api/group/member/GetGroupMemberBlacklistApi.kt` | `GetGroupMemberBlacklistApi` | `GET .../member_blacklist?cursor&limit`。 |

所有 `public` 声明都写多行 KDoc。模型使用 `@ApiModel`、`@Serializable`，构造器使用现有 `@ApiModelConstructor`；字符串字段的 JSON 名和 Kotlin 名不同时逐个写 `@SerialName`，不要依赖全局命名策略。

## 2. 模型契约

以下 Kotlin 类型是实施时的唯一命名规范。括号内给出 JSON 字段；`?` 表示必须允许缺失或 null；列表/分页游标提供安全默认值，避免平台灰度字段缺失时整页解码失败。

### 2.1 群资料

```kotlin
@ApiModel @Serializable
public data class GroupInfo @ApiModelConstructor constructor(
    @SerialName("group_openid") public val groupOpenid: String,
    @SerialName("group_name") public val groupName: String = "",
    @SerialName("group_finger_memo") public val groupFingerMemo: String = "",
    @SerialName("group_class_text") public val groupClassText: String = "",
    @SerialName("group_tags") public val groupTags: List<String> = emptyList(),
    @SerialName("group_member_num") public val groupMemberNum: Int = 0,
)

@ApiModel @Serializable
public data class GroupBotState @ApiModelConstructor constructor(
    @SerialName("member_openid") public val memberOpenid: String,
    @SerialName("joined_at") public val joinedAt: String = "",
    @SerialName("allow_proactive_msg") public val allowProactiveMsg: Boolean = false,
    @SerialName("recv_msg_setting") public val recvMsgSetting: String = "",
    @SerialName("member_role") public val memberRole: String = "",
)
```

`GroupBotState` companion 提供 `RECV_MSG_SETTING_ALL`、`RECV_MSG_SETTING_ONLY_MENTION`、`RECV_MSG_SETTING_MENTION_AND_CONTEXT` 和 `MEMBER_ROLE_MEMBER/OWNER/ADMIN` 常量。即使字段今天有固定枚举，也不替换成 enum，理由见总体决策。

### 2.2 入群申请

```text
GroupJoinRequestPage
├─ list: List<GroupJoinRequest> = emptyList()                 (list)
└─ nextCursor: String = ""                                    (next_cursor)

GroupJoinRequest
├─ joinRequestId: String = ""                                (join_request_id)
├─ riskTips: String = ""                                     (risk_tips)
├─ unionOpenid: String?                                       (union_openid)
├─ memberOpenid: String = ""                                 (member_openid)
├─ username: String = ""                                     (username)
├─ applyAt: String = ""                                      (apply_at)
├─ applySource: String = ""                                  (apply_source)
├─ invitedBy: String?                                         (invited_by)
├─ bot: Boolean = false
└─ verifyInfo: GroupJoinRequestVerifyInfo?                    (verify_info)

GroupJoinRequestVerifyInfo
├─ method: String = ""
├─ verifyMessage: String?                                     (verify_message)
└─ reviewQaList: List<GroupJoinRequestReviewQuestion> = ...   (review_qa_list)

GroupJoinRequestReviewQuestion
├─ question: String = ""
└─ answer: String = ""
```

`GroupJoinRequest` 提供 `APPLY_SOURCE_SELF_APPLY`、`APPLY_SOURCE_INVITED`；验证对象提供 `METHOD_VERIFY_MESSAGE`、`METHOD_ADMIN_REVIEW_QA`。`riskTips`、验证消息和问答答案是敏感数据：实现 `toString()` 时不得手工把它们拼进日志；如使用 data class 自动 `toString`，不得在新代码中记录整对象。

审批 body 单独定义为：

```kotlin
@ApiModel @Serializable
public data class GroupJoinRequestApproval @ApiModelConstructor constructor(
    public val op: String,
    @SerialName("join_request_id") public val joinRequestId: String? = null,
    @SerialName("reject_reason") public val rejectReason: String? = null,
    @SerialName("add_to_member_blacklist") public val addToMemberBlacklist: Boolean? = null,
)
```

其 companion 固定提供 `OP_APPROVE = "approve"` 和 `OP_DECLINE = "decline"`。`addToMemberBlacklist` 不是 `decline` 以外操作的通用开关，core 便利方法只能在拒绝路径设置它。

### 2.3 禁言

```text
GroupRestrictChatState
├─ globalRule: GroupGlobalMuteRule?                           (global_rule)
└─ members: List<GroupMemberMuteState> = emptyList()

GroupGlobalMuteRule
├─ mode: String = ""
├─ scheduleRules: List<GroupMuteScheduleRule> = emptyList()   (schedule_rules)
└─ recurringRules: List<GroupMuteRecurringRule> = emptyList() (recurring_rules)

GroupMuteScheduleRule: taskId/startAt/endAt/enabled           (task_id/start_at/end_at)
GroupMuteRecurringRule: taskId/weekdays/startTime/endTime/enabled
GroupMemberMuteState: memberOpenid/muteExpireAt/username/unionOpenid
```

`GroupGlobalMuteRule` 常量为 `MODE_NONE`、`MODE_ALWAYS`、`MODE_SCHEDULE`。`GroupMuteRecurringRule.weekdays` 是 `List<Int>`，不转换成 locale 周枚举；官方固定 1=周一、7=周日，`startTime/endTime` 原样为北京时间 `HH:mm`。

设置请求必须保留两层 JSON：

```kotlin
@ApiModel @Serializable
public data class GroupMemberMuteUpdate @ApiModelConstructor constructor(
    public val op: String,
    @SerialName("member_openid") public val memberOpenid: String,
    @SerialName("mute_expire_at") public val muteExpireAt: String? = null,
)

@ApiModel @Serializable
public data class GroupMemberMuteUpdateRequest @ApiModelConstructor constructor(
    public val members: List<GroupMemberMuteUpdate>? = null,
)
```

更新项提供 `OP_ADD`、`OP_UPDATE`、`OP_DEL`。调用方显式传入 `muteExpireAt = ""` 才能表达官方定义的“立即解除”，所以不使用 `encodeDefaults`、不把空串改为 null，也不创建自动计算当前时间的低层重载。

### 2.4 成员与黑名单

```text
GroupMember
├─ memberOpenid: String                                      (member_openid)
├─ username: String = ""
├─ memberRole: String = ""                                  (member_role)
├─ bot: Boolean = false
├─ joinedAt: String = ""                                    (joined_at)
└─ unionOpenid: String?                                      (union_openid)

GroupMemberPage
├─ members: List<GroupMember> = emptyList()
└─ nextCursor: String = ""                                  (next_cursor)

GroupBlacklistUser
├─ unionOpenid: String?
├─ memberOpenid: String                                      (member_openid)
├─ username: String = ""
├─ bannedAt: String = ""                                    (banned_at)
└─ bot: Boolean = false

GroupBlacklistPage
├─ users: List<GroupBlacklistUser> = emptyList()
└─ nextCursor: String = ""                                  (next_cursor)
```

`GroupMember` 的角色常量沿用 `member/owner/admin` 原文，但不复用事件包的 `GroupMessageAuthorRole`。下批需要的输入/回执一并在这里加入：

```text
GroupMemberRemovalRequest(memberOpenids: List<String>, addToMemberBlacklist: Boolean? = null)
GroupMemberRemovalResult(removeMembersResult: String = "", addToMemberBlacklistFailOpenids: List<String> = emptyList())
GroupMemberBlacklistOperation(op: String, memberOpenids: List<String>)
GroupMemberBlacklistOperationResult(failOpenids: List<String> = emptyList())
```

这四个类逐项标注 `member_openids`、`add_to_member_blacklist`、`remove_members_result`、`add_to_member_blacklist_fail_openids`、`fail_openids`。操作 companion 提供 `OP_ADD`/`OP_DEL`；移除成功字符串 `success` 只作为 `RESULT_SUCCESS` 常量，不要把“列表为空”误判为全成功。

### 2.5 自动审批策略

```text
JoinApprovalStrategyPage
├─ strategies: List<JoinApprovalStrategy> = emptyList()
└─ nextCursor: String = ""                                  (next_cursor)

JoinApprovalStrategy
├─ strategyId: String                                        (strategy_id)
├─ groupOpenids: List<String> = emptyList()                  (group_openids)
├─ groupIds: List<String> = emptyList()                      (group_ids)
├─ whitelistUserCount: Int = 0                               (whitelist_user_count)
├─ isEnable: String = ""                                    (is_enable)
├─ expireAt/createdAt/updatedAt: String = ""                (..._at)
└─ remark: String? = null
```

`groupIds` 必须是 `List<String>`，即使官方文本写 uint64。`isEnable` 提供 `ENABLE_ON`/`ENABLE_OFF` 常量。响应中的 `groupOpenids/groupIds` 空列表和字段缺失都解码为 `emptyList()`，不能据此推断“策略未关联任何群”。

写模型和回执的精确定义如下，批次 2 只消费这些类型：

| 类型 | Kotlin 属性（JSON） |
| --- | --- |
| `JoinApprovalStrategyCreate` | `groupOpenids?` (`group_openids`)、`groupIds?` (`group_ids`)、`isEnable?`、`expireAt?`、`remark?` |
| `JoinApprovalStrategyGroupAction` | `op`、`groupOpenids?`、`groupIds?` |
| `JoinApprovalStrategyUpdate` | `isEnable?`、`expireAt?`、`groupAction?`、`remark?` |
| `JoinApprovalStrategyWhitelistOperation` | `op`、`whitelistUsers` (`whitelist_users`) |
| `JoinApprovalStrategyCreated` | `strategyId`、`isEnable`、`expireAt` |
| `JoinApprovalStrategyUpdated` | `isEnable`、`expireAt` |
| `JoinApprovalStrategyWhitelistUpdated` | `strategyId`、`whitelistUserCount`、`updatedAt` |

上述所有 `String` 列表必须原样序列化；不去重、排序、切片，长度限制交给平台。实现时为每个公开类和每个常量写 KDoc，说明官方最大值、互斥性或敏感性。

## 3. 8 个 GET 请求对象

所有类遵循这个骨架；不同之处只允许是路径、参数和 serializer：

```kotlin
public class GetGroupInfoApi private constructor(
    private val groupOpenid: String,
) : GetQQGuildApi<GroupInfo>() {
    public companion object Factory : SimpleGetApiDescription(
        "/v2/groups/{group_openid}/info"
    ) {
        @JvmStatic
        public fun create(groupOpenid: String): GetGroupInfoApi = GetGroupInfoApi(groupOpenid)
    }

    override val path: Array<String> = arrayOf("v2", "groups", groupOpenid, "info")
    override val resultDeserializationStrategy: DeserializationStrategy<GroupInfo>
        get() = GroupInfo.serializer()
}
```

具体表：

| 类 | 父类/结果 | `path` | `buildUrl()` |
| --- | --- | --- | --- |
| `GetGroupInfoApi` | `GetQQGuildApi<GroupInfo>` | `v2/groups/{group}/info` | 无 |
| `GetGroupBotStateApi` | `GetQQGuildApi<GroupBotState>` | `v2/groups/{group}/bot_state` | 无 |
| `GetGroupJoinRequestListApi` | `GetQQGuildApi<GroupJoinRequestPage>` | `v2/groups/{group}/join_request_list` | `cursor?.also { parameters.append("cursor", it) }`；`limit?.also { ... }` |
| `GetGroupRestrictChatSettingApi` | `GetQQGuildApi<GroupRestrictChatState>` | `v2/groups/{group}/restrict_chat_setting` | 无 |
| `GetJoinApprovalStrategyListApi` | `GetQQGuildApi<JoinApprovalStrategyPage>` | `v2/groups/join_approval_strategy` | cursor、limit 同上 |
| `GetGroupMemberListApi` | `GetQQGuildApi<GroupMemberPage>` | `v2/groups/{group}/members` | **只**附加可空 `cursor` |
| `GetGroupMemberApi` | `GetQQGuildApi<GroupMember>` | `v2/groups/{group}/members/{member}` | 无 |
| `GetGroupMemberBlacklistApi` | `GetQQGuildApi<GroupBlacklistPage>` | `v2/groups/{group}/member_blacklist` | cursor、limit 同上 |

工厂方法参数顺序固定为：路径参数在前，查询参数在后；查询参数均 `String? = null`/`Int? = null`。例如：

```kotlin
GetGroupJoinRequestListApi.create(groupOpenid, cursor = null, limit = null)
GetJoinApprovalStrategyListApi.create(cursor = null, limit = null)
GetGroupMemberListApi.create(groupOpenid, cursor = null)
```

不得给 `GetGroupMemberListApi` 凭空增加 `limit`，不得在 Factory 内 `require(limit in 1..50)`；这两条分别保护官方差异和当前项目“低层不做本地业务校验”的惯例。

## 4. 本批自检点

1. 新模型中的所有 snake_case 都有对应 `@SerialName`；使用 `QQGuild.DefaultJson` 对资料页合法响应例子解码。
2. `GroupBotState` 的测试不直接采用官方坏 JSON，而是使用资料页记录的已修正夹具，并另有一条注释说明来源瑕疵。
3. 对 8 个 API 各断言 `HttpMethod.Get`、`url.encodedPath`、参数存在/缺失和 `body == null`；其中成员列表断言 URL 没有 `limit`。
4. 执行本模块的 common/JVM 测试后再进入批次 2；这只证明序列化与请求构造，未证明白名单、管理员权限或内邀资格。
