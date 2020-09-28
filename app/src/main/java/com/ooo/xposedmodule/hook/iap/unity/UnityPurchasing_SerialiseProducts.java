package com.ooo.xposedmodule.hook.iap.unity;

import com.ooo.xposedmodule.util.XPLog;

import de.robv.android.xposed.XC_MethodHook;

public class UnityPurchasing_SerialiseProducts extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        XPLog.e("SerialiseProducts is " + (String) param.getResult());
    }
}
