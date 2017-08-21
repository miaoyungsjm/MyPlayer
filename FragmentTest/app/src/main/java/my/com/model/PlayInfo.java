package my.com.model;

/**
 * Created by MY on 2017/8/11.
 *
 */

public class PlayInfo {

    private static int mPlayPosition = 0 ;

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


    public static void setmPlayPosition(int p){
        mPlayPosition = p;
    }
    public static int getmPlayPosition(){
        return mPlayPosition;
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
