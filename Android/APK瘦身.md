# APK瘦身

官方有一篇文档详细说明了如何缩小app，详细可参考 [Shrink your app](https://developer.android.com/studio/build/shrink-code)，今天试了一下WebP格式图片，效果不错。

## 图片格式转换为WebP

在AndroidStudio中res目录，右键->Convert to WebP，可以将此目录中的图片转换为WebP格式，由于自己项目中图片数目过多，转换后，对比观测apk总体小了4M左右。

| 打包模式 | 转换前 | 转换后 | 缩小  |
| -------- | ------ | ------ | ----- |
| debug    | 75.2MB | 71.1MB | 4.1MB |
| release  | 64MB   | 60.5MB | 3.5MB |




关于WebP，以下是简单介绍，参考 [这里](https://developers.google.com/speed/webp)和[维基百科](https://zh.wikipedia.org/wiki/WebP)

> WebP is a modern image format that provides superior lossless and lossy compression for images on the web. Using WebP, webmasters and web developers can create smaller, richer images that make the web faster.
> 
> WebP lossless images are 26% smaller in size compared to PNGs. WebP lossy images are 25-34% smaller than comparable JPEG images at equivalent SSIM quality index.

## x86架构so库

市面上目前基本没有x86架构的手机，移除项目中对应的so库，apk缩小了十几M，效果明显

[Android ABI](https://developer.android.com/ndk/guides/abis?hl=zh-cn) 详细介绍了ABI相关内容，armeabi已经在NDK r17移除，armeabi-v7a兼容armeabi，所以项目中仅配置armeabi-v7a即可

```
 defaultConfig {
    ……
    ndk {
        abiFilters "armeabi-v7a"
    }
}
```