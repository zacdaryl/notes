# issues

## AIDL

项目开发过程中遇到两个关于AIDL的问题：
1. 在aidl文件中插入方法，导致方法错乱，调用失败
2. client和server的aidl版本不一致，client调用了一个服务端不存在的方法，结果返回null

通过查看编译生成的Stub类可知，aidl接口中方法调用其实是和其在.aidl文件中的顺序有关的，若中间插入方法，就导致其后的方法都会调用失败，于是aidl中新加方法最好是加在最后，否则必须维护client和server端aidl版本的一致。

```java
static final int TRANSACTION_basicTypes = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getVersion = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
```

android 10之后，提供了稳定aidl的支持，可以设置默认实现，比如新加的方法，服务端没有实现，则客户端可以调用默认实现的方法

```kotlin
if(ITAInterface.Stub.getDefaultImpl() == null) {
    ITAInterface.Stub.setDefaultImpl(object : ITAInterface.Default() {
        override fun getName(): String {
            return "defname"
        }
    })
}
```

## Chip

更换背景色

## notifyItemChanged

使用notifyItemChanged局部刷新时，发现会重新创建一个新的ViewHolder，并且交替使用，导致数据错乱。[stackoverflow](https://stackoverflow.com/questions/30667014/why-recyclerview-notifyitemchanged-will-create-a-new-viewholder-and-use-both-t/31787795#31787795)有讨论这个问题，是因为ItemAnimator导致的。

> RecyclerView use both of ViewHolder for smooth animation from an old state to a new. This is default behaviour of RecyclerView.ItemAnimator.
> You can disable animation by passing an empty item animator to RecyclerView:
> `listView.setItemAnimator(null);`