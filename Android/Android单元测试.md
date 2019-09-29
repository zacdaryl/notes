# Android 单元测试

## 官网

https://developer.android.com/studio/test/index.html?hl=zh-cn

## 测试类型及位置

### 本地单元测试

module-name/src/test/java/

### 仪器测试

依赖Android API，需要在真机或者模拟器上运行

module-name/src/androidTest/java/

## 问题

运行在真机上的测试总是报错：junit.framework.AssertionFailedError，最后究其原因是以为build.gradle配置错误，正确配置如下：

```
defaultConfig {
    testInstrumentationRunner 'androidx.test.runner.AndroidJUnitRunner'
    ...
}

dependencies {
    testImplementation 'junit:junit:4.12'

    // Required for instrumented tests
    androidTestImplementation 'androidx.annotation:annotation:1.1.0'
    androidTestImplementation 'androidx.test:runner:1.2.0'
    ...
}
```