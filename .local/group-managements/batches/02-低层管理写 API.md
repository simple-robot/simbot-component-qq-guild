# 批次 2：低层管理写 API

## 目标与依赖

本批在批次 1 的 `model.group` 上实现其余 9 个 HTTP 写操作。它只负责精确表达平台请求和响应，绝不把“审批”“禁言”“移除”“拉黑”“异步执行”自动编排为多请求工作流。core 的易用封装留给批次 3、4。

依赖：批次 1 的模型、统一 `QQGuildApi` 请求基础设施，以及 [官方资料](../reference/官方接口资料与原始示例.md)。所有请求都走已有 `requestData`，因此鉴权头、HTTP 错误、trace ID 和 Ktor client 行为不在本批复制。

## 1. 新增 API 类总表

| 文件 / 类 | Method、路径 | body | 结果 |
| --- | --- | --- | --- |
| `api/group/ApproveGroupJoinRequestApi.kt` | `POST /v2/groups/{group}/approval_join_request/{member}` | `GroupJoinRequestApproval` | `Unit` |
| `api/group/SetGroupRestrictChatSettingApi.kt` | `POST /v2/groups/{group}/restrict_chat_setting` | `GroupMemberMuteUpdateRequest` | `Unit` |
| `api/group/approval/CreateJoinApprovalStrategyApi.kt` | `POST /v2/groups/join_approval_strategy` | `JoinApprovalStrategyCreate` | `JoinApprovalStrategyCreated` |
| `api/group/approval/ModifyJoinApprovalStrategyApi.kt` | `PATCH /v2/groups/join_approval_strategy/{strategy}` | `JoinApprovalStrategyUpdate` | `JoinApprovalStrategyUpdated` |
| `api/group/approval/DeleteJoinApprovalStrategyApi.kt` | `DELETE /v2/groups/join_approval_strategy/{strategy}` | 无 | `Unit` |
| `api/group/approval/ExecuteJoinApprovalStrategyApi.kt` | `POST /v2/groups/join_approval_strategy/{strategy}/execute` | 无 | `Unit` |
| `api/group/approval/ModifyJoinApprovalStrategyWhitelistApi.kt` | `POST /v2/groups/join_approval_strategy/{strategy}/whitelist_users` | `JoinApprovalStrategyWhitelistOperation` | `JoinApprovalStrategyWhitelistUpdated` |
| `api/group/member/RemoveGroupMembersApi.kt` | `POST /v2/groups/{group}/batch_remove_members` | `GroupMemberRemovalRequest` | `GroupMemberRemovalResult` |
| `api/group/member/ModifyGroupMemberBlacklistApi.kt` | `POST /v2/groups/{group}/member_blacklist` | `GroupMemberBlacklistOperation` | `GroupMemberBlacklistOperationResult` |

所有 API 类的伴生对象必须继承正确的 `SimplePostApiDescription`、`SimplePatchApiDescription` 或 `SimpleDeleteApiDescription`；描述字符串写带占位符的官方路径，不能写实际参数值。所有公开 `create` 均标 `@JvmStatic`，参数顺序为路径标识符在前、body 在后。

## 2. 共同实现模板

有请求体的 `POST/PATCH` 类使用与 `CreateCommandPanelApi` 相同的模式，防止父类默认 body 工厂覆盖传入对象：

```kotlin
public class XxxApi private constructor(
    pathId: String,
    override val body: XxxRequest,
) : PostQQGuildApi<XxxResult>() {
    public companion object Factory : SimplePostApiDescription("/v2/...") {
        @JvmStatic
        public fun create(pathId: String, body: XxxRequest): XxxApi = XxxApi(pathId, body)
    }

    override val path: Array<String> = arrayOf("v2", /* ... */)
    override val resultDeserializationStrategy: DeserializationStrategy<XxxResult>
        get() = XxxResult.serializer()
    override fun createBody(): Any? = null
}
```

结果为空的请求改为 `QQGuildApiWithoutResult`：

```kotlin
public class XxxApi private constructor(pathId: String) :
    PostQQGuildApi<Unit>(), QQGuildApiWithoutResult {
    // Factory、path；不覆盖 body，也不提供虚假的 serializer
}
```

`PatchQQGuildApi` 已将 method 覆盖成 `HttpMethod.Patch`；不要误继承 `PutQQGuildApi`，也不要新增 `X-HTTP-Method-Override`。删除与执行路径也不附加 `{}` body，资料页中的 `{}` 只是展示样例；在当前请求框架中 `body == null` 会使用 `EmptyContent`。

## 3. 群范围写操作

### 3.1 `ApproveGroupJoinRequestApi`

```text
Factory.create(groupOpenid: String, memberOpenid: String, body: GroupJoinRequestApproval)
description: /v2/groups/{group_openid}/approval_join_request/{member_openid}
path: ["v2", "groups", groupOpenid, "approval_join_request", memberOpenid]
method/result: POST / Unit
```

不可在 `create` 中强制 `joinRequestId` 非空：官方写“可选”，而且审批路径已经包含 `member_openid`。也不可根据 `op` 删除 `rejectReason` 或强制 `addToMemberBlacklist=false`；低层原样转发。core 的 `approve/decline` 再提供具名便利调用。

### 3.2 `SetGroupRestrictChatSettingApi`

```text
Factory.create(groupOpenid: String, body: GroupMemberMuteUpdateRequest)
description: /v2/groups/{group_openid}/restrict_chat_setting
path: ["v2", "groups", groupOpenid, "restrict_chat_setting"]
method/result: POST / Unit
```

