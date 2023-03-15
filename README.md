#  

<div align="center">
<img src=".simbot/logo.png" alt="logo" style="width:230px; height:230px; border-radius:50%; " />
<h2>
    ~ simple-robot for qq-guild ~ 
</h2>
<a href="https://github.com/simple-robot/simbot-component-tencent-guild/releases/latest"><img alt="release" src="https://img.shields.io/github/v/release/simple-robot/simbot-component-tencent-guild" /></a>
<a href="https://repo1.maven.org/maven2/love/forte/simbot/component/simbot-component-qq-guild-api/" target="_blank">
  <img alt="release" src="https://img.shields.io/maven-central/v/love.forte.simbot.component/simbot-component-qq-guild-api" /></a>
<a href="https://www.yuque.com/simpler-robot/simpler-robot-doc" target="_blank">
  <img alt="doc" src="https://img.shields.io/badge/doc-yuque-brightgreen" /></a>
   <hr>
   <img alt="stars" src="https://img.shields.io/github/stars/simple-robot/simbot-component-tencent-guild" />
   <img alt="forks" src="https://img.shields.io/github/forks/simple-robot/simbot-component-tencent-guild" />
   <img alt="watchers" src="https://img.shields.io/github/watchers/simple-robot/simbot-component-tencent-guild" />
   <img alt="repo size" src="https://img.shields.io/github/repo-size/simple-robot/simbot-component-tencent-guild" />
   <img alt="lines" src="https://img.shields.io/tokei/lines/github/simple-robot/simbot-component-tencent-guild" />
   <img alt="issues" src="https://img.shields.io/github/issues-closed/simple-robot/simbot-component-tencent-guild?color=green" />
   <img alt="last commit" src="https://img.shields.io/github/last-commit/simple-robot/simbot-component-tencent-guild" />
   <a href="./COPYING"><img alt="copying" src="https://img.shields.io/github/license/simple-robot/simbot-component-tencent-guild" /></a>

</div>

这是 [simbot3](https://github.com/simple-robot/simpler-robot)
下的子项目， 本库提供对 [QQ频道机器人](https://bot.q.qq.com/wiki/develop/api/) 中`API`内容的完整实现以及事件相关的信令实现， 以及BOT对于事件的监听与交互。
本库中的api响应数据会一定程度实现 `simple-robot` 的`v3.x`api模块接口。 (下文 `simple-robot v3.x` 简称 `simbot3` )

<br>

## 文档

了解simbot3: [simbot3官网](https://simbot.forte.love)

KDoc(JavaDoc): [文档引导站点](https://docs.simbot.forte.love) 中的 [QQ频道KDoc站点](https://docs.simbot.forte.love/components/tencent-guild)

<br>

本库通过 `kotlin` 协程达成全异步的高效实现，基于 `ktor` 进行网络交互，基于 `kotlinx-serialization` 进行数据序列化/反序列化。

<br>

## 前言

目前无论是当前仓库还是 `simbot3` 都处于紧张的早中期建设阶段，在正式版发布之前必然存在很多不可预知的问题，并且开发进度无法清晰预估，毕竟一天一共就24个小时，而我们团队的精力并非无限。

如果你想要协助我们对当前仓库或者 [simbot3](https://github.com/ForteScarlet/simpler-robot/tree/v3-dev)
的建设，欢迎通过issue提出宝贵意见或者通过pr参与到建设当中，十分感谢。


## 模块引导




## License

`simbot-component-tencent-guild` 使用 `LGPLv3` 许可证开源。

```
This program is free software: you can redistribute it and/or modify it under the terms
of the GNU Lesser General Public License as published by the Free Software Foundation, 
either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this program. 
If not, see <https://www.gnu.org/licenses/>.
```

