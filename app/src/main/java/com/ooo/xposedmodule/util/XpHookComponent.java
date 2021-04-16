package com.ooo.xposedmodule.util;

import android.app.Activity;
import android.text.TextUtils;

import com.ooo.xposedmodule.XposedModuleAction;
import com.ooo.xposedmodule.hook.Normal_Replace_Hook;
import com.ooo.xposedmodule.hook.XDebugable;
import com.ooo.xposedmodule.hook.XDexClassLoader;
import com.ooo.xposedmodule.hook.XSystemLoad;
import com.ooo.xposedmodule.hook.XTencentBugly;
import com.ooo.xposedmodule.hook.iap.google.IabHelper_launchPurchaseFlow;
import com.ooo.xposedmodule.hook.iap.google.IabHelper_logDebug;
import com.ooo.xposedmodule.hook.iap.google.IabHelper_queryInventoryAsync;
import com.ooo.xposedmodule.hook.iap.unity.GooglePlayPurchasing_Purchase;
import com.ooo.xposedmodule.hook.iap.unity.GooglePlayPurchasing_instance;
import com.ooo.xposedmodule.hook.iap.unity.UnityPlayer_UnitySendMessage;
import com.ooo.xposedmodule.hook.iap.unity.UnityPurchasing_OnProductsRetrieved;
import com.ooo.xposedmodule.hook.iap.unity.UnityPurchasing_SerialiseProducts;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.ooo.xposedmodule.XposedModuleAction.*;
import static com.ooo.xposedmodule.util.XpUtils.*;
/**
 * 一些通用的hook实现放到这里
 */
public class XpHookComponent {

    /**
     * unity和底层通讯方法
     * @param pkgname 要hook的包名
     */
    public static void unitySendMsgHook(String pkgname){
        if (!loadPackageParam.packageName.equals(pkgname))
            return;
        XposedBridge.hookAllMethods(XposedHelpers.findClass("com.unity3d.player.UnityPlayer", loadPackageParam.classLoader),
                "UnitySendMessage", new UnityPlayer_UnitySendMessage());
    }

    /**
     * google的默认内购hook
     * @param pkgname 要hook的包名
     */
    public static void iapGoogle(String pkgname){
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

    public static void iapGoogle(String iapHelperClassName,
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
    public static void iapUnity(String pkgname){
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
    public static void tencentBUglyHook(){
        hookMethod("com.tencent.bugly.crashreport.common.info.AppInfo","d",new Normal_Replace_Hook());
        hookMethod("com.tencent.bugly.crashreport.CrashReport","initCrashReport",new Normal_Replace_Hook());
    }

    public static void classLoaderHook(){
        XposedBridge.hookAllConstructors(DexClassLoader.class,new XDexClassLoader());
    }
    public static void systemLoadHook(){
        hookMethodAssignCallback(Runtime.class,"load0",Class.class,String.class,new XSystemLoad());
    }

    public static void injectFirda(){
        String path = Utils.copyLocalFile2Cache(apkContext,"frida-gadget-12.11.18-android-arm64.so");
        System.load(path);
    }
//
//    public static void hookV1V2Sign(final XC_LoadPackage.LoadPackageParam loadPackageParam){
//        Class<?> localClass = XposedHelpers.findClass("com.android.server.pm.PackageManagerService", loadPackageParam.classLoader);
//        final Class<?> packageClass = XposedHelpers.findClass("android.content.pm.PackageParser$Package", loadPackageParam.classLoader);
//        //允许降级安装
//        XposedBridge.hookAllMethods(localClass, "checkDowngrade", new XC_MethodHook() {
//            /* access modifiers changed from: protected */
//            public void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
//                Object packageInfoLite = methodHookParam.args[0];
//                Field field = packageClass.getField("mVersionCode");
//                field.setAccessible(true);
//                field.set(packageInfoLite, -1);
//            }
//        });
//        //允许要安装的apk签名和已经安装的不一致
//        XposedBridge.hookAllMethods(localClass, "compareSignatures", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                param.setResult(0);
//            }
//        });
//
//        //绕过v1签名验证
//        XposedHelpers.findAndHookMethod("java.security.Signature", loadPackageParam.classLoader,"verify",byte[].class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                XPLog.e("XSignatureVerify is work:");
//                param.setResult(Boolean.TRUE);
//            }});
//
//        //剩下的都是为了绕过v2签名验证
//        XposedHelpers.findAndHookMethod(localClass, "verifySignaturesLP",XposedHelpers.findClass("com.android.server.pm.PackageSetting",loadPackageParam.classLoader)
//                ,packageClass, XC_MethodReplacement.returnConstant(null));
//
//        //判断摘要是否相同，返回true
//        XposedHelpers.findAndHookMethod("java.security.MessageDigest", loadPackageParam.classLoader, "isEqual", byte[].class, byte[].class, new XC_MethodHook() {
//            public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
//                methodHookParam.setResult(true);
//            }
//        });
//
//        //获取当前apk签名版本，返回为v1
//        XposedBridge.hookAllMethods(XposedHelpers.findClass("android.content.pm.PackageParser", loadPackageParam.classLoader), "getApkSigningVersion", XC_MethodReplacement.returnConstant(1));
//
//
//        try {
//            //绕过系统判断是不是用的v2签名
//            Field field = XposedHelpers.findClass("android.content.pm.PackageParser$Package", loadPackageParam.classLoader).getField("SF_ATTRIBUTE_ANDROID_APK_SIGNED_ID");
//            field.setAccessible(true);
//            field.set(null,-1);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//        XposedBridge.hookAllConstructors(XposedHelpers.findClass("android.util.apk.ApkSignatureSchemeV2Verifier", loadPackageParam.classLoader), new XC_MethodHook() {
//            public void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//                Object packageInfoLite = param.thisObject;
//                Field field = XposedHelpers.findClass("android.content.pm.PackageParser$Package", loadPackageParam.classLoader).getField("SF_ATTRIBUTE_ANDROID_APK_SIGNED_ID");
//                field.setAccessible(true);
//                field.set(packageInfoLite, -1);
//            }
//        });
//
//        // 加强保护，防止从文件中删除新的签名方案(例如，APK签名方案v2)，或者{@code false}忽略任何此类保护。
//        // 设置成false，从而忽略保护
//        XposedBridge.hookAllConstructors(XposedHelpers.findClass("android.util.jar.StrictJarVerifier", loadPackageParam.classLoader), new XC_MethodHook() {
//            public void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//                param.args[3] = false;
//            }
//        });
//
//        final Object[] o = {null};
//        //如果在apk中增加了文件，hook此方法绕过找不到签名信息
//        XposedBridge.hookAllMethods(XposedHelpers.findClass("android.content.pm.PackageParser", loadPackageParam.classLoader)
//                , "loadCertificates", new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        if(null == param.getResult()){
//                            param.setResult(o[0]);
//                        }else {
//                            o[0] = param.getResult();
//                        }
//                    }
//                });
//
//        //修改是否签名的结果为true
//        XposedBridge.hookAllConstructors(XposedHelpers.findClass("android.util.jar.StrictJarFile", loadPackageParam.classLoader), new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                Class<?> clazz = XposedHelpers.findClass("android.util.jar.StrictJarFile", loadPackageParam.classLoader);
//                Field isSigned = clazz.getDeclaredField("isSigned");
//                isSigned.setAccessible(true);
//                isSigned.setBoolean(param.thisObject,true);
//            }
//        });
//
//    }
    public static void hookV1V2Sign1(final XC_LoadPackage.LoadPackageParam loadPackageParam){
        Class<?> localClass = XposedHelpers.findClass("com.android.server.pm.PackageManagerService", loadPackageParam.classLoader);
        final Class<?> packageClass = XposedHelpers.findClass("android.content.pm.PackageParser$Package", loadPackageParam.classLoader);
        //允许降级安装
        XposedBridge.hookAllMethods(localClass, "checkDowngrade", new XC_MethodHook() {
            /* access modifiers changed from: protected */
            public void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                Object packageInfoLite = methodHookParam.args[0];
                Field field = packageClass.getField("mVersionCode");
                field.setAccessible(true);
                field.set(packageInfoLite, -1);
            }
        });
        //允许要安装的apk签名和已经安装的不一致
        XposedBridge.hookAllMethods(localClass, "compareSignatures", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(0);
            }
        });

        //绕过v1签名验证
        XposedHelpers.findAndHookMethod("java.security.Signature", loadPackageParam.classLoader,"verify",byte[].class, XC_MethodReplacement.returnConstant(true));

        //剩下的都是为了绕过v2签名验证
        XposedHelpers.findAndHookMethod(localClass, "verifySignaturesLP",XposedHelpers.findClass("com.android.server.pm.PackageSetting",loadPackageParam.classLoader)
                ,packageClass, XC_MethodReplacement.returnConstant(null));

        XposedHelpers.findAndHookMethod("java.security.MessageDigest", loadPackageParam.classLoader, "isEqual", byte[].class, byte[].class, new XC_MethodHook() {
            public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                methodHookParam.setResult(true);
            }
        });

