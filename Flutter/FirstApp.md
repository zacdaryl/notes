# 第一个 Flutter App 开发日志

了解Flutter有一段时间了，也run了几个Demo，效果确实不错。于是将自用的一个简单的Android App用Flutter重写了一下，就可以同时在Android、iOS设备上使用了。

先介绍下这个简单App的功能，从url获取数据后，解析html，然后展示一个List，点击每个item后，跳转到浏览器，打开对应的url。功能很简单，这里主要记录下开发过程中重新认识或者新学到的一些内容。

## 网络

网络请求直接参考 Cookbook 的[Fetch data from the internet](https://flutter.dev/docs/cookbook/networking/fetch-data)部分。比较有意思的是添加http依赖时，发现其版本号和平时理解的语义化版本管理不太一样，格式如下：

```
version: 1.0.0+1
```

`+`后边的数字代表应用的 build number，对应Android的 versionCode，iOS的 CFBundleVersion

`pubspec.yaml`中给出了详细的注释

```
# The following defines the version and build number for your application.
# A version number is three numbers separated by dots, like 1.2.43
# followed by an optional build number separated by a +.
# Both the version and the builder number may be overridden in flutter
# build by specifying --build-name and --build-number, respectively.
# In Android, build-name is used as versionName while build-number used as versionCode.
# Read more about Android versioning at https://developer.android.com/studio/publish/versioning
# In iOS, build-name is used as CFBundleShortVersionString while build-number used as CFBundleVersion.
# Read more about iOS versioning at
# https://developer.apple.com/library/archive/documentation/General/Reference/InfoPlistKeyReference/Articles/CoreFoundationKeys.html
```

异步请求，使用到了 Future 和 FutureBuilder，Future负责发送请求，FutureBuilder中获取返回的数据并使用。

## 解析Html

get请求直接拿到html格式的数据，这就需要一个html解析器，按照[html](https://pub.dev/packages/html)的说明，很容易就可以把返回的html内容解析成一个Document对象。

本来以为很顺利，结果还是会有问题找上门，对于utf8编码，提示不支持，Google了一番，发现 dart:convert libaray的 [Utf8Codec](https://api.dartlang.org/stable/2.7.0/dart-convert/Utf8Codec-class.html)可以完美解决。

```dart
Utf8Codec utf8 = Utf8Codec(allowMalformed: true);
String utf8Body = utf8.decode(response.bodyBytes, allowMalformed: true);
```

## 正则

用正则和List的where方法筛选出满足条件的数据

```dart
RegExp exp = RegExp(r"_\d.\d.\d");
var verList =
    aTagList.where((t) => exp.hasMatch(t.innerHtml)).toList();
```

## WebView

在Flutter中如何打开一个Native的WebView呢? 使用 [webview_flutter](https://pub.dev/packages/webview_flutter)插件，封装成一个Widget后，可以直接使用。

```dart
import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';

class FlutterWebView extends StatelessWidget {
  const FlutterWebView({Key key, this.url, this.title}) : super(key: key);

  final String url;
  final String title;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
            title: Text(title),
        ),
        body: WebView(
            initialUrl: url,
            javascriptMode: JavascriptMode.unrestricted,
        ),
    );
  }
}
```

## 打开浏览器

本来解析出来的url在WebView中展示正常，但是由于页面中的链接需要下载内容，于是就转而直接使用浏览器直接打开url。Flutter中打开浏览器显示一个页面，使用插件[url_launcher](https://pub.dev/packages/url_launcher)相当方便。url_launcher不仅提供了打开网页的功能，还可以创建邮件、打电话、发短信等。

```dart
_launchURL() async {
  const url = 'https://flutter.dev';
  if (await canLaunch(url)) {
    await launch(url);
  } else {
    throw 'Could not launch $url';
  }
}
```

## build结果

开发完成，手机运行ok后，想知道最终生成的apk或ipa在什么地方，根据直接直接去build目录查找。在 `build/app/outputs/apk/` 目录中找到Android的apk文件，在`build/ios/`目录中发现真机、模拟器对应目录下的Runner.app，不像Android，iOS并不能直接得到ipa文件，需要借助xcode。如果发布iOS App的话，参考：[Build and release an iOS app](https://flutter.dev/docs/deployment/ios)。

## Android启动黑屏

Debug模式下在Android手机上打开Flutter App时，在启动页面和首页显示之间，会有短暂黑屏现象，估计是Flutter引擎初始化的过程，目前网上所说好多都是解决Android自身App启动时白屏问题，这个黑屏问题没发现有效解决办法，不过幸好在Release模式下，不存在此问题。