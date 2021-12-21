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