不要把 `members` 空列表改成 `null`，也不要补齐为 20 个。调用方传入的 `GroupMemberMuteUpdateRequest` 是 JSON 的唯一来源；最大 20、不可操作管理员/机器人、最长 30 天都是平台校验条件，需在 KDoc 和 core 方法中提示。

## 4. 自动审批策略写操作

### 4.1 创建

```text
CreateJoinApprovalStrategyApi.create(body: JoinApprovalStrategyCreate)
POST /v2/groups/join_approval_strategy
result serializer: JoinApprovalStrategyCreated.serializer()
```

`JoinApprovalStrategyCreate` 同时把 `groupOpenids`、`groupIds` 原样传递。不得在 Factory 内检查二选一、最大 100、最多 20 个策略、255 汉字或默认一年；这些规则在 KDoc 写清楚，由 QQ 服务端作为唯一裁判。不能把创建回执当作完整策略：它没有关联群、白名单数、创建时间、备注。

### 4.2 修改

```text
ModifyJoinApprovalStrategyApi.create(strategyId: String, body: JoinApprovalStrategyUpdate)
PATCH /v2/groups/join_approval_strategy/{strategy_id}
result serializer: JoinApprovalStrategyUpdated.serializer()
```

`JoinApprovalStrategyUpdate.groupAction` 内有 `op` 与两种群标识。文档必须说明“创建时使用何种标识，后续修改也必须使用同种标识”；SDK 不查询远端策略来替调用方判断。更新回执只有 `is_enable` 和 `expire_at`，不能补出 `remark` 或群集合。

### 4.3 删除与执行

```text
DeleteJoinApprovalStrategyApi.create(strategyId: String)
DELETE /v2/groups/join_approval_strategy/{strategy_id}
result: Unit

ExecuteJoinApprovalStrategyApi.create(strategyId: String)
POST /v2/groups/join_approval_strategy/{strategy_id}/execute
result: Unit
```

两类都无 body。`Execute...` 的 KDoc 必须逐字表达关键语义：“向关联群发起异步全量扫描，官方称约十分钟完成”。禁止命名为 `executeAndWait`、`awaitCompletion`，禁止使用 delay/polling，禁止把 HTTP 成功包装为“审批完成”。

### 4.4 修改白名单

```text
ModifyJoinApprovalStrategyWhitelistApi.create(
    strategyId: String,
    body: JoinApprovalStrategyWhitelistOperation,
)
POST /v2/groups/join_approval_strategy/{strategy_id}/whitelist_users
result: JoinApprovalStrategyWhitelistUpdated
```

`whitelistUsers` 使用 `List<String>`、JSON `whitelist_users`。不得使用 `Long`、`ULong` 或 `Number`，也不得把 QQ 号码输出到 debug/info 日志。单次 10000、总量 10 万属于文档约束，不由低层切片或拆包，因为拆包会改变操作的失败边界。

## 5. 内邀成员管理写操作

### 5.1 批量移除

```text
RemoveGroupMembersApi.create(groupOpenid: String, body: GroupMemberRemovalRequest)
POST /v2/groups/{group_openid}/batch_remove_members
result: GroupMemberRemovalResult
```

`GroupMemberRemovalResult` 必须完整保留两个独立结论：`removeMembersResult` 与 `addToMemberBlacklistFailOpenids`。仅当第一个字段等于官方常量 `success` 才能说移除成功；第二个列表为空才说明“同时拉黑”没有失败。两者不能合并为一个 Boolean，也不能因拉黑失败而吞掉已成功的移除结果。

### 5.2 黑名单增删

```text
ModifyGroupMemberBlacklistApi.create(groupOpenid: String, body: GroupMemberBlacklistOperation)
POST /v2/groups/{group_openid}/member_blacklist
result: GroupMemberBlacklistOperationResult
```

KDoc 写明 `op=add` 只能作用于已不在群内的成员；SDK 不在 add 前调用成员详情，也不自动改走“移除并拉黑”接口。`failOpenids` 原样返回，`op=del` 时也沿用这个字段的失败含义。单次最多 20 和内邀状态都保留在文档注释里。

## 6. 低层测试清单（代码与批次 5 可分开提交）

每类至少有“路径/方法/body”断言；建议在 `commonTest/kotlin/GroupManagementApiTests.kt` 分组，风格模仿 `MenuPanelApiTests` 和 `MessageDeleteApiTests`。

| API | 必测断言 |
| --- | --- |
| 审批 | POST、完整两级路径、`op/join_request_id/reject_reason/add_to_member_blacklist` 的 snake_case。 |
| 禁言 | POST、`members` 数组，`mute_expire_at` 空串不会被序列化丢弃。 |
| 创建/修改策略 | POST/PATCH、群 ID 两种字段保留互斥输入、`group_action` 嵌套 JSON。 |
| 删除/执行 | DELETE/POST、准确路径、`body == null`。 |
| 白名单 | `whitelist_users` 为 JSON string array，不能变数值数组。 |
| 移除 | `member_openids` 与结果体的两个分别命名字段。 |
| 黑名单 | `op=add/del` 与 `fail_openids`。 |

测试不请求真实 QQ 平台，不携带真实群、成员、申请或白名单标识。Mock 测试只能证明 SDK 的序列化与 URL；管理员权限、内邀资格、平台数量限制和十分钟异步扫描均留到批次 5 的受控联调。
