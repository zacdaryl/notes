# Flutter 

## ValueChanged

```
final ValueChanged<bool> onChanged;
```
第一次看到使用ValueChanged做回调时，感觉很神秘，还以为和Function有什么神秘的区别，带着疑问揭开神秘的面纱，会发现它就是个Function。

> void ValueChanged (
>   T value
> )
> Signature for callbacks that report that an underlying value has changed.

> typedef ValueChanged<T> = void Function(T value);

## createState()

Creates the mutable state for this widget at a given location in the tree.

createState()的调用时机：

> The framework can call this method multiple times over the lifetime of a StatefulWidget. For example, if the widget is inserted into the tree in multiple locations, the framework will create a separate State object for each location. Similarly, if the widget is removed from the tree and later inserted into the tree again, the framework will call createState again to create a fresh State object, simplifying the lifecycle of State objects.

## RaisedButton

如需将RaisedButton设置为不可点击状态，只需将onPressed属性设置为null即可。