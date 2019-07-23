package com.ooo.xposedmodule.hook;

import android.util.Log;

import com.ooo.xposedmodule.XposedModuleAction;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

/**
 * Created by zzm on 18-4-8.
 */

public class Game_GameView_addScore_Hook extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        Log.i("zzmhook:sucess:",""+param.args[0]);
        XposedBridge.log(XposedModuleAction.LD_TAG + "setPower:"
                +param.args[0]);
        int score= (int)param.args[0];
        param.args[0]=score*2;
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
    }
}
