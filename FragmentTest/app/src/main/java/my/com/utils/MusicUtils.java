package my.com.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import my.com.model.PlayInfo;

/**
 * Created by MY on 2017/8/14.
 */

public class MusicUtils {       //  音乐工具类

    /*
     *  扫描本地音频文件，并返回 List<PlayInfo>
     *
     */
    public static List<PlayInfo> ScanLocalMusic(Context context){

        List<PlayInfo> mPlayInfoList = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null,
                MediaStore.Audio.AudioColumns.IS_MUSIC);

        if (cursor != null){
            while (cursor.moveToNext()){
                PlayInfo playInfo = new PlayInfo();
                playInfo.mName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                playInfo.mSinger = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                playInfo.mDuration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                playInfo.mPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                playInfo.mSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                //  过滤比较小的文件
                if (playInfo.mSize > 1000 * 800) {
                    // 本地媒体库读取的歌曲信息不规范，切割标题，分离出歌曲名和歌手

                    String[] temp = playInfo.mName.split("\\.");
                    playInfo.mName = temp[0];

                    if (playInfo.mName.contains("-")) {
                        String[] str = playInfo.mName.split("-");
                        playInfo.mSinger = str[0];
                        playInfo.mName = str[1];
                    }
                    mPlayInfoList.add(playInfo);
                }
            }
            // 释放资源
            cursor.close();
        }

        return mPlayInfoList;
    }

}