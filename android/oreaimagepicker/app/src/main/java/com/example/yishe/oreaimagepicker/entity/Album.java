package com.example.yishe.oreaimagepicker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author yishe
 * @date 2019/8/7.
 * email：yishe@tencent.com
 * description：
 */
public class Album implements Parcelable {

    public String name;
    public List<ImageItem> items;
    public ImageItem thumbnail;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeTypedList(this.items);
        dest.writeParcelable(this.thumbnail, flags);
    }

    public Album() {
    }

    protected Album(Parcel in) {
        this.name = in.readString();
        this.items = in.createTypedArrayList(ImageItem.CREATOR);
        this.thumbnail = in.readParcelable(ImageItem.class.getClassLoader());
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
