package com.ooo.xposedmodule.hook;

import com.ooo.xposedmodule.util.Utils;
import com.ooo.xposedmodule.util.XPLog;

import de.robv.android.xposed.XC_MethodHook;

import static com.ooo.xposedmodule.XposedModuleAction.apkContext;

public class XSystemLoad extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        XPLog.e("XSystemLoad: "+param.args[1]);
    }
}