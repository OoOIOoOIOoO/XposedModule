package com.ooo.example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.ooo.xposedmodule.R;

public class MainActivity extends Activity{
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.tc);
        new Thread(){
            @Override
            public void run() {
                super.run();
                
            }
        }.start();
    }
}
