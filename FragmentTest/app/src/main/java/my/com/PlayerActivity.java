package my.com;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * Created by MY on 2017/7/21.
 */

public class PlayerActivity extends Activity {

    private AudioManager audiomanager;
    private int currentVolume, maxVolume;
    private SeekBar player_volume_seekbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // 获取系统的音频服务
        audiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 获取系统最大音量
        maxVolume = audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 获取系统当前音量
        currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);

        player_volume_seekbar = (SeekBar) findViewById(R.id.player_volume_seekbar);
        player_volume_seekbar.setOnSeekBarChangeListener(seekBarChangeListener);
        player_volume_seekbar.setMax(maxVolume);
        player_volume_seekbar.setProgress(currentVolume);
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i("TAG","onStartTrackingTouch");
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i("TAG","onStopTrackingTouch");
        }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.i("TAG","onProgressChanged");
           Toast.makeText(PlayerActivity.this, "Current Volume : " + progress + "%", Toast.LENGTH_SHORT).show();

            audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_SHOW_UI);
            currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);     // 获取系统当前音量
            //player_volume_seekbar.setProgress(currentVolume);
        }
    };
}
