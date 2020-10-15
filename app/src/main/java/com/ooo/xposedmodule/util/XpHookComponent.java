package com.ooo.xposedmodule.util;

import android.app.Activity;
import android.text.TextUtils;

import com.ooo.xposedmodule.hook.iap.google.IabHelper_launchPurchaseFlow;
import com.ooo.xposedmodule.hook.iap.google.IabHelper_logDebug;
import com.ooo.xposedmodule.hook.iap.google.IabHelper_queryInventoryAsync;
import com.ooo.xposedmodule.hook.iap.unity.GooglePlayPurchasing_Purchase;
import com.ooo.xposedmodule.hook.iap.unity.GooglePlayPurchasing_instance;
import com.ooo.xposedmodule.hook.iap.unity.UnityPlayer_UnitySendMessage;
import com.ooo.xposedmodule.hook.iap.unity.UnityPurchasing_OnProductsRetrieved;
import com.ooo.xposedmodule.hook.iap.unity.UnityPurchasing_SerialiseProducts;

import java.util.List;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static com.ooo.xposedmodule.XposedModuleAction.*;

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
}
