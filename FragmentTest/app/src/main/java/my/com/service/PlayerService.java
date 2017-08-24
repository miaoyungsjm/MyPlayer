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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import my.com.action.BroadcastAction;
import my.com.model.PlayInfo;
import my.com.utils.MusicUtils;

/**
 * Created by MY on 2017/7/29.
 *
 */
public class PlayerService extends Service{

    private LocalBroadcastManager mLocalBroadcastManager;    //  本地广播管理

    private List<PlayInfo> mPlayList;           //  播放列表
    int mPlayPosition;               //  播放位置

    private String musicPath;       //  播放路径
    private String musicName;       //  《你的名字》
    private String singer;          //  歌手

    private MediaPlayer mMediaPlayer;    //  媒体播放器的对象
    private int currentProgress, maxProgress;   //  歌曲当前进度，最大进度

    private Timer mTimer;    //  定时器对象
    private TimerTask mTimerTask;

    int count = 0 ;      //  记录开启服务次数


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
     *  控制 mMediaPlayer 的播放、暂停、跳条、模式
     */
    private PlayerControlBinder playerControlBinder = new PlayerControlBinder();
    public class PlayerControlBinder extends Binder{

        public void mPlay(){
            Log.d(TAG, " -- PlayerControlBinder : mPlay()");
            mMediaPlayer.start();
        }
        public void mPause(){
            Log.d(TAG, " -- PlayerControlBinder : mPause()");
            if(mMediaPlayer.isPlaying())mMediaPlayer.pause();
        }
        public void mSeekTo(int msec){
            Log.d(TAG, " -- PlayerControlBinder : mSeekTo()");
            mMediaPlayer.seekTo(msec);
        }
        public void mSkip(int position){
            Log.d(TAG, " -- PlayerControlBinder : mSkip()");

            PlayInfo tPlayInfo;;

            mPlayList = MusicUtils.getPlayList();//  重新获取播放列表

            if(mPlayList.size() > 0) { //  当播放列表不为空时才可以选择下一首播放

                mPlayPosition = MusicUtils.getPlayPosition();//  获取前播放位置

                if (mPlayPosition >= 0 ){//  重置前播放状态
                    tPlayInfo = mPlayList.get(mPlayPosition);
                    tPlayInfo.mState = false;
                    Log.d(TAG, " tPlayInfo.mState = false    mPlayPosition = " + mPlayPosition);
                }

                mPlayPosition = position;//  设置新播放位置
                MusicUtils.setPlayPosition(mPlayPosition);

                tPlayInfo = mPlayList.get(mPlayPosition);
                tPlayInfo.mState = true;
                Log.d(TAG, " tPlayInfo.mState = true    mPlayPosition = " + mPlayPosition);

                musicPath = tPlayInfo.getPath();
                musicName = tPlayInfo.getName();
                singer = tPlayInfo.getSinger();

                if (musicPath != null)init_MediaPlayer();// 初始化多媒体播放器
            }else {
                Log.d(TAG, "  mPlayList.size() == 0");
            }
        }
        public void mToLoop(boolean isLoop){
            Log.d(TAG, " -- PlayerControlBinder : mToLoop()");
            if(isLoop){            // 设置是否单曲循环
                mMediaPlayer.setLooping(true);
                Log.d(TAG, "  setLooping(true)");
            }else {
                mMediaPlayer.setLooping(false);
                Log.d(TAG, "  setLooping(false)");
            }
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
             *  传递音乐当前进度。否则，就和服务一起“挂着”，做做计时什么也不干
             *
             *  那为什么不把 Timer 关了？挂着不会显得浪费资源吗？
             *  这就是留下来的一个坑！！！
             *
             *  不关的原因：
             *  1）timer 定时器，关键任务还是有 timerTask 去做，而 timerTask 用到多线程处理，
             *     线程是没有暂停的，只能 设置标记休眠线程 或 重开线程 等方法来实现线程的控制
             *  2）该什么时候关闭 Timer ？？？ 又什么时候重新创建？？？ 情况有点复杂！！！
             *    （自己脑补：播放、暂停、切歌怎么操作）
             */
        }

    }

    //    // 线程的编写，本来想用于更新 PlayerActivity 的播放进度的，但 Timer 是多线程，就没必要了
    //    new Thread(new Runnable() {
    //        @Override
    //        public void run() {
    //            Log.d(TAG," -- Thread : run()  线程");
    //
    //        }
    //    }).start();


    /*
     *
     *  初始化定时器 Timer
     *  因为服务中不进行耗时操作，启动一个线程来传递音乐当前进度
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
//                    Log.d(TAG," -- Timer : run()");
                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
                        //获取当前进度
                        currentProgress = mMediaPlayer.getCurrentPosition();

                        Intent intent = new Intent(BroadcastAction.PlayInfoProgressAction);
                        intent.putExtra("currentprogress", currentProgress);

                        mLocalBroadcastManager.sendBroadcast(intent);
                        Log.d(TAG," -- Timer : mLocalBroadcastManager.sendBroadcast(intent)  PlayInfoProgressAction  " +
                                "  currentprogress = " + currentProgress);
                    }
                }
            };
        }
        if(mTimer != null) mTimer.schedule(mTimerTask, 0, 1000);
    }


    /*
     *
     *  启动服务
     */
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.i(TAG, " ----- PlayerService : onStartCommand");

        //  实例化 / 重置 播放列表
        if (mPlayList == null){
            mPlayList = new ArrayList<>();
        }

