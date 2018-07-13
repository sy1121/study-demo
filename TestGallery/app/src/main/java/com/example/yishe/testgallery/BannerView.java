package com.example.yishe.testgallery;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by yishe on 2018/3/2.
 */

public class BannerView<T> extends FrameLayout{

    private static final String TAG="BannerView";
    // 内容宽高
    private int mItemWidth;
    private int mItemHeight = LayoutParams.WRAP_CONTENT;

    private boolean isAuto=true;

    private RelativeLayout mViewPagerContainer;
    private ViewPager mViewPager;
    private LinearLayout vBottomBar;
    private TextView vitleBar;
    //private ViewPagerIndicator vIndicator;
    private LoopVPAdapter<T> mPagerAdapter;

    public static final int VISIBLE_AUTO = 0;
    public static final int VISIBLE_ALWAYS = 1;
    public static final int VISIBLE_NEVER = 2;

    private long mDelay;        // 多久后开始滚动
    private long mInterval;     // 滚动间隔
    private boolean mIsAuto;    // 是否自动滚动
    private float mWidthRatio;  // 宽度百分比
    private float mHeightRatio; // 高度百分比
    private int  mPageMargin;  //page 的间隔

    private int mCurrentPosition;
    private int mIndicatorVisible;
    private ViewPagerIndicator mIndicator;


    // 设备密度
    private DisplayMetrics mDm;

