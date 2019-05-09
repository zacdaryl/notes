# Shell

> 记录下Shell编程的点点滴滴

[Shell语法文档](https://wiki.ubuntu.org.cn/Shell%E7%BC%96%E7%A8%8B%E5%9F%BA%E7%A1%80#if_.E8.AF.AD.E5.8F.A5)

> shell脚本过长时，如何换行？

使用 " \ + Enter "反斜杠后边加回车

> 正则表达式判断是否匹配

```
=~
```

> 判断之前脚本是否运行成功

$? 显示上一条命令的返回值，如果为0则表示执行成功

```
if [ $? -ne 0 ]; then
    echo "failed!"
    exit 1
fi
```