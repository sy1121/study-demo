package com.example.yishe.oreaimagepicker.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.yishe.oreaimagepicker.entity.Album;
import com.example.yishe.oreaimagepicker.entity.ImageItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yishe
 * @date 2019/8/7.
 * email：yishe@tencent.com
 * description：
 */
public class ImageDataSource implements LoaderManager.LoaderCallbacks<Cursor> {

    private final Uri URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private final String[] PROJECTIONS = new String[]{
            MediaStore.Images.Media.DISPLAY_NAME,//名称
            MediaStore.Images.Media.DATA,//路径
            MediaStore.Images.Media.WIDTH,//宽
            MediaStore.Images.Media.HEIGHT,//高
            MediaStore.Images.Media.SIZE,//大小
            MediaStore.Images.Media.MIME_TYPE,//类型
            MediaStore.Images.Media.DATE_ADDED//创建时间
    };

    private WeakReference<Context> mContext;
    private OnImageLoadListener mLoadListener;

    public ImageDataSource(Context context,OnImageLoadListener listener){
        mContext = new WeakReference<>(context);
        mLoadListener = listener;
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        Context context = mContext.get();
        if(context == null) return null;
        CursorLoader cursorLoader = new CursorLoader(mContext.get(),URI,PROJECTIONS,null,null,MediaStore.Images.Media.DATE_ADDED + " DESC");
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        List<Album> albums = new ArrayList<>();
        if(cursor != null){
            List<ImageItem> items = new ArrayList<>();
            while(cursor.moveToNext()){
                ImageItem image = new ImageItem();
                image.name = cursor.getString(cursor.getColumnIndexOrThrow(PROJECTIONS[0]));
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }


    public interface OnImageLoadListener{
        void onLoadFinished(List<Album>albums);
    }
}
