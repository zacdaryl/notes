# 创建ViewModel

```
class TasksFragment : Fragment() {

    private val viewModel by viewModels<TasksViewModel> { getViewModelFactory() }
    ……
```

这里的viewModel是一个[委托属性](https://kotlinlang.org/docs/delegated-properties.html), 通过Fragment的扩展函数viewModels来提供。

viewModels的实现源码如下：

```
inline fun <reified VM : ViewModel> Fragment.viewModels(
    noinline ownerProducer: () -> ViewModelStoreOwner = { this },
    noinline factoryProducer: (() -> Factory)? = null
) = createViewModelLazy(VM::class, { ownerProducer().viewModelStore }, factoryProducer)
```

这里引入reified关键字，stackoverflower上的说明简单理解就是用reified修饰的范型可在运行时使用，详细讨论链接如下：

[How does the reified keyword in Kotlin work?](https://stackoverflow.com/questions/45949584/how-does-the-reified-keyword-in-kotlin-work)

## 导航组件

https://developer.android.com/guide/navigation

[使用 Safe Args 传递安全的数据](https://developer.android.com/guide/navigation/navigation-pass-data#Safe-args)
