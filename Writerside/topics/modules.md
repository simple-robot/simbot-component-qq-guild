# 模块概述

<include from="snippets.md" element-id="to-main-doc" />

本章节对整个 [Simple Robot QQ频道组件库](https://github.com/simple-robot/simbot-component-qq-guild)
中出现的各模块进行一个简单的介绍。

## API模块 {id='module-api'}

API模块即指 `simbot-component-qq-guild-api` 模块，
是对 [**QQ频道API**](https://bot.q.qq.com/wiki/develop/api/)
的封装与实现，会尽可能保留API的**原本风味**，不会做过多的封装。

API模块**并非** simbot 的组件库实现，但是组件库实现的基础。

它是一个可以**独立使用**的轻量级QQ频道API实现库。

如果想要请求这个API，那么你需要构建它，并提供所有所需的外部变量，比如
一个用于请求的 `HttpClient`、一个 `token`、甚至可选的提供一个第三方的服务器地址。

有关更多使用API的方式，可前往
<a href="api.md#api-usage" />
了解。

<procedure collapsible="true" title="安装与引用">

如果想要引用它：

<include from="snippets.md" element-id="deps">
<var name="module" value="simbot-component-qq-guild-api"></var>
</include>

</procedure>


## Stdlib模块 {id='module-stdlib'}

Stdlib模块，也可称之为标准库模块，即 `simbot-component-qq-guild-stdlib` 模块，
是在
<a href="#module-api">API模块</a>
的基础上，提供一种**轻量级的** `Bot` 功能实现的模块，
也就是提供了对**事件订阅与处理**能力的轻量级实现。

Stdlib模块**并非**simbot的组件库实现，但是组件库实现的基础。

它是一个可以**独立使用**的轻量级QQ频道SDK实现库。


如果想要引用它：

<include from="snippets.md" element-id="deps">
<var name="module" value="simbot-component-qq-guild-stdlib"></var>
</include>

## Core模块 {id='module-core'}

Core模块，也称为组件库模块、核心模块、组件核心模块，即
`simbot-component-qq-guild-core` 模块。
它是在
<a href="#module-stdlib">标准库模块</a>
的基础上实现 simbot 标准API的**真正意义**上的simbot组件库实现。

文档中大部分的功能介绍章节也都会分出一部分来介绍如何在核心模块下的使用，
例如 `Bot` (标准库模块内的类型) 与 `QGBot` (组件库模块内的类型)。

如果想要引用它：

<include from="snippets.md" element-id="deps">
<var name="module" value="simbot-component-qq-guild-core"></var>
</include>

## Simple Robot 模块与组件库模块之间的关系 {id='with-simbot'}

> 你可以前往
[Simple Robot 应用手册](https://simbot.forte.love)
来了解更多 Simple Robot 的模块说明。

首先，
<a href="#module-api">API模块</a>
和
<a href="#module-stdlib">标准库模块</a>
不直接实现simbot标准API，而只是部分借用simbot提供的**通用功能**
(例如多平台的 `atomic` 实现、多平台的 `ConcurrentMap` 实现等)
来简化代码。

而
<a href="#module-core">组件库模块</a>
是**直接实现**simbot标准API的 
_(也就是 `love.forte.simbot:simbot-api`)_，
并实现其中的诸多类型，来使得自己成为一个**组件库**，并可以让自己在simbot的核心库
_(也就是 `love.forte.simbot:simbot-core`)_
或其他实现应用实现 _(例如 simbot 的 Spring Boot starter 实现)_ 中使用。


