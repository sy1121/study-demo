package com.example.yishe.testacceball;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private AcceBallView ballView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private  void initView(){
        ballView =(AcceBallView)findViewById(R.id.ballView);
        ballView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ballView.setCurRate(Math.abs(new Random(System.currentTimeMillis()).nextInt(30)+30));//Math.abs(new Random(System.currentTimeMillis()).nextInt(30)+30)
            }
        });
    }
}
