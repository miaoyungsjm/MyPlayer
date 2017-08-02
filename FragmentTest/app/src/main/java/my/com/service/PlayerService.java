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

    int count;

    private String musicName;       // 《你的名字》

    private static final String TAG = "PlayerService";         // 调试信息 TAG 标签


    /*
     *  服务的生命周期 ：
     *
     *  服务的创建 -- onCreate() 只会执行一次
     *
     *  执行：
     *  Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
     *  startService(intent);
     *  结果：
     *  -- onCreate()  -- onStartCommand()
     *
     *  执行：
     *  bindService(intent, serviceConnection, BIND_AUTO_CREATE);
     *  结果：
     *  -- onCreate()  -- onBind()
     *
     *  服务的销毁 -- onDestroy()
     *  关闭服务需同时调用 unbindService(serviceConnection) 和 stopService(stopIntent)
     *
     */



    /*
     * 服务的控制（接口）
     * 控制 mediaplayer 的播放、暂停、跳转
     */
    private PlayerControlBinder playerControlBinder = new PlayerControlBinder();
    public class PlayerControlBinder extends Binder{

        public void mPlay(){
            Log.i(TAG, " -- PlayerControlBinder : mPlay");
            mediaplayer.start();
        }
        public void mPause(){
            Log.i(TAG, " -- PlayerControlBinder : mPause");
            mediaplayer.pause();
        }
        public void mSeekTo(int msec){
            Log.i(TAG, " -- PlayerControlBinder : mSeekTo");
            mediaplayer.seekTo(msec);
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, " -- PlayerService : onBind");
        return playerControlBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, " -- PlayerService : onCreate");

        // 计算播放次数
        count = 0;

        // 实例化本地广播管理器，使用本地广播发送播放信息
        if(localBroadcastManager == null)
            localBroadcastManager = LocalBroadcastManager.getInstance(this);

        // 实例化媒体播放器
        if(mediaplayer == null)
            mediaplayer = new MediaPlayer();

        /*
         * 关于定时器的开启与关闭：
         *
         * 当你听歌到一半时切歌，如果你不想通过关闭服务，
         * 再开启服务来进行媒体播放器歌曲的更换，定时器就无需再次实例化，
         * 只需执行一次就好
         */
        // 初始化定时器 Timer ，利用本地广播发送播放信息，用于更新 PlayerActivity 的进度条
        init_timer();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.i(TAG, " -- PlayerService : onStartCommand");

        count++;

        // 启动服务时，传入要播放的歌名
        if(intent != null)musicName = intent.getStringExtra("musicname");

        // 初始化多媒体播放器
        init_mediaplayer();

        return super.onStartCommand(intent, flags, startId);
    }

    public void init_mediaplayer(){
        Log.i(TAG, "  init_mediaplayer()");

        File file = new File("/sdcard/Files/", musicName);
        Log.i(TAG,"  file.getAbsolutePath() :" + file.getAbsolutePath());

        if(file.exists()){
            Log.i(TAG,"  file.exists()");

            if(mediaplayer == null)mediaplayer = new MediaPlayer();

            try{
                // 重置
                mediaplayer.reset();
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

                        // 发送本地广播，告诉 PlayerActivity : Type, Name, MaxProgress
                        Intent intent_playInfo = new Intent(BroadcastAction.PlayInfoAction);
                        intent_playInfo.putExtra("type", 0);
                        intent_playInfo.putExtra("musicname", musicName);
                        intent_playInfo.putExtra("maxprogress", maxProgress);
                        localBroadcastManager.sendBroadcast(intent_playInfo);
                        Log.i(TAG,"  sendBroadcast(intent_playInfo)" +
                                "    type = 0" +
                                "    musicname = " + musicName +
                                "    maxprogress = " + maxProgress);

                        // 此处不需要一启动服务就开始播放
                        if(count > 1)mp.start();
                        Log.i(TAG,"  mp.start()  播放开始");
                    }
                });


                /*
                 * 设置 mediaplayer 播放完成监听器，做切歌操作
                 */
                mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.i(TAG," -- OnCompletionListener : onCompletion()  播放结束");

//                        // 无需关闭服务
//                        stopSelf();
//                        Log.i(TAG,"  stopSelf()  关闭服务");
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
    }

//    // 线程的编写，本来想用于更新 PlayerActivity 的播放进度的，但有了 Timer 就没必要了
//    new Thread(new Runnable() {
//        @Override
//        public void run() {
//            Log.i(TAG," -- Thread : run()  线程");
//
//        }
//    }).start();

    /*
     * 初始化定时器 Timer
     * 因为服务中不进行耗时操作，启动一个线程来传递音乐当前进度，然而 Timer 就是一个线程
     * 传递方法为：本地广播 localBroadcastManager.sendBroadcast(intent_playInfo)
     */
    public void init_timer(){
        /*
         * Timer 就是一个线程，使用schedule方法完成对TimerTask的调度，多个TimerTask可以共用一个Timer，
         * 也就是说Timer对象调用一次schedule方法就是创建了一个线程，并且调用一次schedule后TimerTask是
         * 无限制的循环下去的，使用Timer的cancel()停止操作。当然同一个Timer执行一次cancel()方法后，所
         * 有Timer线程都被终止。
         */
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG," -- Timer : run()");
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


    /*
     *  关闭服务需同时调用 unbindService(serviceConnection) 和 stopService(stopIntent)
     *  才会执行 onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, " ------------------------------- PlayerService : onDestroy");
        // 释放媒体播放器
        mediaplayer.release();
        // 关闭定时器
        timer.cancel();

    }
}
