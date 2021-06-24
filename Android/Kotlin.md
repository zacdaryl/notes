# Kotlin

> 在Google I/O 2017中，Google宣布在Android上为Kotlin提供最佳支持。[Kotlin on Android. Now official](https://blog.jetbrains.com/kotlin/2017/05/kotlin-on-android-now-official/)

这里记录下使用Kotlin过程中遇到的一些问题，或是和Java不一样的语法，加深下记忆。

## Array
习惯了Java的数组声明：`int[] nums = {1,2,3,4};`，在Kotlin中很容易也想以同样方式声明一个数组，结果是不行的，Kotlin中声明数组得使用arrayOf，`var nums = arrayOf(1,2,3,4)`。

遍历数组的时候，在Java中，习惯使用`for(int i = 0; i < arrays.length; i++)`，在Kotlin中就不行了，得这么遍历：

```kotlin
for (item in collection) print(item)

for (item: Int in ints) {
    // ...
}

//遍历数组想使用索引
for (i in array.indices) {
    println(array[i])
}

for ((index, value) in array.withIndex()) {
    println("the element at $index is $value")
}
```

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

## Backing Fields

Fields cannot be declared directly in Kotlin classes. However, when a property needs a backing field, Kotlin provides it automatically. This backing field can be referenced in the accessors using the field identifier:


```kotlin
var counter = 0 // Note: the initializer assigns the backing field directly
    set(value) {
        if (value >= 0) field = value
    }
```
The field identifier can only be used in the accessors of the property.

A backing field will be generated for a property if it uses the default implementation of at least one of the accessors, or if a custom accessor references it through the field identifier.

For example, in the following case there will be no backing field:
```kotlin
val isEmpty: Boolean
    get() = this.size == 0
```

https://medium.com/@nomanr/backing-field-in-kotlin-explained-9f903f27946c

https://kotlinlang.org/docs/reference/properties.html#properties-and-fields

## if 表达式

习惯了Java中的三元运算符：`() ? :`，Kotlin中并没有这个，取而代之的是if表达式。

> In Kotlin, if is an expression, i.e. it returns a value. Therefore there is no ternary operator (condition ? then : else), because ordinary if works fine in this role.

```
// Traditional usage 
var max = a 
if (a < b) max = b

// With else 
var max: Int
if (a > b) {
    max = a
} else {
    max = b
}
 
// As expression 
val max = if (a > b) a else b
```

## playground

> Scratches let us create code drafts in the same IDE window as our project and run them on the fly. Scratches are not tied to projects; you can access and run all your scratches from any IntelliJ IDEA window on your OS.

[scratches-and-worksheets](https://kotlinlang.org/docs/tutorials/quick-run.html#scratches-and-worksheets)

## Safe Calls

对Kotlin的 [Null Safety](https://kotlinlang.org/docs/reference/null-safety.html) 一直存在一个误解，以为不管怎样写，只要编译通过了，就不会出现空指针异常。直到这个异常出现后，才又认真的看了一遍其中的 Safe Calls，发现自己并没有深入理解其空指针安全。

举个例子：

```kotlin
var text = getText() //getText可返回null

//刚开始这样写
//if (text.contains("bc")) println("safe") //编译报错

//编译器报错后，跟着提示一路fix后。
if (text?.contains("bc")!!) println("safe") //错误的想法：因为kotlin是空指针安全的，text为空时就不会执行contains方法。
```

以上代码，执行时就会抛出异常：

`Exception in thread "main" kotlin.KotlinNullPointerException`

怎么回事？空指针安全，现在不安全了！

其实编译器报错时，已经很明确给了提示，应该对text做非空判断，然后再继续使用，但错误的理解了编译器的提示修复功能，虽然解决了编译器的问题，但把异常带到了运行时。

除了进行非空判断外，还可以继续使用Safe Calls来解决问题，就是和let结合起来使用，代码如下：

```kotlin
text?.let { //text不为空时，才会执行打印
    print("safe");
}
```

## 协程

## Functional interfaces

https://kotlinlang.org/docs/fun-interfaces.html#sam-conversions

