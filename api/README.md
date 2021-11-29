# API

api模块是对官方提供的所有 api 的 **底层** 级别封装，不提供较为高级的应用层面封装，是一个单纯的底层api库。

如果你想配合bot的事件监听的简易实现，请参考 [core](../core) 模块。

## 使用
⚠ 尚未发布至中央仓库

本库将会 `simple-robot` 3.x发布时（或相对较其早的时刻）发布于Maven中央仓库。主要是因为 `simple-robot 3.x` 仍处于设计开发阶段，可能会出现一些接口变动。

如果你现在就想尝试，有两个办法：

1. issue进行留言，我会临时提供编译版本
2. 自行clone当前仓库以及 <https://github.com/ForteScarlet/simpler-robot/tree/dev-v3.0.0-preview>(注意观察，分支是 `v3.0.0-preview`), 然后先将 simple-robot 发布至个人本地仓库，然后编译本库。

### Maven

```xml
<dependency>
    <groupId>love.forte.simple-robot</groupId>
    <artifactId>tencent-guild-api</artifactId>
    <version>敬请期待</version>
</dependency>
```

### Gradle groovy
api模块：
```groovy
implementation "love.forte.simple-robot:tencent-guild-api:$version"
```

### Gradle kotlin DSL
api模块：
```kotlin
implementation("love.forte.simple-robot:tencent-guild-api:$version")
```

## 示例
api请求：

### Kotlin
```kotlin
    val client: HttpClient = //....
    val token: String = "Bot + 你的token" // token    

    suspend fun run() {
        // 得到一个api请求对象
        val api = BotGuildListApi(before = null, after = null, limit = 10)
    
        // api.request 发起请求
        val guildList: List<TencentGuildInfo> = api.request(
            client = client,
            server = Url("https://sandbox.api.sgroup.qq.com"), // 请求server地址. 你可以通过 TencentGuildApi.URL 得到一个官方地址的默认值。
            token = token,
            decoder = Json // 可以省略，但最好给一个自定义的
        )
    
        guildList.forEach { guildInfo ->
            println(guildInfo)
            println(guildInfo.id)
            println(guildInfo.name)
        }
    }
```

### Java
Java中，你最好想办法弄到一个 `HttpClient` 实例与 `Json` 实例。

```java
    public void run() {
        // 得到一个 http client
        final HttpClient client = ApiRequestUtil.newHttpClient();
        // 得到一个 Json
        final Json decoder = ApiRequestUtil.newJson((builder) -> {
            builder.setLenient(true);
            builder.setIgnoreUnknownKeys(true);
        });

        // 构建请求
        final BotGuildListApi api = new BotGuildListApi(null, null, 10);

        // 使用 ApiRequestUtil.doRequest
        final List<? extends TencentGuildInfo> infoList = ApiRequestUtil.doRequest(
                api,
                client,
                "https://sandbox.api.sgroup.qq.com",
                "token: Bot + 你的token",
                decoder
        );

        for (TencentGuildInfo info : infoList) {
            System.out.println(info);
            System.out.println(info.getName());
            System.out.println(info.getOwnerId());
        }
    }
```

可以看到，上述命名为 `xxxxxApi` 的类就是用于请求的封装类了，它们存在于 [api](api) 模块的 `love.forte.simbot.tencentguild.api` 包下，基本与官方API文档中提及的API一一对应：

- channel相关
    - GetChannelApi
    - GetGuildChannelList
- guild相关
    - BotGuildListApi
    - GetGuildApi
- member相关
    - GetMemberApi
- message相关
    - GetMessageApi
    - MessageSendApi
- role相关
    - AddMemberRoleApi
    - CreateGuildRoleApi
    - DeleteGuildRoleApi
    - GetGuildRoleListApi
    - ModifyGuildRoleApi
    - RemoveMemberRoleApi

