package my.com;

import android.app.Activity;
import android.content.Context;
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

/**
 * Created by MY on 2017/7/21.
 */

public class PlayerActivity extends BaseActivity {

    private AudioManager audiomanager;          // 控制系统音量的对象
    private int currentVolume, maxVolume;       // 获取系统当前音量，最大音量
    private SeekBar player_volume_seekbar;      // 音量大小拖动条


    private MediaPlayer mediaplayer;    // 媒体播放器的对象
    private Timer timer;    // 定时器对象
    private int currentProgress, maxProgress;
    private SeekBar player_progress_seekbar;    // 进度拖动条
    private TextView player_progress_time1, player_progress_time2;
    private String str_progress_time1, str_progress_time2;

    private boolean isPlay = false;

    private ImageView player_mplay_iv;

    private static final String TAG = "PlayerActivity";


    /*
     * 重写按键事件 onKeyDown
     *
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK :
                if(event.getRepeatCount() == 0)moveTaskToBack(true);
                return true;

            case KeyEvent.KEYCODE_VOLUME_UP :
                audiomanager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                updatePlayerVolumeSeekbar();
                return true;

            case KeyEvent.KEYCODE_VOLUME_DOWN :
                audiomanager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                updatePlayerVolumeSeekbar();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void updatePlayerVolumeSeekbar(){
        // 重新获取系统当前音量
        currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // 更新音量拖动条
        player_volume_seekbar.setProgress(currentVolume);
        // 页面浮动提示信息：Toast.makeText()
        Toast.makeText(PlayerActivity.this, "Current Volume : " + currentVolume*100/maxVolume + "%", Toast.LENGTH_SHORT).show();
    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        moveTaskToBack(true);
//    }

    /*
    onCreate(@Nullable Bundle savedInstanceState)
    */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        volume_seekbar();      // 音量拖动条（player_volume_seekbar）的处理
        progress_seekbar();    // 进度拖动条（player_progress_seekbar）的处理
    }


    // 音量拖动条（player_volume_seekbar）的处理
    private void volume_seekbar(){
        // 获取系统的音频服务，实例化系统声音控制对象 audiomanager
        audiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 获取系统最大音量 getStreamMaxVolume（int streamType）
        maxVolume = audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 获取系统当前音量 getStreamVolume(int streamType)
        currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);

        // (SeekBar) findViewById()
        player_volume_seekbar = (SeekBar) findViewById(R.id.player_volume_seekbar);
        // 设置音量拖动条的最大值
        player_volume_seekbar.setMax(maxVolume);
        // 设置音量拖动条的当前值
        player_volume_seekbar.setProgress(currentVolume);
        // 设置拖动条事件监听器，volume_seekBarChangeListener 为对应监听方法的变量（个人理解类似匿名内部类）
        player_volume_seekbar.setOnSeekBarChangeListener(volume_seekBarChangeListener);
    }
    // 音量拖动条事件监听器  SeekBar.OnSeekBarChangeListener volume_seekBarChangeListener
    private SeekBar.OnSeekBarChangeListener volume_seekBarChangeListener = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // 调试信息
            Log.i(TAG,"onProgressChanged : progress = " + progress );

            // 设置音量大小 setStreamVolume(int streamType, int index, intflags)
            audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_SHOW_UI);
            // 重新获取系统当前音量
            currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);// 或者这样写：currentVolume = progress;
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i(TAG,"onStartTrackingTouch");
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i(TAG,"onStopTrackingTouch");

            // 页面浮动提示信息：Toast.makeText()
            Toast.makeText(PlayerActivity.this, "Current Volume : " + currentVolume*100/maxVolume + "%", Toast.LENGTH_SHORT).show();
        }
    };
    // 当按手机的音量 + - 键，系统音量发生改变时，需要更新音量拖动条（player_volume_seekbar）状态
