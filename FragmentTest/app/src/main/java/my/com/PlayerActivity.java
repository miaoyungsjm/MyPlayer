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

import my.com.Data.PlayInfo;
import my.com.action.BroadcastAction;
import my.com.adapter.PlayListArrayAdapter;
import my.com.service.PlayerService;

/**
 * Created by MY on 2017/7/21.
 */

public class PlayerActivity extends Activity {

    //  AudioManager
    private AudioManager mAudiomanager;          //  控制系统音量和钤声模式对象
    private int currentVolume, maxVolume;       //  获取系统当前音量，最大音量
    private SeekBar player_volume_seekbar;      //  音量拖动条

    //
    private boolean isPlay = false;     //  判断是否在播放，更新进度条的判断
    private String str_musicName;       //  歌名
    private TextView title_player_musicName_tv;       //  歌名显示控件
    private int currentProgress, maxProgress;       //  歌曲当前进度，最大进度
    private String str_currentProgress, str_maxProgress;    //  int 转 str
    private TextView player_currentProgress_tv, player_maxProgress_tv;  //歌曲当前时间，结束时间
    private SeekBar player_progress_seekbar;    // 进度拖动条

    //
    private ImageView player_mplay_iv;          //播放按钮
    private ImageView player_mnext_iv;          //下一首按钮
    private ImageView player_mlast_iv;          //上一首按钮
    private ImageView player_mlist_iv;
    private ImageView title_player_hide_iv;        // 左上角返回键

    //  PopupWindow
    private PopupWindow mPopupWindow;       //  播放列表
    private List<PlayInfo> mPlayInfoList;
    private PlayListArrayAdapter mPlaylistArrayAdapter;
    private int mItemClickPosition;
    private int mItemLastPosition;

    private VolumeChangeReceiver volumeChangeReceiver;      // 系统音量广播接收器（内部类对象）

    private LocalBroadcastManager mLocalBroadcastManager;    // 本地/局部广播管理器
    private PlayInfoLocalReceiver playInfoLocalReceiver;    // PlayService 本地广播接收器（内部类对象）


    private static final String TAG = "PlayerActivity";         // 调试信息 TAG 标签


