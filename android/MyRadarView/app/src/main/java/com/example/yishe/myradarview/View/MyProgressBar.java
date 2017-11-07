package com.example.yishe.myradarview.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.example.yishe.myradarview.R;

/**
 * Created by yishe on 2017/9/15.
 */

public class MyProgressBar extends View{
    private static final String TAG ="MyProgressBar";
    private int progress=0;
    private Paint  backPaint,progressPaint;

    private int width,height;
    public MyProgressBar(Context context){
        super(context);
        init();
    }

    public MyProgressBar(Context context,AttributeSet attributeSet){
        super(context,attributeSet);
        init();
    }

    private void  init(){
        backPaint =new Paint();
        backPaint.setAntiAlias(true);
        backPaint.setColor(Color.WHITE);
        Shader shader=new LinearGradient(0,height/2,width,height/2, R.color.shadow_blue,R.color.dark_blue, Shader.TileMode.CLAMP);
        backPaint.setShader(shader);
        backPaint.setStrokeWidth(15);
        backPaint.setStyle(Paint.Style.FILL);

        progressPaint =new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(Color.BLUE);
        progressPaint.setStrokeWidth(10);
        progressPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width =w;
        height=h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //drawBackground,
         canvas.drawLine(0.0f,(float)height/2,(float)width,(float)height/2,backPaint);
        //draw progress
        float endx = progress*width/100f;
        canvas.drawLine(0.0f,(float)height/2,(float)endx,(float)height/2,progressPaint);
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
