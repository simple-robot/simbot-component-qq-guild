# QQ 群管理能力实施文档

## 结论

本目录把 `docs.md` 中列出的 17 个 QQ 官方 v2 接口，设计为本仓库的两层能力：

- `simbot-component-qq-guild-api`：无状态、可单独使用的 KMP HTTP 请求对象与 Kotlinx Serialization 模型；17 个接口全部在此层落地。
- `simbot-component-qq-guild-core`：把适合现有 `QGGroup`、`QGGroupMember`、`QGBot` 的能力接入 simbot 语义；不能自然映射到通用群模型的“入群自动审批策略”保留为 `QGBot` 上的专用管理器。

实现不新增持久化、轮询任务、缓存或新的网关事件。所有快照均是一次 HTTP 查询的瞬时结果；所有分页集合均为冷 `Collectable`，仅在收集时请求；异步策略执行只返回“已受理”，不虚构完成状态。

## 阅读顺序与交付边界

| 文档 | 用途 |
| --- | --- |
| [reference/官方接口资料与原始示例.md](reference/官方接口资料与原始示例.md) | 17 个官方接口的 URL、路径、频率、字段、原始 HTTP/JSON 示例与已发现的官方文档瑕疵。离线实施时先以此核对契约。 |
| [总体架构与一致性决策.md](总体架构与一致性决策.md) | 当前代码事实、API 命名与模型边界、权限/安全/兼容性决策。 |
| [batches/01-低层模型与查询 API.md](<batches/01-低层模型与查询 API.md>) | 批次 1：所有读模型及 8 个 GET 请求对象。 |
| [batches/02-低层管理写 API.md](<batches/02-低层管理写 API.md>) | 批次 2：审批、禁言、移除、黑名单和策略的 9 个写请求对象。 |
| [batches/03-群对象与成员整合.md](batches/03-群对象与成员整合.md) | 批次 3：让 `QGGroup`、成员、入群申请和禁言状态使用低层能力。 |
| [batches/04-自动审批策略管理器.md](batches/04-自动审批策略管理器.md) | 批次 4：`QGBot.joinApprovalStrategies` 专用 facade、句柄与分页。 |
| [batches/05-测试-兼容性与发布.md](batches/05-测试-兼容性与发布.md) | 批次 5：单元测试矩阵、ABI/JPMS/文档更新、人工联调与验收。 |

`docs.md` 是用户提供的官方入口清单，保持不改。所有新增文件均是设计和实施说明，不包含生产代码变更。

## 建议执行次序

```text
批次 1：API 模型 + 查询 HTTP 层
        │
批次 2：API 写操作 HTTP 层
        │
        ├─────────────┐
        ▼             ▼
批次 3：QGGroup     批次 4：QGBot 的全局策略管理器
        │             │
        └──────┬──────┘
               ▼
批次 5：测试、ABI、JPMS、文档、受控联调
```

批次 3 与批次 4 可并行，但均依赖批次 1、2。批次 5 只能在全部代码完成后执行。每一批都要先运行该批涉及模块的常规测试；没有白名单/内邀资格时，网络联调是明确的未验证项，不能用编译或 Mock 测试替代。

## 能力映射

| 官方能力 | 低层 API 包 | core 落点 | 平台前置条件 |
| --- | --- | --- | --- |
| 群信息、机器人群内状态 | `api.group` | `QGGroup.info()`、`QGGroup.botState()` | 两个查询页均列出 `11253` 白名单限制。 |
| 入群申请列表、审批 | `api.group` | `QGGroup.joinRequests(...)`、申请快照的 `approve/decline` | 机器人为群管理员。 |
| 成员禁言状态、成员禁言 | `api.group` | `QGGroup.restrictChatState()`、`updateMemberMutes(...)` | 机器人为群管理员；最长 30 天。 |
| 自动审批策略（含白名单与执行） | `api.group.approval` | `QGBot.joinApprovalStrategies` | 策略真正生效时机器人必须是关联群管理员。 |
| 成员列表/详情、移除、黑名单 | `api.group.member` | `QGGroup.members`、`member(...)`、成员快照和黑名单集合 | 官方页面明确写“内邀接入中”；多个接口还列出 `11253`。 |

## 完成标准

实施完成不等于“请求类能编译”。至少同时满足：

1. 17 个 `ApiDescription.path/method` 与资料摘录逐项相同，查询参数落在 URL 而不是 GET body。
2. 每一个响应/请求模型均有 `@Serializable`、准确的 `@SerialName`，并且不把官方未承诺的字段设成无默认值。
3. `QGGroup` 不再对已被官方开放的成员查询永久返回空值/`null`，但 `name` 不在属性读取时偷偷发网络请求。
4. 自动审批策略的“execute”不被包装成同步完成；黑名单的“成员必须已不在群内”不被 SDK 忽略。
5. API 请求构造、样例反序列化、core MockEngine、公共 ABI、JPMS 导出以及手册状态均通过本目录的验收清单。
