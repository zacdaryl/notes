# Retrofit

之前使用Retrofit来承接网络请求，根据官网Demo快速上手，使用起来也很方便，特别是使用注解来设置请求参数，比之前手写接口省了不少事。但是用着很爽，总有一个疑问在心头，Retrofit框架是怎么解析注解参数的？又是怎么使用OkHttp发送请求的？现在带着疑问，通过阅读源码来慢慢了解下Retrofit框架内部是如何工作的。

## 创建Retrofit

使用内置的Builder方便快速的组装出一个Retrofit对象

```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build();
```

这里使用了构建者模式来创建对象，那么什么是构建者模式呢？

> The intent of the Builder design pattern is to separate the construction of a complex object from its representation. By doing so the same construction process can create different representations.
> 将一个复杂对象的构建与其表示分离，使得同样的构建过程可以创建不同的表示

简单的理解一下，使用链式调用的方式，一步步设置参数，并组装出一个复杂的对象。

参考链接：https://zhuanlan.zhihu.com/p/58093669

## 定义接口，并创建对应的Service

```java
public interface GitHubService {
  @GET("users/{user}/repos")
  Call<List<Repo>> listRepos(@Path("user") String user);
}

GitHubService service = retrofit.create(GitHubService.class);
```

我们只定义了一个接口GitHubService，那么create方法如何创建实现类的呢？进入create方法里边看看都做了什么操作。

```java
 public <T> T create(final Class<T> service) {
    Utils.validateServiceInterface(service);
    if (validateEagerly) {
      eagerlyValidateMethods(service);
    }
    return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
        new InvocationHandler() {
          private final Platform platform = Platform.get();
          private final Object[] emptyArgs = new Object[0];

          @Override public @Nullable Object invoke(Object proxy, Method method,
              @Nullable Object[] args) throws Throwable {
            // If the method is a method from Object then defer to normal invocation.
            if (method.getDeclaringClass() == Object.class) {
              return method.invoke(this, args);
            }
            if (platform.isDefaultMethod(method)) {
              return platform.invokeDefaultMethod(method, service, proxy, args);
            }
            return loadServiceMethod(method).invoke(args != null ? args : emptyArgs);
          }
        });
  }
```

