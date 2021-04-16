package com.ooo.xposedmodule;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import com.ooo.xposedmodule.util.Utils;
import com.ooo.xposedmodule.util.XPLog;

import org.json.JSONObject;

import java.util.HashMap;

import static com.ooo.xposedmodule.Module.initFrames;
import static com.ooo.xposedmodule.util.XpUtils.*;
import static com.ooo.xposedmodule.util.XpHookComponent.*;

public class XposedModuleAction implements IXposedHookLoadPackage{
    public static ClassLoader apkClassLoader, apkDynamicClassLoader;
    public static XC_LoadPackage.LoadPackageParam loadPackageParam;
    public static String packageName;
    public static Context apkContext;
    public void init(XC_LoadPackage.LoadPackageParam loadPackageParam, Context context){
        String pidTime = String.valueOf(android.os.Process.myPid());
        String pidIsHook = Utils.getSharP(context,loadPackageParam.packageName);
        if(!TextUtils.isEmpty(pidIsHook) && pidTime.equals(pidIsHook)) {
            XPLog.i("hook module is already initialized:"+loadPackageParam.packageName + this);
            return;
        } else {
            Utils.setSharP(context, loadPackageParam.packageName, pidTime);
            XPLog.i("init hook module:" + loadPackageParam.packageName + this);
        }
        this.apkContext = context;
        handleLoadPackage(loadPackageParam);
    }

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
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam){
        this.apkClassLoader = loadPackageParam.classLoader;
        this.loadPackageParam = loadPackageParam;
        this.packageName = loadPackageParam.packageName;

        if(packageName.equals(HookPkgNames.ARCFACE)){
            hookMethod("com.arcsoft.arcfacedemo.activity.RegisterAndRecognizeActivity","searchFace");
        }
        if(packageName.equals(HookPkgNames.EXAMPLE)) {
            //hook a中的所有方法
            hookAllMethods("com.test.a");
            //hook a中的所有方法,add除外
            hookAllMethods("com.test.a","add");
            //hook a中的add方法
            hookMethod("com.test.a","add");
            //hook a中指定参数类型的add方法
            hookMethod("com.test.a","add",int.class,int.class);
            //hook 替换 a中的add方法
            replaceMethod("com.test.a","add",int.class,int.class);
        }
    }
}
