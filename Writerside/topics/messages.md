# 消息类型

<tldr>

- 对那些 **核心库** 中、实现了 simbot4 标准库的 `Message.Element` 消息元素类型的说明，
- 对一些与 **核心库** 中“消息”相关的内容的补充说明。

</tldr>

## 消息元素实现

所有的 `Message.Element` 特殊实现类型均定义在包 `love.forte.simbot.component.qguild.message` 中。

它们都继承了 `love.forte.simbot.component.qguild.message.QGMessageElement` 。

<deflist>
<def title="QGArk">对 API 模块中 Ark 消息的包装体，可用来发送 <code>Ark</code> 消息。</def>
<def title="QGContentText"></def>
<def title="QGEmbed">对 API 模块中 Embed 消息的包装体，可用来发送 <code>Embed</code> 消息。</def>
<def title="QGOfflineImage">TODO Message type</def>
<def title="QGImage">TODO Message type</def>
<def title="QGReference">

发送消息时，QQ频道的消息引用。与官方发送消息API中的 `reference` 对应。

</def>
<def title="QGReplyTo">

<tip>仅用于发送。</tip>

发送消息时，指定一个需要回复的目标消息ID。

</def>
</deflist>
