# Git 操作脚本

### 修改本地分支名称，并提交到远程，然后删除远程被修改的分支

```
//修改分支名称
git branch -m old new
git branch -m <newname>

//新分支提交到远程仓库
git push origin HEAD
git push origin -u <newname>

```
删除远程分支还有一个无厘头的语法 git push [远程名] :[分支名]
```
$ git push origin :serverfix
To git@github.com:schacon/simplegit.git
 - [deleted]         serverfix
```

#### 删除本地分支

`git branch -d <branch-name>`

### 删除远程tag
```
 git push origin :refs/tags/tagname
``` 

release分支发布之后，一般都要打个tag，方便后续追溯代码。分支命名规范后，可以通过分支名，每次打包的时候，自动加上tag。这里将分支命名规范为：release_vX.X.X

```
version=`git branch | grep '*' | cut -d _ -f 2`
git tag -f ${version}
git push -f origin ${version}
```

### 合并多次提交
commit多次之后，如果发现每次commit都是同一件事情，为了保证commit信息的清晰，可以将多次commit的信息进行合并，减少冗余信息

```
git rebase -i HEAD~n //n表示往前n个commit
```

然后会显示commit信息，且为倒序，即最近一次提交在最下边，此时将需要合并的都改为s，保存即可。如果有冲突，处理完冲突后，使用如下命令继续：

```
git rebase --continue
```

### 从tag创建分支

```
git branch new_branch_name vtag_name
git checkout new branch_name
```

### git-flow 最佳分支管理实践

https://nvie.com/posts/a-successful-git-branching-model/

### 本地仓库关联github远程仓库

[Adding an existing project to GitHub using the command line](https://help.github.com/en/articles/adding-an-existing-project-to-github-using-the-command-line)

```
$ git remote add origin https://github.com/user/repo.git
# Set a new remote

$ git remote -v
# Verify new remote
> origin  https://github.com/user/repo.git (fetch)
> origin  https://github.com/user/repo.git (push)
```

### 修改历史记录Author信息

GitHub已经创建了一个脚本处理此事，试用后，效果不错，参考这篇文章[Changing author info](https://help.github.com/en/github/using-git/changing-author-info)

### 仅clone子目录

只关心一个仓库个别目录的情况下，只想clone特定目录，而不想把整个仓库都clone下来，该怎么做？

```
mkdir <local-dir>
cd <local-dir>
git init
git remote add origin <url>
git config core.sparseCheckout true
//指定clone的子目录
echo "<sub-dir>" >> .git/info/sparse-checkout
git pull --depth=1 origin master
```

参考：[How do I clone a subdirectory only of a Git repository?](https://stackoverflow.com/questions/600079/how-do-i-clone-a-subdirectory-only-of-a-git-repository/28039894#28039894)

### 分支间迁移文件

功能A开发完成后，在分支B上需要同样的功能，但又不能将功能A的分支合并到B，此时就需要将对应文件移到分支B上。

最初的想法是，切到A分支，把对应文件copy一份出来，再切到分支B，添加copy出来的文件。这种方法能解决问题，但感觉很low，强大的git应该提供有解决方案，于是Google之后，答案来了：

```
git checkout <branch-name> -- <file-path> //经测试直接filename是找不到的，需要加上对应路径
```

### 找回已删除的commit信息

当把分支删除后，或者reset到某个commit后，后悔了，想返回到已经删除的commit怎么办？

使用 `git reflog` 显示本地git仓库更新记录，可找到对应的commit点，然后再恢复即可

### 切换远程仓库

代码已经push到远程仓库后，又接到一个通知，说把代码传到新的仓库，在本地切换远程代码仓库，然后再push即可。

`git remote set-url origin <url>`
`git push`

### 本地分支覆盖远程分支

`git push origin <branch-name> --force`