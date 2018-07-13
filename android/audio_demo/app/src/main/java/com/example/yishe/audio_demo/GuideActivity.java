package com.example.yishe.audio_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GuideActivity extends Activity implements View.OnClickListener{


    private Button audio_record,camera_preview,decode_mp4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initWidget();
    }

    private void initWidget(){
        audio_record = findViewById(R.id.audio_record_play);
        camera_preview = findViewById(R.id.camere_preview);
        decode_mp4 = findViewById(R.id.decode_mp4);

        audio_record.setOnClickListener(this);
        camera_preview.setOnClickListener(this);
        decode_mp4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.audio_record_play:
                startActivity(new Intent(GuideActivity.this,MainActivity.class));
                break;
            case R.id.camere_preview:
                startActivity(new Intent(GuideActivity.this,CameraPreviewActivity.class));
                break;
            case R.id.decode_mp4:
                startActivity(new Intent(GuideActivity.this,Playmp4Activity.class));
                break;
        }
    }
}
