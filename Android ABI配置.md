# 踩坑涨知识

## 背景

> 今天集成一个自己的so库，一番折腾之后，终于顺利build，然而这并没有结束，run起来之后，发现weex页面无法打开。奇了个怪了，之前好好的，怎么突然这样了？

刚开始编译错误，报Okhttp3需要使用java 8，还以为是这个问题，但改为java7后，问题依旧。差点走在了okhttp方向上的不归路，本想降低版本，怕是那个地方有冲突，现在看来，当时是想多了。

解决问题，就要回归问题本身来，Google Weex页面加载失败的Exception：

erroecode -1001 degradeToH5|createInstance fail|wx_create_instance_error isJSFrameworkInit==false reInitCount == 1

github上有对应 [issue](https://github.com/apache/incubator-weex/issues/1596)，于是感觉是ABI的原因导致的，查看apk包，发现包含weex so库的ABI只有：armeabi、armeabi-v7a、x86，而自己的so库包含全部的ABI，原因清楚了，于是过滤下ABI即可：

```gradle
android {
    defaultConfig {
        ndk {
            abiFilters "armeabi", "armeabi-v7a", "x86"
        }
    }
}

```

[官方ABI管理指南](https://developer.android.com/ndk/guides/abis)