package com.ooo.xposedmodule.util;

import com.ooo.xposedmodule.hook.Hook_All_Method;
import com.ooo.xposedmodule.hook.Normal_Replace_Hook;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static com.ooo.xposedmodule.XposedModuleAction.*;

/**
 * 基于xposed的hook，封装了一层，调用方式更简单
 */
public class XpUtils {

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
        findAndHookMethod(XposedHelpers.findClass(className,apkClassLoader),methodName,new Hook_All_Method(),parameterTypes);
    }

    /**
     * 封装xposed的hook，简单化，自定义hook实现
     * @param className 要hook的类名
     * @param methodName 要hook的方法
     * @param parameterTypesAndCallback hook方法的参数和自定义的hook实现
     */
    public static void hookMethodAssignCallback(String className,String methodName,Object... parameterTypesAndCallback){
        XposedHelpers.findAndHookMethod(className, apkClassLoader, methodName, parameterTypesAndCallback);
    }

    /**
     * 封装xposed的replace method hook，简单化，自定义hook实现
     * @param className 要hook的类名
     * @param methodName 要hook的方法
     * @param parameterTypes hook方法的参数
     */
    public static void replaceMethod(String className,String methodName,Object... parameterTypes){
        findAndHookMethod(XposedHelpers.findClass(className,apkClassLoader),methodName,new Normal_Replace_Hook(),parameterTypes);
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

    public static XC_MethodHook.Unhook findAndHookMethod(Class<?> clazz, String methodName,XC_MethodHook callback ,Object... parameterTypesAndCallback) {
        Method m = XposedHelpers.findMethodExact(clazz, methodName, getParameterClasses(clazz.getClassLoader(), parameterTypesAndCallback));
        return XposedBridge.hookMethod(m, callback);
    }

    public static Class<?>[] getParameterClasses(ClassLoader classLoader, Object[] parameterTypesAndCallback) {
        Class[] parameterClasses = null;

        for(int i = parameterTypesAndCallback.length - 1; i >= 0; --i) {
            Object type = parameterTypesAndCallback[i];
            if (type == null) {
                throw new XposedHelpers.ClassNotFoundError("parameter type must not be null", (Throwable)null);
            }
            if (parameterClasses == null) {
                parameterClasses = new Class[i + 1];
            }
            if (type instanceof Class) {
                parameterClasses[i] = (Class)type;
            } else {
                if (!(type instanceof String)) {
                    throw new XposedHelpers.ClassNotFoundError("parameter type must either be specified as Class or String", (Throwable)null);
                }
                parameterClasses[i] = XposedHelpers.findClass((String)type, classLoader);
            }
        }

        if (parameterClasses == null) {
            parameterClasses = new Class[0];
        }
        return parameterClasses;
    }
}
