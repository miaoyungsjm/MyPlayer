package my.com;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import my.com.action.BroadcastAction;

/**
 * Created by MY on 2017/7/21.
 */

public class PlayerActivity extends BaseActivity {

    private AudioManager audiomanager;          // 控制系统音量的对象
    private int currentVolume, maxVolume;       // 获取系统当前音量，最大音量
    private SeekBar player_volume_seekbar;      // 音量拖动条


    private MediaPlayer mediaplayer;    // 媒体播放器的对象
    private Timer timer;    // 定时器对象
    private int currentProgress, maxProgress;   // 歌曲当前进度，最大进度
    private SeekBar player_progress_seekbar;    // 进度拖动条
    private TextView player_progress_time1, player_progress_time2;  //歌曲当前时间，结束时间
    private String str_progress_time1, str_progress_time2;  //歌曲当前时间，结束时间转字符串
    private boolean isPlay = false;             // 判断当前是否正在播放，用于开关定时器和更新播放按钮状态
    private ImageView player_mplay_iv;          //播放按钮


    private IntentFilter intentFilter;          // 用于系统音量广播接收器注册的过滤器
    private VolumeChangeReceiver volumeChangeReceiver;      // 系统音量广播接收器内部类对象


    private static final String TAG = "PlayerActivity";         // 调试信息 TAG 标签



