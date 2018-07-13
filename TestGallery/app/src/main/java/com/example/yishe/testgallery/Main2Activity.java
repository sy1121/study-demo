package com.example.yishe.testgallery;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    private BannerView bannerView;
    private ArrayList<AdItem> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        bannerView = (BannerView)findViewById(R.id.banner);
        testData();
        bannerView.setAdapter(new ImgAdapter(this,mDataList,true));
    }

    private void testData(){
        if(mDataList==null)
            mDataList = new ArrayList<AdItem>();
        mDataList.add(new AdItem("https://s2.mogucdn.com/mlcdn/c45406/170422_678did070ec6le09de3g15c1l7l36_750x500.jpg","少林足球",4567));
        mDataList.add(new AdItem("https://s2.mogucdn.com/mlcdn/c45406/170420_1hcbb7h5b58ihilkdec43bd6c2ll6_750x500.jpg","我的青春遇见你",5455));
        mDataList.add(new AdItem("http://s18.mogucdn.com/p2/170122/upload_66g1g3h491bj9kfb6ggd3i1j4c7be_750x500.jpg","布达佩斯大饭店",4567));
        mDataList.add(new AdItem("http://s18.mogucdn.com/p2/170204/upload_657jk682b5071bi611d9ka6c3j232_750x500.jpg","焦点访谈",1111));
        mDataList.add(new AdItem("http://s16.mogucdn.com/p2/170204/upload_56631h6616g4e2e45hc6hf6b7g08f_750x500.jpg","天下足球",45677));
        mDataList.add(new AdItem("http://s16.mogucdn.com/p2/170206/upload_1759d25k9a3djeb125a5bcg0c43eg_750x500.jpg","今日说法",544));
        Log.i("hahaha","testData");
    }

    public class ImgAdapter extends LoopVPAdapter<AdItem> {

        public ImgAdapter(Context context, ArrayList<AdItem> datas, boolean isLoop) {
            super(context, datas,isLoop );
        }

        private ViewGroup.LayoutParams layoutParams;

        @Override
        protected View getItemView(final AdItem data) {
           /* if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //ImageUtils.loadImage(mContext, data, imageView);
            Log.i("hahaha","getItemView data="+data);
            Glide.with(mContext).load(data).transform(new GlideRoundTransform(mContext,5)).into(imageView);*/

            View view= LayoutInflater.from(mContext).inflate(R.layout.banner_item,null);
            ImageView ad_pic=(ImageView)view.findViewById(R.id.ad_pic);
            TextView title = (TextView)view.findViewById(R.id.title);
            TextView visit_count = (TextView)view.findViewById(R.id.visit_count);
            ad_pic.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(mContext).load(data.getmUrl()).transform(new GlideRoundTransform(mContext,5)).into(ad_pic);
            title.setText(data.getmTitle());
            visit_count.setText(data.getmVisit()+"");

            ad_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Main2Activity.this,data.getmTitle(),Toast.LENGTH_LONG).show();
                }
            });

            return view;
        }


    }
}
