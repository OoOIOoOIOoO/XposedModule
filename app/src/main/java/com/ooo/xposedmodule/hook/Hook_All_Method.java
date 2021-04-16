package com.ooo.xposedmodule.hook;

import android.content.Context;
import android.graphics.Bitmap;

import com.ooo.xposedmodule.XposedModuleAction;
import com.ooo.xposedmodule.util.Utils;
import com.ooo.xposedmodule.util.XPLog;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_All_Method extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        XPLog.e("Hook_All_Method: " + param.method.getDeclaringClass().getName()+ "." + param.method.getName());
//        XPLog.printStack(param.method.getName()+":");

        if(param.method.getName().equals("searchFace")) {
            Bitmap bitmap = Utils.getImage((Context)param.args[2],"replace");
            if(bitmap == null)
                return;
            Class clazz = XposedHelpers.findClass("com.arcsoft.arcfacedemo.util.FaceUtils", XposedModuleAction.apkClassLoader);
            Method method = clazz.getMethod("getFaceFeature", Context.class,Bitmap.class);
            method.setAccessible(true);
            param.args[0] = method.invoke(null, (Context) param.args[2], bitmap);
        }
    }

}
