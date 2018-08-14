package com.myzzm.xposedmodule.hook;

import android.util.Log;

import de.robv.android.xposed.XC_MethodReplacement;

/**
 * Created by zzm on 18-4-8.
 */

public class Game_GameView_explode_Hook extends XC_MethodReplacement {
    @Override
    protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
        Log.i("zzmhook:sucess:","战机摧毁");
        return null;
    }
}
