package com.ooo.xposedmodule.hook;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import de.robv.android.xposed.XC_MethodHook;

public class Android_Activity_Hook extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        Log.i("zzmhook:sucess","Activity");
        Activity activity = (Activity) param.thisObject;
        Log.i("zzmhook:sucess","china位置"+activity.getClass());
//        Toast.makeText(activity, "当前"+activity.getClass(), Toast.LENGTH_LONG).show();
        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
    }
}
