/*
 * Copyright (c) 2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package qg.internal.processors.intents

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.jvm.jvmName
import com.squareup.kotlinpoet.jvm.jvmStatic
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.concurrent.atomic.AtomicBoolean

private const val OUTPUT_PACKAGE = "love.forte.simbot.qguild.event"
private const val AGGREGATION_OBJ_NAME = "EventIntentsAggregation"
private const val AGGREGATION_FILE_NAME = "$AGGREGATION_OBJ_NAME.generated"

private const val EVENT_INTENTS_PACKAGE = "love.forte.simbot.qguild.event"
private const val EVENT_INTENTS_CLASS_NAME = "EventIntents"
private val EventIntentsClassName = ClassName(EVENT_INTENTS_PACKAGE, EVENT_INTENTS_CLASS_NAME)

private const val INTENTS_CONST_NAME = "INTENTS"

private const val INTENTS_PACKAGE = "love.forte.simbot.qguild.event"
private const val INTENTS_CLASS_NAME = "Intents"
private val IntentsClassName = ClassName(INTENTS_PACKAGE, INTENTS_CLASS_NAME)

private const val NO_WARP = "·"

/**
 * 寻找所有 `love.forte.simbot.qguild.event.EventIntents` 的子 `object` 类型，
 * 并生成它们 intents 的各种聚合到 `EventIntentsAggregation` 中。
 *
 * @author ForteScarlet
 */
