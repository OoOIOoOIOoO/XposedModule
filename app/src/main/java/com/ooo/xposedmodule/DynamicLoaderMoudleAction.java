package com.ooo.xposedmodule;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.ooo.xposedmodule.hook.XDebugable;
import com.ooo.xposedmodule.util.Utils;
import com.ooo.xposedmodule.util.XPLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.PathClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static com.ooo.xposedmodule.HookPkgNames.TAG;
import static de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * @author OoO
 * 由于每次都要手动加载模块的apk,所以性能会有损耗
 * 调试完成可以将assets/xposed_init的类改为xposedModuleActionClass
 */

public class DynamicLoaderMoudleAction implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    //    该模块的包名,移植请更换
    private final String thisModulePackage = "com.ooo.xposedmodule";
    //    进行hook处理的类,将assets/xposed_init的类改为这个,就变为了重启后模块生效
    private String xposedModuleActionClass = XposedModuleAction.class.getName();

    @Override
    public void handleLoadPackage(final LoadPackageParam loadPackageParam) throws Throwable {
        if(!(new JSONObject(Utils.readFile(Utils.pkgList))).has(loadPackageParam.packageName)) {
           return;
        }
        //将loadPackageParam的classloader替换为要hook程序Application的classloader,解决多dex导致ClassNotFound的问题
        //而且还解决了加固hook不到的问题，替换classloader之后，拿到的就是壳的classloader了
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = ((Context) param.args[0]);
                loadPackageParam.classLoader = context.getClassLoader();
                invokeHandleHookMethod(context, thisModulePackage, xposedModuleActionClass, loadPackageParam);
            }
        });
    }

    private void invokeHandleHookMethod(Context context, String thisAppPackage, String xposedModuleActionClass, LoadPackageParam loadPackageParam) throws Throwable {
        String apkPath = getAppPath(context, thisAppPackage);
        PathClassLoader pathClassLoader = new PathClassLoader(apkPath, ClassLoader.getSystemClassLoader());
        Class<?> cls = Class.forName(xposedModuleActionClass, true, pathClassLoader);
        Object instance = cls.newInstance();
        Method method = cls.getDeclaredMethod("handleLoadPackage", LoadPackageParam.class);
        method.invoke(instance, loadPackageParam);
    }

    private String getAppPath(Context context, String thisModulePackage) {
        String apkPath = null;
        try {
            apkPath = context.getPackageManager().getApplicationInfo(thisModulePackage, 0).publicSourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getPackageManager not found" + e);
        }
        if (new File(apkPath).exists())
            return apkPath;
        Log.e(TAG, "加载" + thisModulePackage + "出错,未找到apk文件");
        return null;
    }

    // 实现的接口IXposedHookZygoteInit的函数
    @Override
    public void initZygote(final IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        // /frameworks/base/core/java/android/os/Process.java
        // Hook类android.os.Process的start函数
        Log.e(TAG, "initZygote");
        XposedBridge.hookAllMethods(android.os.Process.class, "start", new XDebugable());
    }
}
