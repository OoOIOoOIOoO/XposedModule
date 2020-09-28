package com.ooo.xposedmodule.util;

import com.ooo.xposedmodule.XposedModuleAction;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedHelpers;

public class Utils {
    public final static String pkgList = "/data/local/tmp/xposedHookList";
    /// 写入文件的方法
    public static void savaFile(String filename, String filecontent) {
        try {
            //这里就不要用openFileOutput了,那个是往手机内存中写数据的
            FileOutputStream output = new FileOutputStream(filename);
            output.write(filecontent.getBytes());
            //将String字符串以字节流的形式写入到输出流中
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
//            Util.ShowMyLog( "写文件错误"+e.toString());
        }
        //关闭输出流
    }
    public static String readFile(String filename) {
        StringBuilder sb = new StringBuilder("");
        try {
            //打开文件输入流
            FileInputStream input = new FileInputStream(filename);
            byte[] temp = new byte[1024];

            int len = 0;
            //读取文件内容:
            while ((len = input.read(temp)) > 0) {
                sb.append(new String(temp, 0, len));
            }
            //关闭输入流
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    public static void unitySendMessage(String objectName, String methodName, String parmsData) {
        try {
            Class<?> UnityPlayer = XposedHelpers.findClass("com.unity3d.player.UnityPlayer", XposedModuleAction.apkClassLoader);
            Method mUnitySend = UnityPlayer.getDeclaredMethod("UnitySendMessage", String.class, String.class, String.class);
            mUnitySend.invoke(null, objectName, methodName, parmsData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
