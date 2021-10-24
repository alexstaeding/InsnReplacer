package org.sourcegrade.insnreplacer

import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.javaMethod

internal data class MethodInsn(
  val opcode: Int,
  val owner: String,
  val name: String,
  val descriptor: String,
  val isInterface: Boolean,
)

internal typealias InsnReplacement = (MethodInsn) -> MethodInsn?

internal infix fun KClass<*>.replaces(originalType: KClass<*>): InsnReplacement {
  val replacementInsns = functions.asSequence()
    .mapNotNull { it.javaMethod }
    .map { it.toMethodInsn(originalType) to it.toMethodInsn() }
    .toMap()
  return {
    replacementInsns[it] ?: error("Could not find method in $this matching ${it.withSurrogate(this)}")
  }
}

internal inline fun MethodInsn.replace(replacement: InsnReplacement, block: (MethodInsn) -> Unit) {
  block(replacement(this) ?: this)
}

private fun Method.toMethodInsn(surrogate: KClass<*>? = null): MethodInsn {
  // not 100% correct, but it will work in most cases
  // e.g. INVOKESPECIAL via superclass method invocation from base class not covered here
  val opcode = when {
    Modifier.isStatic(modifiers) -> Opcodes.INVOKESTATIC
    Modifier.isPrivate(modifiers) -> Opcodes.INVOKESPECIAL
    declaringClass.isInterface -> Opcodes.INVOKEINTERFACE
    else -> Opcodes.INVOKEVIRTUAL
  }
  var owner = Type.getInternalName(declaringClass)
  var descriptor = Type.getMethodDescriptor(this)
  if (surrogate != null) {
    val surrogateOwner = Type.getInternalName(surrogate.java)
    descriptor = descriptor.replace(owner, surrogateOwner)
    owner = surrogateOwner
  }
  return MethodInsn(opcode, owner, name, descriptor, surrogate?.java?.isInterface ?: declaringClass.isInterface)
}

private fun MethodInsn.withSurrogate(surrogate: KClass<*>): MethodInsn {
  val surrogateOwner = Type.getInternalName(surrogate.java)
  val descriptor = descriptor.replace(owner, surrogateOwner)
  return copy(owner = surrogateOwner, descriptor = descriptor)
}
