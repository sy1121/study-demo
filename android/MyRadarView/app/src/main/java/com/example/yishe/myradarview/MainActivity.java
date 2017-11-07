package com.example.yishe.myradarview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private RadarView radarView;
    private Button start;
    private Button end;
    private Button go;
    private Button go1;
    private Button go2;
    private  ObjectAnimator ianim;
    private ObjectAnimator oanim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radarView = (RadarView)findViewById(R.id.radar);
        start =(Button)findViewById(R.id.start);
        end =(Button)findViewById(R.id.end);
        go =(Button)findViewById(R.id.go);
        go1 =(Button)findViewById(R.id.go1);
        go2 =(Button)findViewById(R.id.go2);
        initAnimator();
        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ianim.start();
                oanim.start();
            }
        });
        end.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                radarView.setIround(0);
                radarView.setOround(0);
            }
        });
        go.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,Main2Activity.class);
                startActivity(i);
            }
        });
        go1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,Main3Activity.class);
                startActivity(i);
            }
        });
        go2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,Main5Activity.class);
                startActivity(i);
            }
        });
    }

    private void initAnimator(){
        ianim= ObjectAnimator.ofInt(radarView,"irotate",0,360);
        oanim=ObjectAnimator.ofInt(radarView,"orotate",0,360);
        ianim.setInterpolator(new AccelerateDecelerateInterpolator());
        oanim.setInterpolator(new AccelerateDecelerateInterpolator());
        ianim.setDuration(1800);
        ianim.setRepeatCount(3);
        oanim.setDuration(1600);
        oanim.setRepeatCount(4);
        ianim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                radarView.setIround(RadarView.INROUND);
            }
        });
        oanim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                radarView.setOround(RadarView.OUTROUND);
            }
        });
    }
}
