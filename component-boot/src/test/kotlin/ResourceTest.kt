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

import love.forte.simboot.core.internal.ResourcesScanner
import love.forte.simboot.core.internal.visitJarEntry
import love.forte.simboot.core.internal.visitPath
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility

fun main() {

    val pathReplace = Regex("[/\\\\]")
    ResourcesScanner<KClass<*>>().use { scanner ->
        scanner.scan("")
            .also {
                for (scanPkg in listOf("")) {
                    it.glob(scanPkg.replace(".", "/") + "**.class")
                }
            }
            .visitJarEntry { entry, _ ->
                val classname = entry.name.replace(pathReplace, ".").substringBeforeLast(".class")
                try {
                    sequenceOf(scanner.classLoader.loadClass(classname).kotlin)
                } catch (e: UnsupportedClassVersionError) {
                    println("entry.name: ${entry.name}")
                    println("classname: $classname")
                    println()
                    emptySequence()
                }
            }
            .visitPath { (_, r) ->
                // '/Xxx.class'
                val classname = r.replace(pathReplace, ".").substringBefore(".class").let {
                    if (it.startsWith(".")) it.substring(1) else it
                }
                sequenceOf(scanner.classLoader.loadClass(classname).kotlin)
            }
            .collectSequence(true)
            /* Packages and file facades are not yet supported in Kotlin reflection. Meanwhile please use Java reflection to inspect this class: class ResourceGetTestKt */
            .filter { k -> runCatching { k.visibility == KVisibility.PUBLIC }.getOrDefault(false)  }
            .toList()
    }
}