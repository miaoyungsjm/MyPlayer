package my.com;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import my.com.action.BroadcastAction;
import my.com.service.PlayerService;

/**
 * Created by MY on 2017/7/21.
 */

public class PlayerActivity extends BaseActivity {

    private AudioManager audiomanager;          // 控制系统音量的对象
    private int currentVolume, maxVolume;       // 获取系统当前音量，最大音量
    private SeekBar player_volume_seekbar;      // 音量拖动条


    private String str_musicName;
    private TextView title_player_music_name;
    private SeekBar player_progress_seekbar;    // 进度拖动条
    private int currentProgress, maxProgress;   // 歌曲当前进度，最大进度
    private String str_currentProgress, str_maxProgress;
    private TextView player_current_progress, player_max_progress;  //歌曲当前时间，结束时间
    private boolean isPlay = false;
    private ImageView player_mplay_iv;          //播放按钮
    private ImageView player_mnext_iv;          //下一首按钮
    private ImageView player_mlast_iv;          //上一首按钮

    private ImageView title_player_hide;

    private VolumeChangeReceiver volumeChangeReceiver;      // 系统音量广播接收器内部类对象

    private LocalBroadcastManager localBroadcastManager;    // 本地广播管理
    private PlayInfoLocalReceiver playInfoLocalReceiver;    // PlayService 本地广播接收器内部类对象


    private static final String TAG = "PlayerActivity";         // 调试信息 TAG 标签


