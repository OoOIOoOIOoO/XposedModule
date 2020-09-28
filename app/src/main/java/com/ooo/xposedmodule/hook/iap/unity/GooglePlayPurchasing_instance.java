package com.ooo.xposedmodule.hook.iap.unity;

import com.ooo.xposedmodule.util.HookLisentsrs;
import com.ooo.xposedmodule.util.XPLog;

import de.robv.android.xposed.XC_MethodHook;


public class GooglePlayPurchasing_instance extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        HookLisentsrs.iUnityCallback = param.args[0];
        XPLog.e("hook GooglePlayPurchasing instance");
    }
}
