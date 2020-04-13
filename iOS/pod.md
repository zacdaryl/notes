# Cocoapods

## pod install error

> Specs satisfying the `` dependency were found, but they required a higher minimum deployment target.

执行pod install命令时，总是提示这个错误，分析了一下原因，因为 .podspec文件中的版本号设置的有点高, 将platform、deployment_target 从10.0改为9.0后，命令执行正常

```
s.platform     = :ios, "9.0"

#  When using multiple platforms
s.ios.deployment_target = "9.0"
```