package com.ooo.xposedmodule.hook;

import android.util.Log;

import org.json.JSONObject;

import de.robv.android.xposed.XC_MethodHook;

public class cloudmusic_UserInfoList extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
//        Log.i("zzmhook:sucess","Activity");
//        Activity activity = (Activity) param.thisObject;
//        Log.i("zzmhook:sucess","china位置"+activity.getClass());
//        Toast.makeText(activity, "当前"+activity.getClass(), Toast.LENGTH_LONG).show();
        Log.i("zzmhook:sucess","用户fromJsonForProfileList方法json：："+ param.args[0].toString());
        Log.i("zzmhook:sucess","用户fromJsonForProfileList方法Long：："+(Long)param.args[1]);

    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        Log.i("zzmhook:sucess","解密后："+param.getResult().toString());
        super.afterHookedMethod(param);
    }
}
