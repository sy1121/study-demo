package com.example.yishe.myradarview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.example.yishe.myradarview.View.LeafProgressBar;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main4Activity extends AppCompatActivity {
    @BindView(R.id.fenshan)
    ImageView fenshan;
    @BindView(R.id.leaf_progress)
    LeafProgressBar leafProgressBar;
    private static final int REFRESH_PROGRESS = 0x10;
    private int mProgress =0;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REFRESH_PROGRESS:
                    if(mProgress<40){
                        mProgress+=1;
                        // 随机800ms以内刷新一次
                        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS,new Random().nextInt(800));
                        leafProgressBar.setmProgress(mProgress);
                    }else{
                        mProgress +=1;
                        //随机1200ms以内刷新一次
                        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS,new Random().nextInt(1200));
                        leafProgressBar.setmProgress(mProgress);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        ButterKnife.bind(this);
        initViews();
        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS,3000);
    }

    private void initViews(){
        RotateAnimation rotateAnimation =new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(2000);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        fenshan.startAnimation(rotateAnimation);
    }


}
