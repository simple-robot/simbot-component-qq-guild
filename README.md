# Simply-Robot for tencent-guild


这是 [simple-robot](https://github.com/ForteScarlet/simpler-robot) 下的子项目，本库提供对 [腾讯频道机器人](https://bot.q.qq.com/wiki/develop/api/) 中`API`内容的完整实现以及事件相关的信令实现，但 **不提供** 直接的BOT事件监听与交互。


本库中的api响应数据会一定程度实现 `simple-robot` 的`v3.x`api模块接口。


对于针对BOT、事件等交互相关内容的实现，将会由 `simple-robot` 的组件模块进行对接。


本库通过 `kotlin` 协程达成全异步的高效实现，基于 `ktor` 进行网络交互，基于 `kotlinx-serialization` 进行数据序列化/反序列化。


~~虽然之前是打算通过kotlin做多平台实现的但是最后还是放弃了~~


当然, 如果你只需要使用API，不考虑bot、事件相关内容，你可以通过此库来直接使用相关API。

<br>

以及，这个README看上去挺乱的，等一切安好之后会再考虑优化一下。

对于各个API的底层封装，参考模块 [api](api)

对于bot事件监听的简易实现，参考模块 [core](core)

对于整个频道bot的应用级的友好框架实现，请关注 [simple-robot](https://github.com/ForteScarlet/simpler-robot) 的3.x版本更新情况。
