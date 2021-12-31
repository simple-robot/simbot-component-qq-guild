<div align="center">
    <img src="../.simbot/logo.png" alt="logo" style="width:230px; height:230px; border-radius:50%; " />
    <h2>
        - simple-robot-component : tencent-guild -
    </h2>
    <h4>
        ~ api ~
    </h4>
    <br />
<a href="https://repo1.maven.org/maven2/love/forte/simbot/component/simbot-component-tencent-guild-api" target="_blank">
  <img alt="release" src="https://img.shields.io/maven-central/v/love.forte.simbot.component/simbot-component-tencent-guild-api" /></a>
<a href="https://www.yuque.com/simpler-robot/simpler-robot-doc" target="_blank">
  <img alt="doc" src="https://img.shields.io/badge/doc-yuque-brightgreen" /></a>
<hr />
</div>
api模块是对官方提供的所有 api 的 **底层** 级别封装，不提供较为高级的应用层面封装，是一个单纯的底层api库。

如果你想配合bot的事件监听的简易实现，请参考 [core](../core) 模块。

## 使用

本库，目前仅基于 `simple-robot 3.0.0-preview.0.1` 版本api，从版本可以看出来，simbot 3.x的版本仍然处于极早期的预览阶段，
因此当前模块也相应的仅仅只是一个"预览"版本。

本库将会 `simple-robot` 3.x发布时（或相对较其早的时刻）发布相应的正式版本。主要是因为 `simple-robot 3.x` 仍处于设计开发阶段，可能会出现一些接口变动。

### Maven
版本参考：*见头部图标*

```xml
<dependency>
    <groupId>love.forte.simbot.component</groupId>
    <artifactId>simbot-component-tencent-guild-api</artifactId>
    <version>${version}</version>
</dependency>
```

### Gradle groovy
api模块：
```groovy
implementation "love.forte.simbot.component:simbot-component-tencent-guild-api:$version"
```

### Gradle kotlin DSL
api模块：
```kotlin
implementation("love.forte.simbot.component:simbot-component-tencent-guild-api:$version")
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

可以看到，上述命名为 `xxxxxApi` 的类就是用于请求的封装类了，它们存在于 [api](.) 模块的 `love.forte.simbot.tencentguild.api` 包下，基本与官方API文档中提及的API一一对应：

- **channel相关**
    - GetChannelApi
    - GetGuildChannelList
- **channel permission相关**
  - GetChannelMemberPermissionsApi
  - ModifyChannelMemberPermissionsApi
- **guild相关**
    - GetBotGuildListApi
    - GetGuildApi
- **member相关**
    - GetMemberApi
- **message相关**
    - GetMessageApi
    - MessageSendApi
- **role相关**
    - AddMemberRoleApi
    - CreateGuildRoleApi
    - DeleteGuildRoleApi
    - GetGuildRoleListApi
    - ModifyGuildRoleApi
    - RemoveMemberRoleApi




<br>
<br>

## 待建设内容

- 考虑api的缓存支持
