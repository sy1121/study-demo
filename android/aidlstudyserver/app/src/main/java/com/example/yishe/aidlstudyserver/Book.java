package com.example.yishe.aidlstudyserver;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author yishe
 * @date 2019/9/2.
 * email：yishe@tencent.com
 * description：
 */
public class Book implements Parcelable {
    private String name;

    public void setName(String name){
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    public Book(String name) {
        this.name = name;
    }

    protected Book(Parcel in) {
        this.name = in.readString();
    }

    public void readFromParcel(Parcel dest) {
        name = dest.readString();
    }


    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