internal class EventIntentsAggregationProcessor(
    val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val processed = AtomicBoolean(false)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (!processed.compareAndSet(false, true)) {
            return emptyList()
        }

        val eventIntentsDeclaration = resolver.getClassDeclarationByName(EventIntentsClassName.canonicalName)
            ?: error("EventIntents declaration not found")

        val allSubObjects = eventIntentsDeclaration
            .getSealedSubclasses()
            .toList()

//        val allSubObjects = resolver.getAllFiles()
//            .filterIsInstance<KSClassDeclaration>()
//            .flatMap { dec ->
//                sequence {
//                    yield(dec)
//                    yieldAll(dec.getSealedSubclasses())
//                }
//            }
//            .filter { declaration ->
//                declaration.classKind == ClassKind.OBJECT &&
//                        declaration.asStarProjectedType()
//                            .isAssignableFrom(eventIntentsDeclaration.asStarProjectedType())
//            }
//            .toList()

        environment.logger.info("Found sub object: $allSubObjects")

        val aggregationBuilder = TypeSpec.objectBuilder(AGGREGATION_OBJ_NAME)

        generateAll(aggregationBuilder, allSubObjects)
        generateGetByName(aggregationBuilder, allSubObjects)

        val fileBuilder = FileSpec.builder(OUTPUT_PACKAGE, AGGREGATION_FILE_NAME)
        fileBuilder.addType(aggregationBuilder.build())
        fileBuilder.addFileComment("本文件内容为自动生成，生成于 %L", OffsetDateTime.now(ZoneOffset.ofHours(8)).toString())

        val targetFile = fileBuilder.build()

        targetFile.writeTo(
            environment.codeGenerator,
            Dependencies(
                aggregating = false,
                sources = buildList {
                    eventIntentsDeclaration.containingFile?.also(::add)
                    allSubObjects.forEach {
                        it.containingFile?.also(::add)
                    }
                }.toTypedArray()
            )
        )

        return emptyList()
    }

    /**
     * 生成常量 `ALL` 和函数 `allIntents`。
     */
    private fun generateAll(builder: TypeSpec.Builder, list: List<KSClassDeclaration>) {
        data class ConstPair(val classDeclaration: KSClassDeclaration, val constant: KSPropertyDeclaration)

        val constValueCode = list.map {
            val intentsConst = it.getAllProperties().first { p ->
                p.simpleName.asString() == INTENTS_CONST_NAME
                        && Modifier.CONST in p.modifiers
            }
            ConstPair(it, intentsConst)
        }

        val constValueFormat = constValueCode.joinToString(" or ") {
            "%T.$INTENTS_CONST_NAME"
        }
        val constValueFormatArgs = constValueCode.flatMap { (clz, _) ->
            sequence<Any?> {
                yield(clz.asStarProjectedType().toClassName())
            }
        }

        val allConst = PropertySpec.builder(
            "ALL",
            Int::class,
        ).apply {
            addModifiers(KModifier.PUBLIC)
            addModifiers(KModifier.CONST)
            initializer(constValueFormat, *constValueFormatArgs.toTypedArray())
            addKdoc("得到 [%T] 的所有子类型的 intents 的聚合结果, 包括:\n", EventIntentsClassName)
            constValueCode.forEach { (clz, _) ->
                addKdoc("- [%T] \n", clz.asStarProjectedType().toClassName())
            }
            addKdoc("\n@see allIntents\n")
        }

        val func = FunSpec.builder("allIntents").apply {
            jvmName("allIntents")
            jvmStatic()

            addCode("return %T(ALL)", IntentsClassName)
            addKdoc("得到 [%T] 的所有子类型的 intents 的聚合结果, 内容值同 [ALL]\n", EventIntentsClassName)
            addKdoc("@see ALL")

            returns(IntentsClassName)
        }

        // 生成返回所有 EventIntents 实例列表的函数
        val allIntentsFunc = FunSpec.builder("allEventIntents").apply {
            jvmStatic()

            addKdoc("获取到 [%T] 的所有子类型object的实例列表\n", EventIntentsClassName)
            addKdoc("@see %T", EventIntentsClassName)

            addCode("return listOf(")
            list.forEachIndexed { index, declaration ->
                addCode("%T", declaration.asStarProjectedType().toClassName())
                if (index < list.lastIndex) {
                    addCode(", ")
                }
            }
            addCode(")")
            returns(LIST.parameterizedBy(EventIntentsClassName))
        }

        builder.addProperty(allConst.build())
        builder.addFunction(func.build())
        builder.addFunction(allIntentsFunc.build())
    }

    /**
     * 生成根据名称获取结果的 `getByName(name: String)`,
     * 名称支持驼峰、全大写和全小写。
     */
    private fun generateGetByName(builder: TypeSpec.Builder, list: List<KSClassDeclaration>) {
        data class NamesToType(val names: Set<String>, val declaration: KSClassDeclaration)

        val nameToTypes = list.map { declaration ->
            val baseName = declaration.simpleName.asString()
            val set = setOf(
                baseName,
                // 开头小写
                baseName.replaceFirstChar(Char::lowercaseChar),
                // 下划线的全大写与全小写
                baseName.toSnack(false),
                baseName.toSnack(true)
            )

            NamesToType(set, declaration)
        }

        val doc = CodeBlock.builder().apply {
            addStatement("使用简单的字符串名称来获取一个对应的 [%T] 子类型的 intents 值，", EventIntentsClassName)
            addStatement("字符串名称与这个类型的简单类型相关：类名的名称，以及对应的snack(下滑线)格式。")
            addStatement("名称支持开头大写、小写的驼峰格式以及对应的全大写、小写的snack格式，但大小写仍然是敏感的。")
            addStatement("")
            addStatement("%L", "| 名称 | 类型 |")
            addStatement("%L", "| --- | --- |")
            nameToTypes.forEach { (names, clz) ->
                val nameMarks = names.joinToString(",$NO_WARP") { "`%S`" }
                val args = buildList<Any?> {
                    addAll(names)
                    add(clz.simpleName.asString())
                    add(clz.asStarProjectedType().toClassName())
                }
                addStatement("|$NO_WARP$nameMarks$NO_WARP|$NO_WARP[%L][%T]$NO_WARP|", *args.toTypedArray())
            }
        }.build()

        val func = FunSpec.builder("getByName").apply {
            jvmName("getByName")
            jvmStatic()

            addParameter("name", String::class)
            addKdoc(doc)

            addCode(
                CodeBlock.builder().apply {
                    add("return ")
                    beginControlFlow("when(name)")

                    nameToTypes.forEach { (names, declaration) ->
                        add(names.joinToString(", ") { "%S" }, *names.toTypedArray())
                        add(" -> ")
                        add("%T.intents\n", declaration.asStarProjectedType().toClassName())
                    }

                    add("else -> throw IllegalArgumentException(%P)", "Unknown name: \$name")

                    endControlFlow()
                }.build()
            )
            returns(IntentsClassName)
        }.build()

        builder.addFunction(func)

        // generate getByNames?


    }


}

private fun String.toSnack(allUpper: Boolean): String = buildString(length) {
    this@toSnack.forEachIndexed { i, char ->
        fun append0() {
            if (allUpper) {
                append(char.uppercaseChar())
            } else {
                append(char.lowercaseChar())
            }
        }

        if (char.isUpperCase()) {
            if (i > 0) {
                append('_')
            }
            append0()
        } else {
            append0()
        }
    }

}
