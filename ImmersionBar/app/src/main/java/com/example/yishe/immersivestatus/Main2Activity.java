package com.example.yishe.immersivestatus;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yishe.immersivestatus.View.MyToolBar;

public class Main2Activity extends Activity implements MyToolBar.OnToolbarClickListener{

    private MyToolBar  myToolBar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        myToolBar = (MyToolBar)findViewById(R.id.my_toolbar);
        myToolBar.setmOnToolbarClickListener(this);
        textView = (TextView)findViewById(R.id.toolbar_title);
        textView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(Main2Activity.this,"点击了子View",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLeftBtnClick() {
        finish();
    }

    @Override
    public void onRightBtn1Click() {
        Toast.makeText(this,"点击了右边第一个Btn",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRightBtn2Click() {
        Toast.makeText(this,"点击了右边第二个Btn",Toast.LENGTH_SHORT).show();
    }
}