    /*
     * serviceConnection -- PlayerService 服务的绑定与解除
     * playerControlBinder -- PlayerService 服务的控制（接口函数）对象
     */
    private PlayerService.PlayerControlBinder playerControlBinder;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, " -- ServiceConnection : onServiceConnected");
            playerControlBinder = (PlayerService.PlayerControlBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, " -- ServiceConnection : onServiceDisconnected");
        }
    };

    /*
     * onCreate(@Nullable Bundle savedInstanceState)
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Log.d(TAG, " ------------------------------ PlayerActivity : onCreate");

        // 初始化界面控件
        init_views();

        // 获取系统的音频服务，实例化系统声音控制对象 audiomanager
        audiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volume_receiver_register();      // 系统音量广播接收器的注册，用于监听系统音量键的按下

        // PlayService 本地广播接收器的注册
        // 用于接收 PlayService 发来的 mediaplayer 对象播放的歌名、进度最大值、进度当前值
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        playinfo_local_receiver_register();


        volume_seekbar();       // 音量拖动条（player_volume_seekbar）的处理
        progress_seekbar();     // 进度拖动条（player_progress_seekbar）的处理
    }

    private void init_views(){
        player_volume_seekbar = (SeekBar) findViewById(R.id.player_volume_seekbar);

        title_player_music_name = (TextView) findViewById(R.id.title_player_music_name);

        player_current_progress = (TextView) findViewById(R.id.player_current_progress);
        player_max_progress = (TextView) findViewById(R.id.player_max_progress);
        player_progress_seekbar = (SeekBar) findViewById(R.id.player_progress_seekbar);

        player_mplay_iv = (ImageView) findViewById(R.id.player_mplay_iv);
        player_mnext_iv = (ImageView) findViewById(R.id.player_mnext_iv);
        player_mlast_iv = (ImageView) findViewById(R.id.player_mlast_iv);

        title_player_hide = (ImageView) findViewById(R.id.title_player_hide);
    }

    /*
     * 系统音量广播接收器内部类 VolumeChangeReceiver
     * 当收到系统音量变化时，执行 onReceive 方法
     */
    private class VolumeChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG," -- VolumeChangeReceiver : onReceive()");
            // 获取系统当前音量 getStreamVolume(int streamType)
            currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
            // 设置音量拖动条的当前值
            player_volume_seekbar.setProgress(currentVolume);
            // 页面浮动提示信息：Toast.makeText()
            //Toast.makeText(PlayerActivity.this, "Current Volume : " + currentVolume*100/maxVolume + "%", Toast.LENGTH_SHORT).show();
            Log.i(TAG,"  player_volume_seekbar.setProgress(currentVolume)\n" +
                    "  currentVolume = " + currentVolume);
        }
    }
    private void volume_receiver_register(){
        IntentFilter volumeIntentFilter = new IntentFilter();
        volumeIntentFilter.addAction(BroadcastAction.VolumeAction);// "android.media.VOLUME_CHANGED_ACTION"
        volumeChangeReceiver = new VolumeChangeReceiver();
        registerReceiver(volumeChangeReceiver, volumeIntentFilter);
        Log.i(TAG," -- registerReceiver(volumeChangeReceiver, volumeIntentFilter)\n");
    }


    /*
     * PlayService 本地广播接收器内部类 PlayInfoLocalReceiver
     * 当收到 PlayService 发来的本地广播时，执行 onReceive 方法
     */
    private class PlayInfoLocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int i = intent.getIntExtra("type", -1);
            switch (i){
                case 0:
                    str_musicName = intent.getStringExtra("musicname");
                    maxProgress = intent.getIntExtra("maxprogress", -1);
                    Log.i(TAG, " -- PlayInfoReceiver : onReceive()  type = 0" +
                            "  name = " + str_musicName +
                            "  maxprogress = " + maxProgress);
                    // 设置进度条的最大值
                    player_progress_seekbar.setMax(maxProgress);
                    Log.i(TAG,"  player_progress_seekbar.setMax(maxProgress)\n" +
                            "  maxProgress = " + maxProgress);

                    // <Textview> 控件 player_max_progress 显示进度条最大值
                    str_maxProgress = String.format("%1$02d:%2$02d",(maxProgress/1000)/60,
                            (maxProgress/1000)%60);
                    player_max_progress.setText(str_maxProgress);

                    // <Textview> 控件 title_player_music_name 显示歌名
                    title_player_music_name.setText(str_musicName);
                    break;

                case 1:
                    currentProgress = intent.getIntExtra("currentprogress", -1);
                    Log.i(TAG, " -- PlayInfoReceiver : onReceive()  type = 1" +
                            "  currentprogress = " + currentProgress);
                    // 设置进度条的当前值
                    player_progress_seekbar.setProgress(currentProgress);
                    break;

                default:
                    Log.i(TAG,"  type = -1");
            }
        }
    }
    private void playinfo_local_receiver_register(){
        IntentFilter playinfo_intentFilter = new IntentFilter();
        playinfo_intentFilter.addAction(BroadcastAction.PlayInfoAction);// "com.my.broadcast.PLAYINFO_LOCAL_ACTION"
        playInfoLocalReceiver = new PlayInfoLocalReceiver();
        localBroadcastManager.registerReceiver(playInfoLocalReceiver, playinfo_intentFilter);
        Log.i(TAG," -- localBroadcastManager.registerReceiver(playInfoLocalReceiver, intentFilter)\n");
    }



    /*
     * 音量拖动条（player_volume_seekbar）的处理
     */
    private void volume_seekbar(){
        Log.i(TAG, " -- volume_seekbar");
        // 获取系统最大音量 getStreamMaxVolume（int streamType）
        maxVolume = audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 获取系统当前音量 getStreamVolume(int streamType)
        currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);

        // 设置音量拖动条的最大值
        player_volume_seekbar.setMax(maxVolume);
        Log.i(TAG, "  player_volume_seekbar.setMax(maxVolume)\n" +
                "  maxVolume = " + maxVolume);
        // 设置音量拖动条的当前值
        player_volume_seekbar.setProgress(currentVolume);
        Log.i(TAG, "  player_volume_seekbar.setProgress(currentVolume)\n" +
                "  currentVolume = " + currentVolume);
        // 设置拖动条事件监听器，volume_seekBarChangeListener 为对应监听方法的变量（个人理解类似匿名内部类）
        player_volume_seekbar.setOnSeekBarChangeListener(volume_onSeekBarChangeListener);
    }

    /*
     * 音量拖动条事件监听器  SeekBar.OnSeekBarChangeListener volume_onSeekBarChangeListener
     */
    private SeekBar.OnSeekBarChangeListener volume_onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // 更新当前音量
            currentVolume = progress;
            // 设置音量大小 setStreamVolume(int streamType, int index, intflags)
            audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_SHOW_UI);
            Log.i(TAG," -- OnSeekBarChangeListener : onProgressChanged" +
                    "  progress = " + progress +
                    "  currentVolume = " + currentVolume);
            Log.i(TAG,"  audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_SHOW_UI)");
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
        Log.i(TAG, " -- progress_seekbar");

        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);

        // 启动服务 PlayerService
        intent.putExtra("musicname", "music.mp3");      // 传入播放的歌名
        startService(intent);
        Log.i(TAG, "  startService(startIntent)");

        // 绑定服务 PlayerService
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        Log.i(TAG, "  bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE)");


