# OkHttp研究

曾经被问过是否看过OkHttp的源码，很惭愧，之前总是停留在使用阶段，很少研究源码。今天抽空看了一下OkHttp的请求调度，了解到其内部是如何对连续请求进行调度的。

这篇文章分析的不错 [彻底理解OkHttp - OkHttp 源码解析及OkHttp的设计思想](https://juejin.im/post/5c1b23b9e51d4529096aaaee)

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

## 拦截器

需求要修改并转换http请求内容，做到业务层代码零侵入，这样的场景自然是使用okhttp的拦截器来实现。

这里主要记录下从拦截器获取一个请求的所有参数，初看RequestBody对象，只能获取到contentType，contentLength，如何获取到具体的参数内容呢？content只是在create的时候传入，没有明确的get方法获取，在这个地方纠结了许久，writeTo方法倒是将内容写入BufferedSink对象中，直观感觉还是无法获取。Google后发现有人说查看okhttp的logging拦截器如何实现打印请求报文的，是个思路，于是查看HttpLoggingInterceptor源码，发现关键就是用writeTo来实现。

```
val buffer = Buffer()
requestBody.writeTo(buffer)

val contentType = requestBody.contentType()
val charset: Charset = contentType?.charset(UTF_8) ?: UTF_8

logger.log("")
if (buffer.isProbablyUtf8()) {
logger.log(buffer.readString(charset))
logger.log("--> END ${request.method} (${requestBody.contentLength()}-byte body)")
} else {
logger.log(
"--> END ${request.method} (binary ${requestBody.contentLength()}-byte body omitted)")
}
```
通过源码，一目了然，它是使用writeTo把内容存入Buffer对象中，然后通过buffer的read方法，把内容又读出来打印。

于是，拦截器中要获取请求体中的全部内容，就要借助RequestBody的writeTo方法，将内容存入Buffer，然后再从Buffer中获取内容。