    /*
     * onCreate(@Nullable Bundle savedInstanceState)
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        init_views();       // 界面控件和对象（声音控制、媒体播放器）初始化
        volume_receiver_register();      // 音量广播接收器的注册

        volume_seekbar();       // 音量拖动条（player_volume_seekbar）的处理
        progress_seekbar();     // 进度拖动条（player_progress_seekbar）的处理
    }

    private void init_views(){
        // 获取系统的音频服务，实例化系统声音控制对象 audiomanager
        audiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // (SeekBar) findViewById()
        player_volume_seekbar = (SeekBar) findViewById(R.id.player_volume_seekbar);

        // 实例化媒体播放器 MediaPlayer
        mediaplayer = new MediaPlayer();
        // () findViewById(R.id);
        player_progress_time1 = (TextView) findViewById(R.id.player_progress_time1);
        player_progress_time2 = (TextView) findViewById(R.id.player_progress_time2);
        player_progress_seekbar = (SeekBar) findViewById(R.id.player_progress_seekbar);
        // (ImageView) findViewById(R.id)
        player_mplay_iv = (ImageView) findViewById(R.id.player_mplay_iv);
    }

    /*
     * 系统音量广播接收器内部类 VolumeChangeReceiver
     * 当收到系统音量变化时，执行 onReceive 方法
     */
    class VolumeChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG," -- VolumeChangeReceiver : onReceive");
            // 获取系统当前音量 getStreamVolume(int streamType)
            currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
            // 设置音量拖动条的当前值
            player_volume_seekbar.setProgress(currentVolume);
            // 页面浮动提示信息：Toast.makeText()
            //Toast.makeText(PlayerActivity.this, "Current Volume : " + currentVolume*100/maxVolume + "%", Toast.LENGTH_SHORT).show();
            Log.i(TAG,"  update player_volume_seekbar");
        }
    }
    private void volume_receiver_register(){
        intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastAction.VolumeAction);// "android.media.VOLUME_CHANGED_ACTION"
        volumeChangeReceiver = new VolumeChangeReceiver();
        registerReceiver(volumeChangeReceiver, intentFilter);
    }

    /*
     * 音量拖动条（player_volume_seekbar）的处理
     */
    private void volume_seekbar(){
        // 获取系统最大音量 getStreamMaxVolume（int streamType）
        maxVolume = audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 获取系统当前音量 getStreamVolume(int streamType)
        currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);

        // 设置音量拖动条的最大值
        player_volume_seekbar.setMax(maxVolume);
        Log.i(TAG, "  player_volume_seekbar.setMax(maxVolume) : maxVolume = " + maxVolume);
        // 设置音量拖动条的当前值
        player_volume_seekbar.setProgress(currentVolume);
        Log.i(TAG, "  player_volume_seekbar.setProgress(currentVolume) : currentVolume" + currentVolume);
        // 设置拖动条事件监听器，volume_seekBarChangeListener 为对应监听方法的变量（个人理解类似匿名内部类）
        player_volume_seekbar.setOnSeekBarChangeListener(volume_seekBarChangeListener);
    }

    /*
     * 音量拖动条事件监听器  SeekBar.OnSeekBarChangeListener volume_seekBarChangeListener
     */
    private SeekBar.OnSeekBarChangeListener volume_seekBarChangeListener = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // 调试信息
            Log.i(TAG," -- OnSeekBarChangeListener : onProgressChanged  progress = " + progress);
            // 设置音量大小 setStreamVolume(int streamType, int index, intflags)
            audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_SHOW_UI);
            // 重新获取系统当前音量
            currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
            // 或者这样写：
            // currentVolume = progress;
            // audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_SHOW_UI);
            Log.i(TAG,"  update AudioManager.STREAM_MUSIC");
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i(TAG," -- OnSeekBarChangeListener : onStartTrackingTouch");
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i(TAG," -- OnSeekBarChangeListener : onStopTrackingTouch");
            // 页面浮动提示信息：Toast.makeText()
            Toast.makeText(PlayerActivity.this, "Current Volume : " + currentVolume*100/maxVolume + "%", Toast.LENGTH_SHORT).show();
        }
    };


    /*
     * 进度拖动条（player_progress_seekbar）的处理
     */
    private void progress_seekbar(){
        // 媒体播放器 mediaplayer 初始化
        mediaplayer_init();
        // 定时器 timer 初始化
        timer_init();

        // 设置拖动条事件监听器， progress_seekBarChangeListener 为对应监听方法的变量（个人理解类似匿名内部类）
        player_progress_seekbar.setOnSeekBarChangeListener(progress_seekBarChangeListener);

        // 设置播放按钮的点击事件
        player_mplay_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlay == false){
                    player_mplay_iv.setSelected(true);
                    isPlay = true;
                    Log.i(TAG,"  isPlay = true  Timer Run");
                    //mediaplayer.reset();
                    mediaplayer.start();
                    Log.i(TAG,"  mediaplayer.start()");
                }
                else {
                    player_mplay_iv.setSelected(false);
                    isPlay = false;
                    Log.i(TAG,"  isPlay = false  Timer Stop");
                    mediaplayer.pause();
                    Log.i(TAG,"  mediaplayer.pause()");
                }
            }
        });
    }


    /* ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
     * 之后需要学习服务组件，线程的使用
     * 把 MediaPlayer 放在 Service 里，由服务来控制 MediaPlayer
     * ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
     */

    // 媒体播放器 mediaplayer 初始化
    public void mediaplayer_init(){
        File file = new File("/sdcard/Files/", "music.mp3");
        Log.i(TAG," -- file.getAbsolutePath() :" + file.getAbsolutePath());
        if(file.exists()){
            Log.i(TAG," -- file.exists()");
            try{
                Log.i(TAG," -- try");
                // 设置音频类型
                mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                // 设置mp3数据源
                mediaplayer.setDataSource(file.getAbsolutePath());
                Log.i(TAG,"  mediaplayer.setDataSource(file.getAbsolutePath())");
                // 数据异步缓冲
                mediaplayer.prepareAsync();
                Log.i(TAG,"  mediaplayer.prepareAsync()");
                // 监听缓存事件
                mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Log.i(TAG," -- OnPreparedListener : onPrepared()  异步缓冲结束");
                        // 不需要一进去就播放
                        //mp.start();

                        // 设置进度条最大值
                        maxProgress = mediaplayer.getDuration();
                        player_progress_seekbar.setMax(mediaplayer.getDuration());
                        Log.i(TAG,"  player_progress_seekbar.setMax() : maxProgress = " + maxProgress);
                        // <Textview>控件 player_progress_time2 显示进度条最大值
                        str_progress_time2 = String.format("%1$02d:%2$02d",(maxProgress/1000)/60,(maxProgress/1000)%60);
                        player_progress_time2.setText(str_progress_time2);
                    }
                });

                mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.i(TAG," -- OnCompletionListener : onCompletion()  音乐播放结束");
                        player_mplay_iv.setSelected(false);
                        isPlay = false;
                        Log.i(TAG,"  isPlay = false  Timer Stop");
                        player_progress_seekbar.setProgress(0);
                        Log.i(TAG,"  player_progress_seekbar.setProgress(0)");
                    }
                });

            }catch (Exception e){
                Log.i(TAG," -- catch (Exception e)");
                Toast.makeText(getApplicationContext(), "mediaplayer error", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                System.out.println(e);
            }
        } else {
            Toast.makeText(getApplicationContext(), "file error", Toast.LENGTH_LONG).show();
            Log.i(TAG,file.getAbsolutePath() + " -- file.exists() == false");
        }
    }

    // 定时器 timer 初始化
    public void timer_init(){
        // 设置定时器（1s），判断 isPlay 状态，更新进度拖动条状态
        isPlay = false;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(isPlay){
                    Log.i(TAG," -- Timer Run : update player_progress_seekbar");
                    player_progress_seekbar.setProgress(mediaplayer.getCurrentPosition());
                }
            }
        },0,1000);
    }

    /*
     * 进度拖动条事件监听器 SeekBar.OnSeekBarChangeListener progress_seekBarChangeListener
     */
    private SeekBar.OnSeekBarChangeListener progress_seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // 记录播放进度
            currentProgress = progress;
            Log.i(TAG,"  OnSeekBarChangeListener : onProgressChanged  " + currentProgress + " : " + maxProgress);

            // 显示当前进度
            str_progress_time1 = String.format( "%1$02d:%2$02d", ( currentProgress/1000)/60, (currentProgress/1000)%60 );
            player_progress_time1.setText(str_progress_time1);
            Log.i(TAG,"  OnSeekBarChangeListener : onProgressChanged  " + str_progress_time1 + " : " + str_progress_time2);

        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i(TAG," -- OnSeekBarChangeListener : onStartTrackingTouch");
            // 滑动时,暂停后台定时器
            player_mplay_iv.setSelected(false);
            isPlay = false;
            Log.i(TAG,"  isPlay = false  Timer Stop");
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i(TAG," -- OnSeekBarChangeListener : onStopTrackingTouch");
            // 滑动结束后，重新设置值
            player_mplay_iv.setSelected(true);
            isPlay = true;
            Log.i(TAG,"  isPlay = true  Timer Run");
            mediaplayer.seekTo(player_progress_seekbar.getProgress());
            Log.i(TAG,"  mediaplayer.seekTo()");
            mediaplayer.start();
            Log.i(TAG,"  mediaplayer.start()");
        }
    };

    /*
     * 重写按键事件 onKeyDown
     * 1）重写返回键的功能为：将活动转至后台 moveTaskToBack
     * 2）音量按键的重写已被注释，改为使用音量变化的广播接收器 volumeChangeReceiver
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK :
                if(event.getRepeatCount() == 0)moveTaskToBack(true);
                Toast.makeText(PlayerActivity.this, "moveTaskToBack", Toast.LENGTH_LONG).show();
                return true;

//            case KeyEvent.KEYCODE_VOLUME_UP :
//                audiomanager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
//                updatePlayerVolumeSeekbar();
//                return true;
//
//            case KeyEvent.KEYCODE_VOLUME_DOWN :
//                audiomanager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
//                updatePlayerVolumeSeekbar();
//                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
//    public void updatePlayerVolumeSeekbar(){
//        // 重新获取系统当前音量
//        currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        // 更新音量拖动条
//        player_volume_seekbar.setProgress(currentVolume);
//        // 页面浮动提示信息：Toast.makeText()
//        Toast.makeText(PlayerActivity.this, "Current Volume : " + currentVolume*100/maxVolume + "%", Toast.LENGTH_SHORT).show();
//    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        moveTaskToBack(true);
//    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(volumeChangeReceiver);
        mediaplayer.release();
        timer.cancel();
        timer = null;
        mediaplayer = null;
        super.onDestroy();
    }

}