        mPlayList = MusicUtils.getPlayList();//  获取列表
        Log.d(TAG, "    mPlayList = MusicUtils.getPlayList()  获取列表");
        if (mPlayList.size() <= 0){//  如果播放列表为空，则扫描本地音乐
            mPlayList = MusicUtils.scanLocalMusic(this);
            Log.d(TAG, "  mPlayList = MusicUtils.scanLocalMusic(this)  获取本地列表");
        }

        if(mPlayList.size() > 0) {
            mPlayList = MusicUtils.updatePlayList(mPlayList);

            mPlayPosition = MusicUtils.getPlayPosition();

            PlayInfo tPlayInfo = mPlayList.get(mPlayPosition);
            tPlayInfo.mState = true;

            musicPath = tPlayInfo.getPath();
            musicName = tPlayInfo.getName();
            singer = tPlayInfo.getSinger();

            if (musicPath != null)init_MediaPlayer();           // 初始化多媒体播放器
        }else {
            Log.d(TAG, "  mPlayList is null");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void init_MediaPlayer(){
        Log.d(TAG, " -- init_MediaPlayer()");

        count++;        //  记录播放次数

        File file = new File(musicPath);
        Log.d(TAG,"    file.getAbsolutePath() :" + file.getAbsolutePath());

        if(file.exists()){
            Log.d(TAG,"    file.exists()");

            if(mMediaPlayer == null) mMediaPlayer = new MediaPlayer();

            try{
                // 重置
                mMediaPlayer.reset();
                // 设置音频类型
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                // 设置 mp3 数据源
                mMediaPlayer.setDataSource(file.getAbsolutePath());
                Log.d(TAG,"  mMediaPlayer.setDataSource(file.getAbsolutePath())\n" +
                        "    file.getAbsolutePath() = " + file.getAbsolutePath());

                // 数据异步缓冲
                mMediaPlayer.prepareAsync();
                Log.d(TAG,"  mMediaPlayer.prepareAsync()");
                // 监听缓存事件
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Log.d(TAG," -- OnPreparedListener : onPrepared()  异步缓冲结束");

                        // 获取进度条最大值
                        maxProgress = mMediaPlayer.getDuration();
                        Log.d(TAG,"    maxProgress = " + maxProgress);

                        // 发送本地广播，告诉 PlayerActivity :  Name, Singer, MaxProgress
                        Intent intent = new Intent(BroadcastAction.PlayInfoDataAction);
                        intent.putExtra("musicname", musicName);
                        intent.putExtra("singer", singer);
                        intent.putExtra("maxprogress", maxProgress);
                        mLocalBroadcastManager.sendBroadcast(intent);
                        Log.d(TAG,"  mLocalBroadcastManager.sendBroadcast(intent_playInfo)  PlayInfoDataAction  " +
                                "  musicname = " + musicName +
                                "  singer = " + singer +
                                "  maxprogress = " + maxProgress);

                        // 此处不需要一启动服务就开始播放
                        if(count > 1)mp.start();
                        Log.d(TAG,"  mp.start()  播放开始");
                    }
                });


                /*
                 *  监听 mMediaPlayer 是否播放完成
                 */
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.d(TAG," -- OnCompletionListener : onCompletion()  播放结束");

//                        // 无需关闭服务
//                        stopSelf();
//                        Log.d(TAG,"  stopSelf()  关闭服务");

                        // 发送本地广播，更新 PlayerActivity 当前播放位置
                        Intent intent = new Intent(BroadcastAction.PlayInfoProgressAction);
                        intent.putExtra("currentprogress", 0);
                        intent.putExtra("isFinish", true);
                        mLocalBroadcastManager.sendBroadcast(intent);
                        Log.d(TAG,"  mLocalBroadcastManager.sendBroadcast(intent)\n"+
                                "    PlayInfoProgressAction :" +
                                "    isFinish = true");

                        //  如果不是单曲循环，切歌
                        if(!mMediaPlayer.isLooping()){

                            PlayInfo tPlayInfo;

                            //  重新获取播放列表
                            mPlayList = MusicUtils.getPlayList();

                            //  当播放列表不为空时才可以选择下一首播放
                            if(mPlayList.size() > 0) {

                                //  获取前播放位置
                                mPlayPosition = MusicUtils.getPlayPosition();

                                //  重置前播放状态
                                if (mPlayPosition >= 0 ){    //  当第一首被删除，mPlayPosition 为负数，无法找到对象
                                    tPlayInfo = mPlayList.get(mPlayPosition);
                                    tPlayInfo.mState = false;
                                }

                                //  设置新播放位置
                                if (mPlayPosition < mPlayList.size() - 1) {
                                    mPlayPosition++;
                                } else {
                                    mPlayPosition = 0;
                                }
                                //  更新播放位置
                                MusicUtils.setPlayPosition(mPlayPosition);

                                //  更新播放状态
                                tPlayInfo = mPlayList.get(mPlayPosition);
                                tPlayInfo.mState = true;

                                musicPath = tPlayInfo.getPath();
                                musicName = tPlayInfo.getName();
                                singer = tPlayInfo.getSinger();

                                if (musicPath != null)init_MediaPlayer();// 初始化多媒体播放器

                            }else {
                                Log.d(TAG, "  mPlayList is null");
                            }
                        }else {
                            Log.d(TAG, "  mMediaPlayer.isLooping()");
                        }


                    }
                });

            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "mMediaPlayer error", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                System.out.println(e);
            }
        } else {
            Toast.makeText(getApplicationContext(), "File error", Toast.LENGTH_LONG).show();
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
