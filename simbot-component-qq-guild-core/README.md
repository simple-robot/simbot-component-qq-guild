# core组件模块

请先阅读 [**模块说明**](Module.md) .

## 命名说明

在QQ频道组件中，所有相关的类型（包括但不限于事件、频道、子频道等相关对象）均会以 `QG` 为前缀命名 (例如 `QGGuild` 、`QGBot`)，代表 `QQ-Guild`。
其中除了 `Component` 的实现 `QQGuildComponent`。组件的实现使用全名 `QQGuild` 作为前缀。


## 事件支持

目前，组件支持的事件有：

| 事件                                | 描述              |
|:----------------------------------|:----------------|
| `QGGuildEvent`                    | 频道服务器相关事件       |
| -> `QGGuildCreateEvent`           | 频道服务器进入         |
| -> `QGGuildUpdateEvent`           | 频道服务器信息更新       |
| -> `QGGuildDeleteEvent`           | 频道服务器离开         |
| `QGChannelEvent`                  | 子频道相关事件         |
| -> `QGChannelCreateEvent`         | 子频道新增           |
| -> `QGChannelUpdateEvent`         | 子频道信息变更         |
| -> `QGChannelDeleteEvent`         | 子频道删除           |
| -> `QGChannelCategoryCreateEvent` | 子频道分类新增         |
| -> `QGChannelCategoryUpdateEvent` | 子频道分类信息变更       |
| -> `QGChannelCategoryDeleteEvent` | 子频道分类删除         |
| `QGMemberEvent`                   | 成员相关事件          |
| ->  `QGMemberAddEvent`            | 新增频道成员          |
| ->  `QGMemberUpdateEvent`         | 频道成员信息更新        |
| ->  `QGMemberRemoveEvent`         | 频道成员离开/移除       |
| `QGMessageEvent`                  | 消息事件            |
| -> `QGAtMessageCreateEvent`       | At消息（公域消息）事件    |

以及一个用于兜底儿的 `QGUnsupportedEvent` 类型事件。

## 示例

### 直接使用

```kotlin
suspend fun main() {
    val app = createSimpleApplication {
        useQQGuild()
    }

    // 注册事件
    app.eventListenerManager.listeners {
        // 监听事件: 频道成员信息变更事件
        QGMemberUpdateEvent { event ->
            println("UPDATE:   $event")
            println("member:   ${event.member()}")
            println("operator: ${event.operator()}")
            println("guild:    ${event.guild()}")
        }
        
        // 公域消息事件
        // 也可以使用 ChannelMessageEvent -> 这是来自 simbot 标准API的事件类型，也是 QGAtMessageCreateEvent 的父类
        QGAtMessageCreateEvent { event ->
            event.reply("纯文本消息，<会自动转义>")
            event.reply(QGContentText("content文本消息，不会转义，会引发内嵌格式 <#123456>"))

            // 使用消息链
            event.reply(Face(1.ID) + "纯文本".toText() + QGContentText("content文本") + AtAll)
            
            // 也可以使用 send 发送消息
            event.channel().send("纯文本")
        }
        
    }

    // 注册qq频道bot
    app.qqGuildBots {
        val bot = register(
            "APP ID",
            "SECRET",
            "TOKEN"
        ) {
            // 标准库里的bot配置
            botConfig {
                // 其他配置，例如使用沙箱环境
                useSandboxServerUrl()
            }
        }
        
        // 启动bot
        bot.start()
        
        // 直接操作bot获取一些信息
        // 获取bot的频道服务器列表
        val list = bot.guilds.toList()
        println(list)
        println(list.size)

        // 找到一个频道（这里我找的是沙箱的灰度频道）
        val home = list.first { "法欧莉" in it.name }
        
        // 获取所有的子频道
        home.channels.collect {
            println("Channel: $it")
            // 得到这个频道的所属分组
            val channelCategory = it.category.resolve()
        }
        
        // 获取所有的分类频道
        home.categories.collect {
            println("Category: $it")
        }

        // 这个频道的一些其他信息
        println("Owner:       ${home.owner()}")
        println("Permissions: ${home.permissions()}")
        
        // ...
        
    }


    app.join()

}
```

<details><summary>Java</summary>

```java
SimpleApplication application = Applications.createSimbotApplication(Simple.INSTANCE, (c) -> {}, (builder, configuration) -> {
    builder.install(QQGuildComponent.Factory, ($1, $2) -> Unit.INSTANCE);
    builder.install(QGBotManager.Factory, ($1, $2) -> Unit.INSTANCE);
});

// 监听一个事件
application.getEventListenerManager().register(SimpleListeners.listener(QGMemberUpdateEvent.Key, (context, event) -> {
    System.out.println("event:          " + event);
    System.out.println("event.raw:      " + event.getEventRaw());
    System.out.println("event.operator: " + event.getOperator());
    System.out.println("event.guild:    " + event.getGuild());
}));

// 寻找并注册一个QQ频道Bot
for (BotManager<?> botManager : application.getBotManagers()) {
    if (botManager instanceof QGBotManager) {
        QGBot bot = ((QGBotManager) botManager).register("101986850", "972f64f7c426096f9344b74ba85102fb", "g57N4WsHHRIx1udptqy7GBAEVsfLgynq", (config) -> {
            config.botConfig((bc) -> {
                bc.useSandboxServerUrl();
                return Unit.INSTANCE;
            });

            return Unit.INSTANCE;
        });

        bot.startBlocking();
        QGGuild forliy = null;
        List<QGGuild> guilds = bot.getGuilds().collectToList();
        for (QGGuild guild : guilds) {
            System.out.println("Guild: " + guild);
            if (guild.getName().contains("法欧莉")) {
                forliy = guild;
            }
        }
        assert forliy != null;

        System.out.println("================");

        forliy.getChannels().collect((channel) -> {
            System.out.println("Channel: " + channel);
            //System.out.println("Channel.category: " + channel.getCategory().resolveBlocking());
        });

        forliy.getCategories().collect((category) -> {
            System.out.println("Category: " + category);
        });

        break;
    }


}

application.joinBlocking();
```

</details>

### Spring Boot

如果希望使用 Spring Boot，参考 https://simbot.forte.love/docs/quick-start/spring-boot-starter 中的说明。
QQ频道组件实现了 `simbot` 约定的 `service SPI`，支持自动加载。 

## 注意事项
