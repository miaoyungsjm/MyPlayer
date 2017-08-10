package my.com.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import my.com.action.BroadcastAction;

/**
 * Created by MY on 2017/7/29.
 */


/*
 *
 *  关于服务的设计想法
 *
 *  因为不可能每次播放一首歌都要重新开启服务，创建 MediaPlayer ，播完后又要关闭服务。
 *  特别是当我听到一半时，突然切歌，那该该怎么操作呀？？？
 *  关闭服务，再重新开启，重新创建 MediaPlayer ？？？
 *  显然是多余的，所以只需把服务挂着，当要播放时就控制服务工作（播放、暂停、快进、切歌）
 *
 */

public class PlayerService extends Service{

    private LocalBroadcastManager mLocalBroadcastManager;    //  本地广播管理

    private MediaPlayer mMediaPlayer;    //  媒体播放器的对象
    private int currentProgress, maxProgress;   //  歌曲当前进度，最大进度

    private Timer mTimer;    //  定时器对象
    private TimerTask mTimerTask;

    private boolean isLoop;     //  播放模式，是否单曲循环

    int count;      //  播放次数，判断第一次进入播放器界面，不用播放

    private String musicName;       //  《你的名字》

    private static final String TAG = "PlayerService";         //  调试信息 TAG 标签


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
     *  服务的控制（接口）
     *  控制 mMediaPlayer 的播放、暂停、跳转
     */
    private PlayerControlBinder playerControlBinder = new PlayerControlBinder();
    public class PlayerControlBinder extends Binder{

        public void mPlay(){
            Log.i(TAG, " -- PlayerControlBinder : mPlay");
            mMediaPlayer.start();
        }
        public void mPause(){
            Log.i(TAG, " -- PlayerControlBinder : mPause");
            mMediaPlayer.pause();
        }
        public void mSeekTo(int msec){
            Log.i(TAG, " -- PlayerControlBinder : mSeekTo");
            mMediaPlayer.seekTo(msec);
        }
    }


