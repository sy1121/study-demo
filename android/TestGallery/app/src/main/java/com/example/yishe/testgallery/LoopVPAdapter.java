package com.example.yishe.testgallery;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by yishe on 2018/3/2.
 */

public abstract class LoopVPAdapter<T> extends PagerAdapter {

    protected Context mContext;
    protected ArrayList<View> views;
    protected ViewPager mViewPager;
    private int realDataCount;
    private boolean mIsLoop;

    public LoopVPAdapter(Context context, ArrayList<T> datas,boolean isLoop) {
        realDataCount = datas.size();
        mIsLoop = isLoop;
        mContext = context;
        views = new ArrayList<>();
        if(isLoop) { //无线轮播
//        如果数据大于一条
            if (datas.size() > 1) {
//            添加最后一页到第一页
                datas.add(0, datas.get(datas.size() - 1));
//            添加第一页(经过上行的添加已经是第二页了)到最后一页
                datas.add(datas.get(1));
            }
        }
        for (T data:datas) {
            views.add(getItemView(data));
        }
    }

    public int getRealCount(){
        return realDataCount;
    }

    public boolean getmIsLoop(){
        return mIsLoop;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }


    protected abstract View getItemView(T data);



}
