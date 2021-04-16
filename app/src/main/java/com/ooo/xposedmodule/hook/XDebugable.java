package com.ooo.xposedmodule.hook;

import com.ooo.xposedmodule.BuildConfig;
import com.ooo.xposedmodule.HookPkgNames;
import com.ooo.xposedmodule.util.Utils;
import com.ooo.xposedmodule.util.XPLog;


import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class XDebugable extends XC_MethodHook {

    private static final int DEBUG_ENABLE_DEBUGGER = 0x1;

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        if(!Utils.getXpModuleSharP("DEBUG_PKG").equals((String)param.args[1])) {
            return;
        }
        XPLog.d("app debugable hook:" + param.args[1]);
        int id = 5;
        int flags = (Integer) param.args[id];
        if ((flags & DEBUG_ENABLE_DEBUGGER) == 0) {
            flags |= DEBUG_ENABLE_DEBUGGER;
        }
        param.args[id] = flags;
        if (BuildConfig.DEBUG) {
            XPLog.d("app debugable flags to 1 :" + param.args[1]);
        }
    }
}
