package com.activitytest.model;

/**
 * Created by MY on 2017/8/11.
 *
 */

public class PlayInfo {

    public String mName;
    public String mSinger;
    public int mDuration;
    public String mPath;
    public long mSize;

    public boolean mState = false;

    /*
     *  构造函数
     */
    public PlayInfo() {
        super();
    }

    public PlayInfo(String name){
        this.mName = name;
    }

    public PlayInfo(String name, String singer ,long size){
        this.mName = name;
        this.mSinger = singer;
        this.mSize = size;
    }

    public String getPath(){
        return mPath;
    }
    public String getName(){
        return mName;
    }
    public String getSinger(){
        return mSinger;
    }
    public boolean getState(){
        return mState;
    }
    public int getDuration(){
        return mDuration;
    }
    public long getSize(){
        return mSize;
    }
}
