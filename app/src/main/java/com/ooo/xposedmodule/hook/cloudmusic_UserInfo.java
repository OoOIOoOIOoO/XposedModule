package com.ooo.xposedmodule.hook;

import android.util.Log;

import org.json.JSONObject;

import de.robv.android.xposed.XC_MethodHook;

public class cloudmusic_UserInfo extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

        super.beforeHookedMethod(param);
//        Log.i("zzmhook:sucess","Activity");
//        Activity activity = (Activity) param.thisObject;
//        Log.i("zzmhook:sucess","china位置"+activity.getClass());
//        Toast.makeText(activity, "当前"+activity.getClass(), Toast.LENGTH_LONG).show();
        JSONObject user = (JSONObject) param.args[0];
        Log.i("zzy:sucess","用户fromJson方法json："+param.args[0].toString());
//        int userid = user.getInt("userId");
//        String uesrVip = "{\"associator\":{\"vipCode\":100,\"expireTime\":1580140799000,\"sign\":false,\"signIap\":false},\"musicPackage\":{\"vipCode\":220,\"expireTime\":1580313599000,\"sign\":true,\"signIap\":false},\"oldCacheProtocol\":false,\"userId\":46573478,\"code\":200}";
//        JSONObject userNew = new JSONObject(uesrVip);
//        userNew.put("userId",userid);
//        param.args[0]=userNew;
        Log.i("zzy:sucess","用户fromJson方法修改后json："+param.args[0].toString());
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//        Log.i("zzmhook:sucess","解密后："+param.getResult().toString());
        super.afterHookedMethod(param);
    }
}