//    private void updata_volume_seekbar(){
// 涉及广播组件的使用，学习吧 ！！！ ~\(≧▽≦)/~
//    }
// ！！！
// 暂时使用重写按键 onKeyDown(int keyCode, KeyEvent event)
// BUG ：
// 1）重写了按键功能
// 2）只限于这个 Activity 的按键功能有效
// 3）写法极其不规范，方法不能复用
// ！！！



    // 进度拖动条（player_progress_seekbar）的处理
    private void progress_seekbar(){
        // 实例化媒体播放器 MediaPlayer
        mediaplayer = new MediaPlayer();

        m_init();

        // () findViewById(R.id);
        player_progress_time1 = (TextView) findViewById(R.id.player_progress_time1);
        player_progress_time2 = (TextView) findViewById(R.id.player_progress_time2);
        player_progress_seekbar = (SeekBar) findViewById(R.id.player_progress_seekbar);
        // 设置拖动条事件监听器， progress_seekBarChangeListener 为对应监听方法的变量（个人理解类似匿名内部类）
        player_progress_seekbar.setOnSeekBarChangeListener(progress_seekBarChangeListener);

        // (ImageView) findViewById(R.id)
        player_mplay_iv = (ImageView) findViewById(R.id.player_mplay_iv);

        // 设置播放按钮的点击事件
        player_mplay_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlay == false){
                    player_mplay_iv.setSelected(true);
                    isPlay = true;
                    Log.i(TAG,"isPlay = true  Timer Run");
                    //mediaplayer.reset();
                    mediaplayer.start();
                    Log.i(TAG,"mediaplayer.start()");
                }
                else {
                    player_mplay_iv.setSelected(false);
                    isPlay = false;
                    Log.i(TAG,"isPlay = false  Timer Stop");
                    mediaplayer.pause();
                    Log.i(TAG,"mediaplayer.pause()");
                }
            }
        });
    }

    public void m_init(){
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
                Log.i(TAG,"mediaplayer.setDataSource(file.getAbsolutePath())");
                // 数据异步缓冲
                mediaplayer.prepareAsync();
                Log.i(TAG,"mediaplayer.prepareAsync()");
                // 监听缓存事件
                mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Log.i(TAG," -- onPrepared");
                        // 设置进度条最大值
                        player_progress_seekbar.setMax(mediaplayer.getDuration());
                        maxProgress = mediaplayer.getDuration();
                        Log.i(TAG,"player_progress_seekbar.setMax(mediaplayer.getDuration());");

                        // 显示进度条最大值
                        str_progress_time2 = String.format("%1$02d:%2$02d",(maxProgress/1000)/60,(maxProgress/1000)%60);
                        player_progress_time2.setText(str_progress_time2);
                    }
                });

                isPlay = false;
                //定时器，监听播放时回调函数，更新进度拖动条状态
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

            }catch (Exception e){
                Log.i(TAG," -- catch (Exception e)");
                Toast.makeText(getApplicationContext(), "play error", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                System.out.println(e);
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "file error", Toast.LENGTH_LONG).show();
            Log.i(TAG,file.getAbsolutePath() + " -- file.exists() fail");
        }
    }

    // 进度拖动条事件监听器 SeekBar.OnSeekBarChangeListener progress_seekBarChangeListener
    private SeekBar.OnSeekBarChangeListener progress_seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // 记录播放进度
            currentProgress = progress;
            Log.i(TAG," -- onProgressChanged  " + currentProgress + " : " + maxProgress);

            // 显示当前进度
            str_progress_time1 = String.format( "%1$02d:%2$02d", ( currentProgress/1000)/60, (currentProgress/1000)%60 );
            player_progress_time1.setText(str_progress_time1);

//            ！！！
//            BUG        这里的播放完成判断有点小问题！！！！！！！！！
//            ！！！
            if(currentProgress >= maxProgress - 1000)
            {
                Log.i(TAG," -- currentProgress >= maxProgress - 1000");
                player_mplay_iv.setSelected(false);
                isPlay = false;
                Log.i(TAG,"isPlay = false  Timer Stop");
                player_progress_seekbar.setProgress(0);
                Log.i(TAG," player_progress_seekbar.setProgress(0)");
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i(TAG," -- onStartTrackingTouch");
            // 滑动时,暂停后台定时器
            player_mplay_iv.setSelected(false);
            isPlay = false;
            Log.i(TAG,"isPlay = false  Timer Stop");
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i(TAG," -- onStopTrackingTouch");

            // 滑动结束后，重新设置值
            player_mplay_iv.setSelected(true);
            isPlay = true;
            Log.i(TAG,"isPlay = true  Timer Run");
            mediaplayer.seekTo(player_progress_seekbar.getProgress());
            Log.i(TAG,"mediaplayer.seekTo(player_progress_seekbar.getProgress())");
            mediaplayer.start();
            Log.i(TAG,"mediaplayer.start()");
        }
    };



//    @Override
//    protected void onDestroy() {
//        mediaplayer.release();
//        timer.cancel();
//        timer = null;
//        mediaplayer = null;
//        super.onDestroy();
//    }

}