    /*
     *  绑定服务
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, " ----- PlayerService : onBind");
        return playerControlBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, " ----- PlayerService : onCreate");

        // 计算播放次数
        count = 0;

        // 实例化本地广播管理器，使用本地广播发送播放信息
        if(mLocalBroadcastManager == null) {
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        }

        // 实例化媒体播放器
        if(mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            init_Timer();       // 初始化定时器 Timer ，利用本地广播发送播放信息，用于更新 PlayerActivity 的进度条

            /*
             *
             *  关于 媒体播放器 MediaPlayer 和 定时器 Timer 的开启与关闭设计想法：
             *
             *  首先说一下 Timer 的作用：
             *  当 mMediaPlayer != null && mMediaPlayer.isPlaying() 时，就定时发送本地广播向 Activity
             *  传递音乐当前进度。否则，就和服务一起挂着，做做计时什么也不干
             *
             *
             *  那为什么不把 Timer 关了？挂着不会显得浪费资源吗？
             *  这就是留下来的一个坑！！！！！！！！！！！！！！！！！！！！！！！！！！！！  BUG
             *  timer 只是定时器,关键任务还是有 timerTask 去做，而 timerTask 用到多线程处理,
             *  线程是没有暂停的，只能通过设置标记和重开线程等方法做到线程的暂停,继续等功能
             *
             *
             */
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
     *  初始化定时器 Timer
     *  因为服务中不进行耗时操作，启动一个线程来传递音乐当前进度，然而 Timer 就是一个线程
     *  传递方法为：本地广播 mLocalBroadcastManager.sendBroadcast(intent_playInfo)
     */
    public void init_Timer(){
        /*
         * Timer 就是一个线程，使用schedule方法完成对TimerTask的调度，多个TimerTask可以共用一个Timer，
         * 也就是说Timer对象调用一次schedule方法就是创建了一个线程，并且调用一次schedule后TimerTask是
         * 无限制的循环下去的，使用Timer的cancel()停止操作。当然同一个Timer执行一次cancel()方法后，所
         * 有Timer线程都被终止。
         */
        if(mTimer == null) mTimer = new Timer();
        if(mTimerTask == null){
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Log.i(TAG," -- Timer : run()");
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
                        //获取当前进度
                        currentProgress = mMediaPlayer.getCurrentPosition();

                        Intent intent_playInfo = new Intent(BroadcastAction.PlayInfoAction);
                        intent_playInfo.putExtra("type", 1);
                        intent_playInfo.putExtra("currentprogress", currentProgress);
                        mLocalBroadcastManager.sendBroadcast(intent_playInfo);
                        Log.i(TAG," -- Timer : mLocalBroadcastManager.sendBroadcast(intent_playInfo)" +
                                "  type = 1" +
                                "  currentprogress = " + currentProgress);
                    }
                }
            };
        }
        if(mTimer != null && mTimerTask != null) mTimer.schedule(mTimerTask, 0, 1000);
    }


    /*
     *
     *  启动服务
     */
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.i(TAG, " ----- PlayerService : onStartCommand");

        count++;        //  播放次数加一

        if(intent != null){
            //  启动服务时，传入要播放的歌名
            musicName = intent.getStringExtra("musicname");

            // 初始化多媒体播放器
            init_MediaPlayer();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void init_MediaPlayer(){
        Log.i(TAG, " -- init_MediaPlayer()");

        File file = new File("/sdcard/Files/", musicName);
        Log.i(TAG,"    file.getAbsolutePath() :" + file.getAbsolutePath());

        if(file.exists()){
            Log.i(TAG,"    file.exists()");

            if(mMediaPlayer == null) mMediaPlayer = new MediaPlayer();

            try{
                // 重置
                mMediaPlayer.reset();
                // 设置音频类型
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                // 设置 mp3 数据源
                mMediaPlayer.setDataSource(file.getAbsolutePath());
                Log.i(TAG,"  mMediaPlayer.setDataSource(file.getAbsolutePath())\n" +
                        "    file.getAbsolutePath() = " + file.getAbsolutePath());


                // 数据异步缓冲
                mMediaPlayer.prepareAsync();
                Log.i(TAG,"  mMediaPlayer.prepareAsync()");
                // 监听缓存事件
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Log.i(TAG," -- OnPreparedListener : onPrepared()  异步缓冲结束");

                        // 获取进度条最大值
                        maxProgress = mMediaPlayer.getDuration();
                        Log.i(TAG,"    maxProgress = " + maxProgress);

                        // 发送本地广播，告诉 PlayerActivity : Type, Name, MaxProgress
                        Intent intent_playInfo = new Intent(BroadcastAction.PlayInfoAction);
                        intent_playInfo.putExtra("type", 0);
                        intent_playInfo.putExtra("musicname", musicName);
                        intent_playInfo.putExtra("maxprogress", maxProgress);
                        mLocalBroadcastManager.sendBroadcast(intent_playInfo);
                        Log.i(TAG,"  mLocalBroadcastManager.sendBroadcast(intent_playInfo)" +
                                "  type = 0" +
                                "  musicname = " + musicName +
                                "  maxprogress = " + maxProgress);

                        // 此处不需要一启动服务就开始播放
                        if(count > 1)mp.start();
                        Log.i(TAG,"  mp.start()  播放开始");

                        // 设置单曲循环
                        isLoop = true;
                        mp.setLooping(isLoop);
                        Log.i(TAG,"  mp.setLooping(isLoop)  单曲循环");
                    }
                });


                /*
                 * 设置 mMediaPlayer 播放完成监听器，做切歌操作
                 */
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.i(TAG," -- OnCompletionListener : onCompletion()  播放结束");

//                        // 无需关闭服务
//                        stopSelf();
//                        Log.i(TAG,"  stopSelf()  关闭服务");
                    }
                });

            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "mMediaPlayer error", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                System.out.println(e);
            }
        } else {
            Toast.makeText(getApplicationContext(), "file error", Toast.LENGTH_LONG).show();
        }
    }


    /*
     *  关闭服务需同时调用 unbindService(serviceConnection) 和 stopService(stopIntent)
     *  才会执行 onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, " ----- PlayerService : onDestroy");

        // 释放媒体播放器
        mMediaPlayer.release();

        // 关闭定时器
        mTimerTask.cancel();
        mTimer.cancel();


    }
}
