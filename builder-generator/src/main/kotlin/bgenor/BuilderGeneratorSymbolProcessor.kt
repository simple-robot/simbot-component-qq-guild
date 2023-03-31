/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package bgenor

import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo


/**
 *
 * @author ForteScarlet
 */
class BuilderGeneratorSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val logger get() = environment.logger
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val classes = resolver.getSymbolsWithAnnotation("bgenor.GenerateBuilder", false)
            .filterIsInstance<KSClassDeclaration>()
            .toSet()

        logger.info("annotated @bgenor.GenerateBuilder classes: ${classes.joinToString { it.simpleName.asString() }}")

        val codeGenerator = BgenorCodeGenerator(environment)

        classes.forEach { ksClass ->
            val context = BgenorContext()

            ksClass.primaryConstructor?.also { context.resolvePrimaryConstructor(it) }

            ksClass.getAllProperties().forEach {
                context.resolveProperty(it)
            }

            codeGenerator.generate(resolver, ksClass, context)
        }

        return emptyList()
    }


}

internal class BgenorContext {
    internal val constructorParams = mutableListOf<Param>()
    internal val properties = mutableListOf<Property>()


    fun resolvePrimaryConstructor(func: KSFunctionDeclaration) {
        for (parameter in func.parameters) {
            constructorParams.add(Param(parameter))
        }
    }

    fun resolveProperty(property: KSPropertyDeclaration) {
        if (!property.isMutable) {
            return
        }

        if (!property.isPublic()) {
            return
        }

        properties.add(Property(property))
    }

    internal data class Property(val property: KSPropertyDeclaration)
    internal data class Param(val property: KSValueParameter)

}


private class BgenorCodeGenerator(
    private val environment: SymbolProcessorEnvironment,
) {
    fun generate(
        resolver: Resolver,
        target: KSClassDeclaration,
        context: BgenorContext
    ) {
        val targetClassName = target.simpleName.asString() + "Builder"

        val fileSpecBuilder = FileSpec.builder(
            target.packageName.asString(),
            "${target.simpleName.asString()}$\$Builder"
        )


        val constructorProperties = context.constructorParams.map { param ->
            val p = param.property


            val modifiers: Array<KModifier>
            val initNull: Boolean
            if (p.hasDefault) {
                modifiers = arrayOf(KModifier.PUBLIC)
                initNull = true
            } else {
                modifiers = arrayOf(KModifier.PUBLIC, KModifier.LATEINIT)
                initNull = false
            }



            PropertySpec.builder(
                p.name!!.asString(),
                p.type.toTypeName().copy(nullable = initNull), *modifiers
            ).apply {
//                this.addKdoc() // TODO
                mutable()
                if (initNull) {
                    initializer("null")
                }
            }.build()
        }


        val builderClass = TypeSpec.classBuilder("${target.simpleName.asString()}Builder").apply {
            addProperties(constructorProperties)
            addFunction(FunSpec.builder("build").apply {
                returns(target.toClassName())

                propertySpecs.forEach { p ->
                    addCode(
                        """
                            val ${p.name} = this.${p.name}
                            
                        """.trimIndent()
                    )
                }

                addCode(
                    """
                    return TODO()
                """.trimIndent()
                )
            }.build())
        }.build()


        fileSpecBuilder.addType(builderClass).build().writeTo(environment.codeGenerator, false)
    }

}







