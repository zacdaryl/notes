# Git 操作脚本

> 修改本地分支名称，并提交到远程，然后删除远程被修改的分支

```
//修改分支名称
git branch -m old new

//新分支提交到远程仓库
git push origin HEAD

//删除远程仓库旧分支名
git branch -d -r origin/old

```

> 删除远程分支还有一个无厘头的语法 git push [远程名] :[分支名]

```
$ git push origin :serverfix
To git@github.com:schacon/simplegit.git
 - [deleted]         serverfix
```

> 删除远程tag
```
 git push origin :refs/tags/tagname
``` 

> release分支发布之后，一般都要打个tag，方便后续追溯代码。分支命名规范后，可以通过分支名，每次打包的时候，自动加上tag。这里将分支命名规范为：release_vX.X.X

```
version=`git branch | grep '*' | cut -d _ -f 2`
git tag -f ${version}
git push -f origin ${version}
```