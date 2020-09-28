package com.ooo.xposedmodule.hook;

import com.ooo.xposedmodule.util.XPLog;

import de.robv.android.xposed.XC_MethodHook;

public class Hook_All_Method extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        XPLog.e("Hook_All_Method: " + param.method.getDeclaringClass().getName()+ "." + param.method.getName() );
//        XPLog.printStack(param.method.getName()+":");

    }

}
