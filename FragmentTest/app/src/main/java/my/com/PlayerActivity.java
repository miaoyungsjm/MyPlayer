package my.com;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import my.com.model.PlayInfo;
import my.com.action.BroadcastAction;
import my.com.adapter.PlayListArrayAdapter;
import my.com.service.PlayerService;
import my.com.utils.MusicUtils;

/**
 * Created by MY on 2017/7/21.
 *
 */

public class PlayerActivity extends Activity {

    //  音量部分
    private AudioManager mAudiomanager;          //  控制系统音量和钤声模式对象
    private int currentVolume, maxVolume;      //  获取系统当前音量，最大音量
    private SeekBar player_volume_seekbar;      //  音量拖动条

    //  播放信息
    private String str_musicName;        //  歌名
    private String str_singer;        //  歌手
    private TextView title_player_musicName_tv;     //  歌名显示控件
    private TextView title_player_singer_tv;
    private int currentProgress, maxProgress;      //  歌曲当前进度，最大进度
    private String str_currentProgress, str_maxProgress;    //  int 转 str
    private TextView player_currentProgress_tv, player_maxProgress_tv;      //歌曲当前时间，结束时间
    private SeekBar player_progress_seekbar;        // 进度拖动条

    //  播放状态
    private int mPlayPosition;             //  播放位置
    private boolean isFinish = false;
    private boolean isPlay = false;     //  判断是否在播放，更新进度条的判断
    private boolean isLoop = false;     //  判断是否要循环播放

    //  按钮
    private ImageView player_mplay_iv;          //  播放按钮
    private ImageView player_mnext_iv;          //  下一首按钮
    private ImageView player_mlast_iv;          //  上一首按钮
    private ImageView player_mmode_iv;          //  播放模式按钮
    private ImageView player_mlist_iv;          //  列表按钮
    private ImageView title_player_hide_iv;    //  左上角返回键按钮

    //  列表
    private PopupWindow mPopupWindow;           //  PopupWindow
    private List<PlayInfo> mPlayList;       //  播放列表  List<PlayInfo>
    private PlayListArrayAdapter mPlaylistArrayAdapter;     //  适配器

    // 系统音量广播接收器（内部类对象）
    private VolumeChangeReceiver volumeChangeReceiver;

    // PlayService 本地广播接收器（内部类对象）
    private LocalBroadcastManager mLocalBroadcastManager;    // 本地/局部广播管理器
    private PlayInfoLocalReceiver playInfoLocalReceiver;


    private static final String TAG = "PlayerActivity";         // 调试信息 TAG 标签