        XposedBridge.hookAllMethods(XposedHelpers.findClass("android.content.pm.PackageParser", loadPackageParam.classLoader), "getApkSigningVersion", XC_MethodReplacement.returnConstant(1));

        XposedBridge.hookAllConstructors(XposedHelpers.findClass("android.util.jar.StrictJarVerifier", loadPackageParam.classLoader), new XC_MethodHook() {
            public void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                param.args[3] = false;
            }
        });

        try {
            //绕过系统判断是不是用的v2签名
            Field field = XposedHelpers.findClass("android.content.pm.PackageParser$Package", loadPackageParam.classLoader).getField("SF_ATTRIBUTE_ANDROID_APK_SIGNED_ID");
            field.setAccessible(true);
            field.set(null,-1);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        //解决新增文件
        final Object[] o = {null};
        XposedBridge.hookAllMethods(XposedHelpers.findClass("android.content.pm.PackageParser", loadPackageParam.classLoader)
                , "loadCertificates", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if(null == param.getResult()){
                            param.setResult(o[0]);
                        }else {
                            o[0] = param.getResult();
                        }
                    }
                });
        //解决删除文件
        final Object[] o1 = {null};
        XposedBridge.hookAllMethods(XposedHelpers.findClass("android.util.jar.StrictJarFile", loadPackageParam.classLoader)
                , "findEntry", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if(null == param.getResult()){
                            param.setResult(o1[0]);
                            XPLog.e("zzres: " + param.getResult().toString());
                        }else if(o1[0] == null){
                            o1[0] = param.getResult();
                            XPLog.e("zzset: " + param.getResult().toString());
                        }
                    }
                });

        XposedBridge.hookAllConstructors(XposedHelpers.findClass("android.util.jar.StrictJarFile", loadPackageParam.classLoader), new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
//                Class<?> clazz = XposedHelpers.findClass("android.util.jar.StrictJarFile", loadPackageParam.classLoader);
                Field isSigned = param.thisObject.getClass().getDeclaredField("isSigned");
                isSigned.setAccessible(true);
                isSigned.setBoolean(param.thisObject,true);
            }
        });

    }
}
