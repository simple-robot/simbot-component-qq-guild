package test

import love.forte.simbot.qguild.event.C2CMessageCreate
import kotlin.test.Test
import kotlin.test.assertNotNull

class C2CMessageCreateAbiTests {
    @Test
    fun c2cMessageDataKeepsPre47ConstructorAndCopySignatures() {
        val type = C2CMessageCreate.Data::class.java
        val arguments = arrayOf(
            String::class.java,
            C2CMessageCreate.Author::class.java,
            String::class.java,
            String::class.java,
            List::class.java,
        )

        assertNotNull(type.getDeclaredConstructor(*arguments))
        assertNotNull(type.getDeclaredMethod("copy", *arguments))
        assertNotNull(
            type.declaredConstructors.singleOrNull { constructor ->
                constructor.parameterTypes.map { it.name } == listOf(
                    String::class.java.name,
                    C2CMessageCreate.Author::class.java.name,
                    String::class.java.name,
                    String::class.java.name,
                    List::class.java.name,
                    "int",
                    "kotlin.jvm.internal.DefaultConstructorMarker",
                )
            }
        )
    }
}
