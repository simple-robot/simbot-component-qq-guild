# Simply-Robot for tencent-guild


这是 [simple-robot](https://github.com/ForteScarlet/simpler-robot) 下的子项目，本库提供对 [腾讯频道机器人](https://bot.q.qq.com/wiki/develop/api/) 中`API`内容的完整实现以及事件相关的信令实现，但 **不提供** 直接的BOT事件监听与交互。


本库中的api响应数据会一定程度实现 `simple-robot` 的`v3.x`api模块接口。


对于针对BOT、事件等交互相关内容的实现，将会由 `simple-robot` 的组件模块进行对接。


本库通过 `kotlin` 协程达成全异步的高效实现，基于 `ktor` 进行网络交互，基于 `kotlinx-serialization` 进行数据序列化/反序列化。


~~虽然之前是打算通过kotlin做多平台实现的但是最后还是放弃了~~


当然, 如果你只需要使用API，不考虑bot、事件相关内容，你可以通过此库来直接使用相关API。


以及，这个README看上去挺乱的，等一切安好之后会再考虑优化一下。


## 使用
⚠ 尚未发布至中央仓库

本库将会 `simple-robot` 3.x发布时（或相对较其早的时刻）发布于Maven中央仓库。主要是因为 `simple-robot 3.x` 仍处于设计开发阶段，可能会出现一些接口变动。

如果你现在就想尝试，有两个办法：
1. issue进行留言，我会临时提供编译版本
2. 自行clone当前仓库以及 <https://github.com/ForteScarlet/simpler-robot/tree/dev-v3.0.0-preview>(注意观察，分支是 `v3.0.0-preview`), 然后先将 simple-robot 发布至个人本地仓库，然后编译本库。

### Maven
```xml
<dependiency>
    <dependency>
        <groupId>love.forte.simple-robot</groupId>
        <artifactId>tencent-guild-api</artifactId>
        <version>敬请期待</version>
    </dependency>
</dependiency>
```

### Gradle groovy
```groovy
implementation "love.forte.simple-robot:tencent-guild-api:$version"
```

### Gradle kotlin DSL
```kotlin
implementation("love.forte.simple-robot:tencent-guild-api:$version")
```

## 示例
api请求：

### Kotlin
```kotlin
    val client: HttpClient = //....
    val token: String = "" // token    

    suspend fun run() {
        // 得到一个api请求对象
        val api = BotGuildListApi(before = null, after = null, limit = 10)
    
        // api.request 发起请求
        val guildList: List<TencentGuildInfo> = api.request(
            client = client,
            server = Url("https://sandbox.api.sgroup.qq.com"), // 请求server地址
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
                "token",
                decoder
        );

        for (TencentGuildInfo info : infoList) {
            System.out.println(info);
            System.out.println(info.getName());
            System.out.println(info.getOwnerId());
        }
    }
```



