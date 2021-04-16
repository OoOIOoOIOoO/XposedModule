package com.ooo.xposedmodule.hook;

import com.ooo.xposedmodule.util.XPLog;
import com.ooo.xposedmodule.util.XpUtils;

import de.robv.android.xposed.XC_MethodReplacement;

public class Normal_Replace_Hook extends XC_MethodReplacement {
    @Override
    protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
        XPLog.e("Normal_Replace_Hook: " + methodHookParam.method.getDeclaringClass().getName()+ "." + methodHookParam.method.getName());
        XpUtils.printArgs(methodHookParam);
//        XPLog.printStack("Hook_All_Method: " + methodHookParam.method.getDeclaringClass().getName()+ "." + methodHookParam.method.getName());
        return null;
    }

}