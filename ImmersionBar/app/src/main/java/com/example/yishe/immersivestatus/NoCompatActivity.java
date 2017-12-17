package com.example.yishe.immersivestatus;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

public class NoCompatActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_compat);
        initState();
        //下面代码可以设置状态栏颜色
/*      Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorAccent));*/
    }

    /**
     * 4.4 系统设置
     *  设置透明状态栏
     *
     */
    private void initState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}
