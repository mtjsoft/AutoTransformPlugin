# AutoTransformPlugin
ASM字节码插桩插件，向代码中插入垃圾代码，用于马甲包代码混淆

## 引用发布的库

project build.gradle引入Maven地址

```
buildscript {
   repositories {
       maven {
            url 'https://raw.githubusercontent.com/mtjsoft/AutoTransformPlugin/master/repo'
       }
   }
   dependencies { 
       // 导入插件 
       classpath "cn.mtjsoft.www:burypoint.plugin:1.0.17"
   }
}

allprojects {
   repositories {
       // // 仓库地址
       maven {
            url 'https://raw.githubusercontent.com/mtjsoft/AutoTransformPlugin/master/repo'
       }
   }
}
```

## 在module build.gradle 中启用
```
    apply plugin: 'mtjsoft.auto.burypoint.plugin'
    methodhook {
        // 是否启用字节码插入
        enable = true
        // 项目中的class true：全部插桩
        all = true
        // 在满足以下类名正则的类中插入垃圾代码
        classRegexs = [
                "cn/mtjsoft/www/autotransformplugin/keep.*?Java",
                "cn/mtjsoft/www/autotransformplugin.*?Activity"
        ]
        // 排除jar包操作
        jarRegexs = []
        // 每个类中随机插入的变量、方法数量
        randomCount = 10
    }
```
