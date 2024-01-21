# BOT配置文件

```json
{
    "component": "simbot.qqguild",
    "ticket": {
        "type": "plain",
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
        "timeout": {
          "apiHttpRequestTimeoutMillis": null,
          "apiHttpConnectTimeoutMillis": null,
          "apiHttpSocketTimeoutMillis": null
        },
        "cache": {
          "enable": true,
          "transmit": {
            "enable": true
          }
        },
        "clientProperties": null
    }
}
```


> 文件配置类的各属性定义可参考API文档: 
> [`QGBotFileConfiguration`](https://docs.simbot.forte.love/components/qq-guild/simbot-component-qq-guild-core-common/love.forte.simbot.component.qguild.config/-q-g-bot-file-configuration/index.html)

## 配置项

### component

固定值 `simbot.qqguild`，**必填**，代表此配置文件为QQ频道组件的。

### ticket

bot的票据信息，**必填**。

- `type`: 配置属性的类型，详见后文
- `appId`: BotAppID
- `token`: 机器人令牌
- `secret`: 机器人密钥 (目前可能不会用到，可以用 `""` 代替)
- 
#### plain

当 `type=plain` 时，与 `Ticket` 属性基本一致的配置类型， 也是默认的方案。

```json
{
  "ticket": {
    "type": "plain",
    "appId": "appId-value",
    "secret": "secret-value",
    "token": "token-value"
  }
}
```

:::note 省略type

当 simbot-core 版本为 `3.2.0+` 时，`type` 作为默认值 `plain` 时可以省略：

```json
{
  "ticket": {
    "appId": "appId-value",
    "secret": "secret-value",
    "token": "token-value"
  }
}
```

:::

#### env

当 `type=env` 时，使用环境变量的方式进行配置。

```json
{
  "ticket": {
    "type": "env",
    "appId": "APP_ID",
    "secret": "SECRET",
    "token": "TOKEN",
    "plain": false
  }
}
```

解析时会首先尝试获取 JVM 参数，即运行时的 `-Dxxx=xxx` （也就是 `System.getProperty`），
当不存在时会尝试通过环境变量获取（即 `System.getenv`）。

**原始输入**

当 `plain` 为 `true` 时（默认为 `false`），如果某属性通过上述流程无法获取到值，则会尝试直接使用原始输入值。

例如：

```json
{
  "ticket": {
    "type": "env",
    "appId": "aaa",
    "secret": "MY_SECRET",
    "token": "MY_TOKEN",
    "plain": true
   }
}
```

示例中的 `appId` 并没有找到名为 `aaa` 的 JVM 参数或环境变量，因此它会直接使用 `aaa` 作为 `appId`。 
而如果 `plain` 为 `false`，则会直接抛出 `IllegalStateException` 异常。

当一个属性以 `PLAIN:` （区分大小写） 为前缀，则会直接使用原始输入值，不会尝试从环境变量中获取。

例如：

```json
{
  "ticket": {
    "type": "env",
    "appId": "PLAIN:aaa",
    "secret": "MY_SECRET",
    "token": "MY_TOKEN",
    "plain": false
  }
}
```

示例中 `appId` 会直接使用 `aaa` 作为 `appId`，而不会尝试从 JVM 参数或环境变量中获取。

### config

其他配置，可选，默认为 `null`。

#### config.serverUrl

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

#### config.shard

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

#### config.intents

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
