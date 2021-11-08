package org.sourcegrade.insnreplacer

import org.objectweb.asm.Type
import kotlin.reflect.KClass

internal data class FieldElement(
  val access: Int,
  val name: String,
  val descriptor: String,
  val signature: String?,
  val value: Any?,
) : BytecodeElement {
  override fun withSurrogate(original: KClass<*>, surrogate: KClass<*>): FieldElement {
    val originalDescriptor = Type.getDescriptor(original.java)
    val surrogateDescriptor = Type.getDescriptor(surrogate.java)
    val descriptor = descriptor.replace(originalDescriptor, surrogateDescriptor)
    return copy(descriptor = descriptor)
  }

  companion object Factory : BytecodeElement.Replacer.Factory<FieldElement> {
    override fun create(original: KClass<*>, surrogate: KClass<*>): BytecodeElement.Replacer<FieldElement> {
      return BytecodeElement.Replacer {
        if (it.descriptor.contains(Type.getDescriptor(original.java))) {
          it.withSurrogate(original, surrogate)
        } else null
      }
    }
  }
}
