---
title: 使用SpringBoot
sidebar_position: 4 
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

## 前言

本编文档介绍在JVM环境下使用:

- `simboot-core-spring-boot-starter` _(`simbot3` 的 Spring Boot Starter)_
- `simbot-component-qq-guild-core` _(`simbot3` QQ频道组件)_

来编写一个QQ频道机器人。

## 前提准备

首先你应当准备至少一个可用的 [QQ频道机器人](https://q.qq.com/bot) 。

## 项目构建

首先准备一个SpringBoot项目。可以考虑前往 [start.spring.io](https://start.spring.io) 或借助IDE等工具。

然后添加两个我们需要的依赖：
- `love.forte.simbot.boot:simboot-core-spring-boot-starter` ([**版本参考**](https://github.com/simple-robot/simpler-robot/releases))
- `love.forte.simbot.component:simbot-component-qq-guild-core` ([**版本参考**](https://github.com/simple-robot/simbot-component-qq-guild/releases))

:::info 保持住

注意，在使用 Spring Boot 的时候你需要一些能够使程序保持运行的组件，例如通过 `spring-web` 启用一个服务器，否则程序可能会自动终止。
因为simbot的 starter 并不提供维持程序运行的能力。

下述示例我们选择使用 `spring-boot-starter-webflux`，具体选择请根据你的实际需求决定。

:::

:::tip 我也懒

下述配置示例基于 [start.spring.io](https://start.spring.io) 生成，版本号等信息请根据实际情况做修改。

:::

<Tabs groupId="use-dependency">

<TabItem value="Gradle Kotlin DSL">

```kotlin
plugins {
  java
  kotlin("jvm") version "1.8.10" // 在Gradle中你需要使用Kotlin插件，但是不代表一定要使用Kotlin语言开发。它的作用是运行Gradle自动根据环境选择多平台依赖的具体依赖。  
  id("org.springframework.boot") version "3.0.5"
  id("io.spring.dependency-management") version "1.1.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.projectreactor:reactor-test")
  
  // simbot相关依赖
  // simbot core starter  
  implementation("love.forte.simbot.boot:simboot-core-spring-boot-starter:$SIMBOT_VERSION") // 版本请参考前文的参考链接
  // QQ频道组件  
  implementation("love.forte.simbot.component:simbot-component-qq-guild-core:$COMPONENT_VERSION") // 版本请参考前文的参考链接
}

tasks.withType<Test> {
  useJUnitPlatform()
}
```

</TabItem>

<TabItem value="Gradle Groovy">

```groovy
plugins {
    java
    id 'org.springframework.boot' version '3.0.5'
    id 'io.spring.dependency-management' version '1.1.0'
    id 'org.jetbrains.kotlin.jvm' version '1.8.10' // 在Gradle中你需要使用Kotlin插件，但是不代表一定要使用Kotlin语言开发。它的作用是运行Gradle自动根据环境选择多平台依赖的具体依赖。  
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'io.projectreactor:reactor-test'

    // simbot相关依赖
    // simbot core starter  
    implementation 'love.forte.simbot.boot:simboot-core-spring-boot-starter:$SIMBOT_VERSION' // 版本请参考前文的参考链接
    // QQ频道组件  
    implementation 'love.forte.simbot.component:simbot-component-qq-guild-core:$COMPONENT_VERSION' // 版本请参考前文的参考链接
}

tasks.named('test') {
    useJUnitPlatform()
}
```

</TabItem>

<TabItem value="Maven">

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.0.5</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.example</groupId>
	<artifactId>demo</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>demo</name>
	<description>Demo project for Spring Boot</description>
	<properties>
		<java.version>17</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-webflux</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>io.projectreactor</groupId>
			<artifactId>reactor-test</artifactId>
			<scope>test</scope>
		</dependency>
        
        <!-- simbot相关依赖 -->
        <!-- simbot core starter -->
        <dependency>
            <groupId>love.forte.simbot.boot</groupId>
            <artifactId>simboot-core-spring-boot-starter</artifactId>
            <!-- 版本请参考前文的参考链接 -->
            <version>${SIMBOT_VERSION}</version>
        </dependency>
        
        <!-- QQ频道组件 -->
        <dependency>
            <groupId>love.forte.simbot.component</groupId>
            <artifactId>simbot-component-qq-guild-core</artifactId>
            <!-- 版本请参考前文的参考链接 -->
            <version>${COMPONENT_VERSION}</version>
        </dependency>
        
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```

</TabItem>

</Tabs>

## BOT配置

接下来，在项目**资源文件**目录下的 `simbot-bots` 文件夹中创建一个用于配置bot的配置文件 `xxx.bot.json` ( 文件名随意，扩展名应为 `.bot` 或 `.bot.json` ) ，
而配置文件的内容则参考章节 [**BOT配置文件**](../bot-config) 。

> 此路径以 IDEA 的项目结构风格为准，如果是其他IDE，使用对应的资源文件目录。

```
${PROJECT_SRC}/main/resources/simbot-bots/xxx.bot.json
```

:::tip 可配置

如果想要修改此路径，可在 Spring Boot 的配置文件中进行配置：

<Tabs groupId="spring-boot-config">

<TabItem value="properties">

```properties
# 自定义配置bot资源文件的扫描路径。
# 默认为 classpath:simbot-bots/*.bot*
# 如果要使用本地文件可以使用 `file:` 开头
simbot.bot-configuration-resources[0]=classpath:simbot-bots/*.bot*
```

</TabItem>


<TabItem value="yaml">

```yaml
simbot:
  
  # 自定义配置bot资源文件的扫描路径。
  # 默认为 classpath:simbot-bots/*.bot*
  # 如果要使用本地文件可以使用 `file:` 开头
  bot-configuration-resources:
    - 'classpath:simbot-bots/*.bot*'
```

</TabItem>

</Tabs>

:::


## 启动类

像每一个 Spring Boot 应用一样，你需要一个启动类，并通过标注 `@EnableSimbot` 来启用 `simbot` ：

<Tabs groupId="code">

<TabItem value="Kotlin">

```kotlin title='com.example.App.kt'
@EnableSimbot
@SpringBootApplication
class App

fun main(vararg args: String) {
    runApplication<App>(args = args)
}
```

</TabItem>

<TabItem value="Java">

:::tip 早有预防

如果你在Java中遇到了无法引用 `@EnableSimbot` 等情况，或许可以参考 [**这篇FAQ**](https://simbot.forte.love/faq/%E5%8C%85%E5%BC%95%E7%94%A8%E5%BC%82%E5%B8%B8/)。

:::

```java title='com.example.App.java'
@EnableSimbot
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

</TabItem>

</Tabs>


## 监听事件

接下来就是逻辑代码所在的地方了，编写一个监听函数并监听一个事件。

此处我们监听 `ChannelMessageEvent`，也就是 **_子频道的消息事件_**。

假设：要求bot必须**被AT**，并且说一句 `你好`，此时bot会**引用**用户发送的消息并回复 `你也好!` ，类似于：

```
用户: 
@BOT 你好

BOT:
> 用户: @BOT 你好
你也好! 
```

<Tabs groupId="code">

<TabItem value="Kotlin">

```kotlin title='com.example.listener.ExampleListener.kt'
import love.forte.simboot.annotation.ContentTrim
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.Listener
import love.forte.simbot.event.ChannelMessageEvent

@Component
class ExampleListener {
    
    @Listener
    @Filter(value = "你好", targets = Filter.Targets(atBot = true))
    @ContentTrim // 当匹配被at时，将'at'这个特殊消息移除后，剩余的文本消息大概率存在前后空格，通过此注解在匹配的时候忽略前后空格
    suspend fun onChannelMessage(event: ChannelMessageEvent) { // 将要监听的事件类型放在参数里，即代表监听此类型的消息
        event.reply("你也好!")
    }
}


```

</TabItem>

<TabItem value="Java" label="Java Blocking">


```java title='com.example.listener.ExampleListener.java'
import love.forte.simboot.annotation.ContentTrim
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.Listener
import love.forte.simbot.event.ChannelMessageEvent
        
@Component
public class ExampleListener {
    
    @Listener
    @Filter(value = "你好", targets = @Filter.Targets(atBot = true))
    @ContentTrim // 当匹配被at时，将'at'这个特殊消息移除后，剩余的文本消息大概率存在前后空格，通过此注解在匹配的时候忽略前后空格
    public void onChannelMessage(ChannelMessageEvent event) { // 将要监听的事件类型放在参数里，即代表监听此类型的消息
        
        // Java中的阻塞式API
        event.replyBlocking("你也好!");
    }
    
}
```

</TabItem>

<TabItem value="Java Async">


```java title='com.example.listener.ExampleListener.java'
import love.forte.simboot.annotation.ContentTrim
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.Listener
import love.forte.simbot.event.ChannelMessageEvent
        
@Component
public class ExampleListener {
    
    @Listener
    @Filter(value = "你好", targets = @Filter.Targets(atBot = true))
    @ContentTrim // 当匹配被at时，将'at'这个特殊消息移除后，剩余的文本消息大概率存在前后空格，通过此注解在匹配的时候忽略前后空格
    public CompletableFuture<?> onChannelMessage(ChannelMessageEvent event) { // 将要监听的事件类型放在参数里，即代表监听此类型的消息
        
        // 将 CompletableFuture 作为返回值，simbot会以非阻塞的形式处理它
        return event.replyAsync("你也好!");
    }
    
}
```

</TabItem>

<TabItem value="Java Reactive">

:::tip 有要求

如果返回值是需要第三方库的响应式类型，那么你的项目环境依赖中必须存在 `Kotlin courotines` 对其的支持库才可使用。
你可以参考文档中  [_响应式的处理结果_](https://simbot.forte.love/docs/basic/event-listener#%E5%8F%AF%E5%93%8D%E5%BA%94%E5%BC%8F%E7%9A%84%E5%A4%84%E7%90%86%E7%BB%93%E6%9E%9C) 的内容。

:::

```java title='com.example.listener.ExampleListener.java'
import love.forte.simboot.annotation.ContentTrim
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.Listener
import love.forte.simbot.event.ChannelMessageEvent
        
@Component
public class ExampleListener {
    
    @Listener
    @Filter(value = "你好", targets = @Filter.Targets(atBot = true))
    @ContentTrim // 当匹配被at时，将'at'这个特殊消息移除后，剩余的文本消息大概率存在前后空格，通过此注解在匹配的时候忽略前后空格
    public Mono<?> onChannelMessage(ChannelMessageEvent event) { // 将要监听的事件类型放在参数里，即代表监听此类型的消息
        
        // 将 Mono 等响应式类型作为返回值，simbot会以非阻塞的形式处理它
        return Mono.fromCompletionStage(event.replyAsync("你也好!"));
    }
    
}
```

</TabItem>

</Tabs>

## 启动

接下来，启动程序并在你的沙箱频道中@它试试看吧。

当然，如果遇到了预期外的问题也不要慌，积极反馈问题才能使我们变得更好，可以前往 [Issues](https://github.com/simple-robot/simpler-robot/issues) 反馈问题、[社区](https://github.com/orgs/simple-robot/discussions) 提出疑问。
