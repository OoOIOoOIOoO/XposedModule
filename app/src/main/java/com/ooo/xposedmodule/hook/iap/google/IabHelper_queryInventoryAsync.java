package com.ooo.xposedmodule.hook.iap.google;

import com.ooo.xposedmodule.util.XPLog;

import de.robv.android.xposed.XC_MethodReplacement;

public class IabHelper_queryInventoryAsync  extends XC_MethodReplacement {
    @Override
    protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
        XPLog.printStack("zzm " + methodHookParam.method.getName());
        return null;
    }
}