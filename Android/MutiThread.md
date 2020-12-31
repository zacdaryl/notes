# MutiThread

Android应用启动之后，默认情况下，同一个应用的所有组件均在相同的进程中运行，如果需要改变组件运行的进程，可以在清单文件中使用android:process属性。多进程通信，以后再另作研究，本文主要记录下Android下的多线程编程。

官方[这篇文章](https://developer.android.com/guide/components/processes-and-threads)，[还有这篇](https://developer.android.com/topic/performance/threads)写的不错，可以对Android多线程有一个大致的理解。

## 线程

当一个应用程序被启动时，系统会为该应用程序创建一个执行线程，称为 "main"。这个线程非常重要，因为它负责将事件派遣到相应的用户界面部件，包括绘图事件。它也几乎总是你的应用程序与Android UI工具包中的组件（android.widget和android.view包中的组件）交互的线程。因此，主线程有时也被称为UI线程。

主线如果阻塞大约5秒钟，则著名的ANR对话框便会出现，一般情况下，耗时的任务需要在另外的线程中处理。

## 工作线程

为了不阻塞主线程，需要在单独的线程中执行耗时任务，即在工作线程中运行。Android中操作线程执行任务的常用方法有如下几种：

1. Handler+Thread
2. HandlerThread
3. AsyncTask
4. IntentService
5. 线程池

## Handler + Thread

在主线程中创建Handler对象的实例，同时创建新的线程执行耗时任务，任务执行完成，通过handler发送消息到消息队列，然后主线程丛消息队列中获取消息并处理，于是在主线程中更新UI。

通常情况下，我们会在主线程实例化一个Handler的匿名内部类来处理消息

```java
handler = new Handler() {
    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        textView.setText((String)msg.obj);
    }
};
```

创建新的线程执行耗时任务，并通过handler发送消息到主线程

```Java
new Thread(() -> {
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    Message message = handler.obtainMessage();
    message.what = 1;
    message.obj = "hello";
    handler.sendMessage(message);
}).start();
```

这种方式最大的弊端就是容易造成内存泄漏，因为匿名内部类默认持有外部类的引用，这样在工作线程还没有执行完成，activity需要销毁的时候，GC就无法回收此activity实例。

解决此问题的方法是，使用静态内部类+弱引用的方式。

```java
private static class MyHandler extends Handler {
    private WeakReference<HandleJavaActivity> ref;

    public MyHandler(HandleJavaActivity activity) {
        ref = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        ref.get().textView.setText((String)msg.obj);
    }
}
```

## HandlerThread

> A `Thread` that has a `Looper`. The `Looper` can then be used to create `Handler`s.

HandlerThread是一个拥有Looper的线程，内部已经实现了Looper循环，通过其Looper对象新建一个Handler，可以向该线程发送消息，线程内部从MessageQueue中获取消息并执行。

看下HandlerThread内部实现的loop循环

```java
@Override
public void run() {
    mTid = Process.myTid();
    Looper.prepare(); //initialize Looper
    synchronized (this) {
        mLooper = Looper.myLooper();
        notifyAll();
    }
    Process.setThreadPriority(mPriority); // 设置线程运行优先级
    onLooperPrepared();
    Looper.loop(); // start loop, run the message queue in this thread.
    mTid = -1;
}
```

举个例子，看看如何使用HandlerThread，从主线程向HandlerThread发送消息，在HandlerThread中取出消息并逐个处理。

```java
//主线程中创建HandlerThread并运行，MyHandlerThread继承了HandlerThread，也可不继承
myHandlerThread = new MyHandlerThread("myHandlerThread");
myHandlerThread.start();

//使用HandlerThread的looper创建handler，以便向线程发送消息
mThreadHandler = new MyThreadHandler(myHandlerThread.getLooper());

for (int i = 0; i < 10; i++) {
    Message msg = mThreadHandler.obtainMessage();
    msg.obj = "message <" + i + "> from main thread";
    mThreadHandler.sendMessage(msg); //向HandlerThread发送消息
}
```

```java
private static class MyThreadHandler extends Handler {
    public MyThreadHandler(@NonNull Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);

      	//模拟耗时操作
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
				//确认消息是在工作线程处理
        Log.d("handler", "handle message on thread: " + Thread.currentThread().getName() +
                "\n message: " + msg.obj);
    }
}
```

## AsyncTask

> AsyncTask is designed to be a helper class around `Thread` and `Handler` and does not constitute a generic threading framework. AsyncTasks should ideally be used for short operations (a few seconds at the most.) If you need to keep threads running for long periods of time, it is highly recommended you use the various APIs provided by the `java.util.concurrent` package such as `Executor`, `ThreadPoolExecutor` and `FutureTask`.

AsyncTask是一个Handler和Thread的帮助类，简化了线程的创建和消息传递的处理，doInBackground回调中的代码在子线程中执行，onPostExecute中获取返回结果，在主线程中处理UI逻辑。

官方不建议在AsyncTask中执行长时间的操作，最多几秒，API 30中已经将其标记为**deprecated**，不推荐使用。其潜在的问题，官方也给了比较详细的说明：

> syncTask was intended to enable proper and easy use of the UI thread. However, the most common use case was for integrating into UI, and that would cause Context leaks, missed callbacks, or crashes on configuration changes. It also has inconsistent behavior on different versions of the platform, swallows exceptions from doInBackground, and does not provide much utility over using Executors directly.

> 对于需要快速将工作从主线程移动到工作线程的应用来说，`AsyncTask` 类是一个简单实用的基元。例如，输入事件可能会触发使用加载的位图更新界面的需求。`AsyncTask` 对象可以将位图加载和解码分流到备用线程；处理完成后，`AsyncTask` 对象可以设法回到主线程上接收工作以更新界面。
>
> 在使用 `AsyncTask` 时，请注意以下几个性能方面的要点。首先，默认情况下，应用会将其创建的所有 `AsyncTask` 对象推送到单个线程中。因此，它们按顺序执行，而且与主线程一样，特别长的工作数据包可能会阻塞队列。鉴于这个原因，我们建议您仅使用 `AsyncTask` 处理持续时间短于 5ms 的工作项。

##IntentService

IntentService通过服务启动一个HandlerThread来做工作线程，并将收到的intent发送给HandlerThread逐个执行，即边多次通过startService启动服务，也只有一个HandlerThread工作线程处理intent。

使用IntentService需要实现onHandleIntent方法，该方法在工作线程中处理intent

下面通过源码看看IntentService是如何工作的

```java
@Override
public void onCreate() {
    // TODO: It would be nice to have an option to hold a partial wakelock
    // during processing, and to have a static startService(Context, Intent)
    // method that would launch the service & hand off a wakelock.

    super.onCreate();
    HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");
    thread.start();

    mServiceLooper = thread.getLooper();
    mServiceHandler = new ServiceHandler(mServiceLooper);
}
```

IntentService创建时，新建一个HandlerThread并运行，其实就是开启了线程内的消息循环，等待处理消息。紧接着又创建了一个ServiceHandler对象负责处理intent，看下ServiceHandler的实现

```java
private final class ServiceHandler extends Handler {
    public ServiceHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        onHandleIntent((Intent)msg.obj);
        stopSelf(msg.arg1);
    }
}
```

ServiceHandler获取消息后，直接调用onHandleIntent去处理intent。那么intent是什么时候传给工作线程的呢，看下IntentService的onStart方法就明白了

```java
public void onStart(@Nullable Intent intent, int startId) {
    Message msg = mServiceHandler.obtainMessage();
    msg.arg1 = startId;
    msg.obj = intent;
    mServiceHandler.sendMessage(msg);
}
```

可见在onstart方法中，mServiceHandler将intent通过消息发送给工作线程去处理，这也说明多个startService方法调用，其intent是按顺序在一个HandlerThread中执行的。

## 线程池

手动创建和管理线程会显得比较繁琐，并且创建线程并不是免费的，它们会占用内存，每个线程至少占用 64k 内存。使用线程池可以有效的管理线程的创建和销毁，并且复用创建好的线程。

> `ThreadPoolExecutor` 是一个可简化此过程的辅助类。这个类可用于管理一组线程的创建，设置其优先级，并管理工作在这些线程之间的分布情况。随着工作负载的增减，该类会创建或销毁更多线程以适应工作负载。

```java
ThreadPoolExecutor(
  //the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set
  int corePoolSize,  
  //the maximum number of threads to allow in the pool
  int maximumPoolSize,
  /**
  when the number of threads is greater than the core, this is the maximum time that excess idle threads will 	wait for new tasks before terminating.
  */
  long keepAliveTime, 
  //the time unit for the keepAliveTime argument
  TimeUnit unit, 
  //the queue to use for holding tasks before they are executed. This queue will hold only the Runnable tasks 	submitted by the execute method.
  BlockingQueue<Runnable> workQueue
)
```

对ThreadPoolExecutor有了大致的了解后，看看Executors提供的几个创建线程池的便捷方法

### newCachedThreadPool

> Creates a thread pool that creates new threads as needed, but will reuse previously constructed threads when they are available. These pools will typically improve the performance of programs that execute many short-lived asynchronous tasks. Calls to `execute` will reuse previously constructed threads if available. If no existing thread is available, a new thread will be created and added to the pool. Threads that have not been used for sixty seconds are terminated and removed from the cache. Thus, a pool that remains idle for long enough will not consume any resources.

该线程池在需要的时候会新建线程来处理任务，并且会重复使用已经创建好的空闲线程，适合执行许多耗时不太长的异步任务，如果线程空闲超过60s，则会被销毁。所以这个线程池如果长时间处于空闲状态，不会消耗资源。下面看下这个线程池是如何创建的：

```java
public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(
      0, //核心线程数0，保证空闲线程都可被销毁
      Integer.MAX_VALUE, //最大线程数量为Int的最大取值，可以理解成在需要时可无限创建线程 
      60L, //空闲线程等待任务的时间，超过60s就销毁
      TimeUnit.SECONDS,
			new SynchronousQueue<Runnable>()
    );
}
```

### newFixedThreadPool

> Creates a thread pool that reuses a fixed number of threads operating off a shared unbounded queue. At any point, at most `nThreads` threads will be active processing tasks. If additional tasks are submitted when all threads are active, they will wait in the queue until a thread is available. If any thread terminates due to a failure during execution prior to shutdown, a new one will take its place if needed to execute subsequent tasks. The threads in the pool will exist until it is explicitly `ExecutorService#shutdown`.

创建固定数量线程的线程池，线程最大数量nThreads，如果线程全部处于活跃状态，新提交的任务将在队列中等待，直到有线程闲下来可用。如果中间有线程被销毁，则会新建线程执行后续的任务。

```java
public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(
      nThreads, nThreads, //核心线程数和最大线程数相同，说明此线程池中的线程创建后一般不会被销毁
			0L, //设置为0，线程不会在空闲时候自动销毁
      TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>());
}
```

### newScheduledThreadPool

>  Creates a thread pool that can schedule commands to run after a given delay, or to execute periodically.

该线程池可以延迟执行任务，或者周期性的执行任务。

```java
public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
    return new ScheduledThreadPoolExecutor(corePoolSize);
}

public ScheduledThreadPoolExecutor(int corePoolSize) {
        super( //super 即父类 ThreadPoolExecutor
          corePoolSize, //设置核心线程数量
          Integer.MAX_VALUE, //可简单理解为无限创建线程
          DEFAULT_KEEPALIVE_MILLIS, //10L，默认空闲线程等待时间10ms
          MILLISECONDS, //等待时间单位，毫秒
         	new DelayedWorkQueue()
        );
}
```

延迟或周期性执行任务，可以使用ScheduledExecutorService的以下方法：

> `schedule(Runnable command, long delay, TimeUnit unit)`
>
> Creates and executes a one-shot action that becomes enabled after the given delay.
>
> `scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)`
>
> Creates and executes a periodic action that becomes enabled first after the given initial delay, and subsequently with the given period; that is, executions will commence after `initialDelay`, then `initialDelay + period`, then `initialDelay + 2 * period`, and so on.

###newSingleThreadExecutor

> Creates an Executor that uses a single worker thread operating off an unbounded queue. (Note however that if this single thread terminates due to a failure during execution prior to shutdown, a new one will take its place if needed to execute subsequent tasks.) Tasks are guaranteed to execute sequentially, and no more than one task will be active at any given time. Unlike the otherwise equivalent `newFixedThreadPool(1)` the returned executor is guaranteed not to be reconfigurable to use additional threads.

这个线程执行器只使用一个工作线程执行任务，保证队列中的任务按顺序执行，

