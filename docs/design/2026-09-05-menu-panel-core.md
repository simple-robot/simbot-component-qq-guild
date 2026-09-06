# 菜单与指令面板的 Core 封装设计

## 背景与范围

API 模块已提供 `/v2/menu` 与 `/v2/panels` 的低层请求、序列化模型和 Builder。Core 的职责是把它们转化为面向 `QGBot` 的稳定访问入口，同时保留对平台新增字段和值的无损访问。

本设计只定义 Core 的后续实现，不在本次变更中提前实现菜单、指令面板 manager。C2C 事件字段的直接投影属于已有事件模型的同步更新，不改变本设计的分期边界。

## 已确认的决策

| 主题 | 决策 |
| --- | --- |
| 资源关系 | 自定义菜单与指令面板没有直接关联；分别使用独立入口。 |
| Core 入口 | `QGBot.customMenu` 与 `QGBot.commandPanels` 两个扩展属性。 |
| 前向兼容 | 已知语义提供便利 DSL；未知字符串值通过包装值对象和 `source` 无损保留。 |
| 缓存与并发 | 不缓存，不伪造基于 `version` 的 CAS；快照只是观察结果。 |
| 面板创建 | 只返回带 ID 的面板句柄，不为了补齐详情自动执行 GET。 |
| 分页 | 按 `scope` 返回 `Collectable`，每次收集均请求服务端。 |
| 错误 | 平台调用直接透传 `QQGuildApiException`；仅对 DSL 能确定的非法结构抛 `IllegalArgumentException`；不隐式重试。 |
| API 稳定性 | 不使用 `@ExperimentalQGApi`；新增公开类、属性和函数均标注 `@since 4.7.0`。 |

## 入口与包结构

自定义菜单仅服务于 C2C 全局窗口，而指令面板能服务于 C2C、群聊、频道和私信。二者使用不同路由、不同生命周期和不同读取粒度，不能聚合为同一个 manager。

```text
love.forte.simbot.component.qguild
├── menu
│   ├── QGCustomMenuManager
│   ├── QGCustomMenuSnapshot
│   └── QGCustomMenuUpdateReceipt
└── panel
    ├── QGCommandPanelManager
    ├── QGCommandPanelHandle
    ├── QGCommandPanelSnapshot
    ├── QGCommandPanelScope
    └── QGCommandPanelTargetType
```

```kotlin
public val QGBot.customMenu: QGCustomMenuManager
public val QGBot.commandPanels: QGCommandPanelManager
```

两个属性均应按 Bot 实例按需构造轻量 facade，不保存远端状态。内部调用既有 `QGBot.executeData` / `execute` 逃生口；调用者仍可直接使用低层 API 以访问尚未被 Core 提升的能力。

## 菜单 API 草图

```kotlin
public interface QGCustomMenuManager {
    public suspend fun get(): QGCustomMenuSnapshot

    public suspend fun update(menu: CustomMenu): QGCustomMenuUpdateReceipt

    public suspend fun update(
        block: CustomMenuBuilder.() -> Unit,
    ): QGCustomMenuUpdateReceipt
}

public class QGCustomMenuSnapshot(
    public val version: Int,
    public val menu: CustomMenu?,
    override val source: CustomMenuSnapshot,
) : QGObjectiveContainer<CustomMenuSnapshot>

public class QGCustomMenuUpdateReceipt(
    public val version: Int,
    override val source: CustomMenuUpdated,
) : QGObjectiveContainer<CustomMenuUpdated>
```

`update` 表示整体覆盖。返回的新版本号只作为服务端回执，不能据此假设本地快照仍然新鲜，也不能由 Core 自动合并并发更新。

## 指令面板 API 草图

