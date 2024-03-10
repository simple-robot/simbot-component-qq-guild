---
switcher-label: Java API 风格
---
<show-structure for="chapter,procedure" depth="3"/>
<var name="jr" value="Reactor"/>

# 角色 QGRole

QQ频道中有一些针对 `角色` 的API。

## API中的角色 {id="api-roles"}

在API模块中存在一些用来获取 `Role` 的API。

比如你可以通过 `GetGuildRoleListApi` 获取一个频道服务器中的角色列表。

> 详细的API列表请参考
> <a href="api-list.md" />
> 或 [API文档](%api-doc%)。


## 组件库中的角色 {id="component-roles"}

在组件库模块中，`QGRole` 实现了simbot标准API中的 `Role`，
并提供了一些高级功能的封装。

> 你可以通过属性 `QGRole.source` 得到API模块中定义的原始 `Role` 类型。

### QGRole的类型 {id='qg-role-types'}

QGRole 有两个子类型，用于在不同的场景下描述角色信息：

| 类型             | 描述            |
|----------------|---------------|
| `QGGuildRole`  | 表示一个频道服务器中的角色 |
| `QGMemberRole` | 表示某个频道成员拥有的角色 |

根据不同的类型，部分API会产生的效果也不同。

### 获取QGRole {id='get-qg-roles'}

在 `QGGuild` 中，你可以通过属性 `roles` 获取到频道服务器中定义的所有 `QGGuildRole`。

> 有关 `QGGuild` 获取角色，前往参考
> <a href="QGGuild.md#get-roles" />。

在 `QGMember` 中，你可以通过属性 `roles` 获取这个成员所拥有的所有 `QGMemberRole`。

> 有关 `QGMember` 获取角色，前往参考
> <a href="QGMember.md#get-roles" />。

在 `QGMemberRole` 中，会额外提供一些属性来获取到它所属的成员ID和对应的 `QGGuildRole` 实例本身。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val memberRole: QGMemberRole = ...
memberRole.memberId  // 此角色所属的成员ID
memberRole.guildRole // 此角色所实际代表的频道角色 
```

</tab>
<tab title="Java" group-key="Java">

```Java
QGMemberRole memberRole = ...
memberRole.getMemberId();  // 此角色所属的成员ID
memberRole.getGuildRole(); // 此角色所实际代表的频道角色 
```

</tab>
</tabs>

### 创建QGRole {id='create-qg-role'}

#### 创建QGGuildRole {id='create-qg-guild-role'}

在 `QGGuild` 中提供了一个API `roleCreator` 可以创建一个用于新建 `QGGuildRole`
的构建器。
前往
<a href="QGGuild.md#create-role" /> 
参考代码示例。


#### 创建QGMemberRole {id='create-qg-member-role'}

实际上 `QGMemberRole` 作为一个"频道成员拥有的角色"，与其说创建，
不如说是 "赋予"："赋予"一个角色给成员，并由此诞生 `QGMemberRole`。

想要赋予给用户一个角色，首先你得先拥有一个 `QGGuildRole`。
`QGGuildRole` 中提供了用于赋予角色的 API `grantTo(...)`。

赋予需要一个参数来代表目标成员，它可以是 `QGMember`、`Member` 或 `ID`，
它们的可靠性依次递减 (类型越宽泛，越可能存在类型校验等异常)。

下面的示例中会以 `QGGuildRole` 和 `QGMember` 为例。

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val role: QGGuildRole = ...
val member: QGMember = ...
// 将角色赋予目标成员，得到 QGMemberRole 结果
val memberRole: QGMemberRole = role.grantTo(member)
```

</tab>
<tab title="Java" group-key="Java">

```Java
QGGuildRole role = ...
QGMember member = ...
// 将角色赋予目标成员，得到 QGMemberRole 结果
role.grantToAsync(member)
        .thenAccept(memberRole -> {});
```
{switcher-key='%ja%'}

```Java
QGGuildRole role = ...
QGMember member = ...
// 将角色赋予目标成员，得到 QGMemberRole 结果
QGMemberRole memberRole = role.grantToBlocking(member);
```
{switcher-key='%jb%'}

```Java
QGGuildRole role = ...
QGMember member = ...
// 将角色赋予目标成员，得到 QGMemberRole 结果
role.grantToReserve(member)
        .transform(SuspendReserves.mono())
        .subscribe(memberRole -> {});
```
{switcher-key='%jr%'}

</tab>
</tabs>

<tip title="子频道管理员">

如果角色是 **子频道管理员(id=5)**，
那么在赋予的时候需要额外的一个 `channelId` 参数来指定一个子频道。

</tip>


### 删除角色/移除角色授权 {id='delete-qg-role'}

`QGGuildRole` 和 `QGMemberRole` 各自实现了 `DeleteSupport`，因此它们支持 `delete` 操作。
根据它们的类型，`delete` 具有不同的含义：

| 类型                         | 含义              |
|----------------------------|-----------------|
| `QGGuildRole.delete(...)`  | 删除这个频道中的角色      |
| `QGMemberRole.delete(...)` | 取消这个成员的角色(移除权限) |

<tabs>
<tab title="QGGuildRole">
<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val role: QGGuildRole = ...
// 删除这个角色
role.delete()
```

</tab>
<tab title="Java" group-key="Java">

```Java
QGGuildRole role = ...
// 删除这个角色
role.deleteAsync()
        .thenAccept(unit -> {  });
```
{switcher-key='%ja%'}

```Java
QGGuildRole role = ...
// 删除这个角色
role.deleteBlocking();
```
{switcher-key='%jb%'}

```Java
QGGuildRole role = ...
// 删除这个角色
role.deleteReserve()
        .transform(SuspendReserves.mono())
        .subscribe(unit -> {  });
```
{switcher-key='%jr%'}

</tab>
</tabs>
</tab>
<tab title="QGMemberRole">
<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val role: QGMemberRole = ...
// 取消此角色对其对应用户的授权
role.delete()
```

</tab>
<tab title="Java" group-key="Java">

```Java
QGMemberRole role = ...
// 取消此角色对其对应用户的授权
role.deleteAsync()
        .thenAccept(unit -> {  });
```
{switcher-key='%ja%'}

```Java
QGMemberRole role = ...
// 取消此角色对其对应用户的授权
role.deleteBlocking();
```
{switcher-key='%jb%'}

```Java
QGMemberRole role = ...
// 取消此角色对其对应用户的授权
role.deleteReserve()
        .transform(SuspendReserves.mono())
        .subscribe(unit -> {  });
```
{switcher-key='%jr%'}

</tab>
</tabs>
</tab>
</tabs>