    /*
     *  PlayerService 服务绑定
     *
     *  serviceConnection -- 服务的绑定与解除
     *  playerControlBinder -- 服务的控制（接口函数）对象
     */
    private PlayerService.PlayerControlBinder playerControlBinder;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, " -- ServiceConnection : onServiceConnected");
            playerControlBinder = (PlayerService.PlayerControlBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, " -- ServiceConnection : onServiceDisconnected");
        }
    };


    /*
     *  onCreate : PlayerActivity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Log.d(TAG, " ----- PlayerActivity : onCreate");

        //  在 LogCat 打印对应 Activity 活动的类名
        Log.d(TAG, "       Activity : " + getClass().getSimpleName());
        //  在 ActivityCollector.activityList 添加 Activity
        ActivityCollector.addActivity(this);


        //  初始化界面，绑定控件
        initView();

        //  获取系统的音频服务
        mAudiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //  系统音量广播接收器的注册，用于监听“音量键”的按下
        volume_receiver_register();

        //  本地广播接收器的注册，用于接收 PlayService 发来 mediaplayer 对象的播放歌名、进度最大值、进度当前值
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        playinfo_local_receiver_register();


        //  音量拖动条（player_volume_seekbar）的处理
        volume_seekbar();

        //  进度拖动条（player_progress_seekbar）的处理
        player();
    }

    private void initView(){
        title_player_musicName_tv = (TextView) findViewById(R.id.title_player_musicName_tv);
        title_player_singer_tv = (TextView) findViewById(R.id.title_player_singer_tv);
        title_player_hide_iv = (ImageView) findViewById(R.id.title_player_hide_iv);

        player_volume_seekbar = (SeekBar) findViewById(R.id.player_volume_seekbar);

        player_currentProgress_tv = (TextView) findViewById(R.id.player_currentProgress_tv);
        player_maxProgress_tv = (TextView) findViewById(R.id.player_maxProgress_tv);
        player_progress_seekbar = (SeekBar) findViewById(R.id.player_progress_seekbar);

        player_mplay_iv = (ImageView) findViewById(R.id.player_mplay_iv);
        player_mnext_iv = (ImageView) findViewById(R.id.player_mnext_iv);
        player_mlast_iv = (ImageView) findViewById(R.id.player_mlast_iv);
        player_mmode_iv = (ImageView) findViewById(R.id.player_mmode_iv);
        player_mlist_iv = (ImageView) findViewById(R.id.player_mlist_iv);
    }

    /*
     *  系统音量广播接收器内部类 VolumeChangeReceiver
     *  当收到系统音量变化时，执行 onReceive 方法
     */
    private class VolumeChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG," -- VolumeChangeReceiver : onReceive()");
            //  获取系统当前音量 getStreamVolume(int streamType)
            currentVolume = mAudiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
            Log.d(TAG, "    VolumeAction : " +
                    "  currentVolume = " + currentVolume);
            //  设置音量拖动条的当前值
            player_volume_seekbar.setProgress(currentVolume);
            Log.d(TAG, "  player_volume_seekbar.setProgress(currentVolume)\n" +
                    "    currentVolume = " + currentVolume);
        }
    }
    //  系统音量广播接收器的注册
    private void volume_receiver_register(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastAction.VolumeAction);// "android.media.VOLUME_CHANGED_ACTION"

        volumeChangeReceiver = new VolumeChangeReceiver();
        registerReceiver(volumeChangeReceiver, intentFilter);
        Log.d(TAG," -- registerReceiver(volumeChangeReceiver, intentFilter)\n");
    }


    /*
     *  本地广播接收器内部类 PlayInfoLocalReceiver
     *  当收到 PlayService 发来的本地广播时，执行 onReceive 方法
     */
    private class PlayInfoLocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, " -- PlayInfoReceiver : onReceive()");
            String action = intent.getAction();

            if(action.equals(BroadcastAction.PlayInfoDataAction)){
                str_musicName = intent.getStringExtra("musicname");
                str_singer = intent.getStringExtra("singer");
                maxProgress = intent.getIntExtra("maxprogress", -1);
                Log.d(TAG, "    PlayInfoDataAction :" +
                        "    musicname = " + str_musicName +
                        "    musicname = " + str_singer +
                        "    maxprogress = " + maxProgress);

                //  设置进度条的最大值
                player_progress_seekbar.setMax(maxProgress);
                Log.d(TAG, "  player_progress_seekbar.setMax(maxProgress)\n" +
                        "    maxProgress = " + maxProgress);

                //  转换进度条最大值的显示格式
                str_maxProgress = String.format("%1$02d:%2$02d",(maxProgress/1000)/60,
                        (maxProgress/1000)%60);

                //  控件显示内容
                title_player_musicName_tv.setText(str_musicName);
                title_player_singer_tv.setText(str_singer);
                player_maxProgress_tv.setText(str_maxProgress);
            }


            if(action.equals(BroadcastAction.PlayInfoProgressAction)) {
                currentProgress = intent.getIntExtra("currentprogress", 0);
                isFinish = intent.getBooleanExtra("isFinish", false);
                Log.d(TAG, "    PlayInfoProgressAction :" +
                        "    currentprogress = " + currentProgress +
                        "    isFinish = " + isFinish);

                //  设置进度条的当前值
                if(isPlay){
                    player_progress_seekbar.setProgress(currentProgress);
                    /*
                     *  这个 isPlay 的布尔变量的使用原因
                     *  因为服务正在播放音乐时是不断发送本地广播，为了告诉活动当前播放进度，
                     *  而活动肯定要接收服务发过来的广播。但是，是否更新进度条可以有自身选择嘛
                     *  比如，现在正在播歌，而我们想拖动进度条改变进度，如果不设置一个值来决定
                     *  是否更新，我们在拖动的过程中，进度条会随着接收到的广播再次更新！
                     *  那样进度条就会有跳来跳去
                     */
                }

                //  播放完一首歌
                if(isFinish){
                    player_mplay_iv.setSelected(false);     //  重置按钮
                    isPlay = false;

                    if (isLoop){        //  是否单曲循环
                        player_mplay_iv.setSelected(true);
                        isPlay = true;

                        //  更新 popupwindow 的适配器，即播放列表显示内容
                        if(mPlaylistArrayAdapter != null)mPlaylistArrayAdapter.notifyDataSetChanged();
                    }
                }else {
                    player_mplay_iv.setSelected(true);
                    isPlay = true;

                    //  更新 popupwindow 的适配器，即播放列表显示内容
                    if(mPlaylistArrayAdapter != null)mPlaylistArrayAdapter.notifyDataSetChanged();
                }

            }

        }
    }
    //  本地广播接收器的注册
    private void playinfo_local_receiver_register(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastAction.PlayInfoDataAction);//   "com.my.broadcast.PLAYINFODATA_LOCAL_ACTION";
        intentFilter.addAction(BroadcastAction.PlayInfoProgressAction);//   "com.my.broadcast.PLAYINFOPROGRESS_LOCAL_ACTION"

        playInfoLocalReceiver = new PlayInfoLocalReceiver();
        mLocalBroadcastManager.registerReceiver(playInfoLocalReceiver, intentFilter);
        Log.d(TAG," -- mLocalBroadcastManager.registerReceiver(playInfoLocalReceiver, intentFilter)\n");
    }


    /*
     * 音量拖动条（player_volume_seekbar）的处理
     */
    private void volume_seekbar(){
        Log.d(TAG, " -- volume_seekbar()");
        // 获取系统最大音量 getStreamMaxVolume（int streamType）
        maxVolume = mAudiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 获取系统当前音量 getStreamVolume(int streamType)
        currentVolume = mAudiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);

        // 设置音量拖动条的最大值
        player_volume_seekbar.setMax(maxVolume);
        Log.d(TAG, "  player_volume_seekbar.setMax(maxVolume)\n" +
                "    maxVolume = " + maxVolume);
        // 设置音量拖动条的当前值
        player_volume_seekbar.setProgress(currentVolume);
        Log.d(TAG, "  player_volume_seekbar.setProgress(currentVolume)\n" +
                "    currentVolume = " + currentVolume);

        // 设置拖动条事件监听器，volume_seekBarChangeListener 为对应监听方法的对象（内部类）
        player_volume_seekbar.setOnSeekBarChangeListener(volume_onSeekBarChangeListener);
    }

    /*
     *  音量拖动条事件监听器
     */
    private SeekBar.OnSeekBarChangeListener volume_onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // 更新当前音量
            currentVolume = progress;
            // 设置音量大小 setStreamVolume(int streamType, int index, intflags)
            mAudiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_SHOW_UI);
            Log.d(TAG," -- OnSeekBarChangeListener : onProgressChanged()\n" +
                    "    progress = " + progress +
                    "    currentVolume = " + currentVolume);
            Log.d(TAG,"  mAudiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_SHOW_UI)");
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.d(TAG," -- OnSeekBarChangeListener : onStartTrackingTouch()");
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.d(TAG," -- OnSeekBarChangeListener : onStopTrackingTouch()");
            // 页面浮动提示信息：Toast.makeText()
            Toast.makeText(PlayerActivity.this, "Current Volume : " + currentVolume*100/maxVolume + "%", Toast.LENGTH_SHORT).show();
        }
    };


    /*
     *  播放器的处理
     */
    private void player(){
        Log.d(TAG, " -- player");

        //  实例化播放列表
        if (mPlayList == null){
            mPlayList = new ArrayList<>();
        }else {
            mPlayList.clear();
        }

        mPlayList = MusicUtils.getPlayList();       //  获取列表
        Log.d(TAG, "  MusicUtils.getPlayList()  获取列表");

        if (mPlayList.size() <= 0){     //  如果播放列表为空，则扫描本地音乐
            mPlayList = MusicUtils.scanLocalMusic(this);
            Log.d(TAG, "  MusicUtils.scanLocalMusic(this)  获取本地列表");
        }


        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);

        //  启动服务 PlayerService
        startService(intent);
        Log.d(TAG, "  startService(intent)");

        // 绑定服务 PlayerService
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        Log.d(TAG, "  bindService(intent, serviceConnection, BIND_AUTO_CREATE)");

