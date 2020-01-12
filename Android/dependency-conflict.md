# 依赖冲突

> DuplicateRelativeFileException: More than one file was found with OS independent path: 'lib/armabi-v7a/your-so-name.so'

今天发现此怪异问题，一时想不出来到底哪里重复引用了so库，一番搜索后，发现配置 packagingOptions 可以解决问题。

```
packagingOptions {
   pickFirst 'lib/armabi-v7a/your-so-name.so' 
}
```

虽然这个方法能保证不报错，但是还是没有搞明白什么原因导致的so重复。继续排查发现，还有一个第三方库引用了包含此so库的aar，也就是说包含so库的library被引用了两次，于是思路清晰了，还是依赖冲突的问题。

那么当发现项目中有第三方库依赖冲突时，如何知道谁依赖了它呢？

在工程目录中执行命令：`./gradlew :[主工程module]:dependencies`, 可以列出依赖的树形结构（如下），于是查找冲突的库都被哪些库依赖，就显得很方便了。

```
+--- com.github.bumptech.glide:glide:4.9.0
|    +--- com.github.bumptech.glide:gifdecoder:4.9.0
|    |    \--- androidx.annotation:annotation:1.0.0 -> 1.1.0
|    +--- com.github.bumptech.glide:disklrucache:4.9.0
|    +--- com.github.bumptech.glide:annotations:4.9.0
|    +--- androidx.fragment:fragment:1.0.0 -> 1.1.0-rc01 (*)
|    \--- androidx.vectordrawable:vectordrawable-animated:1.0.0 -> 1.1.0-rc01 (*)

```

另外，`gradle androidDependencies` 只是列出来依赖库，但并不能明显的查看第三方库又依赖了哪些库。

找到对应的依赖库，排除其依赖冲突，使用 exclude

```
implementation ('***') {
   exclude group: '***.***.***'
}
```