package org.sourcegrade.insnreplacer

class ThreadLocalInstanceFactory<T : Any> {
  private val valueStorage: InheritableThreadLocal<T> = InheritableThreadLocal()
  var value: T
    get() = valueStorage.get()
    set(value) = valueStorage.set(value)
}
