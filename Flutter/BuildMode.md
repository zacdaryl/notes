# Fluter Build Mode

> 开发过程中，总会有debug和release环境下设置不同内容的需求，那么如何在Flutter中判断debug或者release模式呢？

解决这个问题，首先要了解下Flutter的三种Build模式，debug、profile、release

Flutter[文档](https://flutter.dev/docs/testing/build-modes)里是这么介绍的

> A quick summary for when to use which mode is as follows:
>
> - Use debug mode during development, when you want to use hot reload.
> - Use profile mode when you want to analyze performance.
> - Use release mode when you are ready to release your app.

理解了概念之后，如何在代码中进行模式判断呢？经查询，可以使用 dart.vm.product变量，当为true时，当前运行在release模式，false时，运行在debug或profile模式。具体代码如下：

```
const bool inProduction = const bool.fromEnvironment("dart.vm.product");
```