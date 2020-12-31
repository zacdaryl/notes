# Tips

## 查看任务栈

查看当前显示的activity
 adb -d shell dumpsys activity activities | grep mResumedActivity

查看activity
 adb shell dumpsys activity activities | grep Run #

查看堆栈
 adb shell dumpsys activity activities | sed -En -e '/Running activities/,/Run #0/p'



作者：wodezhuanshu
链接：https://www.jianshu.com/p/31d6b3d112ca
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。



