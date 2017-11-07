package com.example.yishe.myradarview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.yishe.myradarview.View.PopStarView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main5Activity extends AppCompatActivity {

    @BindView(R.id.start)
    Button start;
    @BindView(R.id.end)
    Button end;
    @BindView(R.id.popstar)
    PopStarView popStarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        ButterKnife.bind(this);

        start.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                popStarView.startAnimation();
            }
        });
        end.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                popStarView.stopAnimation();
            }
        });
    }
}
