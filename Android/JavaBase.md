# Java Base

## synchronized static 和 synchronized

synchronized修饰的方法，是对该类的实例加锁，同一个实例的不同方法在多线程中互斥
synchronized修饰的静态方法，对类对象加锁，多线程中访问该类对象的数据时互斥

## == and equals

- == 主要对值类型进行比较，Java中有八种值类型，分别是：byte、short、int、long、float、double、boolean、char。

  https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html

  对于两个引用类型变量，通过==比较的是引用的地址

- equals 是Object的一个方法，用于判断两个对象是否相等。默认equals还是比较两个对象的引用地址是否相等。

  ```
  public boolean equals(Object obj) {
      return (this == obj);
  }
  ```

  要保证两个对象的内容是否相等，重写equals方法，如果重写equals方法，也必须重写hashCode方法，因为Java中有约定，两个相等的对象，其各自的hashCode也必须一样。

  > Note that it is generally necessary to override the `hashCode` method whenever this method is overridden, so as to maintain the general contract for the `hashCode` method, which states that equal objects must have equal hash codes.

  这里引出hashcode，下边详细了解下hashcode

## hashcode

hashcode主要用于计算对象在散列表中的位置。

两个对象通过equals比较相等，则两个对象的hashCode必须返回相同的值，反之则未必。

https://www.cnblogs.com/ysocean/p/9054804.html 关于HashMap中hash值的计算以及如何计算数组下标 

`(n - 1) & hash == hash % n` 只有在n为2的幂次方时才成立

## Jvm

### Java内存模型

https://juejin.im/post/6844903986663800845

### Java内存区域

https://dyfloveslife.github.io/2019/11/18/java-memory-areas/

线程共享

- 堆

   存储对象实例及数组

- 方法区

  存储已被虚拟机加载的 `类信息`、`常量`、`静态变量`、`即时编译器编译后的代码` 等数据。

线程私有/隔离

- 程序计数器

  程序计数器（Program Counter Register）是当前线程所执行的字节码的行号指示器，它占用一块较小的内存空间。字节码解释器通过改变该计数器的值来选取下一条需要执行的字节码指令，其中包括分支、循环、跳转、异常处理、线程恢复等基础功能。

  如果线程正在执行的是一个 Java 方法，则该计数器记录的是正在执行的 `虚拟机字节码指令的地址`；如果正在执行的是一个 Native 方法，则该计数器为空（Undefinaed）。

  因为 Java 虚拟机的多线程是通过线程轮流切换并分配处理器执行时间的方式来实现的，在任何一个确定的时刻，一个处理器都会只执行一条线程中的指令。

  因此，为了线程切换后能够恢复到正确的执行位置，每条线程都需要有一个独立的程序计数器，各线程之间的计数器互不影响，独立存储。

- 虚拟机栈

  Java 虚拟机栈（Java Virtual Machine Stacks）也就是我们平时所说的 `栈内存` ，或者指的就是虚拟机栈中的 `局部变量表` 部分。它描述的是 Java 方法执行的内存模型：即每个方法在执行的同时都会创建一个 `栈帧（Stack Frame）`，用于存储局部变量表、操作数栈、动态链接、方法出口等信息。每个方法从调用到执行完毕的过程，就对应着一个栈帧在虚拟机栈中的入栈和出栈的过程。

- 本地方法栈

  本地方法栈（Native Method Stack）与虚拟机栈的作用是相似的。但本地方法栈为虚拟机使用到的 `Native` 方法提供服务。

## ClassLoader

类加载器，负责将Java类加载到内存中。通常Java类是按需加载，在第一次使用类的时候才加载。

JVM有三个默认的类加载器：

- 引导类加载器，Bootstrap类加载器。由原生代码实现，负责加载Java的核心类，在<JAVA_HOME>/jre/lib目录中。
- 扩展类加载器，ExtClassLoader。用来在`<JAVA_HOME>/jre/lib/ext`,[[6\]](https://zh.wikipedia.org/wiki/Java类加载器#cite_note-6)或`java.ext.dirs`中指明的目录中加载 Java的扩展库。
- App类加载器，AppClassLoader。根据 Java应用程序的类路径（`java.class.path`或CLASSPATH环境变量）来加载 Java 类。一般来说，Java 应用的类都是由它来完成加载的。

委托加载模式：

> AppClassLoader 在加载一个未知的类名时，它并不是立即去搜寻 Classpath，它会首先将这个类名称交给 ExtensionClassLoader 来加载，如果 ExtensionClassLoader 可以加载，那么 AppClassLoader 就不用麻烦了。否则它就会搜索 Classpath 。
>
> 而 ExtensionClassLoader 在加载一个未知的类名时，它也并不是立即搜寻 ext 路径，它会首先将类名称交给 BootstrapClassLoader 来加载，如果 BootstrapClassLoader 可以加载，那么 ExtensionClassLoader 也就不用麻烦了。否则它就会搜索 ext 路径下的 jar 包。
>
> 这三个 ClassLoader 之间形成了级联的父子关系，每个 ClassLoader 都很懒，尽量把工作交给父亲做，父亲干不了了自己才会干。每个 ClassLoader 对象内部都会有一个 parent 属性指向它的父加载器。
>
> 作者：老錢
> 链接：https://juejin.im/post/6844903729435508750
> 来源：掘金

## Annotation

> *Annotations*, a form of metadata, provide data about a program that is not part of the program itself. Annotations have no direct effect on the operation of the code they annotate.

**注解** 是元数据的一种形式，它提供了有关程序的数据，但这些数据不是程序本身的一部分。注解对其所注释的代码的运行没有直接影响。

### 注解的作用

> - **Information for the compiler** — Annotations can be used by the compiler to detect errors or suppress warnings.
> - **Compile-time and deployment-time processing** — Software tools can process annotation information to generate code, XML files, and so forth.
> - **Runtime processing** — Some annotations are available to be examined at runtime.

简单总结下，可以给编译器提供一些信息，比如检查错误忽略警告，编译期间生成代码配置等，运行期获取信息并进行一些操作等

### Java内置的注解

https://docs.oracle.com/javase/tutorial/java/annotations/predefined.html

元注解就是注解注解的注解，主要有以下几种：

**@Retention** 标明注解是在源码、编译期、或者运行期有效

**@Documented** 注解是否可以包含在Javadoc中

**@Target** 标记注解应用在哪些Java元素中，类，成员变量，方法，参数等

**@Inherited** 注解是否可以被子类继承

**@Repeatable** jdk1.8加入的，是否对同一元素可以重复标注

预定义的注解有：

`@Deprecated`, `@Override`, and `@SuppressWarnings`.

### 反射获取注解

```java
Class clas = null;
try {
  clas = Class.forName("com.jzm.jbase.AnClass");
} catch (ClassNotFoundException e) {
  e.printStackTrace();
}

if(clas == null) return;

Annotation[] annotations = clas.getAnnotations();
for (Annotation annotation : annotations) {
  MyAnnotation myAnnotation = (MyAnnotation) annotation;
  System.out.println(myAnnotation.value());
}
```

## 动态代理

https://juejin.im/post/6844903744954433544

## 多线程

线程间协作

https://www.cnblogs.com/paddix/p/5381958.html



