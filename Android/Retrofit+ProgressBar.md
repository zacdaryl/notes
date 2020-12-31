# 给网络请求添加进度条

使用Retrofit封装好网络请求后，想在发起网络请求时显示个ProgressBar，请求结束时（成功或失败）取消显示。

貌似没什么问题，应该很容易实现，然而并不是如此。首先想到的是用ProgressDialog，但发现在API Level 26此类已经被标记为deprecated, 官网建议使用ProgressBar替代。好吧，那就按照官网的来。

## 如何替换

一般ProgressBar是写在xml里，Activity中拿到控件进行显示和隐藏，但这个是一个网络请求，不依附于特定的Activity，用一个BaseActivity这么实现倒是可以，但是耦合性就比较高了。

想到ProgressBar的显示又离不开Activity的Context，所以在网络请求类中，要保留一个Activity的引用。通过这个引用，动态的给当前发生网络请求的Activity添加ProgressBar。这里附上动态添加的实现：

```java
activity = weakActivity.get();

progressBar = new ProgressBar(activity);

//通过android.R.id.content获取rootview
ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content).getRootView();

//设置progressbar父容器linearlayout
LinearLayout.LayoutParams params = new
        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);

LinearLayout linearLayout = new LinearLayout(activity);

//保证progressbar居中显示
linearLayout.setGravity(Gravity.CENTER);
linearLayout.addView(progressBar);
rootView.addView(linearLayout,params);

```

## 模态控制

ProgressDialog弹出时，是一个模态对话框，此时用户无法和页面元素交互，但用progressbar，如何保证显示时的不可点击状态？于是就有下边的解决方案：

```java
activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
```

在网络请求结束后，再设置为可点击状态即可。

```java
activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
```

