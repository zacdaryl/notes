# Shell

> 记录下Shell编程的点点滴滴

[Shell语法文档](https://wiki.ubuntu.org.cn/Shell%E7%BC%96%E7%A8%8B%E5%9F%BA%E7%A1%80#if_.E8.AF.AD.E5.8F.A5)

### shell脚本过长时，如何换行？

使用 " \ + Enter "反斜杠后边加回车

### 正则表达式判断是否匹配

```
=~
```

### 判断之前脚本是否运行成功

$? 显示上一条命令的返回值，如果为0则表示执行成功

```
if [ $? -ne 0 ]; then
    echo "failed!"
    exit 1
fi
```

### 从release_vx.x.x分支，截取版本信息

```
git branch | grep release_v | cut -d _ -f 2
```

### 获取当前系统时间

```
time=$(date "+%Y-%m-%d-%H:%M:%S")
```

### 现有目录foo，目录中内容有 version, file1, file2等其他文件或目录，需要将除version外的其他文件或者目录移动到version中

```
cd foo
ls | grep -v version | xargs -t -I '{}' mv {} version
```

### 判断当前分支是否以release_v开头

```shell
version=$(git branch | grep '*' | cut -d '*' -f 2 | tr -d ' ')
echo current branch is: a${version}

if [[ $version =~ ^release_v ]]; then
    echo release prefix
else 
    echo not release prefix
fi
```

去除首尾空格，使用‘tr -d’ 命令，此命令发现匹配正则的时候，if判断中正则表达式不能加双引号

### Node升级

查看当前nvm版本

```
nvm list
```

查看远端nvm版本

```
nvm ls-remote
```

安装最新版node

```
nvm install new-version
```

设置默认版本

```
nvm alias default version
```

### CocoaPods升级

官网说升级只需简单的使用如下命令即可：

```
[sudo] gem install cocoapods
```

但是即便给了权限，依然报错：

```
ERROR: While executing gem ... (Gem::FilePermissionError)

You don't have write permissions for the /usr/bin directory.
```

于是找到如下方法：

```
sudo gem install cocoapods -n /usr/local/bin
```

### tree列出目录结构

很多技术文档中都列出了漂亮的树形目录，怎么做到的呢，答案是使用tree命令。

`brew install tree` 安装，之后 `tree -a` 列出当前目录下所有文件，`tree -d` 只列出目录，`tree --help` 查看更多帮助。

### 后台执行命令

开启一个服务的时候，不想让此服务占据当前终端，而让其在后台运行，今天实验了如下几个方法：

`command &` 可后台运行，但会打印内容到当前终端，可以 control + c 退出当前命令，继续shell操作，但是关闭终端，导致服务也会关闭

`nohup commmand &` 后台运行，日志重定向到nohup.out，但关闭终端后，服务也随之终止

`(command &)` 后台运行，关闭终端后，服务不会停止

`ps -ef | grep 关键字`，`kill -p pid` 删除已经启动的后台服务

参考：[Linux 技巧：让进程在后台运行更可靠的几种方法](https://www.ibm.com/developerworks/cn/linux/l-cn-nohup/index.html)

### 输出内容到剪贴板

`pwd | pbcopy`

### curl

> curl is used in command lines or scripts to transfer data. curl is also used in cars, television sets, routers, printers, audio equipment, mobile phones, tablets, settop boxes, media players and is the Internet transfer engine for thousands of software applications in over ten billion installations.

[curl tutorial](https://curl.se/docs/manual.html)