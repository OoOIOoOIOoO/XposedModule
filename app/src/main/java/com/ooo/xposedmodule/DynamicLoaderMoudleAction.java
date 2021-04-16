package com.ooo.xposedmodule;

import android.app.Application;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.text.TextUtils;

import com.ooo.xposedmodule.hook.XDebugable;
import com.ooo.xposedmodule.util.Utils;
import com.ooo.xposedmodule.util.XPLog;
import com.ooo.xposedmodule.util.XpHookComponent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.PathClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

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
        if (HookPkgNames.ANDROID.equals(loadPackageParam.packageName)
                && loadPackageParam.processName.equals(HookPkgNames.ANDROID)
                && Build.VERSION.SDK_INT<=27) {
            XpHookComponent.hookV1V2Sign1(loadPackageParam);
            XPLog.i("init hook sign");
            return;
        }
        if(TextUtils.isEmpty(Utils.getXpModuleSharP(loadPackageParam.packageName))) {
            XPLog.i("init hook not hook: "+ loadPackageParam.packageName);
            return;
        }
        //将loadPackageParam的classloader替换为要hook程序Application的classloader,解决多dex导致ClassNotFound的问题
        //而且还解决了加固hook不到的问题，替换classloader之后，拿到的就是壳的classloader了
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = ((Context) param.args[0]);
                loadPackageParam.classLoader = context.getClassLoader();
                invokeHandleHookMethod(context, loadPackageParam);
            }
        });
    }

    private void invokeHandleHookMethod(Context context, LoadPackageParam loadPackageParam){
        try {
            String apkPath = Utils.getAppPath(context, thisModulePackage);
            PathClassLoader pathClassLoader = new PathClassLoader(apkPath, ClassLoader.getSystemClassLoader());
            Class<?> cls = null;
            try {
                cls = Class.forName(xposedModuleActionClass, true, pathClassLoader);
            } catch (ClassNotFoundException e) {
                cls = Class.forName(xposedModuleActionClass);
            }
            Object instance = cls.newInstance();
            Method method = cls.getDeclaredMethod("init", LoadPackageParam.class,Context.class);
            method.invoke(instance, loadPackageParam,context);
        } catch (Throwable e) {
            XPLog.i("init hook error:" + e);
        }
    }

    // 实现的接口IXposedHookZygoteInit的函数
    @Override
    public void initZygote(final IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        // /frameworks/base/core/java/android/os/Process.java
        // Hook类android.os.Process的start函数
        XPLog.e("initZygote");
        XposedBridge.hookAllMethods(android.os.Process.class, "start", new XDebugable());

    }
}
