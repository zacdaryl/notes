# AFNetworking踩坑记录

AFNetworking目前iOS项目十分流行的网络框架库，先附上项目地址：http://afnetworking.com

本来以为接入AFNetworking是一帆风顺的，结果发现事实并非如此。这里总结下这次从集成到发送一个demo请求过程中，个人感觉不顺畅的地方。

- 官方示例比较简单，例子直接割裂也比较厉害，对于新手来说，想串联起来，但感觉总是缺点东西，必须搜索一番，才能确定如何写是正确的。

- 在本地搭建了一个web server，模拟接口api，结果Android项目正常运行，接口调用都ok，到了iOS上使用AF时，却发现根本就连不上server。遇到最多的error是：Domain=NSURLErrorDomain Code=-1004 "Could not connect to the server."。

    1. 怀疑是info.plist配置问题，缺少如下配置：
    ```
    <key>NSAppTransportSecurity</key>
	<dict>
		<key>NSAllowsArbitraryLoads</key>
		<true/>
    </dict>
    ```
    
    结果是加上后，问题并没有解决，1004依然出现，无奈

    2. 开始Google看各种文章，发现多数说的都是问题1，也有说是服务的问题，但站在Android正常，浏览器访问服务正常的情况下，一直认为服务应该没事。无奈之下，换了一个服务，不用自己搭建的服务时，发现接口可以发出去了，但是有点不甘心，感觉服务应该没事，是不是哪里配置出了问题，于是又是各种Google，无果！最后无奈之下还是不用自己搭建的服务，转用项目现有api，再修改返回报文，接口总算可以跑起来了。
   
    3. 接口通了之后，发现response content-type不对，导致解析出错，不能正常触发成功回调，于是又Google一番，加上"text/plain"后，总算达到预期

## 总结

这次问题前后差不多折腾了一天时间，主要在于自己有点钻牛角尖，早点更换接口服务，应该能节省不少时间。不过这个问题也说明，AF的兼容性可能不够好，同样的接口服务，在Android上就能正常跑。

总之，以后解决问题发现时间成本很高的话，先放一放，换个路子，不要太经验主义，比如这次的认为Android调用服务就正常，iOS调用有问题，就一定是网络库的问题，其实可能是因为服务搭的比较简单，不够健壮

    
  