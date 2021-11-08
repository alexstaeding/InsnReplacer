package org.sourcegrade.insnreplacer

import kotlin.reflect.KClass

internal data class BytecodeReplacement(
  val field: BytecodeElement.Replacer<FieldElement>,
  val fieldInsn: BytecodeElement.Replacer<FieldInsnElement>,
  val methodInsn: BytecodeElement.Replacer<MethodInsnElement>,
) {
  constructor(
    fieldFactory: BytecodeElement.Replacer.Factory<FieldElement>,
    fieldInsnFactory: BytecodeElement.Replacer.Factory<FieldInsnElement>,
    methodFactory: BytecodeElement.Replacer.Factory<MethodInsnElement>,
    original: KClass<*>,
    surrogate: KClass<*>,
  ) : this(
    fieldFactory.create(original, surrogate),
    fieldInsnFactory.create(original, surrogate),
    methodFactory.create(original, surrogate),
  )
}

internal interface BytecodeElement {
  fun withSurrogate(original: KClass<*>, surrogate: KClass<*>): BytecodeElement
  fun interface Replacer<T : BytecodeElement> {
    fun replace(element: T): T?
    interface Factory<T : BytecodeElement> {
      fun create(original: KClass<*>, surrogate: KClass<*>): Replacer<T>
    }
  }
}

internal infix fun KClass<*>.replaces(originalType: KClass<*>): BytecodeReplacement {
  return BytecodeReplacement(FieldElement, FieldInsnElement, MethodInsnElement, originalType, this)
}

internal inline fun <reified T : BytecodeElement, R> T.replace(replacement: BytecodeElement.Replacer<T>, block: (T) -> R): R {
  return block(replacement.replace(this) ?: this)
}
