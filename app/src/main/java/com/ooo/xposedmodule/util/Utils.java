package com.ooo.xposedmodule.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.ooo.example.MainActivity;
import com.ooo.xposedmodule.HookPkgNames;
import com.ooo.xposedmodule.XposedModuleAction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

public class Utils {
    public static final String TAG = "OoOXPhook";
    public final static String pkgList = "/data/local/tmp/xposedHookList";
    public static int count = 0;
    public static String getXpModuleSharP(String key){
        XSharedPreferences pref = null;
        File prefsFileProt = new File("/data/user_de/0/com.ooo.xposedmodule/shared_prefs/com.ooo.xposedmodule_preferences.xml");
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                pref = new XSharedPreferences(prefsFileProt);
            } else {
                pref = new XSharedPreferences("com.ooo.xposedmodule");
            }
        } catch (Exception e) {
            XPLog.e("getXpModuleSharP:"+e);
            return "";
        }
        String result = pref.getString(key,"");
        if(TextUtils.isEmpty(result))
            result = getPkgName(key);
        return result;
    }
    public static String getPkgName(String pkg){
        if(new File(pkgList).canWrite()){
            String json = readFile(pkgList);
            try {
                return new JSONObject(json).optString(pkg,"");
            } catch (JSONException e) {
                XPLog.e("getPkgName:"+e);
            }
        }
        return "";
    }
    public static void savePkgName(String pkg){
        if(!new File(pkgList).canWrite()){
            XPLog.e("zzm touch strt:");
            exec("touch "+pkgList);
            XPLog.e("zzm touch end:");
            exec("chmod 777 "+pkgList);
        }
        savaFile(pkgList,pkg);
    }
    public static String getSharP(Context context, String key){
        SharedPreferences setting = context.getSharedPreferences("OoOSP", 0);
        return setting.getString(key,"");
    }
    public static void setSharP(Context context, String key, String value){
        context.getSharedPreferences("OoOSP", 0).edit().putString(key, value).apply();
    }
    public static String getAppPath(Context context, String thisModulePackage) {
        String apkPath = null;
        try {
            apkPath = context.getPackageManager().getApplicationInfo(thisModulePackage, 0).publicSourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getPackageManager not found" + e);
        }
        if (new File(apkPath).exists())
            return apkPath;
        XPLog.i("加载" + thisModulePackage + "出错,未找到apk文件");
        return null;
    }

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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Bitmap getImage(Context context, String name) {
        File file = null;
        try {
            file = new File( Environment.getExternalStorageDirectory().getCanonicalPath() + "/."+name+".jpg" );
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            Bitmap bitmap  = BitmapFactory.decodeStream(fis);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static Bitmap getImage(File file) {
        if(null == file)
            return null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            Bitmap bitmap  = BitmapFactory.decodeStream(fis);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static String copyDex2Cache(Context context, String fileName) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String path = context.getCacheDir().getAbsolutePath() + "/" + fileName;
        try {
            File file = new File(path);
            if (!file.exists()) {
                inputStream = context.getAssets().open(fileName);
                outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return path;
    }
    public static String copyLocalFile2Cache(Context context, String fileName) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String path = context.getCacheDir().getAbsolutePath() + "/" + fileName.substring(fileName.lastIndexOf("/")+1);
        Log.e("test",path);
        try {
            File file = new File(path);
            if (!file.exists()) {
                inputStream = new FileInputStream(new File("/data/local/tmp/"+fileName));
                outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return path;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Bitmap[] getImageList(String dir) {
        Bitmap[] bitmaps = new Bitmap[0];
        try {
            dir = Environment.getExternalStorageDirectory().getCanonicalPath() + "/"+dir;
        } catch (IOException e) {
            e.printStackTrace();
            return bitmaps;
        }
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println(dir + "不存在！");
            return null;
        }
        File[] files = dirFile.listFiles();
        bitmaps = new Bitmap[files.length];
        for (int i = 0; i < files.length; i++) {
            Bitmap bitmap = getImage(files[i]);
            if (bitmap != null)
                bitmaps[i] = bitmap;
        }
        return bitmaps;
    }
    public static String getLocalIpAddress() {
        new StringBuilder();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
                while (true) {
                    if (inetAddresses.hasMoreElements()) {
                        InetAddress nextElement = inetAddresses.nextElement();
                        if (!nextElement.isLoopbackAddress() && !nextElement.getHostAddress().toString().contains("::")) {
                            return nextElement.getHostAddress().toString();
                        }
                    }
                }
            }
            return null;
        } catch (SocketException unused) {
            return null;
        }
    }

    public static String getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static byte[] getImage(String path){
        byte[] data = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = fis.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            fis.close();
        } catch (Exception ex1) {
            ex1.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }
    public static String exec(String command) {
        Process process = null;
        BufferedReader reader = null;
        InputStreamReader is = null;
        DataOutputStream os = null;

        try {
            process = Runtime.getRuntime().exec("su");
            is = new InputStreamReader(process.getInputStream());
            reader = new BufferedReader(is);
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            int read;
            char[] buffer = new char[4096];
            StringBuilder output = new StringBuilder();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            process.waitFor();
            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }

                if (reader != null) {
                    reader.close();
                }

                if (is != null) {
                    is.close();
                }

                if (process != null) {
                    process.destroy();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