    private boolean mIsStarted = false;
    private boolean mIsVisible = false;
    private boolean mIsResumed = true;
    private boolean mIsRunning = false;
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsRunning) {
                mViewPager.setCurrentItem(mCurrentPosition + 1);
                if (mPagerAdapter.getmIsLoop()) {
                    postDelayed(mRunnable, mInterval);
                } else {
                    mIsRunning = false;
                }
            }
        }
    };

    private Context mContext;
    public BannerView(Context context) {
        this(context, null, 0);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("all")
    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mDm = context.getResources().getDisplayMetrics();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BannerView);

        boolean hasAspectRatio = ta.hasValue(R.styleable.BannerView_bvAspectRatio);
        float aspectRatio = ta.getFloat(R.styleable.BannerView_bvAspectRatio, 0f);
        boolean isLoop = ta.getBoolean(R.styleable.BannerView_bvIsLoop, true);
        mDelay = ta.getInt(R.styleable.BannerView_bvDelay, 5000);
        mInterval = ta.getInt(R.styleable.BannerView_bvInterval, 5000);
        mIsAuto = ta.getBoolean(R.styleable.BannerView_bvIsAuto, true);
        mPageMargin = ta.getInteger(R.styleable.BannerView_pageMargin,dp2px(10));
        mHeightRatio =ta.getFloat(R.styleable.BannerView_pageHeightRtio,0.3f);
        mWidthRatio = ta.getFloat(R.styleable.BannerView_pageWidthRatio,0.9f);

        int barColor = ta.getColor(R.styleable.BannerView_bvBarColor, Color.TRANSPARENT);
        float barPaddingLeft = ta.getDimension(R.styleable.BannerView_bvBarPaddingLeft, dp2px(10));
        float barPaddingTop = ta.getDimension(R.styleable.BannerView_bvBarPaddingTop, dp2px(10));
        float barPaddingRight = ta.getDimension(R.styleable.BannerView_bvBarPaddingRight, dp2px(10));
        float barPaddingBottom = ta.getDimension(R.styleable.BannerView_bvBarPaddingBottom, dp2px(10));


        mIndicatorVisible = ta.getInteger(R.styleable.BannerView_bvIndicatorVisible, VISIBLE_AUTO);

        int indicatorWidth = ta.getDimensionPixelSize(R.styleable.BannerView_bvIndicatorWidth, dp2px(6));
        int indicatorHeight = ta.getDimensionPixelSize(R.styleable.BannerView_bvIndicatorHeight, dp2px(6));
        int indicatorGap = ta.getDimensionPixelSize(R.styleable.BannerView_bvIndicatorGap, dp2px(6));
        int indicatorColor = ta.getColor(R.styleable.BannerView_bvIndicatorColor, 0x88ffffff);
        int indicatorColorSelected = ta.getColor(R.styleable.BannerView_bvIndicatorColorSelected, Color.BLUE);

        Drawable indicatorDrawable = ta.getDrawable(R.styleable.BannerView_bvIndicatorDrawable);
        Drawable indicatorDrawableSelected = ta.getDrawable(R.styleable.BannerView_bvIndicatorDrawableSelected);

        mContext = context;
        mViewPagerContainer  = new RelativeLayout(context);
        mViewPagerContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        mViewPagerContainer.setPadding(0,0,0,0);
        mViewPagerContainer.setClipChildren(false);
        addView(mViewPagerContainer,new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        mViewPager =new ViewPager(context);
        mViewPager.setClipChildren(false);
        RelativeLayout.LayoutParams dlp=new RelativeLayout.LayoutParams(
                Math.round(getResources().getDisplayMetrics().widthPixels*mWidthRatio),
                Math.round(getResources().getDisplayMetrics().heightPixels*mHeightRatio));
        mViewPagerContainer.addView(mViewPager,dlp);
        //设置预加载数量
        mViewPager.setOffscreenPageLimit(2);
        //设置每页之间的左右间隔
        mViewPager.setPageMargin(mPageMargin);
        initViewPager();

        vBottomBar = new LinearLayout(context);
        vBottomBar.setBackgroundColor(barColor);
        vBottomBar.setPadding((int) barPaddingLeft, (int) barPaddingTop, (int) barPaddingRight, (int) barPaddingBottom);
        vBottomBar.setClipChildren(false);
        vBottomBar.setClipToPadding(false);
        vBottomBar.setOrientation(LinearLayout.HORIZONTAL);
        vBottomBar.setGravity(Gravity.CENTER);
        addView(vBottomBar, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));

        int indicatorGravity = ta.getInt(R.styleable.BannerView_bvIndicatorGravity, Gravity.CENTER);
        mIndicator = new ViewPagerIndicator(context);
        mIndicator.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mIndicator.setItemSize(indicatorWidth, indicatorHeight);
        mIndicator.setItemGap(indicatorGap);
        if (indicatorDrawable != null && indicatorDrawableSelected != null) {
            mIndicator.setItemDrawable(indicatorDrawable, indicatorDrawableSelected);
        } else {
            mIndicator.setItemColor(indicatorColor, indicatorColorSelected);
        }


        if (indicatorGravity == Gravity.CENTER) {
            Log.i(TAG,"indicatorGravity="+indicatorGravity);
            vBottomBar.addView(mIndicator);
        } else if (indicatorGravity == Gravity.RIGHT) {
            vBottomBar.setGravity(Gravity.RIGHT);
            vBottomBar.addView(mIndicator);
        } else if (indicatorGravity == Gravity.LEFT) {
            vBottomBar.setGravity(Gravity.LEFT);
            vBottomBar.addView(mIndicator);
        }

        ta.recycle();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        int width= mViewPagerContainer.getMeasuredWidth()+mViewPagerContainer.getPaddingLeft()+mViewPagerContainer.getPaddingRight();
        int height= mViewPagerContainer.getMeasuredHeight()+mViewPagerContainer.getPaddingTop()+mViewPagerContainer.getPaddingBottom();
        setMeasuredDimension(width,height);
       // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    void initIndicator() {
        mIndicator.setupWithViewPager(mViewPager);
        boolean visible = mIndicatorVisible == VISIBLE_ALWAYS || (mIndicatorVisible == VISIBLE_AUTO && mPagerAdapter.getRealCount() > 1);
        mIndicator.setVisibility(visible ? VISIBLE : INVISIBLE);
        mIndicator.setPosition(mCurrentPosition);
    }

    void initViewPager() {


        //设置ViewPager切换效果，即实现画廊效果
        // mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        //将容器的触摸事件反馈给ViewPager
        mViewPagerContainer.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // dispatch the events to the ViewPager, to solve the problem that we can swipe only the middle view.
                return mViewPager.dispatchTouchEvent(event);
            }

        });

        try {
            if (isAuto) {
                setDuration(mViewPager, 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }


    private void start() {
        mIsStarted = true;
        update();
    }

    void update() {
        boolean running = mIsVisible && mIsResumed && mIsStarted && mIsAuto && mPagerAdapter.getRealCount() > 1 && (mPagerAdapter.getmIsLoop());
        if (running != mIsRunning) {
            if (running) {
                postDelayed(mRunnable, mDelay);
            } else {
                removeCallbacks(mRunnable);
            }
            mIsRunning = running;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsVisible = false;
        update();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mIsVisible = visibility == VISIBLE;
        update();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsResumed = false;
                Log.i(TAG,"ACTION_DOWN");
                update();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsResumed = true;
                Log.i(TAG,"ACTION_UP");
                update();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    public void setAdapter(LoopVPAdapter<T> loopVPAdapter){
        mPagerAdapter = loopVPAdapter;
        mViewPager.setAdapter(mPagerAdapter);
        if(loopVPAdapter.getmIsLoop()){
            mViewPager.removeOnPageChangeListener(mLoopPageListener);
            mViewPager.addOnPageChangeListener(mLoopPageListener);
        }

        mViewPager.setCurrentItem(1);

        if(mIndicatorVisible==VISIBLE_ALWAYS || mIndicatorVisible == VISIBLE_AUTO){
            initIndicator();
        }

        if(mPagerAdapter.getmIsLoop()){
            start();
        }

    }




    private ViewPager.OnPageChangeListener mLoopPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            //这里做切换ViewPager时，底部RadioButton的操作
            mCurrentPosition = position;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mViewPagerContainer != null) {
                mViewPagerContainer.invalidate();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //        若viewpager滑动未停止，直接返回
            if (state != ViewPager.SCROLL_STATE_IDLE) return;
            //        若当前为第一张，设置页面为倒数第二张
            if (mCurrentPosition == 0) {
                mViewPager.setCurrentItem(mPagerAdapter.getCount()-2,false);
            } else if (mCurrentPosition == mPagerAdapter.getCount()-1) {
//        若当前为倒数第一张，设置页面为第二张
                mViewPager.setCurrentItem(1,false);
            }
        }
    };


    private static void setDuration(ViewPager pager, int duration) {
        try {
            FixedSpeedScroller scroller = new FixedSpeedScroller(pager.getContext(), new AccelerateDecelerateInterpolator(), duration);
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            field.set(pager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class FixedSpeedScroller extends Scroller {
        private int mDuration = 450;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, int duration) {
            super(context, interpolator);
            this.mDuration = duration;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, this.mDuration);
        }
    }




    private int dp2px(float dp) {
        return (int) (dp * mDm.density + 0.5f);
    }

    private float sp2px(float sp) {
        return sp * mDm.scaledDensity;
    }

}
