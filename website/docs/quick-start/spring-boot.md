---
title: 使用SpringBoot
position: 1 
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

## 前言

本编文档介绍在JVM环境下使用:

- `simboot-core-spring-boot-starter` _(`simbot3` 的 Spring Boot Starter)_
- `simbot-component-qq-guild-core` _(`simbot3` QQ频道组件)_

来编写一个QQ频道机器人。

## 前提准备

首先你应当有一个可用的 [QQ频道机器人](https://q.qq.com/bot) 。

## 项目构建

首先准备一个SpringBoot项目。可以考虑前往 [start.spring.io](https://start.spring.io) 或借助IDE等工具。

然后添加两个我们需要的依赖：
- `love.forte.simbot.boot:simboot-core-spring-boot-starter` ([**版本参考**](https://github.com/simple-robot/simpler-robot/releases))
- `love.forte.simbot.component:simbot-component-qq-guild-core` ([**版本参考**](https://github.com/simple-robot/simbot-component-qq-guild/releases))

:::info 保持住

注意，在使用 Spring Boot 的时候你需要一些能够使程序保持运行的组件，例如通过 `spring-web` 启用一个服务器，否则程序可能会自动终止，
因为simbot的 starter 并不提供维持程序运行的能力。

下述示例我们选择使用 `spring-boot-starter-webflux`，具体选择请根据你的自身实际需求选择。

:::

:::tip 我也懒

下述配置示例基于 [start.spring.io](https://start.spring.io) 生成，版本号等信息请根据实际情况做修改。

:::

<Tabs groupId="use-dependency">

<TabItem value="Gradle Kotlin DSL">

```kotlin
plugins {
  java
  kotlin("jvm") version "1.8.10" // 在Gradle中你需要使用Kotlin插件，但是必须要使用Kotlin语言开发。它的作用是运行Gradle自动根据环境选择多平台依赖的具体依赖。  
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
    id 'org.jetbrains.kotlin.jvm' version '1.8.10' // 在Gradle中你需要使用Kotlin插件，但是必须要使用Kotlin语言开发。它的作用是运行Gradle自动根据环境选择多平台依赖的具体依赖。  
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


