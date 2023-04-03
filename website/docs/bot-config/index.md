---
title: BOT配置文件
---

:::caution 待施工

待施工

:::

```json title='xxx.bot.json'
{
    "component": "simbot.qqguild",
    "ticket": {
        "appId": "APPID",
        "token": "TOKEN",
        "secret": "SECRET"
    },
    "config": {
        "serverUrl": null,
        "shard": {
            "type": "full"
        },
        "intents": {
            "type": "raw",
            "intents": 1073741827
        },
        "clientProperties": null
    }
}
```

#### `component`

固定值 `simbot.qqguild`，**必填**，代表此配置文件为QQ频道组件的。

#### `ticket`

bot的票据信息，**必填**。

- `appId`: BotAppID
- `token`: 机器人令牌
- `secret`: 机器人密钥 (目前暂时不会用到，可以用 `""` 代替)

#### `config`

其他配置，可选。

##### `config.serverUrl`

内部进行API请求时的服务器地址，参考[官方文档](https://bot.q.qq.com/wiki/develop/api/)

默认为 `null`，为 `null` 时为正式环境，可使用一个固定值 `SANDBOX` 代表使用沙箱环境

```json
{
  "config": {
    "serverUrl": "SANDBOX"
  }  
}
```

或者使用一个具体的其他服务器地址

```json
{
  "config": {
    "serverUrl": "https://example.com"
  }  
}
```

##### `config.shard`

[分片信息](https://bot.q.qq.com/wiki/develop/api/gateway/shard.html)，默认为 `type=full`，即使用 `[0, 1]` 的分片。

可以使用 `type=simple` 自定义分片：

```json
{
  "config": {
    "shard": {
      "type": "simple",
      "value": 0,
      "total": 2
    }
  }  
}
```

##### `config.intents`

[订阅的事件](https://bot.q.qq.com/wiki/develop/api/gateway/intents.html)，默认情况下订阅：

- `Guilds`
- `GuildMembers`
- `PublicGuildMessages`

可通过 `type=raw` 来直接指定一个原始的订阅标记结果值：

```json
{
  "config": {
    "intents": {
      "type": "raw",
      "intents": 1073741827
    }
  }  
}
```

或者使用 `type=nameBased` 通过指定名称（名称选择参考 `EventIntents` 类的所有 `object` 类型的字类类名）：

```json
{
  "config": {
    "intents": {
      "type": "nameBased",
      "names": ["Guilds", "GuildMembers", "PublicGuildMessages"]
    }
  }  
}
```
