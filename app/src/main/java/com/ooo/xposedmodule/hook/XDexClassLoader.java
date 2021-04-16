package com.ooo.xposedmodule.hook;

import com.ooo.xposedmodule.XposedModuleAction;
import com.ooo.xposedmodule.util.Utils;
import com.ooo.xposedmodule.util.XPLog;

import dalvik.system.DexClassLoader;
import de.robv.android.xposed.XC_MethodHook;

import static com.ooo.xposedmodule.XposedModuleAction.apkContext;
import static com.ooo.xposedmodule.util.XpUtils.hookMethod;

public class XDexClassLoader extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        XPLog.e("XDexClassLoader: "+param.args[0]);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        XposedModuleAction.apkDynamicClassLoader = (DexClassLoader)param.thisObject;
        XPLog.e("XDexClassLoader : "+XposedModuleAction.apkDynamicClassLoader);
    }
}
