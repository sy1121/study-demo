package com.example.yishe.myradarview.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.yishe.myradarview.R;

import java.util.Locale;

/**
 * Created by yishe on 2017/9/13.
 */

public class QScoreView extends View{
    private static final String TAG=QScoreView.class.getSimpleName();
    //开始分数滚动动画
    private static final int MSG_START_ANIM= 1;
    //分数每次移动最小距离
    private int MIN_DISTANCE;
    private int mBottom;
    private CheckAnimObserver mObserver;
    private int mCurrentScore=99;

    private int mWidth;
    private int mHeight;
    private int mScoureY;

    private Paint mPaint;

    private boolean mIsAnim;
    private boolean mIsSilent;
    private Context mContext;

    private boolean mScrollable =true;
    private float mPaintSize;
    private boolean mIsFirstCheck;

    private Hundred mFirst;
    private NormalNumber mSecond;
    private NormalNumber mThrid;

    private Bitmap mMaskBmp;
    private PorterDuffXfermode porterDuffXfermode;

    private static final boolean SCROLL_DOWN =true;
    private Canvas mTmpCanvas;
    private Bitmap mTmpBmp;

    public QScoreView(Context context){
        super(context);
        init(context,null);
    }

    public QScoreView(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context,attrs);
    }

    public void registAnimObserver(CheckAnimObserver listener){mObserver = listener;}

    public void unRegisterAnimObserver(){mObserver = null;}

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_START_ANIM:
                    if(mIsAnim){
                        invalidate();
                    }
                    break;
            }
        }
    };

    private void init(Context context,AttributeSet attrs){
        if(mPaintSize ==0 ){
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

            switch(displayMetrics.densityDpi){
                case 480:
                    mPaintSize = 256;
                    break;
                case 320:
                    mPaintSize = 172;
                    break;
                case 240:
                    mPaintSize = 130;
                    break;
                case 160:
                    mPaintSize = 86;
                    break;
                case 120:
                    mPaintSize = 64;
                    break;
                default:
                    mPaintSize = 130;
                    break;
            }
            int relativeY= 189 * getScreenHeight(context)/1080;
            mPaintSize  = relativeY;
        }
        mContext = context;
        mPaint = new Paint();
        mPaint.setTextSize(mPaintSize);
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setTypeface(Typeface.SANS_SERIF);
        mPaint.setShadowLayer(20,10,10,0x33000000);
        mWidth = getStringWidth(mPaint,"11")+dip2px(mContext,20);
        mHeight =getStringHeight(mPaint)+dip2px(mContext,40);

        mBottom = dip2px(mContext,12);
        MIN_DISTANCE = dip2px(mContext,6);

        mScoureY =mHeight -mBottom;
        mFirst =new Hundred();
        mSecond =new NormalNumber();
        mThrid =new NormalNumber();

        porterDuffXfermode =new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

        Drawable drawable =context.getResources().getDrawable(R.mipmap.ic_launcher);
        drawable.setBounds(0,0,mWidth,mHeight);
        mMaskBmp =Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mMaskBmp);
        drawable.draw(canvas);

        mTmpBmp =Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
        mTmpCanvas = new Canvas(mTmpBmp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth,mHeight+getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(this.getVisibility() != View.VISIBLE){
            return ;
        }
        super.onDraw(canvas);
        canvas.drawColor(Color.BLUE);
        //遮罩层实现淡出效果，需要分层处理
        int sc=canvas.saveLayer(0,0,mWidth,mHeight,null,Canvas.MATRIX_SAVE_FLAG|
            Canvas.CLIP_SAVE_FLAG|
            Canvas.HAS_ALPHA_LAYER_SAVE_FLAG|
            Canvas.FULL_COLOR_LAYER_SAVE_FLAG|
            Canvas.CLIP_TO_LAYER_SAVE_FLAG|
            Canvas.ALL_SAVE_FLAG);
        mTmpCanvas.drawColor(Color.TRANSPARENT,PorterDuff.Mode.CLEAR);//清除mTmpBmp 上的之前绘制的内容
        mSecond.drawNumber(mTmpCanvas,0,MIN_DISTANCE*2);
        mThrid.drawNumber(mTmpCanvas,mWidth/2,MIN_DISTANCE*3);

        canvas.drawBitmap(mTmpBmp,0,0,mPaint);
        //添加遮罩实现边界淡出效果 ？
        if(mMaskBmp !=null){
            mPaint.setXfermode(porterDuffXfermode);
            canvas.drawBitmap(mMaskBmp,0,0,mPaint);
            mPaint.setXfermode(null);
        }
        canvas.restoreToCount(sc);
        if(mSecond.isAnim||mThrid.isAnim){
            mIsAnim =true;
            invalidate();
        }else{
            mIsAnim = false;
            if(mObserver != null && mIsFirstCheck){
                mObserver.onScoreAnimationEnd(mCurrentScore,mIsSilent);
                mIsFirstCheck = false;
            }
        }
    }

    public void setScore(int score,boolean silent){
        Log.d(TAG,"set score: "+ score +", silent: "+silent);
        if(score<0&&silent){
            score=0;
        }
        // 保护避免大于100
        if(score>100){
            score = 100;
        }

        mIsFirstCheck = true;
        mIsSilent = silent;

        mCurrentScore = score;

        if(mCurrentScore<0){
            mFirst.endNum=0;
            mSecond.endNum=-1;
            mThrid.endNum=-1;
        }else{
            String endScore= String.format(Locale.getDefault(),"%03d",mCurrentScore);
            mFirst.endNum = Integer.parseInt(endScore.substring(0,1));
            mSecond.endNum = Integer.parseInt(endScore.substring(1,2));
            mThrid.endNum = Integer.parseInt(endScore.substring(2,3));
        }
        mFirst.reset(silent);
        mSecond.reset(silent);
        mThrid.reset(silent);
        if(silent){
            mIsAnim = false;
            invalidate();
        }else if(!mIsAnim){
            mIsAnim =true;
            mHandler.sendEmptyMessage(MSG_START_ANIM);
            if(mObserver!=null){
                mObserver.onScoreAnimationStart();
            }
        }
    }

    private int getScreenHeight(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetric =new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetric);
        return outMetric.heightPixels;
    }

    /**
     *获取字符串宽度
     * @param paint
     * @param str
     * @return
     */
    private int getStringWidth(Paint paint,String str){
        return (int)paint.measureText(str);
    }

    private int getStringHeight(Paint paint){
        Paint.FontMetrics fm=paint.getFontMetrics();
        return (int)Math.abs(fm.ascent);
    }

    private int dip2px(Context context,float dipValue){
        float scale= context.getResources().getDisplayMetrics().density;
        return (int)(dipValue*scale+0.5f);
    }

    /**
     * 封装个位和十位
     */
    private class NormalNumber{
        int currentNum;
        int currentNextNum;
        int endNum;

        int  currentY;
        boolean  isAnim;

        public NormalNumber(){
            currentNum = 0;
            endNum =0;
            currentY = mScoureY;
        }

        public void reset(boolean  silent){
            if(silent){
                currentNum = endNum;
                currentY = mScoureY;
            }else if(!isAnim){
                isAnim = (currentNum == endNum)? false:true;
            }

        }

        public void drawNumber(Canvas canvas,int positionX,int deltaY){
            if(isAnim){
                if(SCROLL_DOWN){
                    currentY = currentY +deltaY;
                    if(currentY > (mScoureY+mHeight)){
                        currentY = currentY -mHeight;
                        currentNum =currentNum-1;
                        if(currentNum<0){
                            currentNum = 9;
                        }
                    }
                }else{
                    currentY = currentY - deltaY;
                    if(currentY<0){
                        currentY = currentY +mHeight;
                        currentNum = currentNum+1;
                        if(currentNum>9){
                            currentNum=0;
                        }
                    }
                }
            }

            if((Math.abs(currentY - mScoureY)<= deltaY)&&(currentNum == endNum)){
                currentY = mScoureY;
                canvas.drawText(String.valueOf(currentNum),positionX,currentY,mPaint);
                isAnim = false;
            }else{
                canvas.drawText(String.valueOf(currentNum),positionX,currentY,mPaint);
                if(SCROLL_DOWN){
                    currentNextNum  = currentNum -1;
                    if(currentNextNum<0){
                        currentNextNum=9;
                    }
                }else {
                    currentNextNum =currentNum+1;
                    if(currentNextNum>9){
                        currentNextNum = 0;
                    }
                }
                canvas.drawText(String.valueOf(currentNextNum),positionX,SCROLL_DOWN?currentY-mHeight:currentY+mHeight,mPaint);
            }
        }
    }

    /**
     * 分装百位
     */
    private class Hundred{
        private int currentNum;
        private int curretntNextNum;
        private int currentY;

        private int endNum;

        private boolean isScrollAnim;
        private int currentTranslate;
        private int translateStep;
        private int translateEnd;
        private boolean translateAnim;

        private int mPaddingX;//1长得比较长 需要一个平移

        public Hundred(){
            currentNum =1;
            endNum=1;
            currentY= mScoureY;
            currentTranslate = -dip2px(mContext,7);
            mPaddingX = dip2px(mContext,10);
        }

        public void reset(boolean silent){
            if(silent){
                currentNum = endNum;
                currentY = mScoureY;
                isScrollAnim = false;

                currentTranslate = (endNum == 1)? -dip2px(mContext,7):-mWidth/6;
                translateAnim = false;
            }else {
                if(!isScrollAnim){
                    isScrollAnim =(currentNum == endNum)?false:true;
                }

                if(endNum == 1){
                    translateEnd = -dip2px(mContext,7);
                    translateStep = MIN_DISTANCE;
                }else{
                    translateEnd = mWidth/6;
                    translateStep = -MIN_DISTANCE;
                }
                if(!translateAnim){
                    translateAnim =(currentTranslate == translateEnd)?false:true;
                }
            }

        }

        public boolean isAnim() { return translateAnim||isScrollAnim;}

        public void drawNum(Canvas canvas,int deltaY){

        }

    }

    private interface  CheckAnimObserver{
        void onScoreAnimationStart();
        void onScoreAnimationEnd(int endScore,boolean isSilent);
    }


}
