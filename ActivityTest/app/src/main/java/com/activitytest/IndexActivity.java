package com.activitytest;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activitytest.Fragment.IndexFragment_Ranking;
import com.activitytest.Fragment.IndexFragment_Recommend;
import com.activitytest.Fragment.IndexFragment_SongList;
import com.activitytest.Fragment.NavigationFragment;
import com.activitytest.adapter.IndexFragmentPagerAdapter;
import com.activitytest.service.PlayerService;

import java.util.ArrayList;
import java.util.List;

public class IndexActivity extends FragmentActivity implements View.OnClickListener{

    private ViewPager mViewPager;

    FragmentManager mFragmentManager;

    private long mExitTime;     //  用于计算双击“返回键”的间隔时长

    private static final String TAG = "IndexActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        Log.i(TAG, " ----- IndexActivity : onCreate()");

        //  在 LogCat 打印对应 Activity 活动的类名
        Log.i(TAG, "       Activity : " + getClass().getSimpleName());
        //  在 ActivityCollector.activityList 添加 Activity ,方便管理
        ActivityCollector.addActivity(this);

        //  判断 Android 版本是否大于23 （Android 6.0）
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //  请求系统读写权限
            requestPermission();
        }else {
            //  启动服务 PlayerService
            Intent intent = new Intent(this, PlayerService.class);
            startService(intent);
            Log.d(TAG, "  startService(intent)");
        }


        initView();

        initViewPager();

        initNavigation(0);
    }

    /*
     *  读写权限申请
     */
    private void requestPermission(){
        Log.i(TAG, " -- requestPermission()");

        int checkReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int checkWritePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (checkReadPermission != PackageManager.PERMISSION_GRANTED){      //  是否已经授予权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else {
            Toast.makeText(this, "READ_EXTERNAL_STORAGE",Toast.LENGTH_SHORT).show();

            //  启动服务 PlayerService
            Intent intent = new Intent(this, PlayerService.class);
            startService(intent);
            Log.d(TAG, "  startService(intent)");
        }

        if (checkWritePermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }else {
            Toast.makeText(this, "WRITE_EXTERNAL_STORAGE",Toast.LENGTH_SHORT).show();
        }
    }
    /*
     *  注册权限申请回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "READ_EXTERNAL_STORAGE ALLOW",Toast.LENGTH_SHORT).show();

                    //  启动服务 PlayerService
                    Intent intent = new Intent(this, PlayerService.class);
                    startService(intent);
                    Log.d(TAG, "  startService(intent)");
                }else {
                    Toast.makeText(this, "READ_EXTERNAL_STORAGE DENY",Toast.LENGTH_SHORT).show();
                }
                break;

            case 2:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "WRITE_EXTERNAL_STORAGE ALLOW",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "WRITE_EXTERNAL_STORAGE DENY",Toast.LENGTH_SHORT).show();
                }
                break;

            default:
        }
    }


    private void initView(){
        ImageView index_playing_iv = (ImageView) findViewById(R.id.index_playing_iv);
        index_playing_iv.setOnClickListener(this);

        TextView index_songlist_tv = (TextView) findViewById(R.id.index_songlist_tv);
        index_songlist_tv.setOnClickListener(this);
        TextView index_recommend_tv = (TextView) findViewById(R.id.index_recommend_tv);
        index_recommend_tv.setOnClickListener(this);
        TextView index_ranking_tv = (TextView) findViewById(R.id.index_ranking_tv);
        index_ranking_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.index_playing_iv :
                Intent intent = new Intent(this, PlayerActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.translate_right_in, R.anim.alpha_out);
                break;

            case R.id.index_songlist_tv:
                mViewPager.setCurrentItem(0);
                break;

            case R.id.index_recommend_tv :
                mViewPager.setCurrentItem(1);
                break;

            case R.id.index_ranking_tv :
                mViewPager.setCurrentItem(2);
                break;
        }
    }


    private void initViewPager(){
        mViewPager = (ViewPager) findViewById(R.id.index_viewpager);

        List<Fragment> list = new ArrayList<>();
        IndexFragment_SongList indexFragment_songlist = new IndexFragment_SongList();
        IndexFragment_Recommend indexFragment_recommend = new IndexFragment_Recommend();
        IndexFragment_Ranking indexFragment_ranking = new IndexFragment_Ranking();
        list.add(indexFragment_songlist);
        list.add(indexFragment_recommend);
        list.add(indexFragment_ranking);

        IndexFragmentPagerAdapter indexFragmentPagerAdapter = new IndexFragmentPagerAdapter(this.getSupportFragmentManager(), list);

        mViewPager.setAdapter(indexFragmentPagerAdapter);
        mViewPager.setCurrentItem(1);
        mViewPager.setOffscreenPageLimit(3);
    }

    private void initNavigation(int to){
        if (mFragmentManager == null){
            mFragmentManager = this.getSupportFragmentManager();
        }

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        NavigationFragment navigationFragment = new NavigationFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", to);
        navigationFragment.setArguments(bundle);

        fragmentTransaction.add(R.id.index_navigation_framelayout, navigationFragment, "index_navigation");

        fragmentTransaction.commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, " ----- MainActivity : onDestroy");

        // 在 ActivityCollector.activityList 移除 Activity
        ActivityCollector.removeActivity(this);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            mExit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void mExit(){
        if((System.currentTimeMillis() - mExitTime) > 2000){
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            //  调用 ActivityCollector.finishAll() 销毁所有 Activity
            ActivityCollector.finishAll();
//            System.exit(0); //  所有的 Activity 的 onDestroy() 都没执行
        }
    }

}
