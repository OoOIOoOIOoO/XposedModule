# XposedModule

简单的xposed模块编写demo,逐渐增加动态加载app，实现免重启手机就可以让插件生效. 
实现了模块修改免重启，多dexhook

使用方法：

   1，在XposedModuleAction里面定义 被hook的 包名，类名， 方法名。
   2, 在DynamicLoaderMoudleAction的 static里面要把 包名添加进去
   3，和正常一样写xpose模块就行了

