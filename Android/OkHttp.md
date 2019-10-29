# OkHttp研究

曾经被问过是否看过OkHttp的源码，很惭愧，之前总是停留在使用阶段，很少研究源码。今天抽空看了一下OkHttp的请求调度，了解到其内部是如何对连续请求进行调度的。


## 请求调度

主要参考了这篇文章：[OkHttp源码之线程调度](https://www.jianshu.com/p/5b197bcd83c0)

从`Dispatcher`的`enqueue`作为入口开始查看，一步步的就比较好理解其调度原理了

```kotlin
internal fun enqueue(call: AsyncCall) {
    synchronized(this) {
      readyAsyncCalls.add(call)

      // Mutate the AsyncCall so that it shares the AtomicInteger of an existing running call to
      // the same host.
      if (!call.get().forWebSocket) {
        val existingCall = findExistingCallWithHost(call.host())
        if (existingCall != null) call.reuseCallsPerHostFrom(existingCall)
      }
    }
    promoteAndExecute() //主要是这个方法
}

/**
   * Promotes eligible calls from [readyAsyncCalls] to [runningAsyncCalls] and runs them on the
   * executor service. Must not be called with synchronization because executing calls can call
   * into user code.
   *
   * @return true if the dispatcher is currently running calls.
   */
  private fun promoteAndExecute(): Boolean {
    assert(!Thread.holdsLock(this))

    val executableCalls = mutableListOf<AsyncCall>()
    val isRunning: Boolean
    synchronized(this) {
      val i = readyAsyncCalls.iterator()
      while (i.hasNext()) {
        val asyncCall = i.next()

        if (runningAsyncCalls.size >= this.maxRequests) break // Max capacity.
        if (asyncCall.callsPerHost().get() >= this.maxRequestsPerHost) continue // Host max capacity.

        i.remove()
        asyncCall.callsPerHost().incrementAndGet()
        executableCalls.add(asyncCall)
        runningAsyncCalls.add(asyncCall)
      }
      isRunning = runningCallsCount() > 0
    }

    for (i in 0 until executableCalls.size) {
      val asyncCall = executableCalls[i]
      asyncCall.executeOn(executorService)
    }

    return isRunning
  }
```

主要的三个队列：

```kotlin
 /** Ready async calls in the order they'll be run. */
  private val readyAsyncCalls = ArrayDeque<AsyncCall>()

  /** Running asynchronous calls. Includes canceled calls that haven't finished yet. */
  private val runningAsyncCalls = ArrayDeque<AsyncCall>()

  /** Running synchronous calls. Includes canceled calls that haven't finished yet. */
  private val runningSyncCalls = ArrayDeque<RealCall>()
```

大致流程如下：
1. 请求过来是先加入readyAsyncCalls
2. 从readyAsyncCalls队列中取出请求，放入runningAsyncCalls中执行
3. 请求结束后，将其从runningAsyncCalls队列中移除