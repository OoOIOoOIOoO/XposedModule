package com.ooo.xposedmodule.hook.iap.unity;

import com.ooo.xposedmodule.util.XPLog;

import de.robv.android.xposed.XC_MethodHook;

public class UnityPlayer_UnitySendMessage extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        if(param.args.length == 3) {
            XPLog.e( String.format("unitySendMessage :[ %-30s %-30s %s ]", param.args[0], param.args[1], param.args[2]));
        } else {
            XPLog.e("unitySendMessage: get param is errors. param.args length is " + param.args.length);
        }

    }
}
