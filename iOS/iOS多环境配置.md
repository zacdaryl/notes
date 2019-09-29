# iOS 多环境配置

## 背景

> 开发过程中，后台存在测试环境、生产发布环境、生产环境，常规方法是通过宏定义区分每个环境，但是这样的弊端就是在代码中要加入很多类似的环境判断，十分的不雅，并且还难以修改。有没有一种更好的方式呢？万事不决问Google，解决方法还是有的。

## .xcconfig文件

搜索一番后，xcconfig文件出现在了我的视线中，距离成功感觉只剩下一步之遥了。然而事情并没有那么顺利，坑在那等着呢。

首先 CMD + N 选择 Configuration Settings File 创建 .xcconfig文件，在文件中加入key=value配置即可。此时遇到第一个坑，url字符转义的问题。

```
BASE_URL = @"http://10.5.1.13:8080/xxx/" 
```
这样写的BASE_URL看着一切正常，然而build一下就报错。只有http，后边的内容都被吃掉了。添加转移后 ‘\\/\\/’，打印出的内容还有'\\'，不达目的不罢休，继续搜，于是下边的写法才是想要的结果。

```
BASE_URL = @"http:/$()/10.5.1.13:8080/xxx/" 
```

这样就大功告成了么，并没有，代码里怎么用这个BASE_URL？看一个哥们的文章说是这个就是宏定义，在代码中可以直接使用，可我直接引用就会提示找不到变量

## 宏定义

为了解决宏定义的问题，需要重新写一个配置文件，做好设置才行，于是新建一个Config-Base.xcconfig:

```
GCC_PREPROCESSOR_DEFINITIONS = $(inherited) BASE_URL='$(KC_BASE_URL)' BASE_WEEX_URL='$(BASE_WEEX_URL)'
```

然后在第一次创建的Config-Local.xcconfig中引用Base即可

```
#include "Config-Base.xcconfig"
BASE_URL = @"http:/$()/10.5.1.13:8080/xxx/" 
BASE_WEEX_URL = @"http:/$()/10.5.1.13:8080/xxx/" 
```

## 添加配置

在Pooject -> Info -> Configurations 中，copy默认的Debug配置，关联上自定义的配置文件即可

![](assets/xcconfig.png)

到此，代码中直接使用宏定义，run时选择对应的Scheme即可修改对应的url地址