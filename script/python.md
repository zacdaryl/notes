# Python Note

## 时间格式化处理

```python
import time
timestamp = time.strftime('%Y%m%d%H%M%S', time.localtime())
```

## fastapi

```
pip install fastapi
```

安装fastapi时总是提示如下错误：

Could not find a version that satisfies the requirement fastapi (from versions: )
No matching distribution found for fastapi

Google后发现可能是因为python版本的问题导致的，于是下载最新的python3.7.3安装

安装后发现python默认版本还是2.7.2，于是如下操作切换python版本：

vi .bash_profile 增加python环境变量：

PATH="/Library/Frameworks/Python.framework/Versions/3.7/bin:${PATH}"
export PATH

alias python=python3

准备工作完毕后，使用python3安装 python -m pip install fastapi 成功

ps: 同时安装了python2 和 python3 使用pip时要明确版本 py -3 pip install ...



