#  

<div align="center">
<img src=".simbot/logo.png" alt="logo" style="width:230px; height:230px; border-radius:50%; " />
<h2>
    ~ simple-robot for qq-guild ~ 
</h2>
<a href="https://github.com/simple-robot/simbot-component-qq-guild/releases/latest"><img alt="release" src="https://img.shields.io/github/v/release/simple-robot/simbot-component-qq-guild" /></a>
<a href="https://repo1.maven.org/maven2/love/forte/simbot/component/simbot-component-qq-guild-api/" target="_blank">
  <img alt="release" src="https://img.shields.io/maven-central/v/love.forte.simbot.component/simbot-component-qq-guild-api" /></a>
<a href="https://www.yuque.com/simpler-robot/simpler-robot-doc" target="_blank">
  <img alt="doc" src="https://img.shields.io/badge/doc-yuque-brightgreen" /></a>
   <hr>
   <img alt="stars" src="https://img.shields.io/github/stars/simple-robot/simbot-component-qq-guild" />
   <img alt="forks" src="https://img.shields.io/github/forks/simple-robot/simbot-component-qq-guild" />
   <img alt="watchers" src="https://img.shields.io/github/watchers/simple-robot/simbot-component-qq-guild" />
   <img alt="repo size" src="https://img.shields.io/github/repo-size/simple-robot/simbot-component-qq-guild" />
   <img alt="lines" src="https://img.shields.io/tokei/lines/github/simple-robot/simbot-component-qq-guild" />
   <img alt="issues" src="https://img.shields.io/github/issues-closed/simple-robot/simbot-component-qq-guild?color=green" />
   <img alt="last commit" src="https://img.shields.io/github/last-commit/simple-robot/simbot-component-qq-guild" />
   <a href="./COPYING"><img alt="copying" src="https://img.shields.io/github/license/simple-robot/simbot-component-qq-guild" /></a>

</div>

这是 [simbot3](https://github.com/simple-robot/simpler-robot)
下的子项目， 本库提供对 [QQ频道机器人](https://bot.q.qq.com/wiki/develop/api/) 中`API`内容的完整实现以及事件相关的信令实现， 以及BOT对于事件的监听与交互。

_下文 `simple-robot v3.x` 简称为 `simbot3`_

<br>

## 文档

了解simbot3: [simbot3官网](https://simbot.forte.love)

KDoc(JavaDoc): [文档引导站点](https://docs.simbot.forte.love) 中的 [QQ频道KDoc站点](https://docs.simbot.forte.love/components/qq-guild)

<br>

本库通过 [`Kotlin`](https://kotlinlang.org/) 提供多平台/JVM平台(core模块) 实现； 
基于 [`kotlinx.coroutines`](https://github.com/Kotlin/kotlinx.coroutines) 与
[`Ktor`](https://ktor.io/)
提供高效易用的API；
基于 [`kotlinx.serialization`](https://github.com/Kotlin/kotlinx.serialization) 进行数据序列化/反序列化操作。

<br>

## 前言

目前无论是当前仓库还是 `simbot3` 都处于紧张的早中期建设阶段，在正式版发布之前必然存在很多不可预知的问题，并且开发进度无法清晰预估，毕竟一天一共就24个小时，而我们团队的精力并非无限。

如果你想要协助我们对当前仓库或者 [simbot3](https://github.com/ForteScarlet/simpler-robot/tree/v3-dev)
的建设，欢迎通过issue提出宝贵意见或者通过pr参与到建设当中，十分感谢。

## 模块引导

### API模块

> JVM/JS/Native

基于 `Ktor` 针对 [QQ频道API](https://bot.q.qq.com/wiki/develop/api/) 的基本完整的封装实现，
是一个简单高效轻量级的API实现模块。

此模块基本不会提供什么多余的实现，其目标为在提供封装的情况下尽可能地保留原始API的使用手感，不做过多的封装。

👉 [前往模块](simbot-component-qq-guild-api) 了解更多。

### 标准库模块

> JVM/JS/Native

基于 [API模块](simbot-component-qq-guild-api) 针对bot的"登录"鉴权实现简单高效轻量级的事件订阅功能。

此模块在API模块的基础上提供了针对事件相关的功能实现，包括事件订阅的能力。
同样的，其目标为在提供封装的情况下尽可能地保留原始API的使用手感，不做过多的封装。

👉 [前往模块](simbot-component-qq-guild-stdlib) 了解更多。

### 核心组件模块

> JVM Only

基于 [标准库模块](simbot-component-qq-guild-stdlib) 对 [simbot3核心库](https://github.com/simple-robot/simpler-robot) 的组件实现，
是一个相对高度封装的模块，并提供simbot3大部分能力，包括事件监听、多组件协同、Spring Boot Starter 等。

👉 [前往模块](simbot-component-qq-guild-core) 了解更多。


## License

`simbot-component-qq-guild` 使用 `LGPLv3` 许可证开源。

```
This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General 
Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) 
any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
details.

You should have received a copy of the GNU Lesser General Public License along with this program. 
If not, see <https://www.gnu.org/licenses/>.
```

