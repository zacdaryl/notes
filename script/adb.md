# adb

## apksigner

https://developer.android.com/studio/command-line/apksigner?hl=zh-cn

验证签名

```
apksigner verify app.apk
```

签名

```
apksigner sign --key release.pk8 --cert release.x509.pem app.apk
```

## wm

修改设备分辨率

`adb shell wm size 1280x720`

恢复

`adb shell wm reset`

多个设备进入shell模式

```
adb devices
adb -s xxx(serial) shell
```

## 删除系统应用
如果AndroidManifest中sharedUserId配置为：android.uid.system，则说明是个系统应用，删除此应用用如下命令：

`adb shell pm uninstall --user 0 your.package.name`
