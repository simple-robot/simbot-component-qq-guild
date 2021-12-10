# Simply-Robot for tencent-guild

> 假装这里有个logo


这是 [simply-robot](https://github.com/ForteScarlet/simpler-robot/tree/dev-v3.0.0-preview) (**注意，是dev-v3.0.0-preview分支**) 下的子项目，
本库提供对 [腾讯频道机器人](https://bot.q.qq.com/wiki/develop/api/) 中`API`内容的完整实现以及事件相关的信令实现， 以及BOT对于事件的监听与交互。
本库中的api响应数据会一定程度实现 `simple-robot` 的`v3.x`api模块接口。

<br>

> 了解simple-robot 3.x: [simple-robot文档](https://www.yuque.com/simpler-robot/simpler-robot-doc/mudleb)

<br>


*本库通过 `kotlin` 协程达成全异步的高效实现，基于 `ktor` 进行网络交互，基于 `kotlinx-serialization` 进行数据序列化/反序列化。*


<br>

*以及，这个README看上去挺乱的，等一切安好之后会再考虑优化一下。*

## 前言

目前无论是当前仓库还是 `simple-robot v3.x` 都处于紧张的早中期建设阶段，在正式版发布之前必然存在很多不可预知的问题，并且开发进度无法清晰预估，毕竟一天一共就24个小时，而我们团队的精力并非无限。

如果你想要协助我们对当前仓库或者 [simply-robot](https://github.com/ForteScarlet/simpler-robot/tree/dev-v3.0.0-preview) 的建设，欢迎通过issue提出宝贵意见或者通过pr参与到建设当中，十分感谢。


## 模块简介

## Api
腾讯频道官方提供了很多api定义，它们可能是http接口，或是一个数据类型格式，又或者是对于事件信令（opcode）的对应描述，诸如此类的API和定义相关的内容，它们会被囊括在 [api](api) 模块中。

api模块的实现是一种**底层**实现，它不会提供过多的便利方法，旨在更加贴近原生的调用这些api，甚至于调用他们的 `HttpClient` 实例和token也需要你主动传递。

如果你只是希望不再去定义这些api的封装，但是不需要其他过多的花里胡哨的东西，那么你可以考虑单独使用此模块。

<hr>

**详情与示例前往 >>>** [**api模块**](api)

<br>

## Core模块

在上述的api模块中，其只提供了针对于BOT和部分事件的**定义**，但是并没有给出BOT连接、事件监听等内容的**具体实现**。那么这些实现由谁来做呢？
这些偏向于功能性的内容将会交由 [core](core) 模块来进行实现。

core模块提供了对于bot连接以及事件监听、处理的简易实现，如果你只需要一个简单的轻量级频道bot，只要能有最基础的事件监听就好，并且希望能够原生的使用api（上述api模块），
那么你便可以选择使用core模块。

core模块还针对bot调用api的情况提供了较为友善的扩展函数，可以省去中间提供client、token等原生参数的步骤。

<hr>

**详情与示例前往 >>>** [**core模块**](core)

<br>

## Component模块

一开始就提到，当前仓库是 `simple-robot 3.x` 的子项目，那么此仓库也必然会存在一个针对 `simple-robot` 的 **组件**。了解 `simple-robot` 的朋友一定对simple-robot的组件概念不陌生，
组件便是一个至少基于 `simple-robot-core` 模块并实现 `simple-robot-api` 中提供的各类标准接口而产生的内容。

简单来说，`simple-robot-api` 是一套通用标准，`simple-robot-core` 为这套通用标准提供通用实现（例如统一事件调度、BOT管理），并将所有的**组件**进行统一管理，尽可能减少不同平台间的差异，
以达成可以以一套标准代码为多个不同组件实现功能的目的。

而 [component模块](component) 便是这样的标准实现下的组件之一。`component` 模块实现simple-robot的标准事件并可以使用`simple-robot`提供的诸多特性（例如统一事件调度）


<hr>

**详情与示例前往 >>>** [**component模块**](component)