```kotlin
public interface QGCommandPanelManager {
    public suspend fun create(request: CommandPanelCreate): QGCommandPanelHandle

    public suspend fun create(
        block: CommandPanelCreateBuilder.() -> Unit,
    ): QGCommandPanelHandle

    public suspend fun get(panelId: ID): QGCommandPanelSnapshot

    public fun list(
        scope: QGCommandPanelScope,
        limit: Int? = null,
    ): Collectable<QGCommandPanelSnapshot>

    public fun handle(panelId: ID): QGCommandPanelHandle
}

public class QGCommandPanelHandle {
    public val id: ID

    public suspend fun get(): QGCommandPanelSnapshot
    public suspend fun update(panel: CommandPanel): QGCommandPanelUpdateReceipt
    public suspend fun update(block: CommandPanelBuilder.() -> Unit): QGCommandPanelUpdateReceipt
    public suspend fun addTargets(targets: QGCommandPanelTargets)
    public suspend fun removeTargets(targets: QGCommandPanelTargets)
    public suspend fun delete()
}

public class QGCommandPanelSnapshot(
    public val id: ID,
    public val scope: QGCommandPanelScope,
    public val targetType: QGCommandPanelTargetType,
    public val panel: CommandPanel,
    public val version: Int?,
    override val source: CommandPanelRecord,
) : QGObjectiveContainer<CommandPanelRecord>
```

`create` 的上游响应只包含面板 ID，因此只产生 `QGCommandPanelHandle`。`get` 与 `list` 才产生快照。内容更新返回服务端的版本回执；目标增删与删除遵循上游空响应，返回 `Unit`，不触发隐式刷新。

## 可扩展值对象与 DSL

`scope`、`targetType` 和目标操作不使用封闭 `enum`。它们使用保留原始字符串的公开包装值对象，并暴露官方已知常量，例如 `QGCommandPanelScope.C2C`。因此平台新增值可被 `QGCommandPanelScope("new_scope")` 无损表达。

第一期的 Core API 接收既有 `CustomMenu`、`CommandPanel`、`CommandPanelCreate` 和 Builder lambda。第二期才加入语义化 DSL：菜单提供发送消息、链接、开关和子菜单构造；面板提供指令和链接元素构造。便利 DSL 只校验自身可确定的结构，例如必填内容和非空标识；平台定义的字段组合、数量上限和未来类型继续交给服务端。

## C2C 事件演进

API 的 `C2CMessageCreate.Data` 已新增 `message_type` 与 `message_scene`。当前事件同步应先在 `QGC2CMessageCreateEvent` 直接投影：

```kotlin
public val messageType: Int?
public val messageScene: C2CMessageCreate.MessageScene?
```

后续 Core 封装阶段将引入：

```kotlin
public class QGC2CMessageScene(
    public val sceneSource: String?,
    public val extensions: List<String>,
    override val source: C2CMessageCreate.MessageScene,
) : QGObjectiveContainer<C2CMessageCreate.MessageScene>
```

`extensions` 必须保留为原始 `List<String>`。当前协议没有定义 `key=value` 的转义、重复键或排序语义，Core 不解析也不合并它们。未来的 `ark_data`、`msg_elements` 在具备独立 API 模型后再单独设计，不能用 Map 或临时类型猜测协议结构。

## 实施与验证顺序

1. 增加两个 Bot 扩展入口、manager、快照、句柄和字符串包装值对象；所有新增公开声明添加多行 KDoc 与 `@since 4.7.0`。
2. 以现有低层模型与 Builder 实现菜单读取/覆盖、面板创建/查询/列表/更新/目标变更/删除；为无结果操作保留 `Unit`。
3. 实现 `QGC2CMessageScene` 投影及事件测试，再追加语义 DSL 和 DSL 结构校验。
4. 为路由、请求体、分页游标、无缓存行为、异常透传、未知字符串值和 C2C 原始扩展列表补充 common/JVM 测试。

每一步均不引入缓存、自动重试或隐式详情请求；低层 `source` 与 `QGBot.executeData` 始终是平台能力演进时的兼容出口。
