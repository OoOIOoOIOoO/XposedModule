package com.ooo.xposedmodule;

import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import com.ooo.xposedmodule.hook.Game_GameView_addScore_Hook;
import com.ooo.xposedmodule.hook.Game_GameView_explode_Hook;
import com.ooo.xposedmodule.hook.XDebugable;
import com.ooo.xposedmodule.hook.cloudmusic_c;

import static com.ooo.xposedmodule.HookPkgNames.TAG;

public class XposedModuleAction implements IXposedHookLoadPackage{
    public static final String LD_TAG = "XPOSED-MODULE";

    /**
     * findAndHookMethod参数说明
     * <p>
     * 1,要hook的类
     * 2,lpparam.classLoader
     * 3,要hook的方法
     * 4+n,要hook方法的参数,参数为自定义类的用XposedHelpers.findClass("类名",loadPackageParam.classLoader)
     * 最后一个,跳转到具体的hook处理
     */
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam)throws Throwable {
        Log.i(TAG, loadPackageParam.packageName);
        if (loadPackageParam.packageName.equals(pck_game)) {
            Log.i("zzmhook:sucess:", loadPackageParam.packageName);

            XposedBridge.log(LD_TAG + loadPackageParam.packageName + "=== 开始hook注入app进程.");
            XposedHelpers.findAndHookMethod(class_game_gameView, loadPackageParam.classLoader,
                    fun_name_game_addScore, int.class, new Game_GameView_addScore_Hook());

            XposedBridge.log(LD_TAG + loadPackageParam.packageName + "=== 开始hook注入app进程.");
            XposedHelpers.findAndHookMethod(class_game_CombatAircraft, loadPackageParam.classLoader,
                    fun_name_game_explode, new Game_GameView_explode_Hook());
        }

//        if (loadPackageParam.packageName.equals(class_workspace_pakg)) {
//            Log.i("zzmhook:sucess:", loadPackageParam.packageName);
//            XposedBridge.log(LD_TAG + loadPackageParam.packageName + "=== 开始hook注入app进程.");
//
//            XposedHelpers.findAndHookMethod(class_workspace_activity, loadPackageParam.classLoader,
//                    class_fun_test, new Recp());
//
//        }

        if (loadPackageParam.packageName.equals(class_cloudmusic_pakg)) {
            Log.i(TAG, loadPackageParam.packageName);
            XposedBridge.log(LD_TAG + loadPackageParam.packageName + "=== 开始hook注入app进程.");
//            XposedHelpers.findAndHookMethod(class_cloudmusic_UserPrivilege, loadPackageParam.classLoader,
//                    class_fun_fromJson, org.json.JSONObject.class, new cloudmusic_UserInfo());
            XposedHelpers.findAndHookMethod(class_cloudmusic_a, loadPackageParam.classLoader,
                    class_fun_c,String.class, new cloudmusic_c());
//            XposedHelpers.findAndHookMethod(class_cloudmusic_UserPrivilege, loadPackageParam.classLoader,
//                    class_fun_fromJson,org.json.JSONObject.class, new cloudmusic_UserInfo());
//            XposedHelpers.findAndHookMethod(class_cloudmusic_UserPrivilege, loadPackageParam.classLoader,
//                    class_fun_fromJsonForProfileList,org.json.JSONObject.class,Long.class, new cloudmusic_UserInfoList());
//            XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class,new ClassLoaderHook());
        }
    }

    public static final String pck_game = "com.ispring.gameplane";
    public static final String fun_name_game_addScore = "addScore";
    public static final String class_game_gameView = "com.ispring.gameplane.game.GameView";
    public static final String fun_name_game_explode = "explode";
    public static final String class_game_CombatAircraft = "com.ispring.gameplane.game.CombatAircraft";


    public static final String class_workspace_pakg = "com.zzm.methoddemo";
    public static final String class_workspace_activity = "com.zzm.methoddemo.MainActivity";
    public static final String class_fun_test = "hook_test";

    public static final String class_cloudmusic_pakg = "com.netease.cloudmusic";
    public static final String class_cloudmusic_a = "a.auu.a";
    public static final String class_fun_c= "c";
    public static final String class_cloudmusic_UserPrivilege = "com.netease.cloudmusic.meta.virtual.UserPrivilege";
    public static final String class_fun_fromJson= "fromJson";//JSONObject
    public static final String class_fun_fromJsonForProfileList= "fromJsonForProfileList";//JSONObject long
    public static final String class_cloudmusic_UserJson = "com.netease.cloudmusic.b.a.a";
    public static final String class_fun_UserJson_a= "a";//JSONObject boolean

}
