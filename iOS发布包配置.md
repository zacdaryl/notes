# iOS发布包配置

> 发布到AppStore的包和开发包的Bundle Identifier不同，之前使用脚本在打包时修改，使用xcconfig配置更加便捷

## 配置Bundle Id

新建Config-Distribution.xcconfig文件，在其中配置发布的BUNDLE_ID

```
BUNDLE_ID = com.company.xxx
```

## Provisioning Profile

Project-Info-Configurations中Duplicate一个新的配置后，在Targets-General中选择对应的provision配置，BuildSetting中的Signing配置要选择相应BUNDLE_ID的设置。

## info.plist

配置文件中，Bundle Identifier设置为 $(BUNDLE_ID)