# Computed Properties VS Methods

使用Vue.js开发的时候，会用到计算属性，但通常情况下，感觉计算属性实现的内容，也可以用一个方法来处理相关逻辑，那么他们两者的区别在哪儿呢？之前就有过这样的疑问，今天又看Vue.js官方文档，里边有清晰的解释。

> Instead of a computed property, we can define the same function as a method instead. For the end result, the two approaches are indeed exactly the same. However, the difference is that computed properties are cached based on their reactive dependencies. A computed property will only re-evaluate when some of its reactive dependencies have changed. This means as long as message has not changed, multiple access to the reversedMessage computed property will immediately return the previously computed result without having to run the function again.

简单概括一句，就是计算属性可以缓存结果，除非关联的属性进行了改变，否则不管调用多少次计算属性，都是返回第一次计算的结果。