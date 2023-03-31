/*
 * Copyright (c) 2023. ForteScarlet.
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

package love.forte.simbot.qguild
//
//
///**
// *
// * ```kotlin
// * @JvmBlocking
// * suspend fun foo(): T = ...
// * ```
// * transform to:
// *
// * ```kotlin
// * @JvmSynthetic
// * suspend fun foo(): T = ...
// *
// * @Api4J
// * fun fooBlocking(): T = runInBlocking { foo() }
// * ```
// *
// */
//@OptIn(ExperimentalMultiplatform::class)
//@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
//@Retention(AnnotationRetention.BINARY)
//@OptionalExpectation
//public expect annotation class JvmBlocking(
//    /**
//     * 生成函数的基础名称，如果为空则为当前函数名。
//     * 最终生成的函数名为 [baseName] + [suffix]。
//     */
//    val baseName: String = "",
//
//    /**
//     * [baseName] 名称基础上追加的名称后缀。
//     */
//    val suffix: String = "Blocking",
//
//    /**
//     * 是否转化为 property 的形式：
//     *
//     * ```kotlin
//     * suspend fun foo(): T = ...
//     *
//     * // Generated
//     * val fooBlocking: T get() = runInBlocking { foo() }
//     * ```
//     *
//     * 只有函数没有参数时有效。
//     *
//     */
//    val asProperty: Boolean = false
//)
//
///**
// * ```kotlin
// * suspend fun run(): Int
// * ```
// *
// * to
// *
// * ```kotlin
// * @JvmSynthetic
// * suspend fun run(): Int
// *
// * @Api4J
// * fun runAsync(): Future<Int> = jvmAsyncScope.future { run() }
// *
// * ```
// *
// */
//@OptIn(ExperimentalMultiplatform::class)
//@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
//@Retention(AnnotationRetention.BINARY)
//@OptionalExpectation
//public expect annotation class JvmAsync(
//    val baseName: String = "",
//    val suffix: String = "Async",
//    /**
//     * 是否转化为 property 的形式：
//     *
//     * ```kotlin
//     * suspend fun foo(): T = ...
//     *
//     * // Generated
//     * val fooAsync: Future<T> get() = runInAsync { foo() }
//     * ```
//     *
//     * 只有函数没有参数时有效。
//     *
//     */
//    actual val asProperty: Boolean = false
//)
