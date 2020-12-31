# Scope Functions

提起Kotlin中的作用域函数，很容易的就会说出来有：let、run、with、apply 和 also。但这五个函数都有什么不同，怎么选择使用呢，可能还得去查一番文档。这里针对此用文字记录下，以便加深下理解！

https://kotlinlang.org/docs/reference/scope-functions.html

https://www.kotlincn.net/docs/reference/scope-functions.html

官方这篇文章对作用域函数讲的十分清楚，有疑惑的时候，可以反复的去看，直到真正的理解并运用自如。

## 区别

文中说作用域函数的主要区别有两点：

- 引用上下文对象的方式
- 返回值

### 上下文对象

访问上下文对象的方式有两种：作为lambda表达式的接收者（this）,或者作为lambda表达式的参数（it）。

run、with、apply通过this引用上下文对象。
let、also将上下文对象作为lambda表达式的参数，使用it访问，并可以修改为自定义参数名称。

### 返回值

- apply、also返回上下文对象。
- run、with、let返回lamdba表达式的结果。

整理成一个表格看具体的区别：

| 函数  | 上下文对象 | 返回值           |
| :---- | :--------- | :--------------- |
| let   | it         | Lamdba表达式结果 |
| run   | this       | Lamdba表达式结果 |
| with  | this       | Lamdba表达式结果 |
| apply | this       | 上下文对象       |
| also  | it         | 上下文对象       |

run 有一个特殊情况，不作为扩展函数使用，可以执行一个多个语句的语句块，并得到表达式的结果。

with是一个非扩展函数，上下文对象实例作为参数传递给with。

