package com.ooo.xposedmodule.hook;

import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;

public class cloudmusic_c extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
//        Log.i("zzmhook:sucess","Activity");
//        Activity activity = (Activity) param.thisObject;
//        Log.i("zzmhook:sucess","china位置"+activity.getClass());
//        Toast.makeText(activity, "当前"+activity.getClass(), Toast.LENGTH_LONG).show();
        Log.i("zzmhook:music","密文："+param.args[0].toString());
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        Log.i("zzmhook:music","解密后："+param.getResult().toString());
        super.afterHookedMethod(param);
    }
}
