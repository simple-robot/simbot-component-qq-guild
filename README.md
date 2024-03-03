#  

<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
<img src=".simbot/logo.png" alt="logo" style="width:230px; height:230px; border-radius:50%; " />
<h2>
    ~ Simple Robot ~ <br/> <small>QQ频道组件</small>
</h2>
<a href="https://github.com/simple-robot/simbot-component-qq-guild/releases/latest"><img alt="release" src="https://img.shields.io/github/v/release/simple-robot/simbot-component-qq-guild" /></a>
<a href="https://repo1.maven.org/maven2/love/forte/simbot/component/simbot-component-qq-guild-api/" target="_blank">
  <img alt="release" src="https://img.shields.io/maven-central/v/love.forte.simbot.component/simbot-component-qq-guild-api" /></a>
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

这是 
[**Simple Robot v4**](https://github.com/simple-robot/simpler-robot/tree/v4-dev)
下的子项目，是针对 
[**QQ频道机器人**](https://bot.q.qq.com/wiki/develop/api/) 
各方面的 simbot 组件库实现， 
包括对 `API` 内容的实现、事件相关的实现以及BOT对于事件的监听与交互等。

QQ频道组件库可以作为底层API依赖使用、
轻量级的QQ频道事件调度框架使用，
也可以基于 simbot 核心库的种种快速开发一个功能强大的QQ频道机器人！

- 基于 [`Kotlin`](https://kotlinlang.org/) 提供 [KMP 多平台](https://kotlinlang.org/docs/multiplatform.html) 特性
- 基于 [`Kotlin coroutines`](https://github.com/Kotlin/kotlinx.coroutines) 与 [`Ktor`](https://ktor.io/) 提供高效易用的API；

- 基于 [`Kotlin`](https://kotlinlang.org/) 提供 [KMP 多平台](https://kotlinlang.org/docs/multiplatform.html) 特性，提供 Java 友好的API。
- 基于 [`Kotlin coroutines`](https://github.com/Kotlin/kotlinx.coroutines) 与 [`Ktor`](https://ktor.io/) 提供轻量高效的API。

> [!Note]
> 下文中 `Simple Robot v4` 简称为 `simbot4`

## 文档

- 了解simbot: [**Simple Robot 应用手册**](https://simbot.forte.love)
- **QQ频道组件**手册：<https://component-qqguild.simbot.forte.love/> (即当前仓库的 GitHub Pages)
- **API文档**: [**文档引导站点**](https://docs.simbot.forte.love) 中QQ频道的 [**KDoc站点**](https://docs.simbot.forte.love/components/qq-guild)

---

我们欢迎并期望着您的
[反馈](https://github.com/simple-robot/simbot-component-qq-guild/issues) 
或 
[协助](https://github.com/simple-robot/simbot-component-qq-guild/pulls)，
感谢您的贡献与支持！

## 模块引导

### API模块

基于 `Ktor` 针对 [QQ频道API](https://bot.q.qq.com/wiki/develop/api/) 
的基本完整的[KMP](https://kotlinlang.org/docs/multiplatform.html)多平台封装实现，
是一个简单高效轻量级的API实现模块。

此模块基本不会提供什么多余的实现，其目标为在提供封装的情况下尽可能地保留原始API的使用手感，不做过多的封装。

👉 [前往模块](simbot-component-qq-guild-api) 了解更多。

### 标准库模块

基于 [API模块](simbot-component-qq-guild-api) 针对bot的"登录"鉴权实现简单高效轻量级的事件订阅功能。

此模块在API模块的基础上提供了针对事件相关的功能实现，包括事件订阅的能力。
同样的，其目标为在提供封装的情况下尽可能地保留原始API的使用手感，不做过多的封装。

👉 [前往模块](simbot-component-qq-guild-stdlib) 了解更多。

### 核心组件模块

基于 
[标准库模块](simbot-component-qq-guild-stdlib) 
对 [simbot4核心库](https://github.com/simple-robot/simpler-robot) 
的组件实现，
是一个相对高度封装的模块，并提供simbot4大部分能力，包括事件监听、多组件协同、Spring Boot Starter 等。

👉 [前往模块](simbot-component-qq-guild-core-1) 了解更多。

## 法欧莉

如果你想看一看使用QQ频道组件实现的具体作品，
可以前往QQ频道添加亲爱的 [法欧莉斯卡雷特](https://qun.qq.com/qunpro/robot/share?robot_appid=101986850) 来体验喔~


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

