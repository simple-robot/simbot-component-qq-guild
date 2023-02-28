/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

@file:Suppress("MemberVisibilityCanBePrivate")

package love.forte.simbot.tencentguild

import love.forte.simbot.*
import love.forte.simbot.tencentguild.model.Message
import java.time.*


/**
 *
 * DSL 构建 [Message.Ark].
 *
 *
 * ```
 * val ark = buildArk(0.ID) {
 *     kvs { // 增加kvs
 *         kv("Key1", "value") // kv
 *         kv("Key2", "value2") {
 *             obj() // empty obj -> { "objKv": [] }
 *             obj {
 *                 kv("objKey1", "valueA")
 *                 kv("objKey2", "valueB")
 *             }
 *         }
 *     }
 *  }
 *
 *
 * ```
 *
 */
public inline fun buildArk(templateId: ID, builder: ArkBuilder.() -> Unit): Message.Ark {
    return ArkBuilder(templateId).also(builder).build()
}

public fun buildArk(templateId: ID): Message.Ark {
    return Message.Ark(templateId)
}


@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class ArkBuilderDSL


@ArkBuilderDSL
public class ArkBuilder(public var templateId: ID) {

    public fun from(ark: Message.Ark): ArkBuilder = also {
        templateId = ark.templateId
        kvs = ark.kv.toMutableList()
    }

    @ArkBuilderDSL
    public var kvs: MutableList<Message.Ark.Kv> = mutableListOf()

    public fun kv(key: String, value: String): ArkBuilder = also {
        kvs.add(Message.Ark.Kv(key, value))
    }

    @ArkBuilderDSL
    public fun kvs(block: ArkKvListBuilder.() -> Unit): ArkBuilder = also {
        kvs.addAll(ArkKvListBuilder().also(block).build())
    }


    public fun build(): Message.Ark {
        return Message.Ark(templateId, kvs.toList())
    }
}


@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class ArkKvBuilderDSL


@ArkKvBuilderDSL
public class ArkKvListBuilder {
    @ArkKvBuilderDSL
    public var arkKvs: MutableList<Message.Ark.Kv> = mutableListOf()

    @ArkKvBuilderDSL
    public fun kv(key: String, value: String): ArkKvListBuilder = also {
        arkKvs.add(Message.Ark.Kv(key, value))
    }

    @ArkKvBuilderDSL
    @JvmOverloads
    public fun kv(key: String, value: String? = null, build: ArkObjListBuilder.() -> Unit): ArkKvListBuilder = also {
        arkKvs.add(Message.Ark.Kv(key, value, ArkObjListBuilder().also(build).build()))
    }

    public fun build(): List<Message.Ark.Kv> = arkKvs.toList()
}


@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class ArkObjBuilderDSL


@ArkObjBuilderDSL
public class ArkObjListBuilder {
    @ArkObjBuilderDSL
    public var objList: MutableList<Message.Ark.Obj> = mutableListOf()

    @ArkObjBuilderDSL
    public fun obj(): ArkObjListBuilder = also {
        objList.add(Message.Ark.Obj())
    }

    @ArkObjBuilderDSL
    public fun obj(block: ArkObjKvListBuilder.() -> Unit): ArkObjListBuilder = also {
        objList.add(Message.Ark.Obj(ArkObjKvListBuilder().also(block).build()))
    }

    public fun build(): List<Message.Ark.Obj> = objList.toList()
}


@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class ArkObjKvBuilderDSL


@ArkObjKvBuilderDSL
public class ArkObjKvListBuilder {

    @ArkObjKvBuilderDSL
    public var objKvs: MutableList<Message.Ark.Obj.Kv> = mutableListOf()

    @ArkObjKvBuilderDSL
    public fun kv(key: String, value: String): ArkObjKvListBuilder = also {
        objKvs.add(Message.Ark.Obj.Kv(key, value))
    }

    public fun build(): List<Message.Ark.Obj.Kv> = objKvs.toList()
}


@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class EmbedBuilderDsl

@EmbedBuilderDsl
public class EmbedBuilder {
    /**
     * 标题
     */
    public lateinit var title: String

    /**
     * 描述
     */
    public lateinit var description: String

    /**
     * 消息弹窗内容
     */
    public lateinit var prompt: String


    /**
     * MessageEmbedField 对象数组	字段信息
     */
    public var fields: MutableList<Message.Embed.Field> = mutableListOf()


    public fun addField(name: String, value: String) {
        fields.add(Message.Embed.Field(name, value))
    }


    public fun build(): Message.Embed {
        return Message.Embed(title, description, prompt, Instant.now().toTimestamp(), fields.toList())
    }
}


