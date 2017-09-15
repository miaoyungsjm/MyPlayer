package com.activitytest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.activitytest.Fragment.NavigationFragment;

/**
 * Created by MY on 2017/8/30.
 *
 */

public class FriendActivity extends FragmentActivity{

    FragmentManager mFragmentManager;

    private long mExitTime;

    private static final String TAG = "FriendActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        Log.i(TAG, " ----- FriendActivity : onCreate()");

        //  在 LogCat 打印对应 Activity 活动的类名
        Log.i(TAG, "       Activity : " + getClass().getSimpleName());
        //  在 ActivityCollector.activityList 添加 Activity ,方便管理
        ActivityCollector.addActivity(this);


        initNavigation(2);
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

        fragmentTransaction.add(R.id.friend_navigation_framelayout, navigationFragment, "friend_navigation");

        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, " ----- FriendActivity : onDestroy");

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
