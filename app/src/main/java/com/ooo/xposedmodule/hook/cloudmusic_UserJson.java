package com.ooo.xposedmodule.hook;

import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;

public class cloudmusic_UserJson extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
//        Log.i("zzmhook:sucess","Activity");
//        Activity activity = (Activity) param.thisObject;
//        Log.i("zzmhook:sucess","china位置"+activity.getClass());
//        Toast.makeText(activity, "当前"+activity.getClass(), Toast.LENGTH_LONG).show();
        Log.i("zzy:sucess","用户总json："+ param.args[0].toString());
//        Log.i("zzmhook:sucess","用户状态boolean："+(boolean)param.args[1]);

    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        Log.i("zzmhook:sucess","解密后："+param.getResult().toString());
        super.afterHookedMethod(param);
    }
}
