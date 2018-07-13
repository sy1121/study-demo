package com.example.yishe.audio_demo;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreviewActivity extends Activity implements SurfaceHolder.Callback{
  /*  参考: 从camera中取源编码生成mp4
    https://github.com/zhongjihao/AVMediaCodecMP4*/
    private SurfaceView mSurfaceView;
    private Camera camera;
    private VideoEncoder videoEncoder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);
        mSurfaceView = findViewById(R.id.preview_view);
        mSurfaceView.getHolder().addCallback(this);

        //打开摄像头并将展示方向旋转90度
        camera =Camera.open();
        camera.setDisplayOrientation(90);


        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);


        camera.setParameters(parameters);
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                videoEncoder.pullVideoData(data);
            }
        });


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try{
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            videoEncoder =new VideoEncoder(mSurfaceView.getWidth(),mSurfaceView.getHeight());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
            camera.release();
    }



}
