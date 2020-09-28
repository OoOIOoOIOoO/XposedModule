package com.ooo.xposedmodule.hook.iap.google;

import de.robv.android.xposed.XC_MethodReplacement;

public class IabHelper_launchPurchaseFlow extends XC_MethodReplacement {
    @Override
    protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
        Object mPurchaseListener = methodHookParam.args[4];
        mPurchaseListener.getClass().getDeclaredMethod("onIabPurchaseFinished");
        return null;
    }
}