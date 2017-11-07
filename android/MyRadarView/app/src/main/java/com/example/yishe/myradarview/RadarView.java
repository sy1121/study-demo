package com.example.yishe.myradarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by yishe on 2017/9/8.
 */

public class RadarView extends View{
    private static final String TAG="RadarView";
    public static final int INROUND=2;
    public  static final int OUTROUND=3;
    private Paint sixEdgePaint,inPaint,outPaint;
    private float mWidth;
    private SweepGradient isg,osg;
    private Matrix imatrix,omatrix;
    private int irotate=0,orotate=0; //转过的角度
    private float f=1.0f;
    private int inPathWidth=30;
    private int outPathWidth=10;
    private int iround=0,oround=0;
    private int maxiround=0,maxoround=0;
    public RadarView(Context context){
        this(context,null);
    }

    public RadarView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public RadarView(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
        init(context);
        //读取属性
        TypedArray typedArray= context.obtainStyledAttributes(attrs,R.styleable.RadarView);
        maxiround=typedArray.getInteger(R.styleable.RadarView_iround,0);
        maxoround=typedArray.getInteger(R.styleable.RadarView_oround,0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float width=getMeasuredWidth();
        float height=getMeasuredHeight();
        mWidth =(float)((Math.min(width,height)+0.5)*0.5);
    }

    private void init(Context context ){

        //六边形画笔
        sixEdgePaint =new Paint();
        sixEdgePaint.setStyle(Paint.Style.FILL);
        sixEdgePaint.setAntiAlias(true);
        int colorInner= Color.WHITE;
        int colorOuter=Color.BLUE;
        RadialGradient rg=new RadialGradient(0,0,300.0f,colorInner,colorOuter, Shader.TileMode.CLAMP);
        sixEdgePaint.setShader(rg);
        //内圈画笔
        inPaint=new Paint();
        inPaint.setStyle(Paint.Style.STROKE);
        inPaint.setStrokeWidth(inPathWidth);
        isg=new SweepGradient(0,0,new int[]{Color.GREEN,Color.WHITE,Color.GREEN,Color.WHITE,Color.GREEN,Color.WHITE},new float[]{0,1.0f/6,2.0f/6,3.0f/6,4.0f/6,5.0f/6});
        inPaint.setShader(isg);
        //外圈画笔
        outPaint=new Paint();
        outPaint.setStyle(Paint.Style.STROKE);
        outPaint.setStrokeWidth(outPathWidth);
        osg=new SweepGradient(0,0,new int[]{Color.WHITE,Color.GREEN,Color.WHITE,Color.GREEN,Color.WHITE,Color.GREEN},new float[]{0,1.0f/6,2.0f/6,3.0f/6,4.0f/6,5.0f/6});
        outPaint.setShader(osg);
        imatrix=new Matrix();
        omatrix=new Matrix();
    }

    public void setIround(int iround) {
        this.iround = iround;
    }

    public void setOround(int oround) {
        this.oround = oround;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画六边形
        drawSixEdge(canvas,sixEdgePaint,0.6f);
        if(iround!=INROUND)
        drawInLine(canvas);
        if(oround!=OUTROUND)
        drawOutLine(canvas);
        invalidate();
    }

    private void drawSixEdge(Canvas canvas,Paint paint,float ratio){
        canvas.save();
        canvas.translate(getMeasuredWidth()/2,getMeasuredHeight()/2);
        float r =(float)(mWidth*ratio);
        float[] points={r,0};
        Path path=new Path();
        path.moveTo(r,0);
        Matrix matrix=new Matrix();
        float[] dst=new float[2];
        for(int i=0;i<6;i++){
            matrix.postRotate(60);
            matrix.mapPoints(dst,points);
            path.lineTo(dst[0],dst[1]);
        }
        canvas.drawPath(path,paint);
        path.reset();
        canvas.restore();
    }

    private void drawInLine(Canvas canvas){
        imatrix.setRotate(irotate);
        isg.setLocalMatrix(imatrix);
        Log.i(TAG,"irotate ="+irotate);
        //irotate+=10;
        if(irotate>=120&&irotate<240){
            f=1.1f;
            inPaint.setStrokeWidth(inPathWidth*0.8f);
        }else if(irotate>=240&&irotate<360){
            f=1.2f;
            inPaint.setStrokeWidth(inPathWidth*0.7f);
        }
        if(irotate>=360){
            irotate=irotate-360;
            f=1.0f;
            inPaint.setStrokeWidth(inPathWidth);
            iround++;
        }
        drawSixEdge(canvas,inPaint,0.7f*f);
    }

    private void drawOutLine(Canvas canvas){
        omatrix.setRotate(orotate);
        osg.setLocalMatrix(omatrix);
        //orotate+=12;
        if(orotate>=120&&orotate<240){
            f=1.1f;
            outPaint.setStrokeWidth(outPathWidth*0.8f);
        }else if(orotate>=240&&orotate<360){
            f=1.2f;
            outPaint.setStrokeWidth(outPathWidth*0.7f);
        }
        if(orotate>=360){
            orotate=orotate-360;
            f=1.0f;
            outPaint.setStrokeWidth(outPathWidth);
            oround++;
        }
        drawSixEdge(canvas,outPaint,0.8f*f);
    }

    public int getIrotate() {
        return irotate;
    }

    public void setIrotate(int irotate) {
        this.irotate = irotate;
    }

    public int getOrotate() {
        return orotate;
    }

    public void setOrotate(int orotate) {
        this.orotate = orotate;
    }



}
