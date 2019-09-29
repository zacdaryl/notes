# Brew Update安装缓慢

brew install watchman，一直卡在HomeBrew Updating，Google有人说切换git源即可

```
# 进入主目录
cd `brew --repo`

# 切换镜像
git remote set-url origin https://git.coding.net/homebrew/homebrew.git

cd ~

brew update

```

几个镜像：

- https://git.coding.net/homebrew/homebrew.git - Coding
- https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/brew.git - 清华
- https://mirrors.ustc.edu.cn/brew.git - 中科大