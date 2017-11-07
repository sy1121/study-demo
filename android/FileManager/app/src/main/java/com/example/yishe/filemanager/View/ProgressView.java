package com.example.yishe.filemanager.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yishe on 2017/9/23.
 */

public class ProgressView extends View{
    private static final String TAG="ProgressView";
    private Paint backgroundPaint;
    private Paint progressPaint;
    private int progress;

    private int width;
    private int height;
    private LinearGradient lg;

    public ProgressView(Context context){
        super(context);
        init();
    }
    public ProgressView(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }
    public ProgressView(Context context,AttributeSet attrs,int defStyleAttrs){
        super(context,attrs,defStyleAttrs);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height= h;
    }

    private void  init(){
        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStrokeWidth(3);
        backgroundPaint.setColor(Color.GRAY);
        backgroundPaint.setStyle(Paint.Style.FILL);

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeWidth(3);
      //  progressPaint.setColor(Color.BLUE);
        progressPaint.setStyle(Paint.Style.FILL);
        progress =0 ;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景轨道
        canvas.drawLine(0,0,width,0,backgroundPaint);
        int layid = canvas.saveLayer(0,0,width,height,null);
        //绘制progress
        float curWidth = width * ((progress+0.1f)/100);
        lg =new  LinearGradient(0,0,width,height,Color.WHITE,Color.BLUE, Shader.TileMode.CLAMP);
        progressPaint.setShader(lg);
        canvas.drawLine(0,0,curWidth,0,progressPaint);
        canvas.restoreToCount(layid);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }
}