    /*
     *  PlayerService 服务的绑定
     *  这里是通过绑定服务，调用服务接口函数，
     *  来实现 Activity 和 Service 的“通信”（就是指挥服务执行什么）
     *
     *  后面执行服务的绑定才回来看，先跳过
     *
     *  serviceConnection -- PlayerService 服务的绑定与解除
     *  playerControlBinder -- PlayerService 服务的控制（接口函数）对象
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
     *  onCreate
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
        progress_seekbar();
    }

    private void initView(){
        title_player_musicName_tv = (TextView) findViewById(R.id.title_player_musicName_tv);
        title_player_hide_iv = (ImageView) findViewById(R.id.title_player_hide_iv);

        player_volume_seekbar = (SeekBar) findViewById(R.id.player_volume_seekbar);

        player_currentProgress_tv = (TextView) findViewById(R.id.player_currentProgress_tv);
        player_maxProgress_tv = (TextView) findViewById(R.id.player_maxProgress_tv);
        player_progress_seekbar = (SeekBar) findViewById(R.id.player_progress_seekbar);

        player_mplay_iv = (ImageView) findViewById(R.id.player_mplay_iv);
        player_mnext_iv = (ImageView) findViewById(R.id.player_mnext_iv);
        player_mlast_iv = (ImageView) findViewById(R.id.player_mlast_iv);
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
            //  设置音量拖动条的当前值
            player_volume_seekbar.setProgress(currentVolume);
            //  页面浮动提示信息：Toast.makeText()
//            Toast.makeText(PlayerActivity.this, "Current Volume : " + currentVolume*100/maxVolume + "%", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"  player_volume_seekbar.setProgress(currentVolume)\n" +
                    "    currentVolume = " + currentVolume);
        }
    }
    //  系统音量广播接收器的注册
    private void volume_receiver_register(){
        IntentFilter volume_IntentFilter = new IntentFilter();
        volume_IntentFilter.addAction(BroadcastAction.VolumeAction);// "android.media.VOLUME_CHANGED_ACTION"
        volumeChangeReceiver = new VolumeChangeReceiver();
        registerReceiver(volumeChangeReceiver, volume_IntentFilter);
        Log.d(TAG," -- registerReceiver(volumeChangeReceiver, volumeIntentFilter)\n");
    }


    /*
     *  本地广播接收器内部类 PlayInfoLocalReceiver
     *  当收到 PlayService 发来的本地广播时，执行 onReceive 方法
     */
    private class PlayInfoLocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //  对收到的本地广播进行分类，0 ：歌名、最大进度；1 ：当前进度
            int to = intent.getIntExtra("type", -1);
            switch (to){
                case 0:
                    str_musicName = intent.getStringExtra("musicname");
                    maxProgress = intent.getIntExtra("maxprogress", -1);
                    Log.d(TAG, " -- PlayInfoReceiver : onReceive()  type = 0" +
                            "  musicname = " + str_musicName +
                            "  maxprogress = " + maxProgress);
                    //  设置进度条的最大值
                    player_progress_seekbar.setMax(maxProgress);
                    Log.d(TAG,"  player_progress_seekbar.setMax(maxProgress)\n" +
                            "    maxProgress = " + maxProgress);

                    //  <Textview> 控件 player_maxProgress_tv 显示进度条最大值
                    str_maxProgress = String.format("%1$02d:%2$02d",(maxProgress/1000)/60,
                            (maxProgress/1000)%60);
                    player_maxProgress_tv.setText(str_maxProgress);

                    //  <Textview> 控件 title_player_musicName_tv 显示歌名
                    title_player_musicName_tv.setText(str_musicName);
                    break;

                case 1:
                    currentProgress = intent.getIntExtra("currentprogress", -1);
                    Log.d(TAG, " -- PlayInfoReceiver : onReceive()  type = 1" +
                            "  currentprogress = " + currentProgress);
                    //  设置进度条的当前值
                    if(isPlay)player_progress_seekbar.setProgress(currentProgress);
                    /*
                     *  这个 isPlay 的布尔变量的使用原因
                     *  因为服务正在播放音乐时是不断发送本地广播，为了告诉活动当前播放进度，
                     *  而活动肯定要接收服务发过来的广播。但是，是否更新进度条可以有自身选择嘛
                     *  比如，现在正在播歌，而我们想拖动进度条改变进度，如果不设置一个值来决定
                     *  是否更新，我们在拖动的过程中，进度条会随着接收到的广播再次更新！
                     *  那样进度条就会有跳来跳去 BUG
                     */

                    break;

                default:
                    Log.d(TAG,"  type = -1");
            }
        }
    }
    //  本地广播接收器的注册
    private void playinfo_local_receiver_register(){
        IntentFilter playinfo_intentFilter = new IntentFilter();
        playinfo_intentFilter.addAction(BroadcastAction.PlayInfoAction);// "com.my.broadcast.PLAYINFO_LOCAL_ACTION"
        playInfoLocalReceiver = new PlayInfoLocalReceiver();
        mLocalBroadcastManager.registerReceiver(playInfoLocalReceiver, playinfo_intentFilter);
        Log.d(TAG," -- mLocalBroadcastManager.registerReceiver(playInfoLocalReceiver, intentFilter)\n");
    }



    /*
     * 音量拖动条（player_volume_seekbar）的处理
     */
    private void volume_seekbar(){
        Log.d(TAG, " -- volume_seekbar");
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
     * 音量拖动条事件监听器  SeekBar.OnSeekBarChangeListener volume_onSeekBarChangeListener
     */
    private SeekBar.OnSeekBarChangeListener volume_onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // 更新当前音量
            currentVolume = progress;
            // 设置音量大小 setStreamVolume(int streamType, int index, intflags)
            mAudiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_SHOW_UI);
            Log.d(TAG," -- OnSeekBarChangeListener : onProgressChanged" +
                    "  progress = " + progress +
                    "  currentVolume = " + currentVolume);
            Log.d(TAG,"  mAudiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_SHOW_UI)");
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.d(TAG," -- OnSeekBarChangeListener : onStartTrackingTouch");
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.d(TAG," -- OnSeekBarChangeListener : onStopTrackingTouch");
            // 页面浮动提示信息：Toast.makeText()
            Toast.makeText(PlayerActivity.this, "Current Volume : " + currentVolume*100/maxVolume + "%", Toast.LENGTH_SHORT).show();
        }
    };


    /*
     *  进度拖动条（player_progress_seekbar）的处理
     */
    private void progress_seekbar(){
        Log.d(TAG, " -- progress_seekbar");


        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
        intent.putExtra("musicname", "music.mp3");      //  传入播放的歌名


        //  启动服务 PlayerService
        startService(intent);
        Log.d(TAG, "    startService(startIntent)");

        // 绑定服务 PlayerService
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        Log.d(TAG, "    bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE)");


//        // 解绑服务
//        unbindService(serviceConnection);
//
//        // 关闭 PlayerService
//        Intent stopIntent = new Intent(PlayerActivity.this, PlayerService.class);
//        stopService(stopIntent);

        // 设置按钮的点击事件
        player_mplay_iv.setOnClickListener(mOnClickListener);
        player_mnext_iv.setOnClickListener(mOnClickListener);
        player_mlast_iv.setOnClickListener(mOnClickListener);
        player_mlist_iv.setOnClickListener(mOnClickListener);
        title_player_hide_iv.setOnClickListener(mOnClickListener);

        // 设置拖动条事件监听器， progress_seekBarChangeListener 为对应监听方法的对象（内部类）
        player_progress_seekbar.setOnSeekBarChangeListener(progress_onSeekBarChangeListener);
    }

    /*
     * 点击事件监听器
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
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
                    mNext("music2.mp3");
                    break;

                case R.id.player_mlast_iv:
                    mLast("music3.mp3");
                    break;

                case R.id.player_mlist_iv:

                    //  显示播放列表 PopUpWindow
                    showPopupWindow();

                    break;

                case R.id.title_player_hide_iv:
                    // 测试关闭服务
//                    Intent stopIntent = new Intent(PlayerActivity.this, PlayerService.class);
//                    stopService(stopIntent);
//                    unbindService(serviceConnection);

                    // 返回主菜单
                    moveTaskToBack(true);
                    Toast.makeText(PlayerActivity.this, "moveTaskToBack", Toast.LENGTH_LONG).show();

                    Intent goBack_Intent = new Intent(PlayerActivity.this, MainActivity.class);
                    startActivity(goBack_Intent);
                    overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
            }
        }
    };

    public void mNext(String playName){
        player_progress_seekbar.setProgress(0);
        player_currentProgress_tv.setText("00:00");

        player_mplay_iv.setSelected(false);
        isPlay = false;

        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
        intent.putExtra("musicname", playName);
        startService(intent);

        player_mplay_iv.setSelected(true);
        isPlay = true;
    }
    public void mLast(String playName){
        player_progress_seekbar.setProgress(0);
        player_currentProgress_tv.setText("00:00");

        player_mplay_iv.setSelected(false);
        isPlay = false;

        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
        intent.putExtra("musicname", playName);
        startService(intent);

        player_mplay_iv.setSelected(true);
        isPlay = true;
    }

    //  实例化并显示 PopupWindow
    private void showPopupWindow(){

        //  获取屏幕的宽高像素
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        //  用 LayoutInflater 获取 R.layout.popuplayout 对应的View
        View contentView = LayoutInflater.from(PlayerActivity.this).inflate(R.layout.popupwindow_playlist, null);
        //  PopupWindow 构造函数
        mPopupWindow = new PopupWindow(contentView, screenWidth, (screenHeight / 3 * 2), true);
        mPopupWindow.setContentView(contentView);


        //  初始化 playlist_ListView 播放列表
        init_playlist_listView(contentView);


        //  外部可点击，即点击 PopupWindow 以外的区域，PopupWindow 消失
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);

        LinearLayout playlist_clear_ll = (LinearLayout) contentView.findViewById(R.id.playlist_clear_ll);
        playlist_clear_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlayInfoList != null && mPlaylistArrayAdapter != null){
                    mPlayInfoList.removeAll(mPlayInfoList);
                    mPlaylistArrayAdapter.notifyDataSetChanged();
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
    /*
     *  初始化播放列表
     */
    private void init_playlist_listView(View v){

        ListView playlist_listView = (ListView) v.findViewById(R.id.playlist_listView);

        if(mPlayInfoList == null){
            mPlayInfoList = new ArrayList<>();

            //  向 mPlayInfoList 添加数据
            for(int i = 0 ; i < 2 ; i++){
                PlayInfo Apple = new PlayInfo("Apple");
                mPlayInfoList.add(Apple);
                PlayInfo Banana = new PlayInfo("Banana");
                mPlayInfoList.add(Banana);
                PlayInfo Orange = new PlayInfo("Orange");
                mPlayInfoList.add(Orange);
                PlayInfo Watermelon = new PlayInfo("Watermelon");
                mPlayInfoList.add(Watermelon);
                PlayInfo Pear = new PlayInfo("Pear");
                mPlayInfoList.add(Pear);
                PlayInfo Grape = new PlayInfo("Grape");
                mPlayInfoList.add(Grape);
                PlayInfo Pineapple = new PlayInfo("Pineapple");
                mPlayInfoList.add(Pineapple);
                PlayInfo Strawberry = new PlayInfo("Strawberry");
                mPlayInfoList.add(Strawberry);
                PlayInfo Cherry = new PlayInfo("Cherry");
                mPlayInfoList.add(Cherry);
                PlayInfo Mango = new PlayInfo("Mango");
                mPlayInfoList.add(Mango);
            }

        }

        if(mPlaylistArrayAdapter == null){

            mPlaylistArrayAdapter = new PlayListArrayAdapter(PlayerActivity.this,
                    R.layout.popupwindow_item_playinfo, mPlayInfoList);


//            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PlayerActivity.this,
//                    android.R.layout.simple_list_item_1, data);
        }

        playlist_listView.setAdapter(mPlaylistArrayAdapter);

        //  当点击了 ListView 的 item ，可以通过 position 参数判断点了什么
        playlist_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mItemClickPosition = position;
                mItemLastPosition = PlayInfo.mPlayPosition;        //  获取 PlayInfo 静态变量 mPlayPosition

                PlayInfo next_playInfo = mPlayInfoList.get(mItemClickPosition);
                Toast.makeText(PlayerActivity.this, next_playInfo.getmName(), Toast.LENGTH_SHORT).show();

                /*
                 *  ！！！修复修复 ListView All BUG ！！！
                 *
                    TextView tv = (TextView) view.findViewById(R.id.playinfo_name_tv);
                    tv.setText("Change item");
                    tv.setTextColor(getResources().getColor(R.color.colorAccent));

                 *  因为 mPlaylistArrayAdapter.notifyDataSetChanged() 或者滚动都会重新调用 getView()
                 *  TextView.setText 就算改变文本，重新加载也没了
                 *  解决：直接改 playInfo 对象数据  -  playInfo.mName = "";
                 *
                 *  TextView.setColor 能改变颜色，但不能保留，而且颜色在滚动会错位
                 *  解决：直接改 playInfo 对象数据  -  playInfo.mState = "" ，适配器再根据 playInfo.mState 的值做颜色修改
                 *
                 */

                //  修改内容
//                next_playInfo.mName = "Change item";


                final PlayInfo last_playInfo = mPlayInfoList.get(mItemLastPosition);
                last_playInfo.mState = false;    //  重置上一个对象

                next_playInfo.mState = true;     //  选择点击的对象，按键状态设为被选择，通过适配器的 tv.setSelected(true) 改变背景颜色
                PlayInfo.mPlayPosition = mItemClickPosition;     //  更新位置

                //  刷新适配器
                mPlaylistArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    /*
     *  继续提高 ListView 的更新效率
     *
     *  更新 ListView 的一个 item
     *  调用一次 getView() 方法
     *
     */
//    private void updateItem(ListView listView, int position, PlayListArrayAdapter adapter) {
//        /**第一个可见的位置**/
//        int firstVisiblePosition = listView.getFirstVisiblePosition();
//        /**最后一个可见的位置**/
//        int lastVisiblePosition = listView.getLastVisiblePosition();
//
//        /**在看见范围内才更新，不可见的滑动后自动会调用getView方法更新**/
//        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
//            /**获取指定位置view对象**/
//            View view = listView.getChildAt(position - firstVisiblePosition);
//            adapter.getView(position, view, listView);
//        }
//    }



    /*
     * 进度拖动条事件监听器 SeekBar.OnSeekBarChangeListener progress_onSeekBarChangeListener
     */
    private SeekBar.OnSeekBarChangeListener progress_onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.d(TAG," -- OnSeekBarChangeListener : onProgressChanged  " + str_currentProgress + " : " + str_maxProgress);

            /*
             *  拖动进度条处理  <Textview> 控件显示拖动值
             */
            str_currentProgress = String.format("%1$02d:%2$02d",(progress/1000)/60,
                    (progress/1000)%60);
            player_currentProgress_tv.setText(str_currentProgress);

        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.d(TAG," -- OnSeekBarChangeListener : onStartTrackingTouch");

            player_mplay_iv.setSelected(false);
            isPlay = false;

        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.d(TAG," -- OnSeekBarChangeListener : onStopTrackingTouch");
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

                Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
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
