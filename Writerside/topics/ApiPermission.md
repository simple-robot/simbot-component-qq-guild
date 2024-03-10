<show-structure for="chapter,procedure" depth="3"/>

# API权限信息

## ApiPermission

在API模块中，有一个类型 `ApiPermission`，它代表了一个频道服务器中、
针对一个接口(例如 `/guilds/{guild_id}/members/{user_id}`)
的授权信息。

可以通过 `GetApiPermissionListApi` 获取到指定频道中针对所有接口的授权信息，
并作为 [`ApiPermissions`](#apipermissions) 类型返回。

> 有关API的更多说明请前往参考
> <a href="api.md" />。

### ApiPermissions

`ApiPermissions` 是对一组 `ApiPermission` 的包装。
除了能够对内部的 `ApiPermission` 进行迭代以外，还额外提供了一些API用于判断是否包含或寻找某些授权信息。

我们以 `GetGuildApi` (获取频道信息详情) 为例：

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
// 可迭代的
apiPermissions.forEach { apiPermission ->  }

// 获取到对 GetGuildApi 的全部授权信息(仅匹配path、忽略method的所有API)
val apiPermissionList = apiPermissions[GetGuildApi] // get(...)

// 寻找对 GetGuildApi 的授权信息
val apiPermissionOrNull = apiPermissions.find(GetGuildApi)

// 判断是否包含对 GetGuildApi 的授权信息
val isContains = GetGuildApi in apiPermissions // contains(...)
```

</tab>
<tab title="Java" group-key="Java">

```Java
for (ApiPermission apiPermission : apiPermissions) {
    // 可迭代的
}

// 获取到对 GetGuildApi (的相同path的所有API) 的全部授权信息(忽略method)
var apiPermissionList = apiPermissions.get(GetGuildApi.Factory);

// 寻找对 GetGuildApi 的授权信息
var apiPermissionOrNull = apiPermissions.find(GetGuildApi.Factory);

// 判断是否包含对 GetGuildApi 的授权信息
var isContains = apiPermissions.contains(GetGuildApi.Factory);
```

</tab>
</tabs>

### ApiDescription

可以看到，这些额外的API可以将一个 API 实现的伴生对象(通常命名为 `Factory`) 作为参数。
实际上，它们的参数类型是 `ApiDescription` —— 一个用于描述API信息的类型，也是用来跟 `ApiPermissions` 进行配合的。

在API模块中，所有 `QQGuildApi` 实现的**伴生对象**均会实现此接口并提供此API的基本信息，以便于在 `ApiPermissions` 中使用。

## ApiPermissionDemand

在API模块中，`DemandApiPermissionApi` 可以用来创建一个 API 接口权限授权链接。

> 参考官方文档: [创建频道 API 接口权限授权链接](https://bot.q.qq.com/wiki/develop/api/openapi/api_permissions/post_api_permission_demand.html)

此API请求成功后的返回值类型即为 `ApiPermissionDemand`。
