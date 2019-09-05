# Kotlin

> 在Google I/O 2017中，Google宣布在Android上为Kotlin提供最佳支持。[Kotlin on Android. Now official](https://blog.jetbrains.com/kotlin/2017/05/kotlin-on-android-now-official/)

Google官方支持Kotlin作为Android开发语言两年了，是时候使用Kotlin开发新的项目了。好记性不如烂笔头，新语言遇到的问题，应该记录下来备忘。

## 静态方法 static

遇到的第一个问题就是，用Kotlin写静态方法，如何写？

使用伴生对象

```kotlin
class MyClass {
    companion object Factory {
        fun create(): MyClass = MyClass()
    }
}

val instance = MyClass.create()
```

即使伴生对象的成员看起来像其他语言的静态成员，在运行时他们仍然是真实对象的实例成员。

当然，在 JVM 平台，如果使用 @JvmStatic 注解，你可以将伴生对象的成员生成为真正的静态方法和字段。

```kotlin
class Authorize {
    companion object {
        @JvmStatic fun authorize(jsonObject: JsonObject) {
            ...
        }
    }
}
```

reference:

https://www.kotlincn.net/docs/reference/object-declarations.html

https://kotlinlang.org/docs/reference/object-declarations.html

## 延迟属性 Lazy

lazy() 是接受一个 lambda 并返回一个 Lazy <T> 实例的函数，返回的实例可以作为实现延迟属性的委托： 第一次调用 get() 会执行已传递给 lazy() 的 lambda 表达式并记录结果， 后续调用 get() 只是返回记录的结果。

```kotlin
val lazyValue: String by lazy {
    println("computed!")
    "Hello"
}
​
fun main() {
    println(lazyValue)
    println(lazyValue)
}
```
默认情况下，对于 lazy 属性的求值是同步锁的（synchronized）：该值只在一个线程中计算，并且所有线程会看到相同的值。如果初始化委托的同步锁不是必需的，这样多个线程可以同时执行，那么将 LazyThreadSafetyMode.PUBLICATION 作为参数传递给 lazy() 函数。 而如果你确定初始化将总是发生在与属性使用位于相同的线程， 那么可以使用 LazyThreadSafetyMode.NONE 模式：它不会有任何线程安全的保证以及相关的开销。

reference: 

https://www.kotlincn.net/docs/reference/delegated-properties.html