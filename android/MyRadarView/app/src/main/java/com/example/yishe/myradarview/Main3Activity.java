package com.example.yishe.myradarview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.yishe.myradarview.View.BaseAnimView;
import com.example.yishe.myradarview.View.MyProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class Main3Activity extends AppCompatActivity implements BaseAnimView.AnimListener {
    private static final String  TAG="Main3Activity";
    private Unbinder unbinder;
    @BindView(R.id.radarView)
    com.example.yishe.myradarview.View.RadarView radarView;
    @BindView(R.id.panel_animation)
    LinearLayout anim_panel;
    @BindView(R.id.result_panel)
    LinearLayout result_panel;
    @BindView(R.id.right_panel)
    LinearLayout rightPanel;
    @BindView(R.id.left_up_panel)
    LinearLayout left_up_panel;
    @BindView(R.id.panel_left_down)
    LinearLayout left_down_panel;
    @BindView(R.id.back_process)
    RelativeLayout back_process;
    @BindView(R.id.rubbish_file)
    RelativeLayout rubbish_file;
    @BindView(R.id.software_cash)
    RelativeLayout software_cash;
    @BindView(R.id.apk_r)
    RelativeLayout apk_r;
    @BindView(R.id.memory_progressbar)
    MyProgressBar memory_progress;
    @BindView(R.id.disk_progressbar)
    MyProgressBar disk_progress;
    private int width,height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        unbinder = ButterKnife.bind(Main3Activity.this);
    }




    @Override
    protected void onResume() {
        super.onResume();
        DisplayMetrics  displayMetrics=new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width=displayMetrics.widthPixels;
        height=displayMetrics.heightPixels;
    }

    public void startAnim(View view){
        radarView.setVisibility(View.VISIBLE);
        radarView.startAnimation();
        radarView.setmAnimListener(this);
    }

    @OnClick(R.id.end)
    public void endAnim(View view){
        radarView.fadeOutAnimation();
    }

    @Override
    public void onAnimEnd() {
        anim_panel.setVisibility(View.GONE);
        result_panel.setVisibility(View.VISIBLE);
        rightPanelInAnimation();
    }

   private void rightPanelInAnimation(){
        TranslateAnimation rightInt=new TranslateAnimation(0,0,200,0);
        rightInt.setDuration(1000);
        rightPanel.startAnimation(rightInt);
        rightInt.setAnimationListener(new AnimationAdapterListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                left_up_animation();
            }
        });
    }

    private void  left_up_animation(){
        TranslateAnimation leftUpIn=new TranslateAnimation(width/3,0,0,0);
        leftUpIn.setDuration(500);
        left_up_panel.setVisibility(View.VISIBLE);
        left_up_panel.startAnimation(leftUpIn);
        leftUpIn.setAnimationListener(new AnimationAdapterListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                memoProgressAnimator();
            }
        });
    }

    private void memoProgressAnimator(){
        ObjectAnimator oa= ObjectAnimator.ofInt(memory_progress,"progress",0,47);
        oa.setDuration(500);
        oa.start();
        oa.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                diskProgressAnimator();
            }
        });
    }

    private void diskProgressAnimator(){
        ObjectAnimator oa= ObjectAnimator.ofInt(disk_progress,"progress",0,68);
        oa.setDuration(500);
        oa.start();
        oa.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                backPreocessAnimation();
            }
        });
    }



     private void backPreocessAnimation(){
        TranslateAnimation backprocess=new TranslateAnimation(width/3,0,0,0);
        backprocess.setDuration(500);
        left_down_panel.setVisibility(View.VISIBLE);
         back_process.setVisibility(View.VISIBLE);
        back_process.startAnimation(backprocess);
        backprocess.setAnimationListener(new AnimationAdapterListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                rubbishFileAnimation();
            }
        });
    }
    private void rubbishFileAnimation(){
        TranslateAnimation backprocess=new TranslateAnimation(width/3,0,0,0);
        backprocess.setDuration(500);
        rubbish_file.startAnimation(backprocess);
        rubbish_file.setVisibility(View.VISIBLE);
        backprocess.setAnimationListener(new AnimationAdapterListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                setSoftware_cashAnimation();
            }
        });
    }

    private void setSoftware_cashAnimation(){
        TranslateAnimation backprocess=new TranslateAnimation(width/3,0,0,0);
        backprocess.setDuration(500);
        software_cash.startAnimation(backprocess);
        software_cash.setVisibility(View.VISIBLE);
        backprocess.setAnimationListener(new AnimationAdapterListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                apkrAnimation();
            }
        });
    }

    private void apkrAnimation(){
        TranslateAnimation backprocess=new TranslateAnimation(width/3,0,0,0);
        backprocess.setDuration(500);
        apk_r.startAnimation(backprocess);
        apk_r.setVisibility(View.VISIBLE);
        backprocess.setAnimationListener(new AnimationAdapterListener() {
            @Override
            public void onAnimationEnd(Animation animation) {

            }
        });
    }

    class AnimationAdapterListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }


}
