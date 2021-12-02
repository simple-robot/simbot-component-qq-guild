# Simply-Robot for tencent-guild


这是 [simple-robot](https://github.com/ForteScarlet/simpler-robot) 下的子项目，本库提供对 [腾讯频道机器人](https://bot.q.qq.com/wiki/develop/api/) 中`API`内容的完整实现以及事件相关的信令实现，但 **不提供** 直接的BOT事件监听与交互。

了解simple-robot 3.x: [simple-robot文档](https://www.yuque.com/simpler-robot/simpler-robot-doc/mudleb)

本库中的api响应数据会一定程度实现 `simple-robot` 的`v3.x`api模块接口。


本库通过 `kotlin` 协程达成全异步的高效实现，基于 `ktor` 进行网络交互，基于 `kotlinx-serialization` 进行数据序列化/反序列化。


<br>

以及，这个README看上去挺乱的，等一切安好之后会再考虑优化一下。

## Api模块
对于各个API的**底层**封装，参考模块 [api](api)

## Core模块
对于bot连接以及事件监听的简易实现，参考模块 [core](core)

## Component模块
对于整个频道bot的应用级的友好框架实现，请期待基于 [simple-robot](https://github.com/ForteScarlet/simpler-robot) 3.x版本的 [component](component) 模块更新情况。
