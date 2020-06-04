# BasicMessageChannel

作为Flutter新手，使用BasicMessageChannel从Android端发送消息给Flutter，却发现无法收到消息，百思不解，折腾了不少时间，最终看了下FlutterActivity源码才解决，很有必要记录下。

## 场景说明

Flutter以module形式嵌入到现有Android工程中，有一个需求，从Android端开启一个Flutter页面时，将一些基本信息传给Flutter。于是选择使用BasicMessageChannel从Android端向Flutter发送消息。

android端发送

```kotlin
override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
    super.configureFlutterEngine(flutterEngine)

    val bmc = BasicMessageChannel(flutterEngine.dartExecutor.binaryMessenger,
        "basic_msg_channel", StandardMessageCodec.INSTANCE)

    val map = HashMap<String, String>()
    map.put("a", "1")
    map.put("b", "2")

    bmc.send(map)
}
```

Flutter端接收消息

```dart
@override
void initState() {
    super.initState();
    const msgChannel = const BasicMessageChannel('basic_msg_channel',
        StandardMessageCodec());
    msgChannel.setMessageHandler((message) => Future(() async{
        print(message["a"]);
        return "";
    }));
}
```

## 预热

刚开始使用预热模式，在Application中事先创建FlutterEngine并缓存使用，这种情况下可以保证首次打开Flutter页面不出现黑屏。事先预热情况下，上述发送和接收消息都是正常的。

## 非预热

不事先预热，每次启动SubActivity时创建FlutterEngine，此时在Flutter端无法接收到Android端发送的消息，怎么回事？

首先看看FlutterEngine的创建时机，查看FlutterActivity的onCreate方法：

```java
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    switchLaunchThemeForNormalTheme();

    super.onCreate(savedInstanceState);

    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);

    delegate = new FlutterActivityAndFragmentDelegate(this);
    delegate.onAttach(this);
    delegate.onActivityCreated(savedInstanceState);

    configureWindowForTransparency();
    setContentView(createFlutterView());
    configureStatusBarForFullscreenFlutterExperience();
}
```

这里创建了一个FlutterActivityAndFragmentDelegate，继续查看delegate.onAttach(this)方法，会发现在这里创建了FlutterEngine，并在最后回调了configureFlutterEngine方法，于是在子类中可以借助新创建的FlutterEngine创建BasicMessageChannel。

这里比较下和预热模式的区别，onCreate中创建了FlutterEngine，但是没有执行DartEntryPoint，很可能的原因就在于此。继续查看FlutterActivity的onStart方法会发现，里边调用了delegate.onStart()，进而调用了doInitialFlutterViewRun，这个方法是关键，看下源码：

```java
 private void doInitialFlutterViewRun() {
    ……
    // Configure the Dart entrypoint and execute it.
    DartExecutor.DartEntrypoint entrypoint =
        new DartExecutor.DartEntrypoint(
            host.getAppBundlePath(), host.getDartEntrypointFunctionName());
    flutterEngine.getDartExecutor().executeDartEntrypoint(entrypoint);
  }
```

这里忽略了前边的一系列if判断，最后看到这里执行了DartEntrypoint。于是发现Android端在configureFlutterEngine中发送消息时，dart代码并未执行，可能channel通道并未建立起来，所以后来Flutter页面起来后，始终无法接收消息。

于是将Android端发送消息的代码移到子类的onStart方法中，一切正常！