//        // 解绑服务
//        unbindService(serviceConnection);
//
//        // 关闭 PlayerService
//        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
//        stopService(intent);

        //  设置按钮的点击事件
        player_mplay_iv.setOnClickListener(mOnClickListener);
        player_mnext_iv.setOnClickListener(mOnClickListener);
        player_mlast_iv.setOnClickListener(mOnClickListener);
        player_mmode_iv.setOnClickListener(mOnClickListener);
        player_mlist_iv.setOnClickListener(mOnClickListener);
        title_player_hide_iv.setOnClickListener(mOnClickListener);

        //  设置拖动条事件监听器， progress_seekBarChangeListener 为对应监听方法的对象（内部类）
        player_progress_seekbar.setOnSeekBarChangeListener(progress_onSeekBarChangeListener);
    }

    /*
     * 点击事件监听器
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.player_mplay_iv:
                    if (isPlay){        //  设置暂停状态
                        player_mplay_iv.setSelected(false);
                        isPlay = false;

                        playerControlBinder.mPause();

                    } else {        //  设置播放状态
                        player_mplay_iv.setSelected(true);
                        isPlay = true;

                        playerControlBinder.mPlay();
                    }
                    break;


                case R.id.player_mnext_iv:
                    player_mplay_iv.setSelected(false);
                    isPlay = false;

                    mPlayPosition = MusicUtils.getPlayPosition();
                    if(mPlayPosition < mPlayList.size()-1 ){
                        mPlayPosition++;
                    }else {
                        mPlayPosition = 0;
                    }
                    playerControlBinder.mSkip(mPlayPosition);

                    player_mplay_iv.setSelected(true);
                    isPlay = true;
                    break;


                case R.id.player_mlast_iv:
                    player_mplay_iv.setSelected(false);
                    isPlay = false;

                    mPlayPosition = MusicUtils.getPlayPosition();
                    if(mPlayPosition > 0){
                        mPlayPosition-- ;
                    }else {
                        mPlayPosition = mPlayList.size() - 1;
                    }
                    playerControlBinder.mSkip(mPlayPosition);

                    player_mplay_iv.setSelected(true);
                    isPlay = true;
                    break;


                case R.id.player_mmode_iv:
                    if(isLoop){        //  取消单曲循环
                        player_mmode_iv.setSelected(false);
                        isLoop = false;
                        playerControlBinder.mToLoop(false);
                    } else {        //  设置单曲循环
                        player_mmode_iv.setSelected(true);
                        isLoop = true;
                        playerControlBinder.mToLoop(true);
                    }
                    break;


                case R.id.player_mlist_iv:
                    //  点击播放列表，显示 PopUpWindow
                    showPopupWindow();      //  初始化 PopupWindow
                    break;


                case R.id.title_player_hide_iv:
                    //  测试服务的关闭
//                    Intent stopIntent = new Intent(PlayerActivity.this, PlayerService.class);
//                    stopService(stopIntent);
//                    unbindService(serviceConnection);

                    //  返回主菜单
                    moveTaskToBack(true);       //  使其后台运行
                    Toast.makeText(PlayerActivity.this, "moveTaskToBack", Toast.LENGTH_LONG).show();

                    //  MainActivity 的启动模式被修改为 singleTop ，这里跳转有点不一样
                    Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
                    startActivity(intent);       //  跳转至 MainActivity
                    overridePendingTransition(R.anim.alpha_in, R.anim.translate_right_out);       //  跳转动画
            }
        }
    };


    /*
     *  初始化 PopupWindow
     */
    private void showPopupWindow(){

        //  获取屏幕的宽高像素
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        //  用 LayoutInflater 获取 R.layout.popuplayout 对应的 View
        View contentView = LayoutInflater.from(PlayerActivity.this).inflate(R.layout.popupwindow_playlist, null);
        //  PopupWindow 构造函数
        mPopupWindow = new PopupWindow(contentView, screenWidth, (screenHeight / 3 * 2), true);
        mPopupWindow.setContentView(contentView);


        //  初始化 ListView
        init_ListView(contentView);


        //  外部可点击，即点击 PopupWindow 以外的区域，PopupWindow 消失
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);

        //  清除歌单按钮
        LinearLayout playlist_clear_ll = (LinearLayout) contentView.findViewById(R.id.playlist_clear_ll);
        playlist_clear_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlayList != null && mPlaylistArrayAdapter != null){
                    mPlayList.clear();
                    mPlaylistArrayAdapter.notifyDataSetChanged();
                    MusicUtils.updatePlayList(mPlayList);
                }
            }
        });

        //  播放模式按钮
        LinearLayout playlist_mode_ll = (LinearLayout) contentView.findViewById(R.id.playlist_mode_ll);
        final ImageView playlist_mode_iv = (ImageView) contentView.findViewById(R.id.playlist_mode_iv);
        final TextView playlist_mode_tv = (TextView) contentView.findViewById(R.id.playlist_mode_tv);
        //  判断当前播放模式
        if(isLoop){
            player_mmode_iv.setSelected(true);
            playlist_mode_iv.setSelected(true);
            playlist_mode_tv.setText("单曲循环");
        } else {
            player_mmode_iv.setSelected(false);
            playlist_mode_iv.setSelected(false);
            playlist_mode_tv.setText("列表循环");
        }
        //  设置点击事件监听器
        playlist_mode_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoop){        //  取消单曲循环
                    player_mmode_iv.setSelected(false);
                    playlist_mode_iv.setSelected(false);
                    playlist_mode_tv.setText("列表循环");
                    isLoop = false;
                    playerControlBinder.mToLoop(false);
                } else {        //  设置单曲循环
                    player_mmode_iv.setSelected(true);
                    playlist_mode_iv.setSelected(true);
                    playlist_mode_tv.setText("单曲循环");
                    isLoop = true;
                    playerControlBinder.mToLoop(true);
                }
            }
        });

        //  设置启动关闭动画
        mPopupWindow.setAnimationStyle(R.style.PopupWindowAnim);

        //  将 PopupWindow 的实例放在一个父容器中，并定位
        View rootView = LayoutInflater.from(PlayerActivity.this).inflate(R.layout.activity_player, null);
        mPopupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

        //  关闭 PopupWindow
