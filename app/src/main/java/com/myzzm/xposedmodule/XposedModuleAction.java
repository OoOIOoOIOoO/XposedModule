package com.myzzm.xposedmodule;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import com.myzzm.xposedmodule.hook.Ccb_application_Hook;
import com.myzzm.xposedmodule.hook.Ccb_application_quit;
import com.myzzm.xposedmodule.hook.Game_GameView_addScore_Hook;
import com.myzzm.xposedmodule.hook.Game_GameView_explode_Hook;
import com.myzzm.xposedmodule.hook.MainActivity_getPrintf_args_Hook;

public class XposedModuleAction implements IXposedHookLoadPackage {
    public static final String LD_TAG = "XPOSED-TEST";
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam)
            throws Throwable {
        Log.i("zzmhook:",loadPackageParam.packageName);

        if(loadPackageParam.packageName.equals(pck_game)){
            Log.i("zzmhook:sucess:",loadPackageParam.packageName);
            XposedBridge.log(LD_TAG+loadPackageParam.packageName+"=== 开始hook注入app进程.");
            XposedHelpers.findAndHookMethod(class_game_gameView, loadPackageParam.classLoader,
                    fun_name_game_addScore, int.class, new Game_GameView_addScore_Hook());

        }
        if(loadPackageParam.packageName.equals(pck_game)){
            Log.i("zzmhook:sucess:",loadPackageParam.packageName);
            XposedBridge.log(LD_TAG+loadPackageParam.packageName+"=== 开始hook注入app进程.");
            XposedHelpers.findAndHookMethod(class_game_CombatAircraft, loadPackageParam.classLoader,
                    fun_name_game_explode,new Game_GameView_explode_Hook());

        }
    }

    public static final String pck_game = "com.ispring.gameplane";
    public static final String fun_name_game_addScore="addScore";
    public static final String class_game_gameView = "com.ispring.gameplane.game.GameView";
    public static final String fun_name_game_explode="explode";
    public static final String class_game_CombatAircraft = "com.ispring.gameplane.game.CombatAircraft";

}
