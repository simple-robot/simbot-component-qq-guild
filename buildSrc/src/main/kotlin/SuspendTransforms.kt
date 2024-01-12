/*
 * Copyright (c) 2023-2024. ForteScarlet.
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

import love.forte.plugin.suspendtrans.*


object SuspendTransforms {
    private val javaIncludeAnnotationApi4JClassInfo = ClassInfo("love.forte.simbot.annotations", "Api4J")
    private val javaIncludeAnnotationApi4J = IncludeAnnotation(javaIncludeAnnotationApi4JClassInfo)
    private val javaIncludeAnnotations = listOf(javaIncludeAnnotationApi4J)

    private val jsIncludeAnnotationApi4JsClassInfo = ClassInfo("love.forte.simbot.annotations", "Api4Js")
    private val jsIncludeAnnotationApi4Js = IncludeAnnotation(jsIncludeAnnotationApi4JsClassInfo)
    private val jsIncludeAnnotations = listOf(jsIncludeAnnotationApi4Js)


    private val SuspendReserveClassInfo = ClassInfo(
        packageName = "love.forte.simbot.suspendrunner.reserve",
        className = "SuspendReserve",

        )

    /**
     * JvmBlocking
     */
    val jvmBlockingTransformer = SuspendTransformConfiguration.jvmBlockingTransformer.copy(
        syntheticFunctionIncludeAnnotations = javaIncludeAnnotations,
        transformFunctionInfo = FunctionInfo("love.forte.simbot.suspendrunner", null, "$\$runInBlocking"),
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmBlockingTransformer.copyAnnotationExcludes + SuspendTransformConfiguration.jvmBlockingTransformer.markAnnotation.classInfo
    )

    /**
     * JvmAsync
     */
    val jvmAsyncTransformer = SuspendTransformConfiguration.jvmAsyncTransformer.copy(
        syntheticFunctionIncludeAnnotations = javaIncludeAnnotations,
        transformFunctionInfo = FunctionInfo("love.forte.simbot.suspendrunner", null, "$\$runInAsyncNullable"),
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes + SuspendTransformConfiguration.jvmAsyncTransformer.markAnnotation.classInfo
    )

    /**
     * JvmReserve
     */
    val jvmReserveTransformer = SuspendTransformConfiguration.jvmAsyncTransformer.copy(
        syntheticFunctionIncludeAnnotations = javaIncludeAnnotations,
        transformFunctionInfo = FunctionInfo("love.forte.simbot.suspendrunner", null, "$\$asReserve"),
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes + SuspendTransformConfiguration.jvmAsyncTransformer.markAnnotation.classInfo,
        transformReturnType = SuspendReserveClassInfo,
        transformReturnTypeGeneric = true,
    )

    /**
     * JsPromise
     */
    val jsPromiseTransformer = SuspendTransformConfiguration.jsPromiseTransformer.copy(
        syntheticFunctionIncludeAnnotations = javaIncludeAnnotations,
        transformFunctionInfo = FunctionInfo("love.forte.simbot.suspendrunner", null, "$\$runInPromise"),
        copyAnnotationExcludes = SuspendTransformConfiguration.jsPromiseTransformer.copyAnnotationExcludes + SuspendTransformConfiguration.jsPromiseTransformer.markAnnotation.classInfo,
    )

    //region @JvmSuspendTrans
    private val suspendTransMarkAnnotationClassInfo = ClassInfo("love.forte.simbot.suspendrunner", "SuspendTrans")

    private val jvmSuspendTransMarkAnnotationForBlocking = MarkAnnotation(
        suspendTransMarkAnnotationClassInfo,
        baseNameProperty = "blockingBaseName",
        suffixProperty = "blockingSuffix",
        asPropertyProperty = "blockingAsProperty",
        defaultSuffix = SuspendTransformConfiguration.jvmBlockingAnnotationInfo.defaultSuffix,
    )
    private val jvmSuspendTransMarkAnnotationForAsync = MarkAnnotation(
        suspendTransMarkAnnotationClassInfo,
        baseNameProperty = "asyncBaseName",
        suffixProperty = "asyncSuffix",
        asPropertyProperty = "asyncAsProperty",
        defaultSuffix = SuspendTransformConfiguration.jvmAsyncAnnotationInfo.defaultSuffix,
    )
    private val jvmSuspendTransMarkAnnotationForReserve = MarkAnnotation(
        suspendTransMarkAnnotationClassInfo,
        baseNameProperty = "reserveBaseName",
        suffixProperty = "reserveSuffix",
        asPropertyProperty = "reserveAsProperty",
        defaultSuffix = "Reserve",
    )
    private val jsSuspendTransMarkAnnotationForPromise = MarkAnnotation(
        suspendTransMarkAnnotationClassInfo,
        baseNameProperty = "jsPromiseBaseName",
        suffixProperty = "jsPromiseSuffix",
        asPropertyProperty = "jsPromiseAsProperty",
        defaultSuffix = "Async",
    )

    val suspendTransTransformerForJvmBlocking = jvmBlockingTransformer.copy(
        markAnnotation = jvmSuspendTransMarkAnnotationForBlocking,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmBlockingTransformer.copyAnnotationExcludes + jvmSuspendTransMarkAnnotationForBlocking.classInfo
    )

    val suspendTransTransformerForJvmAsync = jvmAsyncTransformer.copy(
        markAnnotation = jvmSuspendTransMarkAnnotationForAsync,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes + jvmSuspendTransMarkAnnotationForAsync.classInfo
    )

    val suspendTransTransformerForJvmReserve = jvmReserveTransformer.copy(
        markAnnotation = jvmSuspendTransMarkAnnotationForReserve,
        copyAnnotationExcludes = jvmReserveTransformer.copyAnnotationExcludes + jvmSuspendTransMarkAnnotationForReserve.classInfo,
    )

    val suspendTransTransformerForJsPromise = jsPromiseTransformer.copy(
        markAnnotation = jvmSuspendTransMarkAnnotationForReserve,
        copyAnnotationExcludes = jsPromiseTransformer.copyAnnotationExcludes + jsSuspendTransMarkAnnotationForPromise.classInfo,
    )
    //endregion

    //region @JvmSuspendTransProperty
    private val jvmSuspendTransPropMarkAnnotationClassInfo =
        ClassInfo("love.forte.simbot.suspendrunner", "SuspendTransProperty")

    private val jvmSuspendTransPropMarkAnnotationForBlocking = MarkAnnotation(
        jvmSuspendTransPropMarkAnnotationClassInfo,
        baseNameProperty = "blockingBaseName",
        suffixProperty = "blockingSuffix",
        asPropertyProperty = "blockingAsProperty",
        defaultSuffix = "",
        defaultAsProperty = true
    )
    private val jvmSuspendTransPropMarkAnnotationForAsync = MarkAnnotation(
        jvmSuspendTransPropMarkAnnotationClassInfo,
        baseNameProperty = "asyncBaseName",
        suffixProperty = "asyncSuffix",
        asPropertyProperty = "asyncAsProperty",
        defaultSuffix = SuspendTransformConfiguration.jvmAsyncAnnotationInfo.defaultSuffix,
        defaultAsProperty = true
    )
    private val jvmSuspendTransPropMarkAnnotationForReserve = MarkAnnotation(
        jvmSuspendTransPropMarkAnnotationClassInfo,
        baseNameProperty = "reserveBaseName",
        suffixProperty = "reserveSuffix",
        asPropertyProperty = "reserveAsProperty",
        defaultSuffix = "Reserve",
        defaultAsProperty = true
    )

    val jvmSuspendTransPropTransformerForBlocking = jvmBlockingTransformer.copy(
        markAnnotation = jvmSuspendTransPropMarkAnnotationForBlocking,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmBlockingTransformer.copyAnnotationExcludes + jvmSuspendTransPropMarkAnnotationForBlocking.classInfo
    )

    val jvmSuspendTransPropTransformerForAsync = jvmAsyncTransformer.copy(
        markAnnotation = jvmSuspendTransPropMarkAnnotationForAsync,
        copyAnnotationExcludes = SuspendTransformConfiguration.jvmAsyncTransformer.copyAnnotationExcludes + jvmSuspendTransPropMarkAnnotationForAsync.classInfo
    )

    val jvmSuspendTransPropTransformerForReserve = jvmReserveTransformer.copy(
        markAnnotation = jvmSuspendTransPropMarkAnnotationForReserve,
        copyAnnotationExcludes = jvmReserveTransformer.copyAnnotationExcludes + jvmSuspendTransPropMarkAnnotationForReserve.classInfo
    )
    //endregion
}

