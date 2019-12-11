# Alamofire

https://github.com/Alamofire/Alamofire

## 设置timeout

Alamofire默认的超时时间是60s，如果想改变超时时间，则网络请求需要使用URLRequest

```swift
var urlRequest = try! URLRequest(url: url, method: request.method, headers: headers)
urlRequest.timeoutInterval = timeout

AF.request(urlRequest)
```