可见这里使用了动态代理模式来创建接口的实现类，想要理解create，必须先理解什么是动态代理，详细了解可以参考[这里](https://juejin.im/post/6844903744954433544)。

结合GitHubService来看，Proxy.newProxyInstance创建了GitHubService的实现类，并强转为GitHubService类型。

有了service，我们就可以调用接口了 :

`Call<List<Repo>> repos = service.listRepos("octocat");`

这其实是通过动态代理生成的代理类来调用接口，此时会触发InvocationHandler的invoke方法，进而会走到loadServiceMethod的地方。

先了解下InvocationHandler：

> `InvocationHandler` is the interface implemented by the *invocation handler* of a proxy instance.
>
> Each proxy instance has an associated invocation handler. When a method is invoked on a proxy instance, the method invocation is encoded and dispatched to the `invoke` method of its invocation handler.

简单的理解就是代理实例的方法被调用时，其方法调用会被派发到InvocationHandler的invoke方法。

继续往下看源码

### loadServiceMethod

```java
ServiceMethod<?> loadServiceMethod(Method method) {
  ServiceMethod<?> result = serviceMethodCache.get(method);
  if (result != null) return result;

  synchronized (serviceMethodCache) {
    result = serviceMethodCache.get(method);
    if (result == null) {
      result = ServiceMethod.parseAnnotations(this, method);
      serviceMethodCache.put(method, result);
    }
  }
  return result;
}
```

这里主要是获取一个ServiceMethod对象，并且是有缓存机制的，如果已经加载过了，就直接从缓存中获取。这里发现调用了parseAnnotations方法，顾名思义，可见在这里对注解进行了解析，进去看看详情。

### ServiceMethod

```java
abstract class ServiceMethod<T> {
  static <T> ServiceMethod<T> parseAnnotations(Retrofit retrofit, Method method) {
    RequestFactory requestFactory = RequestFactory.parseAnnotations(retrofit, method);

    Type returnType = method.getGenericReturnType();
    if (Utils.hasUnresolvableType(returnType)) {
      throw methodError(method,
          "Method return type must not include a type variable or wildcard: %s", returnType);
    }
    if (returnType == void.class) {
      throw methodError(method, "Service methods cannot return void.");
    }

    return HttpServiceMethod.parseAnnotations(retrofit, method, requestFactory);
  }

  abstract @Nullable T invoke(Object[] args);
}
```

到这里发现，注解由RequestFactory来解析，获取请求的url，请求方式，请求参数等一系列信息。最后HttpServiceMethod.parseAnnotations返回一个ServiceMethod对象，其实是HttpServiceMethod的三个子类之一，

CallAdapted、SuspendForBody 或 SuspendForResponse，具体使用哪个和具体的接口定义有关，本文的接口定义 `Call<List<Repo>> listRepos(@Path("user") String user);`会返回CallAdapted，如果接口定义使用Kotlin的挂起函数，则会返回后两者之一。

## invoke

根据上文分析，可以知道动态代理中loadServiceMethod调用的invoke方法，其实就是HttpServiceMethod的invoke方法：

```java
@Override final @Nullable ReturnT invoke(Object[] args) {
  Call<ResponseT> call = new OkHttpCall<>(requestFactory, args, callFactory, responseConverter);
  return adapt(call, args);
}
```

这里创建了一个OkHttpCall对象，这是对OkHttp的封装类，请求最终是通过OkHttpCall调用OkHttp的方法发出的，可查看OkHttpCall的源码了解详情。OkHttpCall创建时传入了一个responseConverter，这个就是负责将请求结果转换为我们定义的对象的转换器，即通过在创建Retrofit对象时设置的ConverterFactory获取，`.addConverterFactory(GsonConverterFactory.create())`

## adapt

invoke方法调用了adapt方法，本文例子其实就是调用了CallAdapted的adapt方法。

```
@Override protected ReturnT adapt(Call<ResponseT> call, Object[] args) {
  return callAdapter.adapt(call);
}
```

这个adapt方法调用了callAdapter的adapt方法，继续跟踪，callAdapter如果没有明确设置，会有默认设置，DefaultCallAdapterFactory可以获取一个CallAdapter对象，看看DefaultCallAdapterFactory的get方法：

```
@Override public @Nullable CallAdapter<?, ?> get(
    Type returnType, Annotation[] annotations, Retrofit retrofit) {
  ……
  
  final Executor executor = Utils.isAnnotationPresent(annotations, SkipCallbackExecutor.class)
        ? null
        : callbackExecutor;
        
  return new CallAdapter<Object, Call<?>>() {
    @Override public Type responseType() {
      return responseType;
    }

    @Override public Call<Object> adapt(Call<Object> call) {
      return executor == null
          ? call
          : new ExecutorCallbackCall<>(executor, call);
    }
  };
}
```

adapt方法最终返回一个Call对象可见如果接口设置了SkipCallbackExecutor注解，则直接返回OkHttpCall，没有设置，返回ExecutorCallbackCall。

## ExecutorCallbackCall

```java
static final class ExecutorCallbackCall<T> implements Call<T> {
  final Executor callbackExecutor;
  final Call<T> delegate;

  ExecutorCallbackCall(Executor callbackExecutor, Call<T> delegate) {
    this.callbackExecutor = callbackExecutor;
    this.delegate = delegate;
  }

  @Override public void enqueue(final Callback<T> callback) {
    checkNotNull(callback, "callback == null");

    delegate.enqueue(new Callback<T>() {
      @Override public void onResponse(Call<T> call, final Response<T> response) {
        callbackExecutor.execute(new Runnable() {
          @Override public void run() {
            if (delegate.isCanceled()) {
              // Emulate OkHttp's behavior of throwing/delivering an IOException on cancellation.
              callback.onFailure(ExecutorCallbackCall.this, new IOException("Canceled"));
            } else {
              callback.onResponse(ExecutorCallbackCall.this, response);
            }
          }
        });
      }

      @Override public void onFailure(Call<T> call, final Throwable t) {
        callbackExecutor.execute(new Runnable() {
          @Override public void run() {
            callback.onFailure(ExecutorCallbackCall.this, t);
          }
        });
      }
    });
  }
	……
}
```

这里真正看到了OKHttp发送请求的enqueue方法，ExecutorCallbackCall的一个主要作用是将请求结果回调到主线程，具体使用的是MainThreadExecutor，这个类很简单，就是用一个持有主线程Looper的Handler来处理。

```
static class MainThreadExecutor implements Executor {
  private final Handler handler = new Handler(Looper.getMainLooper());

  @Override public void execute(Runnable r) {
    handler.post(r);
  }
}
```

## 总结

至此，Retrofit框架解析注解，并通过OkHttp发送请求的流程基本分析完毕，总结下最开始的疑问：

- 怎么解析的注解？

  主要通过RequestFactory来解析注解，获取请求的各种参数。

- 怎么使用OkHttp发送请求的？

  使用OkHttpCall来最终调用OkHttp的方法来发送请求

参考：https://juejin.im/post/6844904047443460103