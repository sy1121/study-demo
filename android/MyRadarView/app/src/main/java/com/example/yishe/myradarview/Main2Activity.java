package com.example.yishe.myradarview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.yishe.myradarview.View.QScoreView;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG="Main2Activity";
    private NumberView bit;
    private NumberView ten;
    private Button start;
    private Unbinder unbinder;
    @BindView(R.id.qscore)
    QScoreView qScoreView;

    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int target =msg.arg1;
            Log.i(TAG,"msg.what = "+ msg.what+" msg.arg1="+msg.arg1);
            switch (msg.what){
                case 1:
                    bit.setTarget(target);
                    break;
                case 2:
                    ten.setTarget(target);
                    break;
                case 0:
                    bit.invalidate();
                    ten.invalidate();
                    break;
                default:
                    bit.setTarget(0);
                    ten.setTarget(0);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bit =(NumberView)findViewById(R.id.numberView);
        ten =(NumberView)findViewById(R.id.numberView2);
        new ComputeThread().start();
        start =(Button)findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* bit.setTarget(6);
                ten.setTarget(4);*/
               //new ComputeThread().start();
                Random random =new Random();
                random.setSeed(System.currentTimeMillis());
                int score= Math.abs(random.nextInt()%100);
                qScoreView.setScore(score,false);
            }
        });
        unbinder = ButterKnife.bind(this);
        qScoreView.setScore(13,false);
    }

    class ComputeThread extends Thread{

        @Override
        public void run() {
            //start
           // handler.sendEmptyMessage(0);
            Random r=new Random();
            r.setSeed(System.currentTimeMillis());
            int bit=Math.abs(r.nextInt())%10;
            int ten=Math.abs(r.nextInt())%10;
            Log.i(TAG,"bit ="+bit);
            Log.i(TAG,"ten ="+ten);
            Message message1=handler.obtainMessage(1);
            message1.arg1=bit;
            handler.sendMessageDelayed(message1,3000);
            Message message2=handler.obtainMessage(2);
            message2.arg1=ten;
            handler.sendMessageDelayed(message2,5000);
        }
    }
}
