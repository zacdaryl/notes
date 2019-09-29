# 创建一个Framework的步骤

1. New -> Coco Touch Framework
2. pod init 并引入需要的pod库，之后pod install
3. pod spec create 创建.podspec文件
4. podspec文件修改，需关联git仓库
5. pod spec lint --allow-warnings 校验库是否ok