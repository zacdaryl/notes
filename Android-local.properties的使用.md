# local.properties

Android Studio项目中的local.properties文件中默认配置了本地的sdk目录，但从来没想过用作它用。

项目中存在一种情况，开发过程中，每个同学都要连接到各自的本地服务上进行调试。最只管的思路就是在代码中配置一个基地址，修改这个基地址为本地服务地址即可，但这样一个明显的弊端就是，如果别人修改并push了代码，自己拉下来后，地址就变成了别人的，自己还得再重新修改下，如果不小心提交了，那么就又给别人增加了麻烦，如何破？

今天突然想到用配置的方式解决，于是就想到了local.properties，只要将自己的地址配置在这里，编译的时候，将这个地址读出使用即可

1. 简单配置

    local.url="your local base url"
    
2. build.gradle读取配置

    ```gradle
    local {
        def properties = new Properties()
        def inputStream = project.rootProject.file('local.properties').newDataInputStream()
        properties.load( inputStream )

        buildConfigField "String", "base", properties.getProperty('local.url')

        ...
    }
    ```
3. gradle同步之后，BuildConfig.java中的base即为本地配置的地址

   