//        // 解绑服务
//        unbindService(serviceConnection);
//
//        // 关闭 PlayerService
//        Intent stopIntent = new Intent(PlayerActivity.this, PlayerService.class);
//        stopService(stopIntent);

        // 设置按钮的点击事件
        player_mplay_iv.setOnClickListener(onClickListener);
        player_mnext_iv.setOnClickListener(onClickListener);
        player_mlast_iv.setOnClickListener(onClickListener);
        title_player_hide.setOnClickListener(onClickListener);

        // 设置拖动条事件监听器， progress_seekBarChangeListener 为对应监听方法的变量（个人理解类似匿名内部类）
        player_progress_seekbar.setOnSeekBarChangeListener(progress_onSeekBarChangeListener);
    }

    /*
     * 点击事件监听器
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent  intent;
            switch (v.getId())
            {
                case R.id.player_mplay_iv:
                    if(isPlay == false){
                        player_mplay_iv.setSelected(true);
                        isPlay = true;

                        playerControlBinder.mPlay();
                    } else {
                        player_mplay_iv.setSelected(false);
                        isPlay = false;

                        playerControlBinder.mPause();
                    }
                    break;

                case R.id.player_mnext_iv:
                    mNext();
                    break;

                case R.id.player_mlast_iv:
                    mLast();
                    break;

                case R.id.title_player_hide:
                    Intent stopIntent = new Intent(PlayerActivity.this, PlayerService.class);
                    stopService(stopIntent);
                    unbindService(serviceConnection);
            }
        }
    };

    public void mNext(){
        player_progress_seekbar.setProgress(0);
        player_current_progress.setText("00:00");

        player_mplay_iv.setSelected(false);
        isPlay = false;

        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
        intent.putExtra("musicname", "music2.mp3");
        startService(intent);

        player_mplay_iv.setSelected(true);
        isPlay = true;
    }
    public void mLast(){
        player_progress_seekbar.setProgress(0);
        player_current_progress.setText("00:00");

        player_mplay_iv.setSelected(false);
        isPlay = false;

        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
        intent.putExtra("musicname", "music3.mp3");
        startService(intent);

        player_mplay_iv.setSelected(true);
        isPlay = true;
    }




    /*
     * 进度拖动条事件监听器 SeekBar.OnSeekBarChangeListener progress_onSeekBarChangeListener
     */
    private SeekBar.OnSeekBarChangeListener progress_onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.i(TAG," -- OnSeekBarChangeListener : onProgressChanged  " + str_currentProgress + " : " + str_maxProgress);

            /*
             *  拖动进度条处理  <Textview> 控件显示拖动值
             */
            str_currentProgress = String.format("%1$02d:%2$02d",(progress/1000)/60,
                    (progress/1000)%60);
            player_current_progress.setText(str_currentProgress);

        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i(TAG," -- OnSeekBarChangeListener : onStartTrackingTouch");

            player_mplay_iv.setSelected(false);
            isPlay = false;

        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i(TAG," -- OnSeekBarChangeListener : onStopTrackingTouch");
            // 滑动结束后，重新设置值
            player_mplay_iv.setSelected(true);
            isPlay = true;

            playerControlBinder.mSeekTo(player_progress_seekbar.getProgress());
            playerControlBinder.mPlay();
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
        super.onDestroy();
        Log.d(TAG, " ------------------------------ PlayerActivity : onDestroy");

        // 注销广播接收器
        unregisterReceiver(volumeChangeReceiver);
        localBroadcastManager.unregisterReceiver(playInfoLocalReceiver);

        // 关闭服务
        Intent stopIntent = new Intent(PlayerActivity.this, PlayerService.class);
        stopService(stopIntent);
        unbindService(serviceConnection);
    }

}
