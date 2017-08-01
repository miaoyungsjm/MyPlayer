package my.com.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import my.com.PlayerActivity;
import my.com.action.BroadcastAction;

/**
 * Created by MY on 2017/7/29.
 */

public class PlayerService extends Service{

    private LocalBroadcastManager localBroadcastManager;    // 本地广播管理

    private MediaPlayer mediaplayer;    // 媒体播放器的对象
    private Timer timer;    // 定时器对象
    private int currentProgress, maxProgress;   // 歌曲当前进度，最大进度

    private String musicName;

    private static final String TAG = "PlayerService";         // 调试信息 TAG 标签




    /*
     * 服务的控制
     */
//    private PlayerBinder mBinder = new PlayerBinder();
//    private class PlayerBinder extends Binder{
//
//        public int getPlayerProgress(){
//            Log.d(TAG, " -- PlayerBinder : getPlayerProgress");
//            return 0;
//        }
//    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, " -- PlayerService : onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, " -- PlayerService : onCreate");

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        // 实例化媒体播放器 MediaPlayer
        mediaplayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        musicName = intent.getStringExtra("musicname");

        File file = new File("/sdcard/Files/", musicName);
        Log.i(TAG,"  file.getAbsolutePath() :" + file.getAbsolutePath());

        if(file.exists()){
            Log.i(TAG,"  file.exists()");

            if(mediaplayer == null)mediaplayer = new MediaPlayer();

            mediaplayer.reset();
            Log.i(TAG,"  mediaplayer.reset();");

            try{
                // 设置音频类型
                mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                // 设置 mp3 数据源
                mediaplayer.setDataSource(file.getAbsolutePath());
                Log.i(TAG,"  mediaplayer.setDataSource(file.getAbsolutePath())\n" +
                        "  file.getAbsolutePath() = " + file.getAbsolutePath());
                // 数据异步缓冲
                mediaplayer.prepareAsync();
                Log.i(TAG,"  mediaplayer.prepareAsync()");
                // 监听缓存事件
                mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Log.i(TAG," -- OnPreparedListener : onPrepared()  异步缓冲结束");

                        // 获取进度条最大值
                        maxProgress = mediaplayer.getDuration();
                        Log.i(TAG,"  maxProgress = " + maxProgress);

                        // 发送自定义广播，告诉 PlayerActivity : Type, Name, MaxProgress
                        Intent intent_playInfo = new Intent(BroadcastAction.PlayInfoAction);
                        intent_playInfo.putExtra("type", 0);
                        intent_playInfo.putExtra("musicname", musicName);
                        intent_playInfo.putExtra("maxprogress", maxProgress);
                        localBroadcastManager.sendBroadcast(intent_playInfo);
                        Log.i(TAG,"  sendBroadcast(intent_playInfo)" +
                                "    type = 0" +
                                "    musicname = " + musicName +
                                "    maxprogress = " + maxProgress);

                        // 开始播放
                        mp.start();
                        Log.i(TAG,"  mp.start()  播放开始");

                        // 服务中不进行耗时操作，启动一个线程来传递音乐当前进度
                        // 发送自定义广播，告诉 PlayerActivity : currentProgress
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(TAG," -- Thread : run()  线程");

                                // 设置定时器，每隔 1 秒发送一次
                                timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (mediaplayer != null && mediaplayer.isPlaying()){
                                            //获取当前进度
                                            currentProgress = mediaplayer.getCurrentPosition();

                                            Intent intent_playInfo = new Intent(BroadcastAction.PlayInfoAction);
                                            intent_playInfo.putExtra("type", 1);
                                            intent_playInfo.putExtra("currentprogress", currentProgress);
                                            localBroadcastManager.sendBroadcast(intent_playInfo);
                                            Log.i(TAG," -- Timer : sendBroadcast(intent_playInfo)" +
                                                    "    type = 1" +
                                                    "    currentprogress = " + currentProgress);
                                        }
                                    }
                                },0 ,1000);
                            }
                        }).start();

                    }
                });

                // 设置 mediaplayer 播放完成监听器，
                mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.i(TAG," -- OnCompletionListener : onCompletion()  播放结束");
                        // 关闭定时器
                        timer.cancel();
                        timer = null;
                        Log.i(TAG,"  timer.cancel()  关闭定时器");
                    }
                });

            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "mediaplayer error", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                System.out.println(e);
            }
        } else {
            Toast.makeText(getApplicationContext(), "file error", Toast.LENGTH_LONG).show();
        }


        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, " -- PlayerService : onDestroy");
        mediaplayer.release();
        mediaplayer = null;
        timer.cancel();
        timer = null;
    }
}
