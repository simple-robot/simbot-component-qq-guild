# 用例: 发送图片

<include from="snippets.md" element-id="to-main-doc" />

发送图片是一个经常使用、且经常被问及“怎么发图片”的内容。

本章节介绍在QQ频道组件中，如何发送一个图片。

## API模块 {id='api-module'}

假如你只使用了API模块，那么就需要普通地使用API来发送图片: 
使用 `MessageSendApi` 中的 `fileImage` 或 `image` 属性上传图片。

其中 `image` 比较简单，就是一个图片的地址。

<warning title="预料之中">
需要注意的是，图片地址的域名可能需要提前报备。
</warning>

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
val api = MessageSendApi.create("channel id") {
    image = "https://图片.域名/xxx.jpg"
}

// 请求API，即发送消息
api.request(...)
```

</tab>
<tab title="Java" group-key="Java">

```Java
// 你的图片资源
var myFileImage = ...
// MessageSendApi 的 body 的 builder
var bodyBuilder = MessageSendApi.Body.builder();
bodyBuilder.setImage("https://图片.域名/xxx.jpg");
// 构建API
var api = MessageSendApi.create("channel id", bodyBuilder.build());

// 请求API，即发送消息
XxxRequests.requestXxx(...)
```

</tab>
</tabs>


另一种方式便是使用 `fileImage` 上传一个图片。

首先，准备好你的图片并提供给参数 `fileImage`。

在**所有平台**中，都可以提供如下类型的图片:

- `ByteArray` (字节数组)
- `InputProvider` (参考 [Ktor](https://ktor.io))
- `ByteReadPacket` (参考 [Ktor](https://ktor.io))
- `ChannelProvider` (参考 [Ktor](https://ktor.io))

在 JVM 平台中，**额外**可以提供如下类型的参数：

- `java.io.File` (本地图片资源)
- `java.nio.Path` (本地图片资源)
- `java.net.URL` (远程图片资源，会先下载到本地)
- `java.net.URI` (远程图片资源，会先下载到本地)

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
// 你的图片资源
val myFileImage = ... 
// 构建API
val api = MessageSendApi.create("channel ID") {
    fileImage = myFileImage
    // ...    
}

// 请求API，即发送消息
api.request(...)
```

</tab>
<tab title="Java" group-key="Java">

```Java
// 你的图片资源
var myFileImage = ...
// MessageSendApi 的 body 的 builder
var bodyBuilder = MessageSendApi.Body.builder();
bodyBuilder.setFileImage(myFileImage);
// 构建API
var api = MessageSendApi.create("channel id", bodyBuilder.build());

// 请求API，即发送消息
XxxRequests.requestXxx(...)
```

</tab>
</tabs>

> API的请求可前往参考
> <a href="api.md" />

## 组件库模块 {id='core-module'}

更多的时候也可能是想要在使用组件库配合simbot的时候发送一个图片。

实际上，simbot标准API中提供了一些常见、通用的消息类型，其中就包括图片类型。
当希望发送消息，那么你可以构建一个 `OfflineImage`，
而 `OfflineImage` 则可以通过 `Resource` 构建而来。

> 更多标准API中的消息类型可前往参考 [Simple Robot 应用手册](https://simbot.forte.love/basic-messages.html#message-element)

<tabs group="code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
// 你想要发送消息的目标 QGTextChanenl
// 只有文字子频道 QGTextChanenl 才能发送消息
// 其他类型的 QGChannel 无法发送消息
val channel: QGTextChannel = ...

// 获取到一个 Resource, 此处以 JVM 的 Path 为参考
val imageFile = Path("本地图片/地址/image.png").toResource()
val offlineImage = imageFile.toOfflineImage()

channel.send(offlineImage)
// 或配合其他消息元素发送，比如文字
channel.send("你好".toText() + offlineImage)
```

</tab>
<tab title="Java" group-key="Java">

```Java
// 你想要发送消息的目标 QGTextChanenl
// 只有文字子频道 QGTextChanenl 才能发送消息
// 其他类型的 QGChannel 无法发送消息
QGTextChannel channel = ...

// 获取到一个 Resource, 此处以 Path 为参考
var path = Path.of("本地图片/地址/image.png");
var resource = Resources.valueOf(path);
var offlineImage = OfflineImage.ofResource(resource);

channel.sendXxx(offlineImage);
// 或配合其他消息元素发送，比如文字
channel.sendXxx(Messages.of(Text.of("你好"), offlineImage));
```

</tab>
</tabs>

> 离线图片资源每次发送都会上传，就像在 API 中每次都提供 `fileImage` 参数一样。