//        mPopupWindow.dismiss();

    }

    private void init_ListView(View v){

        ListView playlist_listView = (ListView) v.findViewById(R.id.playlist_listView);

        //  初始化 PlayListArrayAdapter 适配器
        if(mPlaylistArrayAdapter == null){
            //  实例化适配器，传入自定义 item 布局 和 mPlayList
            mPlaylistArrayAdapter = new PlayListArrayAdapter(PlayerActivity.this,
                    R.layout.popupwindow_item_playinfo, mPlayList);

            //  使用默认布局的适配器
//            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PlayerActivity.this,
//                    android.R.layout.simple_list_item_1, data);
        }

        //  将 playlist_listView 绑定该适配器
        playlist_listView.setAdapter(mPlaylistArrayAdapter);

        //  ListView 的 item 监听事件
        playlist_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                player_mplay_iv.setSelected(false);
                isPlay = false;

                PlayInfo playInfo = mPlayList.get(position);
                Toast.makeText(PlayerActivity.this, playInfo.getName(), Toast.LENGTH_SHORT).show();

                //  切歌
                playerControlBinder.mSkip(position);

                //  刷新适配器
                mPlaylistArrayAdapter.notifyDataSetChanged();

                player_mplay_iv.setSelected(true);
                isPlay = true;
            }
        });
    }


    /*
     * 进度拖动条事件监听器 SeekBar.OnSeekBarChangeListener progress_onSeekBarChangeListener
     *
     */
    private SeekBar.OnSeekBarChangeListener progress_onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.d(TAG," -- OnSeekBarChangeListener : onProgressChanged  " + str_currentProgress + " : " + str_maxProgress);

            //  当拖动条值发生改变触发，更新当前的进度 currentProgress
            str_currentProgress = String.format("%1$02d:%2$02d",(progress/1000)/60,
                    (progress/1000)%60);
            player_currentProgress_tv.setText(str_currentProgress);

        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.d(TAG," -- OnSeekBarChangeListener : onStartTrackingTouch");

            //  当拖动条滑动开始时触发
            player_mplay_iv.setSelected(false);
            isPlay = false;

        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.d(TAG," -- OnSeekBarChangeListener : onStopTrackingTouch");

            // 当拖动条滑动结束后触发
            player_mplay_iv.setSelected(true);
            isPlay = true;

            //  通过绑定服务的 playerControlBinder 对象的接口函数，更改播放进度
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

                Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.alpha_in, R.anim.translate_right_out);
                return true;

//            case KeyEvent.KEYCODE_VOLUME_UP :
//                mAudiomanager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
//                updatePlayerVolumeSeekbar();
//                return true;
//
//            case KeyEvent.KEYCODE_VOLUME_DOWN :
//                mAudiomanager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
//                updatePlayerVolumeSeekbar();
//                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
//    public void updatePlayerVolumeSeekbar(){
//        // 重新获取系统当前音量
//        currentVolume = mAudiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
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
        Log.d(TAG, " ----- PlayerActivity : onDestroy");

        // 在 ActivityCollector.activityList 移除 Activity
        ActivityCollector.removeActivity(this);

        // 注销广播接收器
        unregisterReceiver(volumeChangeReceiver);
        mLocalBroadcastManager.unregisterReceiver(playInfoLocalReceiver);

        // 关闭服务
        Intent stopIntent = new Intent(PlayerActivity.this, PlayerService.class);
        stopService(stopIntent);
        unbindService(serviceConnection);
    }

}
