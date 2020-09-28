package com.ooo.xposedmodule;

import android.app.Activity;
import android.text.TextUtils;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import com.ooo.xposedmodule.hook.Hook_All_Method;
import com.ooo.xposedmodule.hook.Normal_Replace_Hook;
import com.ooo.xposedmodule.hook.iap.google.IabHelper_launchPurchaseFlow;
import com.ooo.xposedmodule.hook.iap.google.IabHelper_logDebug;
import com.ooo.xposedmodule.hook.iap.google.IabHelper_queryInventoryAsync;
import com.ooo.xposedmodule.hook.iap.unity.GooglePlayPurchasing_Purchase;
import com.ooo.xposedmodule.hook.iap.unity.GooglePlayPurchasing_instance;
import com.ooo.xposedmodule.hook.iap.unity.UnityPlayer_UnitySendMessage;
import com.ooo.xposedmodule.hook.iap.unity.UnityPurchasing_OnProductsRetrieved;
import com.ooo.xposedmodule.hook.iap.unity.UnityPurchasing_SerialiseProducts;
import com.ooo.xposedmodule.util.XPLog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XposedModuleAction implements IXposedHookLoadPackage{
    public static ClassLoader apkClassLoader;
    public static XC_LoadPackage.LoadPackageParam loadPackageParam;
    public static String packageName;
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
        XPLog.i(loadPackageParam.packageName);
        this.apkClassLoader = loadPackageParam.classLoader;
        this.loadPackageParam = loadPackageParam;
        this.packageName = loadPackageParam.packageName;
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

    /**
     * unity和底层通讯方法
     * @param pkgname 要hook的包名
     */
    private void unitySendMsgHook(String pkgname){
        if (!loadPackageParam.packageName.equals(pkgname))
            return;
        XposedBridge.hookAllMethods(XposedHelpers.findClass("com.unity3d.player.UnityPlayer", loadPackageParam.classLoader),
                "UnitySendMessage", new UnityPlayer_UnitySendMessage());
    }
    /**
     * google的默认内购hook
     * @param pkgname 要hook的包名
     */
    private void iapGoogle(String pkgname){
        if (!loadPackageParam.packageName.equals(pkgname))
            return;
        String iapHelperClassNamea
                = "org.onepf.oms.OpenIabHelper";
        String iapHelperClassName
                = "org.onepf.oms.appstore.googleUtils.IabHelper";
        String consumeFinishedListenerClassName
                = iapHelperClassName+"OnConsumeFinishedListener";
        String consumeMultiFinishedListenerClassName
                = iapHelperClassName+"$OnConsumeMultiFinishedListener";
        String purchaseFinishedListenerClassName
                = iapHelperClassName+"$OnIabPurchaseFinishedListener";
        String queryInventoryFinishedListenerClassName
                = iapHelperClassName+"$QueryInventoryFinishedListener";
        iapGoogle(iapHelperClassNamea,consumeFinishedListenerClassName,consumeMultiFinishedListenerClassName,purchaseFinishedListenerClassName,queryInventoryFinishedListenerClassName);
    }
    private void iapGoogle(String iapHelperClassName,
                           String consumeFinishedListenerClassName,
                           String consumeMultiFinishedListenerClassName,
                           String purchaseFinishedListenerClassName,
                           String queryInventoryFinishedListenerClassName){
        if(!TextUtils.isEmpty(consumeFinishedListenerClassName) && !TextUtils.isEmpty(consumeMultiFinishedListenerClassName)) {
            Class consumeFinishedListener = XposedHelpers.findClass(consumeFinishedListenerClassName, apkClassLoader);
            Class consumeMultiFinishedListener = XposedHelpers.findClass(consumeMultiFinishedListenerClassName, apkClassLoader);
            XposedHelpers.findAndHookMethod(iapHelperClassName, loadPackageParam.classLoader,
                    "consumeAsyncInternal", List.class, consumeFinishedListener, consumeMultiFinishedListener, new IabHelper_queryInventoryAsync());
        }
        if(!TextUtils.isEmpty(queryInventoryFinishedListenerClassName)) {
            Class queryInventoryFinishedListener = XposedHelpers.findClass(queryInventoryFinishedListenerClassName, apkClassLoader);
            XposedHelpers.findAndHookMethod(iapHelperClassName, loadPackageParam.classLoader,
                    "queryInventoryAsync", boolean.class, List.class, queryInventoryFinishedListener, new IabHelper_queryInventoryAsync());
        }
        if(!TextUtils.isEmpty(purchaseFinishedListenerClassName)) {
            Class purchaseFinishedListener = XposedHelpers.findClass(purchaseFinishedListenerClassName, apkClassLoader);
            XposedHelpers.findAndHookMethod(XposedHelpers.findClass(iapHelperClassName, loadPackageParam.classLoader),
                    "launchPurchaseFlow", Activity.class, String.class, String.class, int.class,purchaseFinishedListener, String.class, new IabHelper_launchPurchaseFlow());
        }
        XposedBridge.hookAllMethods(XposedHelpers.findClass(iapHelperClassName, loadPackageParam.classLoader),
                "logDebug", new IabHelper_logDebug());
    }

    /**
     * unity的默认内购hook
     * @param pkgname 要hook的包名
     */
    private void iapUnity(String pkgname){
        if (!loadPackageParam.packageName.equals(pkgname))
            return;
        XposedBridge.hookAllMethods(XposedHelpers.findClass("com.unity.purchasing.common.UnityPurchasing", loadPackageParam.classLoader),
                "SerialiseProducts", new UnityPurchasing_SerialiseProducts());
        XposedBridge.hookAllMethods(XposedHelpers.findClass("com.unity.purchasing.common.UnityPurchasing", loadPackageParam.classLoader),
                "OnProductsRetrieved", new UnityPurchasing_OnProductsRetrieved());
        XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.unity.purchasing.googleplay.GooglePlayPurchasing", loadPackageParam.classLoader),
                "Purchase", XposedHelpers.findClass("com.unity.purchasing.common.ProductDefinition",loadPackageParam.classLoader),
                String.class,new GooglePlayPurchasing_Purchase());
        XposedBridge.hookAllMethods(XposedHelpers.findClass("com.unity.purchasing.googleplay.GooglePlayPurchasing", loadPackageParam.classLoader),
                "instance", new GooglePlayPurchasing_instance());
    }

    /**
     * hook 类中的所有方法
     * @param className 要hook的类名
     * @param notHookMethodName 需要过滤的方法名
     */
    public static void hookAllMethods(String className,String... notHookMethodName){
        List<String> notMethod = new ArrayList<>();
        if(notHookMethodName.length>0)
            notMethod = Arrays.asList(notHookMethodName);
        XPLog.e("hook class " +className);
        Class c = XposedHelpers.findClass(className, apkClassLoader);
        Method[] methods = c.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            XPLog.e("hook method " + methods[i].getName());
            methods[i].setAccessible(true);
            if(!notMethod.contains(methods[i].getName()))
            XposedBridge.hookAllMethods(c, methods[i].getName(), new Hook_All_Method());
        }
    }
    /**
     * 封装xposed的hook，此方法hook类中所有同名method
     * @param className 要hook的类名
     * @param methodName 要hook的方法
     */
    public static void hookMethod(String className,String methodName){
        XposedBridge.hookAllMethods(XposedHelpers.findClass(className, apkClassLoader),methodName, new Hook_All_Method());
    }
    /**
     * 封装xposed的hook，简单化，自定义hook实现
     * @param className 要hook的类名
     * @param methodName 要hook的方法
     * @param parameterTypes hook方法的参数
     */
    public static void hookMethod(String className,String methodName,Object... parameterTypes){
        XposedHelpers.findAndHookMethod(className, apkClassLoader,
                methodName, parameterTypes, new Hook_All_Method());
    }
    /**
     * 封装xposed的hook，简单化，自定义hook实现
     * @param className 要hook的类名
     * @param methodName 要hook的方法
     * @param parameterTypesAndCallback hook方法的参数和自定义的hook实现
     */
    public static void hookMethodAssignCallback(String className,String methodName,Object... parameterTypesAndCallback){
        XposedHelpers.findAndHookMethod(className, apkClassLoader,
                methodName, parameterTypesAndCallback);
    }
    /**
     * 封装xposed的replace method hook，简单化，自定义hook实现
     * @param className 要hook的类名
     * @param methodName 要hook的方法
     * @param parameterTypes hook方法的参数
     */
    public static void replaceMethod(String className,String methodName,Object... parameterTypes){
        XposedHelpers.findAndHookMethod(className,apkClassLoader,
                methodName, parameterTypes, new Normal_Replace_Hook());
    }
    /**
     * 封装xposed的replace method hook，简单化，自定义hook实现
     * @param className 要hook的类名
     * @param methodName 要hook的方法
     * @param parameterTypesAndCallback hook方法的参数和自定义的hook实现
     */
    public static void replaceMethodAssignCallback(String className,String methodName,Object... parameterTypesAndCallback){
        XposedHelpers.findAndHookMethod(className,apkClassLoader,
                methodName, parameterTypesAndCallback);
    }

    /**
     * hook 动态加载dex的class
     * @param className
     * @param classLoader
     */
    public static void hookDynamicDex(String className, ClassLoader classLoader){
        XPLog.e("hookDynamicDex class " + className);
        Class c = XposedHelpers.findClass(className, classLoader);
        Method[] methods = c.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            XPLog.e("hookDynamicDex method " + methods[i].getName());
            methods[i].setAccessible(true);
            XposedBridge.hookAllMethods(c, methods[i].getName(), new Hook_All_Method());
        }
    }
}
