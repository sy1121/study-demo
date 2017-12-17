package com.example.yishe.immersivestatus;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  requestWindowFeature(Window.FEATURE_NO_TITLE);//remove title bar  即隐藏标题栏
        //   getSupportActionBar().hide();// 隐藏ActionBar
        setContentView(R.layout.activity_main);
        go =(Button)findViewById(R.id.go);
        go.setOnClickListener(this);
        initState();
    }
    /**
     * 沉浸式状态栏
     */
    private void initState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go:
                Intent i = new Intent(this,NoCompatActivity.class);
                startActivity(i);
                break;
        }
    }
}
