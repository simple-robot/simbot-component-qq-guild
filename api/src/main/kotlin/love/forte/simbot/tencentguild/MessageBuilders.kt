/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

@file:Suppress("MemberVisibilityCanBePrivate")

package love.forte.simbot.tencentguild

import love.forte.simbot.ID
import love.forte.simbot.toTimestamp
import java.time.Instant


/**
 *
 * DSL 构建 [TencentMessage.Ark].
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
public inline fun buildArk(templateId: ID, builder: ArkBuilder.() -> Unit): TencentMessage.Ark {
    return ArkBuilder(templateId).also(builder).build()
}

public fun buildArk(templateId: ID): TencentMessage.Ark {
    return TencentMessage.Ark(templateId)
}


@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class ArkBuilderDSL


@ArkBuilderDSL
public class ArkBuilder(public var templateId: ID) {

    public fun from(ark: TencentMessage.Ark): ArkBuilder = also {
        templateId = ark.templateId
        kvs = ark.kv.toMutableList()
    }

    @ArkBuilderDSL
    public var kvs: MutableList<TencentMessage.Ark.Kv> = mutableListOf()

    public fun kv(key: String, value: String): ArkBuilder = also {
        kvs.add(TencentMessage.Ark.Kv(key, value))
    }

    @ArkBuilderDSL
    public fun kvs(block: ArkKvListBuilder.() -> Unit): ArkBuilder = also {
        kvs.addAll(ArkKvListBuilder().also(block).build())
    }


    public fun build(): TencentMessage.Ark {
        return TencentMessage.Ark(templateId, kvs.toList())
    }
}


@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class ArkKvBuilderDSL


@ArkKvBuilderDSL
public class ArkKvListBuilder {
    @ArkKvBuilderDSL
    public var arkKvs: MutableList<TencentMessage.Ark.Kv> = mutableListOf()

    @ArkKvBuilderDSL
    public fun kv(key: String, value: String): ArkKvListBuilder = also {
        arkKvs.add(TencentMessage.Ark.Kv(key, value))
    }

    @ArkKvBuilderDSL
    @JvmOverloads
    public fun kv(key: String, value: String? = null, build: ArkObjListBuilder.() -> Unit): ArkKvListBuilder = also {
        arkKvs.add(TencentMessage.Ark.Kv(key, value, ArkObjListBuilder().also(build).build()))
    }

    public fun build(): List<TencentMessage.Ark.Kv> = arkKvs.toList()
}


@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class ArkObjBuilderDSL


@ArkObjBuilderDSL
public class ArkObjListBuilder {
    @ArkObjBuilderDSL
    public var objList: MutableList<TencentMessage.Ark.Obj> = mutableListOf()

    @ArkObjBuilderDSL
    public fun obj(): ArkObjListBuilder = also {
        objList.add(TencentMessage.Ark.Obj())
    }

    @ArkObjBuilderDSL
    public fun obj(block: ArkObjKvListBuilder.() -> Unit): ArkObjListBuilder = also {
        objList.add(TencentMessage.Ark.Obj(ArkObjKvListBuilder().also(block).build()))
    }

    public fun build(): List<TencentMessage.Ark.Obj> = objList.toList()
}


@Retention(AnnotationRetention.BINARY)
@DslMarker
internal annotation class ArkObjKvBuilderDSL


@ArkObjKvBuilderDSL
public class ArkObjKvListBuilder {

    @ArkObjKvBuilderDSL
    public var objKvs: MutableList<TencentMessage.Ark.Obj.Kv> = mutableListOf()

    @ArkObjKvBuilderDSL
    public fun kv(key: String, value: String): ArkObjKvListBuilder = also {
        objKvs.add(TencentMessage.Ark.Obj.Kv(key, value))
    }

    public fun build(): List<TencentMessage.Ark.Obj.Kv> = objKvs.toList()
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
    public var fields: MutableList<TencentMessage.Embed.Field> = mutableListOf()


    public fun addField(name: String, value: String) {
        fields.add(TencentMessage.Embed.Field(name, value))
    }


    public fun build(): TencentMessage.Embed {
        return TencentMessage.Embed(title, description, prompt, Instant.now().toTimestamp(), fields.toList())
    }
}


