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