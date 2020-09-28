package com.ooo.xposedmodule.hook.iap.google;

import com.ooo.xposedmodule.util.XPLog;

import de.robv.android.xposed.XC_MethodHook;

public class IabHelper_logDebug extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        XPLog.e("log is " + param.args[0]);
    }
}