package com.example.yishe.immersivestatus.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.example.yishe.immersivestatus.R;

/**
 * Created by yishe on 2017/12/18.
 */

public class MyToolBar extends RelativeLayout {
    private static final String TAG="MyToolBar";

    private Drawable navigate_icon;
    private Drawable menu_icon;
    private Drawable menu_icon2;
    private Paint mPaint;

    private Context mContext;
    private int mHeight,mWidth;

    private static final int default_padding = 20;
    private static final int default_marggin_of_right_icon=20;


    public OnToolbarClickListener getmOnToolbarClickListener() {
        return mOnToolbarClickListener;
    }

    public void setmOnToolbarClickListener(OnToolbarClickListener mOnToolbarClickListener) {
        this.mOnToolbarClickListener = mOnToolbarClickListener;
    }

    private OnToolbarClickListener mOnToolbarClickListener;

    public MyToolBar(Context context) {
        super(context);
    }

    public MyToolBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta= context.obtainStyledAttributes(attrs, R.styleable.MyToolBar);
        navigate_icon=ta.getDrawable(R.styleable.MyToolBar_navigate_drawable);
        menu_icon=ta.getDrawable(R.styleable.MyToolBar_menu_drawable);
        menu_icon2=ta.getDrawable(R.styleable.MyToolBar_menu_drawable2);
        ta.recycle();
        mPaint = new Paint();
        Log.i(TAG,"MyToolBar()");
        setWillNotDraw(false);
        mContext = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight =h;
        Log.i(TAG,"mHeight="+mHeight+",mWidth="+mWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG,"onDraw");

        int paddingLeft = getPaddingLeft()==0?default_padding:getPaddingLeft();
        int paddingRight = getPaddingRight()==0?default_padding:getPaddingLeft();
        int paddingBottom = getPaddingBottom()==0?default_padding:getPaddingLeft();
        int paddingTop = getPaddingTop()==0?default_padding:getPaddingLeft();
        // 画左边的icon
        int navigate_left  = paddingLeft;
        int navigate_top  = paddingTop;
        if(navigate_icon!=null) {
            navigate_top = (mHeight - paddingTop - paddingBottom - navigate_icon.getIntrinsicHeight()) / 2+paddingTop;
            canvas.drawBitmap(((BitmapDrawable) navigate_icon).getBitmap(), navigate_left, navigate_top, mPaint);
        }else{
            Bitmap defaultIcon = BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.icon_main);
            navigate_top = (mHeight - paddingTop - paddingBottom - defaultIcon.getHeight()) / 2+paddingTop;
            canvas.drawBitmap(defaultIcon,navigate_left,navigate_top,mPaint);
        }

        //画右边的第一个icon
        int menu1_left,menu1_top;
        if(menu_icon!=null){
            menu1_left = mWidth - paddingRight - menu_icon.getIntrinsicWidth();
            menu1_top = (mHeight - paddingTop - paddingBottom - menu_icon.getIntrinsicHeight()) / 2+paddingTop;
            canvas.drawBitmap(((BitmapDrawable) menu_icon).getBitmap(),menu1_left,menu1_top,mPaint);
        }

        // 画右边的第二icon
        int menu2_left,menu2_top;
        if(menu_icon2!=null){
            menu1_left = mWidth - paddingRight - menu_icon.getIntrinsicWidth()- menu_icon2.getIntrinsicWidth()-default_marggin_of_right_icon;
            menu1_top = (mHeight - paddingTop - paddingBottom - menu_icon2.getIntrinsicHeight()) / 2+paddingTop;
            canvas.drawBitmap(((BitmapDrawable) menu_icon2).getBitmap(),menu1_left,menu1_top,mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN: //不拦截down事件，无法接收到up事件
                 return true;
            case MotionEvent.ACTION_UP:
                float x = event.getRawX();
                return findPos(x);
            default:;
        }
        return super.onTouchEvent(event);
    }

    /**
     *
     * @param x
     * @return 三个按钮之外区域的事件不拦截，交给子View处理
     */
    private boolean findPos(float x){
        if(mOnToolbarClickListener==null) return false;
        if(navigate_icon!=null&&x<(getPaddingLeft()+navigate_icon.getIntrinsicWidth())){
            mOnToolbarClickListener.onLeftBtnClick();
            return true;
        }else if(menu_icon!=null&&x>mWidth -getPaddingRight()-menu_icon.getIntrinsicWidth()){
            mOnToolbarClickListener.onRightBtn1Click();
            return true;
        }else if(menu_icon2!=null&&x>(mWidth-getPaddingRight()-menu_icon.getIntrinsicWidth()-default_marggin_of_right_icon-menu_icon2.getIntrinsicWidth())){
            mOnToolbarClickListener.onRightBtn2Click();
            return true;
        }else return false;
    }

    public interface  OnToolbarClickListener{
        void onLeftBtnClick();
        void onRightBtn1Click();
        void onRightBtn2Click();
    }

    public void setMenu_icon(Drawable menu_icon) {
        this.menu_icon = menu_icon;
    }

    public void setNavigate_icon(Drawable navigate_icon) {
        this.navigate_icon = navigate_icon;
    }

    public void setMenu_icon2(Drawable menu_icon2) {
        this.menu_icon2 = menu_icon2;
    }
}
