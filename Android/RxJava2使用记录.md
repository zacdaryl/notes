# RxJava2 使用笔记

首先贴一下github地址[RxJava](https://github.com/ReactiveX/RxJava)，基本使用就不多说了，官方例子都有说明。

## 错误处理

今天使用的过程中，遇到一新问题，就是从Room数据库查询User记录，如果数据库中没有数据，则Room会报错，对应到RxJava的地方却不知道该如何处理，保证出错的情况下，后续链式操作可以继续进行。

最后认真看了下官方文档 [Error Handling Operators](https://github.com/ReactiveX/RxJava/wiki/Error-Handling-Operators#onerrorresumenext), 大概了解了怎么在错误的时候，进行后续的链式操作

Room中数据库操作：

```java
@Query("SELECT * FROM user LIMIT 1")
Single<User> getUser();
```

使用RxJava访问数据库及异常处理：

```java
database.userDao().getUser()
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .onErrorResumeNext(new Function<Throwable, SingleSource<? extends User>>() {
        @Override
        public SingleSource<? extends User> apply(Throwable throwable) throws Exception {
            //如果数据库中没有记录，会走到这个地方，吞掉异常信息，重新构建一个Single对象，传递出去
            User user = new User();
            return Single.just(user);
        }
    })
    .subscribe(new Consumer<User>() {
        @Override
        public void accept(User user) throws Exception {
            //这里做个判断，如果获取不到id，说明是异常情况下返回的user对象，有数据则会返回正常的user实例
            if (!TextUtils.isEmpty(user.getId())) {
                ...
            }

            //正常的后续处理
            ...
        }
    });
```

[Room+RxJava](https://developer.android.com/training/data-storage/room/accessing-data.html#query-rxjava)

总结：如果不使用onErrorResumeNext，则在发生错误时，subscribe中方法不会执行