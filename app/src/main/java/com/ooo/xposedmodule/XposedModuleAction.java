package com.ooo.xposedmodule;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import com.ooo.xposedmodule.util.XPLog;

import static com.ooo.xposedmodule.util.XpUtils.*;

public class XposedModuleAction implements IXposedHookLoadPackage{
    public static ClassLoader apkClassLoader;
    public static XC_LoadPackage.LoadPackageParam loadPackageParam;
    public static String packageName;
    /**
     * findAndHookMethod参数说明
     * <p>
     * 1,要hook的类
     * 2,lpparam.classLoader
     * 3,要hook的方法
     * 4+n,要hook方法的参数,参数为自定义类的用XposedHelpers.findClass("类名",loadPackageParam.classLoader)
     * 最后一个,跳转到具体的hook处理
     */
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam){
        XPLog.i(loadPackageParam.packageName);
        this.apkClassLoader = loadPackageParam.classLoader;
        this.loadPackageParam = loadPackageParam;
        this.packageName = loadPackageParam.packageName;

        if(packageName.equals(HookPkgNames.ARCFACE)){
            hookMethod("com.arcsoft.arcfacedemo.activity.RegisterAndRecognizeActivity","searchFace");
        }
        if(packageName.equals(HookPkgNames.EXAMPLE)) {
            //hook a中的所有方法
            hookAllMethods("com.test.a");
            //hook a中的所有方法,add除外
            hookAllMethods("com.test.a","add");
            //hook a中的add方法
            hookMethod("com.test.a","add");
            //hook a中指定参数类型的add方法
            hookMethod("com.test.a","add",int.class,int.class);
            //hook 替换 a中的add方法
            replaceMethod("com.test.a","add",int.class,int.class);
        }
    }
}
