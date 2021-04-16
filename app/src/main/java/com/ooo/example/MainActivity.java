package com.ooo.example;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.ooo.xposedmodule.BuildConfig;
import com.ooo.xposedmodule.HookPkgNames;
import com.ooo.xposedmodule.R;
import com.ooo.xposedmodule.util.Utils;
import com.ooo.xposedmodule.util.XPLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity{
    SharedPreferences preferences = null;
    private String preferencesFileName = BuildConfig.APPLICATION_ID + "_preferences";
    @SuppressLint("WorldReadableFiles")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            preferences = this.createDeviceProtectedStorageContext().getSharedPreferences(preferencesFileName, MODE_PRIVATE);
            fixPermissionsAsync();
        } else {
            preferences = this.getSharedPreferences(preferencesFileName, MODE_WORLD_READABLE);
        }
        SharedPreferences.Editor editor = preferences.edit();
        Field[] fields = HookPkgNames.class.getDeclaredFields();
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            try {
                String value = (String)fields[i].get(null);
                editor.putString(value,value);
                jsonObject.put(value,value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        editor.putString("DEBUG_PKG",HookPkgNames.DEBUG_PKG);
        editor.apply();
        Utils.savePkgName(jsonObject.toString());
        finish();
    }
    private void fixPermissionsAsync(){
        AsyncTask.execute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                }
                File pkgFolder = MainActivity.this.createDeviceProtectedStorageContext().getFilesDir().getParentFile();
                if(pkgFolder.exists()){
                    pkgFolder.setExecutable(true,false);
                    pkgFolder.setReadable(true,false);
                    File sharedPrefsFolder = new File(pkgFolder.getAbsolutePath()+"/shared_prefs");
                    if (sharedPrefsFolder.exists()) {
                        sharedPrefsFolder.setExecutable(true, false);
                        sharedPrefsFolder.setReadable(true, false);
                        File pref = new File(sharedPrefsFolder + "/" + preferencesFileName + ".xml");
                        if (pref.exists()) {
                            pref.setReadable(true, false);
                            pref.setExecutable(true, false);
                        }
                    }
                }
            }
        });
    }
}
