package com.ooo.xposedmodule.hook;

import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class ClassLoaderHook extends XC_MethodHook {

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Log.i("zzy:sucess","hook classloader is success");
        super.afterHookedMethod(param);
        if (param.hasThrowable()) return;
        Class<?> cls = (Class<?>) param.getResult();
        String name = cls.getName();
        Log.i("zzy:sucess","hook classloader is success : "+name);
        if (class_cloudmusic_UserJson.equals(name)) {
            // 所有的类都是通过loadClass方法加载的
            // 所以这里通过判断全限定类名，查找到目标类
            // 第二步：Hook目标方法
            XposedHelpers.findAndHookMethod(cls, class_fun_UserJson_a, org.json.JSONObject.class,new cloudmusic_UserJson());
        }
    }
    public static final String class_cloudmusic_UserJson = "com.netease.cloudmusic.b.a.a";
    public static final String class_fun_UserJson_a= "a";//JSONObject boolean
}
