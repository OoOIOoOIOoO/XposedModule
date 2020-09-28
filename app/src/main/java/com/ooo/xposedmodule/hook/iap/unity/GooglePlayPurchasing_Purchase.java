package com.ooo.xposedmodule.hook.iap.unity;

import com.ooo.xposedmodule.XposedModuleAction;
import com.ooo.xposedmodule.util.HookLisentsrs;
import com.ooo.xposedmodule.util.XPLog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;


public class GooglePlayPurchasing_Purchase extends XC_MethodReplacement {
    @Override
    protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
        XPLog.e("hook GooglePlayPurchasing Purchase");
        Object productDefinition = methodHookParam.args[0];
        Field f= XposedHelpers.findClass("com.unity.purchasing.common.ProductDefinition", XposedModuleAction.apkClassLoader).getDeclaredField("storeSpecificId");
        f.setAccessible(true);
        String productId =(String)f.get(productDefinition);
        XPLog.e("purchase == " + productId);
        Method OnPurchaseSucceeded=XposedHelpers.findClass("com.unity.purchasing.common.IUnityCallback", XposedModuleAction.apkClassLoader).getDeclaredMethod("OnPurchaseSucceeded",String.class,String.class,String.class);
        OnPurchaseSucceeded.invoke(HookLisentsrs.iUnityCallback, f.get(productDefinition), "", "");
        return null;
    }
}