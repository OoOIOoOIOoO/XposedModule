package com.ooo.example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.ooo.xposedmodule.HookPkgNames;
import com.ooo.xposedmodule.R;
import com.ooo.xposedmodule.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

public class MainActivity extends Activity{
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.tc);
        Field[] fields = HookPkgNames.class.getDeclaredFields();
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            try {
                String value = (String)fields[i].get(null);
                jsonObject.put(value,value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Utils.savaFile(Utils.pkgList,jsonObject.toString());
        finish();
    }

}
