package com.example.yishe.testgallery;

/**
 * Created by yishe on 2018/3/3.
 */

public class AdItem  {
    private String mUrl;
    private String mTitle;
    private int mVisit;
    private String mLink;

    public String getmLink() {
        return mLink;
    }

    public void setmLink(String mLink) {
        this.mLink = mLink;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getmVisit() {
        return mVisit;
    }

    public void setmVisit(int mVisit) {
        this.mVisit = mVisit;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public AdItem(String url){
        mUrl= url;
    }


    public AdItem(String url,String title ,int visit){
        mUrl  = url;
        mTitle = title;
        mVisit = visit;
    }

    public AdItem(String url,String title ,int visit,String link){
        mUrl  = url;
        mTitle = title;
        mVisit = visit;
        mLink = link;
    }
}
