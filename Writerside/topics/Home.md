# 欢迎！

<include from="snippets.md" element-id="to-main-doc" />

这里是
[**Simple Robot v4**](https://github.com/simple-robot/simpler-robot/tree/v4-dev)
的 
[QQ频道组件](https://github.com/simple-robot/simbot-component-qq-guild/) 
应用手册！

## 概述

**QQ频道组件** 
是一个基于 [Kotlin coroutines](https://github.com/Kotlin/kotlinx.coroutines)
对 [**QQ频道机器人**][qg bot doc] 进行实现的 API/SDK
[Kotlin 多平台](https://kotlinlang.org/docs/multiplatform.html)库，异步高效、Java友好。

它同样是一个 [Simple Robot v4][simbot4 gh] (下文简称 simbot)
的组件库，是 simbot 的子项目之一。
借助 simbot 核心库提供的能力，它可以支持更多高级功能和封装，比如组件协同、Spring支持等。

它可以作为一个低级别的 API/SDK 辅助依赖库，
也可在 simbot 核心库的支持下用作为一个轻量级的快速开发框架！

序列化和网络请求相关分别基于 [Kotlin serialization](https://github.com/Kotlin/kotlinx.serialization)
和 [Ktor](https://ktor.io/).

### 了解 **Simple Robot**

- [Simple Robot 应用手册](https://simbot.forte.love)
- [Simple Robot 组织库](https://github.com/simple-robot)

### API文档

- [API文档引导站](https://docs.simbot.forte.love)

## 反馈与协助！

我们欢迎并期望着您的 
[反馈](https://github.com/simple-robot/simbot-component-qq-guild/issues)
或
[协助](https://github.com/simple-robot/simbot-component-qq-guild/pulls)，
感谢您的贡献与支持！

## 法欧莉

如果你想看一看通过 **QQ频道组件** 实现的应用案例，
可以前往QQ频道添加亲爱的 [法欧莉斯卡雷特](https://qun.qq.com/qunpro/robot/share?robot_appid=101986850) 来体验~


## License

`simbot-component-qq-guild` 使用 `LGPLv3` 许可证开源。

```
This program is free software: you can redistribute it and/or 
modify it under the terms of the GNU Lesser General 
Public License as published by the Free Software Foundation, 
either version 3 of the License, or (at your option) 
any later version.

This program is distributed in the hope that it will be useful, 
but WITHOUT ANY WARRANTY; without even the implied 
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public 
License along with this program. 
If not, see <https://www.gnu.org/licenses/>.
```

[simbot4 gh]: https://github.com/simple-robot/simpler-robot/tree/v4-dev
[simbot doc]: https://simbot.forte.love
[qg bot doc]: https://bot.q.qq.com/wiki/develop/api/
[KMP]: https://kotlinlang.org/docs/multiplatform.html
