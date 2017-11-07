package com.example.yishe.myradarview.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yishe on 2017/9/13.
 */

public class RadarView extends BaseAnimView implements  IAnimView{
    private static final String TAG=RadarView.class.getSimpleName().toString();
    private Paint mHexagonPaint; //多边形画笔
    private SweepGradient mSweepGradient;
    private Paint mCenterRectPaint;
    private Path mPath;
    private List<Hexagon> mHexagons;
    private int HEXAGON_STROKE_MAX;
    private DecelerateInterpolator mInterpolator;
    private float mRadio;
    private boolean mPauseDecelerate;

    private Handler mLogicHander;
    private static final int MSG_LOGIC_CYCLE =2;
    private static final int MSG_LOGIC_END =3;
    private static final int MSG_LOGIC_STOP_RUN =4;

    private static final int COLOR_0=0x00ffffff;
    private static final int COLOR_100=Color.WHITE;

    private RectF mCanvasRect,mCenterMashRect;

    private int mDistanceMax;
    private int mHexagonMargin;
    private int mCenterX,mCenterY,mRadiu,mStartRadiu,mAlphaRadiu;
    private static final float  SQRT3   = (float)Math.sqrt(3);
    //用于旋转扫描渐变的矩阵
    Matrix mMatrix =new Matrix();
    int mShaderDegrees=0; //渐变旋转的角度
    //六边形数量的最大值
    private static final int HEXAGON_COUNT=2;
    //动画周期时间
    private static final  int ROTATE_TIME=1000;
    private static final int TRANSLATE_TIME=2000;
    //每帧动画停留事件
    private static final int FRAME_TIME= 40;
    // 动画帧数
    private static final int FRAME_COUNT = TRANSLATE_TIME/FRAME_TIME;
    private static final int ROTATE_COUNT = ROTATE_TIME/FRAME_TIME;

