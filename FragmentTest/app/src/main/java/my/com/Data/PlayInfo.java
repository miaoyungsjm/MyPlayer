package my.com.Data;

/**
 * Created by MY on 2017/8/11.
 */

public class PlayInfo {

    private String mName;
    private String mSinger;
    private int mMaxProgress;

    /*
     *  构造函数
     */
    public PlayInfo(String name){
        this.mName = name;
    }
    public PlayInfo(String name, String singer){
        this.mName = name;
        this.mSinger = singer;
    }

    public PlayInfo(String name, int maxProgress){
        this.mName = name;
        this.mMaxProgress = maxProgress;
    }

    public PlayInfo(String name, String singer, int maxProgress){
        this.mName = name;
        this.mSinger = singer;
        this.mMaxProgress = maxProgress;
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

    public int getMaxProgress(){
        return mMaxProgress;
    }
}
