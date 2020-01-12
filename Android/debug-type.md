# Android 调试类型

怎么都没想到会栽倒在调试类型这个坑里，现象是在通过AndroidStudio的“Attach Debugger To Android Process”按钮连接应用调试时，总是关联不到App的进程。

针对这种现象，试了N多种方法：

1，检查手机debug开关是否打开
2，重启adb `adb kill-server`、`adb start-server`
3，重启AndroidStudio
4，重启手机 
5，重启电脑

各种能想到的法子试了一遍，结果就是不行。

这是个问题，得解决！Google看了很多文章之后，大多也跑不出以上5种方法，最后静下心来看下官方文档关于调试的章节，终于发现问题所在。

官网关于Debug的介绍文档：[Debug your app](https://developer.android.com/studio/debug)，其中 [Change the debugger type](https://developer.android.com/studio/debug#debug-types) 章节详细说明了四种Debug类型：Auto、Java、Native、Dual 

这里看Auto的具体介绍：

> Auto: Select if you want Android Studio to automatically choose the best option for the code you are debugging. For example, if you have any C or C++ code in your project, Android Studio automatically uses the Dual debug type. Otherwise, Android Studio uses the Java debug type.

`if you have any C or C++ code in your project, Android Studio automatically uses the Dual debug type. Otherwise, Android Studio uses the Java debug type.` 问题就出现在这里，我的项目中包含C、C++代码，AS自动使用了Dual Debug模式，所以无法断点调试Java、Kotlin代码，于是赶快选择Java模式，调试得以顺利进行。

最后总结，auto模式，并不如想象中那样自动，遇到问题，最好还是沉下心来看下文档先，解决方法说不定就在文档的某几句话中。

