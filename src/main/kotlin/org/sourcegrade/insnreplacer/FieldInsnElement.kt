package org.sourcegrade.insnreplacer

import org.objectweb.asm.Type
import kotlin.reflect.KClass

internal data class FieldInsnElement(
  val opcode: Int,
  val owner: String,
  val name: String,
  val descriptor: String,
) : BytecodeElement {
  override fun withSurrogate(original: KClass<*>, surrogate: KClass<*>): FieldInsnElement {
    val originalDescriptor = Type.getDescriptor(original.java)
    val surrogateDescriptor = Type.getDescriptor(surrogate.java)
    val descriptor = descriptor.replace(originalDescriptor, surrogateDescriptor)
    return copy(descriptor = descriptor)
  }

  companion object Factory : BytecodeElement.Replacer.Factory<FieldInsnElement> {
    override fun create(original: KClass<*>, surrogate: KClass<*>): BytecodeElement.Replacer<FieldInsnElement> {
      return BytecodeElement.Replacer {
        if (it.descriptor.contains(Type.getDescriptor(original.java))) {
          it.withSurrogate(original, surrogate)
        } else null
      }
    }
  }
}
