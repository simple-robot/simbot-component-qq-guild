[模块说明](Module.md)

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
    public void run(){
// 得到一个 http client
final HttpClient client=ApiRequestUtil.newHttpClient();
// 得到一个 Json
final Json decoder=ApiRequestUtil.newJson((builder)->{
        builder.setLenient(true);
        builder.setIgnoreUnknownKeys(true);
        });

// 构建请求
final BotGuildListApi api=new BotGuildListApi(null,null,10);

// 使用 ApiRequestUtil.doRequest
final List<?extends TencentGuildInfo> infoList=ApiRequestUtil.doRequest(
        api,
        client,
        "https://sandbox.api.sgroup.qq.com",
        "token: Bot + 你的token",
        decoder
        );

        for(TencentGuildInfo info:infoList){
        System.out.println(info);
        System.out.println(info.getName());
        System.out.println(info.getOwnerId());
        }
        }
```

可以看到，上述命名为 `xxxxxApi` 的类就是用于请求的封装类了，它们存在于 [api](.) 模块的 `love.forte.simbot.qguild.api` 包下，基本与官方API文档中提及的API一一对应：

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
