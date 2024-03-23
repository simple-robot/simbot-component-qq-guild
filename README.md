#  

<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
<picture>
  <source media="(prefers-color-scheme: dark)" srcset=".simbot/logo-dark.svg">
  <source media="(prefers-color-scheme: light)" srcset=".simbot/logo.svg">
  <img alt="simbot logo" src=".simbot/logo.svg" width="260" />
</picture>
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

这是一个基于 [Kotlin coroutines](https://github.com/Kotlin/kotlinx.coroutines)
对 [**QQ频道机器人**][qg bot doc] 进行实现的 API/SDK
[Kotlin 多平台][KMP]库，异步高效、Java友好。

它同样是一个 [Simple Robot v4][simbot4 gh] (下文简称 simbot)
的组件库，是 simbot 的子项目之一。
借助 simbot 核心库提供的能力，它可以支持更多高级功能和封装，比如组件协同、Spring支持等。

它可以作为一个低级别的 API/SDK 辅助依赖库，
也可在 simbot 核心库的支持下用作为一个轻量级的快速开发框架！

序列化和网络请求相关分别基于 [Kotlin serialization](https://github.com/Kotlin/kotlinx.serialization)
和 [Ktor](https://ktor.io/).

## 文档与引导

- QQ频道组件手册: [应用手册](https://component-qqguild.simbot.forte.love/) (即当前仓库的 GitHub Pages)
- 了解simbot: [Simple Robot 应用手册](https://simbot.forte.love)
- [文档引导站&API文档](https://docs.simbot.forte.love)
- [**社群**](https://simbot.forte.love/communities.html) 文档中也有提供社群信息喔
- 前往 [组织首页](https://github.com/simple-robot/) 了解更多有关组件、文档、以及社群等相关信息！

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

👉 [前往模块](simbot-component-qq-guild-core) 了解更多。

## 法欧莉!

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


[simbot4 gh]: https://github.com/simple-robot/simpler-robot/tree/v4-dev
[simbot doc]: https://simbot.forte.love
[qg bot doc]: https://bot.q.qq.com/wiki/develop/api/
[KMP]: https://kotlinlang.org/docs/multiplatform.html
