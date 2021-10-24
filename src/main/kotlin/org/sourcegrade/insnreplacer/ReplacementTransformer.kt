package org.sourcegrade.insnreplacer

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.sourcegrade.jagr.api.testing.ClassTransformer
import kotlin.reflect.KClass

@JvmName("create")
fun createReplacementTransformer(
  replacement: Class<*>,
  original: Class<*>,
): ClassTransformer = ReplacementTransformer(replacement.kotlin, original.kotlin)

@JvmName("create")
fun createReplacementTransformer(
  replacement: KClass<*>,
  original: KClass<*>,
): ClassTransformer = ReplacementTransformer(replacement, original)

private class ReplacementTransformer(
  replacement: KClass<*>,
  original: KClass<*>
) : ClassTransformer {
  private val name = "$replacement-replaces-$original"
  private val replacement = replacement replaces original
  private val originalOwner = Type.getInternalName(original.java)
  override fun getName(): String = name
  override fun transform(reader: ClassReader, writer: ClassWriter) = reader.accept(ReplacementVisitor(writer), 0)
  private inner class ReplacementVisitor(classVisitor: ClassVisitor?) : ClassVisitor(Opcodes.ASM9, classVisitor) {
    override fun visitMethod(
      access: Int, name: String, descriptor: String, signature: String?, exceptions: Array<String>?
    ) = ReplacementMethodVisitor(super.visitMethod(access, name, descriptor, signature, exceptions))

    private inner class ReplacementMethodVisitor(methodVisitor: MethodVisitor?) : MethodVisitor(Opcodes.ASM9, methodVisitor) {
      override fun visitMethodInsn(opcode: Int, owner: String, name: String, descriptor: String, isInterface: Boolean) {
        if (owner != originalOwner) {
          return super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        }
        MethodInsn(opcode, owner, name, descriptor, isInterface).replace(replacement) {
          super.visitMethodInsn(it.opcode, it.owner, it.name, it.descriptor, it.isInterface)
        }
      }
    }
  }
}
