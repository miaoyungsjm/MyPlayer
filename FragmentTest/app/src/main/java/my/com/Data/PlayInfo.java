package my.com.Data;

/**
 * Created by MY on 2017/8/11.
 */

public class PlayInfo {

    public static int mPlayPosition = 0 ;

    public String mName;
    public String mSinger;
    public int mMaxProgress;
    public boolean mState = false;

    /*
     *  构造函数
     */
    public PlayInfo(String name){
        this.mName = name;
    }

    public PlayInfo(String name, boolean state){
        this.mName = name;
        this.mState = state;
    }

    public PlayInfo(String name, String singer){
        this.mName = name;
        this.mSinger = singer;
    }

    public PlayInfo(String name, String singer, boolean state){
        this.mName = name;
        this.mSinger = singer;
        this.mState = state;
    }


    /*
     *  方法
     */
    public String getmName(){
        return mName;
    }

    public String getSinger(){
        return mSinger;
    }

    public boolean getState(){
        return mState;
    }

    public int getMaxProgress(){
        return mMaxProgress;
    }
}
