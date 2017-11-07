package com.example.yishe.myradarview.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yishe on 2017/9/13.
 */

public abstract class BaseAnimView extends View {
    public interface  AnimListener{
        void onAnimEnd();
    }

    protected  AnimListener mAnimListener;
    public BaseAnimView(Context context){
        super(context);
    }

    public BaseAnimView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public BaseAnimView(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
    }

    public void setmAnimListener(AnimListener listener){
        mAnimListener =listener;
    }
}
