# XposedModule

xposed模块
实现了模块修改免重启，多dexhook，兼容目前各种兼容了xposed中api的hook框架

使用方法：
   1,在HookPkgNames里面定义要hook的包名，不定义的不会hook
   2,在XposedModuleAction里面写hook
   3,在 com.ooo.xposedmodule.hook里面实现hook

