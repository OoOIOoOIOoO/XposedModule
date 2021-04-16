package com.ooo.xposedmodule.hook;

import com.ooo.xposedmodule.util.XPLog;

import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;


public class XTencentBugly extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        if(param.method.getName().equals("initCrashReport")) {
            if(param.args !=null && param.args.length >= 3) {
                XPLog.e("initCrashReport: " + param.args[2]);
                XPLog.e("initCrashReport: " + param.args.length);
            }
        }
    }
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        if(param.method.getName().equals("d")) {
            XPLog.e("initCrashReport old: " + param.getResult());
//            Map<String, String> stringMap = (Map<String, String>)param.getResult();
//            stringMap.put("BUGLY_ENABLE_DEBUG","true");
//            stringMap.put("BUGLY_APPID","xxxxx");
//            param.setResult(stringMap);
            XPLog.e("initCrashReport new: " + param.getResult());
        }
    }
}