# Block

今天要用到回调，iOS block这块还比较生疏，所以就记录一下，加强记忆。

我的需求是要获取一个数据，但这个数据首先从NSUserDefaults中获取，如果没有，则从网络获取，此时就需要用block给调用者，等待网络请求成功后，拿到正确的结果。

block不熟悉，首先就是查看官方介绍了，[Working with Blocks](https://developer.apple.com/library/archive/documentation/Cocoa/Conceptual/ProgrammingWithObjectiveC/WorkingwithBlocks/WorkingwithBlocks.html) 这里内容丰富，通读下来，可以对block有个全面的认识，当然上面说的需求自然也就有了答案。

如文章中所说，声明一个block为参数的方法：

```
- (void)beginTaskWithCallbackBlock:(void (^)(void))callbackBlock;
```

调用带block的方法：

```
- (void)beginTaskWithCallbackBlock:(void (^)(void))callbackBlock {
    ...
    callbackBlock();
}
```

带参数block：

```
- (void)doSomethingWithBlock:(void (^)(double, double))block {
    ...
    block(21.0, 2.0);
}
```