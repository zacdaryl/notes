# 状态栏以及应用栏设置

> UI设计为沉浸式状态栏，即状态栏的颜色和应用栏的颜色一致，看起来是一个整体

这次需求，主要有两种状态，一是无应用栏，状态栏透明；二是有应用栏，背景色为白色，状态栏需要白底黑字

## 状态栏透明

这种模式常见于首页，将应用栏隐藏掉，状态栏设置为透明，页面内容整体上移，状态栏和页面一体化显示。实现方法如下：

```java
public static void setTransparent(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }
}
```

## 白底黑字

这种情况下，有应用栏，且背景为白色，要想达到一体化效果，需要将状态栏设置为白底黑字，即light模式，实现方法如下：

```java
public static void setLight(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        activity.getWindow().setStatusBarColor(Color.WHITE);
    }
}
```

## 状态栏高度

```java
private static int getStatusBarHeight(Context context) {
    // 获得状态栏高度
    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
    return context.getResources().getDimensionPixelSize(resourceId);
}
```

## 应用栏

为了和iOS风格保持一致，默认的Android ActionBar的title无法满足要求，首先想到的是自定义ActionBar，结果自定义的结果是，view的两端总是有空白，自定义的view不能达到屏幕的宽度，好一番折腾才得以解决。

使用自定义view的设置：

```
ActionBar actionBar = getSupportActionBar();
actionBar.setDisplayShowCustomEnabled(true);
actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
actionBar.setCustomView(R.layout.your_custom_layout);
```

为了保证自定义view能达到屏幕的宽度，需要在主题中进行如下设置：

```xml
<style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
    <item name="actionBarStyle">@style/ActionBarStyle</item>
    <item name="actionBarSize">45dp</item>
</style>

<style name="ActionBarStyle" parent="@style/Widget.AppCompat.Light.ActionBar.Solid">
    <!--解决边距的问题 contentInsetStart = 0 -->
    <item name="contentInsetStart">0dp</item>  
    <item name="contentInsetEnd">0dp</item>    
</style>
```

## 渐变色

StatusBar背景设置为渐变色，总体思路是拿到statusbar对应的view，然后设置background即可。但是在onCreate里无法获取statusbar对象，需要延迟处理，以下为解决方案：

```java
//statusbar绘制完成，获取statusbar对象
Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
    @Override
    public boolean queueIdle() {
        if (statusBarView == null) {
            //获取statusbar对象
            int identifier = getResources().getIdentifier("statusBarBackground", "id", "android");
            statusBarView = getWindow().findViewById(identifier);

            //decorview布局发生改变时，设置statusbar背景色
            getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    statusBarView.setBackgroundResource(R.drawable.your_bg_xml);
                }
            });
        }
        //保证只获取一次
        return false;
    }
});
```

xml渐变色背景

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <gradient android:type="linear"
        android:useLevel="true"
        android:startColor="#ff41c0ff"
        android:endColor="#ff2372dd"
        android:angle="180" />
</shape>
```

附上官方说的用Toolbar替换ActionBar的说明：[设置应用栏](https://developer.android.com/training/appbar/setting-up?hl=zh-cn)

