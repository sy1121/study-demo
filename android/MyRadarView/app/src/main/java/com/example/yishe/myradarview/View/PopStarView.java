package com.example.yishe.myradarview.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by yishe on 2017/9/18.
 */

public class PopStarView extends BaseAnimView {
    private static final String TAG=PopStarView.class.getSimpleName();
    private static final float SQRT3 = (float)Math.sqrt(3);
    private Paint mPaint;
    private int mCenterX,mCenterY,mRadiu;

    private Context mContext;
    private AccelerateDecelerateInterpolator mInterpolator;
    private int mInterpolatorCount;
    /**
     * 六角星内半径
     */
    private int mStarInRadiu;
    private Path starPath,circlePath,path;
    private Handler mLogicHandler;
    private Handler mUIHandler;
    /**
     * 裁剪起始圆环的内半径
     */
    private int CLIP_START_RADIU_IN,CLIP_START_RADIU_OUT;
    /**
     *  当前裁剪圆环的内外半径
     */
    private int inClipRadiu1,outClipRadiu1;
    private int inClipRadiu2,outClipRadiu2;

    private static final int FRAME_TIME = 30;
    private static final int FRAME_COUNT = 22;

    private int mMaxDistanceIn,mMaxDistanceOut;
    private boolean mRunning;

    public PopStarView(Context context){
        super(context);
        init(context);
    }

    public PopStarView(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context);
    }

    public PopStarView(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext =context;
        mUIHandler =new Handler(Looper.getMainLooper());
        mInterpolator =new AccelerateDecelerateInterpolator();
        mInterpolatorCount = FRAME_COUNT -4;

        mPaint =new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(0);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT
                && Build.VERSION.SDK_INT >=  Build.VERSION_CODES.HONEYCOMB){
            setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mCenterX = getWidth()/2;
        mCenterY =getHeight()/2;

        mRadiu =(mCenterX>mCenterY?mCenterY:mCenterX);
        mStarInRadiu =dp2px(mContext,6);
        createStarPath(mStarInRadiu,mRadiu);
        circlePath = new Path();
        path = new Path();

        CLIP_START_RADIU_IN = mRadiu/2;
        CLIP_START_RADIU_OUT =mRadiu*3/4;

        mMaxDistanceIn = mRadiu - CLIP_START_RADIU_IN;
        mMaxDistanceOut = mRadiu - CLIP_START_RADIU_OUT;

        inClipRadiu2 = CLIP_START_RADIU_IN;
        outClipRadiu2 = CLIP_START_RADIU_OUT;
    }

    public static int dp2px(Context context,float dpVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpVal,context.getResources().getDisplayMetrics());
    }

    /**
     * 画基本六边形
     *
     */
    public void createStarPath(int inRadiu,int outRadiu){
        starPath = new Path();
        float dx_s = SQRT3 * inRadiu/2;
        float dx_l = SQRT3 * outRadiu/2;
        starPath.moveTo(0,outRadiu);
        starPath.lineTo(0-inRadiu/2,dx_s);

        starPath.lineTo(0-dx_l,outRadiu/2);
        starPath.lineTo(0-inRadiu,0);

        starPath.lineTo(0-dx_l,0-outRadiu/2);
        starPath.lineTo(0-inRadiu/2,0-dx_s);

        starPath.lineTo(0,0-outRadiu);
        starPath.lineTo(inRadiu/2,0-dx_s);

        starPath.lineTo(dx_l,0-outRadiu/2);
        starPath.lineTo(inRadiu,0);

        starPath.lineTo(dx_l,outRadiu/2);
        starPath.lineTo(inRadiu/2,dx_s);

        starPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLUE);
        canvas.save();
        canvas.translate(mCenterX,mCenterY);
        if(inClipRadiu1 <= mRadiu){
            drawPopStar(canvas,inClipRadiu1,outClipRadiu1);
        }
        if(inClipRadiu1 > CLIP_START_RADIU_IN && inClipRadiu2 <= mRadiu){
            canvas.rotate(30);
            drawPopStar(canvas,inClipRadiu2,outClipRadiu2);
        }
        canvas.restore();
    }

    private void drawPopStar(Canvas canvas,int inClipRadiu,int outClipRadiu){
        path.rewind();//clear data for fast reuse
        path.addPath(starPath);

        circlePath.rewind();
        circlePath.addCircle(0,0,inClipRadiu,Path.Direction.CW);
        canvas.clipPath(circlePath, Region.Op.DIFFERENCE);

        circlePath.rewind();
        circlePath.addCircle(0,0,outClipRadiu,Path.Direction.CW);
        canvas.clipPath(circlePath,Region.Op.INTERSECT);

        canvas.drawPath(path,mPaint);
    }

    public void startAnimation(){
        if(mLogicHandler == null){
            mLogicHandler = getLogicHandler();
        }
        if(!mRunning){
            mLogicHandler.sendEmptyMessage(0);
        }
    }

    public void stopAnimation(){ mRunning = false;}

    public boolean isInAnimation(){return mRunning;}

    private Handler getLogicHandler(){
        Handler handler = new Handler(Looper.getMainLooper()){
            public void handleMessage(Message msg){
                int i = msg.what;
                float  radio = (float)i/(float)mInterpolatorCount;
                if(radio>1)
                    radio =1;

                float ipRadio = mInterpolator.getInterpolation(radio);

                inClipRadiu1 =CLIP_START_RADIU_IN + (int)(mMaxDistanceIn*ipRadio);
                outClipRadiu2 = CLIP_START_RADIU_OUT +(int)(mMaxDistanceIn*ipRadio);

                if(i>=3){
                    float radio2 =(float)(i-3)/(float)mInterpolatorCount;
                    if(radio2 > 1){
                        radio2 = 1;
                    }
                    float ipRadio2 =mInterpolator.getInterpolation(radio2);
                    inClipRadiu2 = CLIP_START_RADIU_IN + (int)(mMaxDistanceIn*ipRadio2);
                    outClipRadiu2 = CLIP_START_RADIU_OUT +(int)(mMaxDistanceIn*ipRadio2);
                }
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        invalidate();
                    }
                });
                if(i<FRAME_COUNT){
                    mLogicHandler.sendEmptyMessageDelayed(i+1,FRAME_TIME);
                }else{
                    mUIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mAnimListener != null){
                                mAnimListener.onAnimEnd();
                            }
                        }
                    });
                }
            }
        };
        return handler;
    }
}
