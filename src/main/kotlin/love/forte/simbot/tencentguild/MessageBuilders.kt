@file:Suppress("MemberVisibilityCanBePrivate")

package love.forte.simbot.tencentguild

import love.forte.simbot.ID


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

    @ArkBuilderDSL
    public var kvs: MutableList<TencentMessage.Ark.Kv> = mutableListOf()


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