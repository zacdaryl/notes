# 创建一个Framework的步骤

Using Xcode

1. New -> Coco Touch Framework
2. pod init 并引入需要的pod库，之后pod install
3. pod spec create 创建.podspec文件
4. podspec文件修改，需关联git仓库
5. pod spec lint --allow-warnings 校验库是否ok

Using Pod Lib Create： https://guides.cocoapods.org/making/using-pod-lib-create.html

## 将编译好的 *.framework 通过pod管理

上述方法是将仓库源码通过pod方式分发的设置，将打包好的 .framework 文件分发需要这样配置：

```
  # ――― Source Location ―――――――――――――――――――――――――――――――――――――――――――――――――――――――――― #
  #
  #  Specify the location from where the source should be retrieved.
  #  Supports git, hg, bzr, svn and HTTP.
  #

  spec.source       = { :http => "http://ip/*.framework.zip" }
  spec.vendored_frameworks = '依赖的framework路径'


  # ――― Source Code ―――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――― #
  #
  #  CocoaPods is smart about how it includes source code. For source files
  #  giving a folder will include any swift, h, m, mm, c & cpp files.
  #  For header files it will include any header in the folder.
  #  Not including the public_header_files will make all headers public.
  #

  spec.source_files  = "**/*.{h,m,mm}"
```

主要是修改 spec.source，改为http的方式，链接的内容是framework的压缩包

## Framework支持的CPU架构

pod lib lint 检查的时候，需要framework支持全架构，所以需要编译通用的Framework

`lipo -info a.framework/a` 查看a.framework支持的CPU架构

## Podspec 配置说明

[Podspec Syntax Reference ](https://guides.cocoapods.org/syntax/podspec.html#private_header_files)

> vendored_frameworks: The paths of the framework bundles that come shipped with the Pod.

## Framework中class为swift

今天创建了一个Swift的Library，在导入Framework后，总是提示找不到module，原因是需要将swift类设置为public，设置成功后，type method在主工程依然无法访问，于是需要给其添加public访问权限。

这里需要了解下swift的 [Access Control
](https://docs.swift.org/swift-book/LanguageGuide/AccessControl.html)

## zip解压失败

> End-of-central-directory signature not found

这个问题是由于上传到github的zip文件，不能直接copy地址栏中的zip地址，需要在 github zip下载文件页面的download 右键，copy link

thanks：https://medium.com/@alexnagy/if-pod-spec-lintgave-you-an-end-of-central-directory-signature-not-found-error-while-downloading-3a4575fdd65e

## source_files 匹配失败

本来是设置的MyLib，改为 ** 好了

`spec.source_files  = "**/*.{h,swift}"`

以上配置还有个问题就是引用的framework只有头文件，没有二进制的包，由于是使用http引用的是framework的zip文件，所以需要如下配置：

`spec.vendored_frameworks = 'MyLib.framework' //zip file is MyLib.framework.zip` 

## pod repo push 

`pod spec lint --allow-warnings` 成功后，`pod repo push` 时失败，提示 The spec did not pass validation，push时加上 --allow-warnings 成功

## framework not found MyLib

pod spec配置一切就绪之后，使用pod MyLib 引用，pod install 一切正常，但是依然报找不到，Google了半天，有提到查看search path是否配置正确，给忽略了，最后就是search path的问题。Framework Search Path 配置 ${PODS_ROOT}之后大功告成！