    private static final int STATE_STOP= 1;
    private static final int STATE_RUNNING=2;
    private static final int STATE_FADE_OUT=3;
    private int mState = STATE_STOP;
    private boolean isFix = false;
    private Handler mHandler =new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            mHexagons.clear();
            List<Hexagon> list =(List<Hexagon>) msg.obj;
            if(list != null){
                for(int i=0;i<list.size();i++){
                    mHexagons.add(list.get(i));
                }
            }
            Log.i(TAG,"mHexagons size = "+mHexagons.size());
            invalidate();
        }
    };

    public RadarView(Context context){
        super(context);
        init(context);
    }
    public RadarView(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context);
    }
    public RadarView(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
        init(context);
    }

    public void init(Context context){
         mHexagonPaint =new Paint();
        mHexagonPaint.setStrokeWidth(10);
        mHexagonPaint.setColor(Color.WHITE);
        mHexagonPaint.setStyle(Paint.Style.STROKE);
        mHexagonPaint.setAntiAlias(true);
        mSweepGradient =new SweepGradient(0,0,
                new int[]{COLOR_0,COLOR_0,COLOR_100,COLOR_0,COLOR_0,COLOR_100,COLOR_0,COLOR_0,COLOR_100},
                new float[]{0f,0.083f,0.333f,0.334f,0.417f,0.666f,0.667f,0.075f,1f});  //??
        mHexagonPaint.setShader(mSweepGradient);
        mCenterRectPaint =new Paint(mHexagonPaint);
        mCenterRectPaint.setShader(null);
        mCenterRectPaint.setStyle(Paint.Style.FILL);
        mCenterRectPaint.setStrokeWidth(0);

        mPath=new Path();
        mHexagons = new ArrayList<Hexagon>();
        HEXAGON_STROKE_MAX =dp2px(context,6);
        mInterpolator = new DecelerateInterpolator(2f);
    }

    private int dp2px(Context context,float dpVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpVal,context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mCenterX = getWidth()/2;
        mCenterY = getHeight()/2;

        mRadiu = mCenterX;
        mStartRadiu = mRadiu * 2/3;
        mAlphaRadiu = mStartRadiu + (mRadiu -mStartRadiu)/10;
        mHexagonMargin = (mRadiu -mStartRadiu)/HEXAGON_COUNT;
        mCanvasRect = new RectF(0,0,getWidth(),getHeight());
        mDistanceMax = mRadiu - mStartRadiu;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLUE);
        int layid=canvas.saveLayer(mCanvasRect,null,Canvas.MATRIX_SAVE_FLAG|Canvas.CLIP_SAVE_FLAG|Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
            |Canvas.FULL_COLOR_LAYER_SAVE_FLAG|Canvas.CLIP_TO_LAYER_SAVE_FLAG);
        //画六边形
        canvas.translate(mCenterX,mCenterY);
        for(int i=0;i<mHexagons.size();i++){
            mMatrix.setRotate(mShaderDegrees+i*30);
            mSweepGradient.setLocalMatrix(mMatrix);
            Hexagon h=mHexagons.get(i);
            mHexagonPaint.setStrokeWidth(h.width);
            mHexagonPaint.setAlpha(getAlpha(h.radiu));
            Log.i(TAG,"before draw path "+h.radiu);
            canvas.drawPath(h.path,mHexagonPaint);
        }
        canvas.restoreToCount(layid);
        mShaderDegrees = mShaderDegrees+(int)(360/ROTATE_COUNT * mRadio);//? 这里为什么要乘以mRadiu
        if(mShaderDegrees>=360){
            mShaderDegrees =mShaderDegrees -360;
        }
    }

    private int getAlpha(int radiu){
        Log.i(TAG,"radiu ="+radiu+" mStartRadiu="+mStartRadiu+" mAlphaRadiu="+mAlphaRadiu+" mRadiu="+mRadiu);
        if(radiu < mStartRadiu){
            return 0;
        }else if(radiu<mAlphaRadiu){
            return (radiu -mStartRadiu)*255/(mAlphaRadiu- mStartRadiu);
        }else if(radiu<mRadiu){
            return  (mRadiu - radiu)*255/(mRadiu-mAlphaRadiu);
        }else{
            return 0;
        }
    }

    @Override
    public void SetVisibility(int visibility) {
         super.setVisibility(visibility);
    }

    @Override
    public void startAnimation() {
        mPauseDecelerate =false;
        if(mLogicHander == null){
            mLogicHander =getLogicHandler();
        }
        if(mState == STATE_STOP){
            mState =STATE_RUNNING;
            mLogicHander.sendMessage(mLogicHander.obtainMessage(MSG_LOGIC_CYCLE));
        }
    }

    private Handler getLogicHandler(){
        Handler handler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_LOGIC_CYCLE:
                        if(mState == STATE_STOP){
                            return;
                        }
                        Log.i(TAG,"draw cycle hexagons");
                        int i=msg.arg1;
                        if(i > FRAME_COUNT){
                            i=1;
                            mPauseDecelerate = true;
                        }
                        float f=(float)i/(float)FRAME_COUNT;
                        if(isFix){
                            f=15/(float)FRAME_COUNT;
                        }
                        if(!mPauseDecelerate){
                            mRadio= mInterpolator.getInterpolation(f);
                        }
                        int r1=(int)(mDistanceMax*f)+mStartRadiu;
                        mHandler.sendMessage(mHandler.obtainMessage(0,getHexagons(r1)));

                        if(mState == STATE_FADE_OUT){
                            mLogicHander.sendMessageDelayed(mLogicHander.obtainMessage(MSG_LOGIC_END,1,0),FRAME_TIME);
                        }else if(mState == STATE_RUNNING){
                            mLogicHander.sendMessageDelayed(mLogicHander.obtainMessage(MSG_LOGIC_CYCLE,i+1,0),FRAME_TIME);
                        }
                        break;
                    case MSG_LOGIC_END:
                        if(mState != STATE_FADE_OUT){
                            return ;
                        }
                        Log.d(TAG,"draw end hexagons");
                        int j= msg.arg1;
                        float f2=(float)j/(float)FRAME_COUNT;
                        int r2= (int)(mDistanceMax*f2)+mStartRadiu;

                        mRadio = mInterpolator.getInterpolation(1-f2);
                        mHandler.sendMessage(mHandler.obtainMessage(0,getFrameOutHexagons(r2)));

                        if(j<= FRAME_COUNT){
                            mLogicHander.sendMessageDelayed(mLogicHander.obtainMessage(MSG_LOGIC_END,j+1,0),FRAME_TIME);
                        }else{
                            mState = STATE_STOP;
                            mHandler.post(new Runnable(){

                                @Override
                                public void run() {
                                    if(mAnimListener != null){
                                        mAnimListener.onAnimEnd();
                                    }
                                }
                            });
                        }
                }
            }
        };
        return handler;
    }

    private Hexagon getHexagon(int radiu){
        Hexagon hexagon =new Hexagon();
        float f = (float) (radiu -mStartRadiu)/(float)(mRadiu-mStartRadiu);
        if(isFix){
            hexagon.width  = HEXAGON_STROKE_MAX*2/3;
        }else{
            hexagon.width = HEXAGON_STROKE_MAX -(int)(HEXAGON_STROKE_MAX*f);
        }
        // draw a hexagon path
        Path path =new Path();
        float dx=SQRT3*radiu/2;
        path.moveTo(0,radiu);
        path.lineTo(0-dx,radiu/2);
        path.lineTo(0-dx,0-radiu/2);
        path.lineTo(0,0-radiu);
        path.lineTo(dx,0-radiu/2);
        path.lineTo(dx,radiu/2);
        path.close();
        hexagon.path = path;
        hexagon.radiu = radiu;
        return hexagon;
    }


    private List<Hexagon> getHexagons(int radiu){
        List<Hexagon> list =new ArrayList<Hexagon>();
        list.add(getHexagon(radiu));
        if(radiu+mHexagonMargin < mRadiu){
            list.add(getHexagon(radiu+ mHexagonMargin));
        }else {
            list.add(getHexagon(radiu-mHexagonMargin));
        }
        return list;

    }

    private List<Hexagon> getFrameOutHexagons(int radiu){
        List<Hexagon> list=new ArrayList<Hexagon>();
        while(radiu < mRadiu){
            list.add(getHexagon(radiu));
            radiu=radiu + mHexagonMargin;
        }
        return list;
    }

    public boolean  isRunning(){ return mState!=STATE_STOP;}

    @Override
    public void fadeOutAnimation() {
        mState = STATE_FADE_OUT;
    }

    @Override
    public void stopAnimation() {
        mState = STATE_STOP;
    }

    @Override
    public void recycle() {

    }

    class Hexagon{
        Path path;
        int width;
        int radiu;
    }
}
