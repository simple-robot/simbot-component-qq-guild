# Bot配置文件

<tldr>
<p>在使用 <b>Spring Boot</b> 时自动注册 bot 所需的配置文件。</p>
</tldr>

## 示例

```json
{
    "component": "simbot.qqguild",
    "ticket": {
        "appId": "你的botId",
        "secret": "你的bot secret, 如果用不到也可以给空字符串",
        "token": "你的bot token"
    }
}
```
{collapsible="true" default-state="expanded" collapsed-title="简单示例"}

```json
{
  "component": "simbot.qqguild",
  "ticket": {
    "appId": "你的botId",
    "secret": "你的bot secret, 如果用不到也可以给空字符串",
    "token": "你的bot token"
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
    "clientProperties": null,
    "timeout": null,
    "cache": {
      "enable": true,
      "transmit": null,
      "dynamic": null,
      "dispatcher": null
    }
  }
}
```
{collapsible="true" default-state="collapsed" collapsed-title="完整示例"}

## 属性描述

<deflist>
<def title="component">

固定值：`simbot.qqguild`

</def>
<def title="ticket">

bot用于登录的票据信息，必填。

<deflist type="wide">
<def title="appId">

`String`

bot开发配置中的 `appID`

</def>
<def title="secret">

`String`

bot开发配置中的 `AppSecret`, 如果用不到可以给空字符串

</def>
<def title="token">

`String`

bot开发配置中的 `Token`。

</def>
</deflist>

</def>

<def title="config">
可选项，提供一些额外的可配置属性。

<deflist>
<def title="serverUrl">

`String`

目标服务器地址。默认为 `null`。

当值为特殊值：`"SANDBOX"` 时会选择使用 `QQGuild.SANDBOX_URL_STRING`，
也就是沙箱服务器地址。

</def>
<def title="shard">

`ShardConfig`

分片信息配置，默认为 `Full`。

根据 `type` 值的不同，可使用不同的属性。

<deflist>
<def title="type='full'">

无额外属性，代表一个全量单片。

```json
{
    "type": "full"
}
```

</def>
<def title="type='simple'">

```json
{
   "type": "simple",
   "value": 0,
   "total": 1
}
```

<deflist type="medium">
<def title="value">对应的分片信息属性。</def>
<def title="total">对应的分片信息属性。</def>
</deflist>

</def>
</deflist>


</def>
<def title="intents">

`IntentsConfig?`

要订阅的事件的 intents 信息。默认 `1073741827`，
也就是订阅：
- 频道相关事件
- 频道成员相关事件
- 公域消息相关事件

根据 `type` 的不同可选的属性不同。

<deflist>
<def title="type='raw'">

直接使用 `intents` 原始的标记位最终数值。

```json
{
  "type": "raw",
  "intents": 1073741824
}
```

</def>
<def title="type='nameBased'">

通过名称寻找所有可用的 `EventIntents` 并合并为最终的 `intents`。
名称基于继承了 `EventIntents` 的 object 的简单类名，例如 `Guilds`。

```json
{
  "type": "nameBased",
  "names": ["Guilds", "PublicGuildMessages"]
}
```

</def>
</deflist>

</def>
<def title="clientProperties">

`Map<String, String>?`

用作 `Signal.Identify.Data.properties` 中的参数。

```json
{
  "config": {
    "clientProperties": {
      "k1": "v1",
      "foo": "bar"
    }
  }
}
```

</def>
<def title="timeout" type="wide">

`TimeoutConfig?`

与部分超时相关的配置信息。
当任意属性不为 `null` 时会为 bot 中用于请求API的 `HttpClient`
配置 [HttpTimeout][HttpTimeout] 插件。

默认为 `null`。

<deflist>
<def title="apiHttpRequestTimeoutMillis">

`Long?`

API请求中的超时请求配置。参考 [HttpTimeout][HttpTimeout] 中的相关说明。

默认为 `null`。

</def>
<def title="apiHttpConnectTimeoutMillis">

`Long?`

API请求中的超时请求配置。参考 [HttpTimeout][HttpTimeout] 中的相关说明。

默认为 `null`。

</def>
<def title="apiHttpSocketTimeoutMillis">

`Long?`

API请求中的超时请求配置。参考 [HttpTimeout][HttpTimeout] 中的相关说明。

默认为 `null`。

</def>
</deflist>

</def>
<def title="cache">

`CacheConfig?`

缓存相关配置。

```json
"config": {
    "cache": {
        "transmit": {
            "enable": true
        }
    }
}
```

有关 `transmit` 的详细描述，
请参考 `TransmitCacheConfig` 的文档注释或 API Doc。

</def>
</deflist>
</def>
</deflist>

[HttpTimeout]: https://ktor.io/docs/timeout.html
