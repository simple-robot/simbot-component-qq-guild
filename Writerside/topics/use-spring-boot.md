# 使用 Spring Boot


<tldr>
<p>使用 <control>核心库(core 模块)</control> 配合 simbot4 Spring Boot starter 来将QQ频道组件作为 simbot4 的组件之一应用在 <code>Spring Boot 3</code> 中。</p>
</tldr>


## 准备

### Java 17

simbot4 的 Spring Boot starter 基于 Spring Boot 3，因此 Java 的版本至少为 **Java17** 。

### 创建 Spring Boot 3 项目

首先你得有个 Spring Boot 3 项目, 你可以前往 [Spring Initializr][spring.start]
或者借助 IDE (比如 IDEA) 的相关功能创建一个 Spring Boot 3 的项目。
你可以自由选择需要添加的任何其他 Spring Boot 组件，比如 `spring-boot-starter-web` 之类的。

<note title="相关参考">

- [Spring Initializr][spring.start]
- [Spring Quickstart Guide](https://spring.io/quickstart/)

</note>

## 安装

<tip>

simbot 核心库的版本尽量不要低于 `v%minimum-core-version%`，可前往
[GitHub Releases](https://github.com/simple-robot/simpler-robot/releases)
查看各版本及其说明。

此处以 `v%minimum-core-version%` 作为**示例**。

</tip>

<tabs group="build">
<tab title="Gradle(Kotlin DSL)" group-key="kts">

```Kotlin
// simbot4核心库
implementation("love.forte.simbot:simbot-core-spring-boot-starter:%minimum-core-version%")
// QQ频道组件库
implementation("love.forte.simbot.component:simbot-component-qq-guild-core:%version%")
```

<tip>

如果你使用 Java 而不配合使用 Gradle 的 `kotlin` 插件,
那么你需要为组件库指定依赖的后缀为 `-jvm`。

```Kotlin
// simbot4核心库
implementation("love.forte.simbot:simbot-core-spring-boot-starter:%minimum-core-version%")
// QQ频道组件库
implementation("love.forte.simbot.component:simbot-component-qq-guild-core-jvm:%version%")
```

</tip>

</tab>
<tab title="Gradle(Groovy)" group-key="groovy">

```Groovy
// simbot4核心库
implementation 'love.forte.simbot:simbot-core-spring-boot-starter:%minimum-core-version%'
// QQ频道组件库
implementation 'love.forte.simbot.component:simbot-component-qq-guild-core:%version%'
```

<tip>

如果你使用 Java 而不配合使用 Gradle 的 `kotlin` 插件,
那么你需要为组件库指定依赖的后缀为 `-jvm`。

```Kotlin
// simbot4核心库
implementation 'love.forte.simbot:simbot-core-spring-boot-starter:%minimum-core-version%'
// QQ频道组件库
implementation 'love.forte.simbot.component:simbot-component-qq-guild-core-jvm:%version%'
```

</tip>

</tab>
<tab title="Maven" group-key="maven">

```xml
<!-- simbot4核心库 -->
<dependency>
    <group>love.forte.simbot</group>
    <artifactId>simbot-core-spring-boot-starter</artifactId>
    <version>%minimum-core-version%</version>
</dependency>
<!-- QQ频道组件库 -->
<dependency>
    <group>love.forte.simbot.component</group>
    <artifactId>simbot-component-qq-guild-core-jvm</artifactId>
    <version>%version%</version>
</dependency>
```

</tab>
</tabs>

## Bot 配置

在你的资源目录中: 
<path>resources/simbot-bots/</path>
中创建任意的一个或多个bot配置文件，并以 `.bot.json` 作为扩展名。

配置文件的内容可前往参考 [Bot配置文件](bot-config.md) 章节。

> 这个扫描目录是可配置的。
> 这是属于 simbot4 Spring Boot starter 的配置，下文会有相关的引导链接到 simbot4 应用手册的相关内容处。

## 使用
### 添加启动注解

在你的启动类上添加 `@EnableSimbot` 注解来启用 simbot。

<tabs group="Code">
<tab title="Kotlin" group-key="Kotlin">

```Kotlin
@EnableSimbot // 启用 simbot
@SpringBootApplication
class MyApplication

fun main(args: Array<String>) {
    runApplicarion<MyApplication>(*args)
}
```

</tab>
<tab title="Java" group-key="Java">

```Java
@EnableSimbot // 启用 simbot
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

</tab>
</tabs>


QQ频道组件支持使用 SPI **自动加载** 组件和插件。
添加依赖到项目环境中、编写完 Bot 配置文件后，可前往
[simbot手册: 使用 Spring Boot 3](https://simbot.forte.love/start-use-spring-boot-3.html) 
了解更多 simbot4 Spring Boot starter 的可配置内容以及启动注解等信息。

## 更多相关参考

- 前往 [simbot官方手册](https://simbot.forte.love/) 阅读有关 simbot API 的相关介绍与示例。
- 前往 [simbot手册: 使用 Spring Boot 3](https://simbot.forte.love/start-use-spring-boot-3.html)
  了解更多 simbot4 Spring Boot starter 的可配置内容以及启动注解等信息。


[spring.start]: https://start.spring.io
