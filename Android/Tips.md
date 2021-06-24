# Tips

## 查看任务栈

查看当前显示的activity
 adb -d shell dumpsys activity activities | grep mResumedActivity

查看activity
 adb shell dumpsys activity activities | grep Run #

查看堆栈
 adb shell dumpsys activity activities | sed -En -e '/Running activities/,/Run #0/p'

## 刷机

https://developers.google.com/android/ota

## 模拟器

模拟器端口映射： https://developer.aliyun.com/article/270580

telnet连接到模拟器
`telnet localhost 5554`

重定向端口，电脑端5000，模拟器9080
`redir add tcp: 5000:9080`

电脑端访问模拟器端口
localhost:5000

删除重定向
`redir del`