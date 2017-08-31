package com.activitytest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.activitytest.Fragment.MyFragment_Download;
import com.activitytest.Fragment.MyFragment_Favourite;
import com.activitytest.Fragment.MyFragment_Local;
import com.activitytest.Fragment.MyFragment_Main;
import com.activitytest.Fragment.NavigationFragment;
import com.activitytest.action.BroadcastAction;


/**
 * Created by MY on 2017/8/29.
 *
 */

public class MyActivity extends FragmentActivity{

    FragmentManager mFragmentManager;
    private Fragment myFragment_main, myFragment_local, myFragment_download, myFragment_favourite;
    private String[] tabs = new String[]{"main", "local", "download", "favourite"};

    //  本地广播接收器
    private LocalBroadcastManager mLocalBroadcastManager;    // 本地/局部广播管理器
    private MYReceiver mMYReceiver;       //  接收器对象，内部类编写处理方法

    int fPosition;

    private long mExitTime;


    private static final String TAG = "MyActivity";

    /*
     *  本地广播接收器
     *
     *  当收到 BroadcastAction.MyFragmentAction 广播，解析广播内容：jumpto
     *  实现 ChildFragment 的页面跳转 showContent(to)
     */
    private class MYReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int to = intent.getIntExtra("jumpto", 0);
            Log.d(TAG, " -- MYReceiver : onReceive()  " +
                    "  jumpto = " + to);

            fPosition = to;
            showContent(to);
        }
    }
    //  注册 - 本地广播接收器
    private void local_receiver_register(){
        IntentFilter intentFilter = new IntentFilter();         //  过滤器
        intentFilter.addAction(BroadcastAction.MyFragmentAction);

        mMYReceiver = new MYReceiver();            //  实例化广播接收器对象
        mLocalBroadcastManager.registerReceiver(mMYReceiver, intentFilter);        //  绑定/注册广播接收器
        Log.i(TAG," --  mLocalBroadcastManager.registerReceiver(mMYReceiver, intentFilter)");
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Log.i(TAG, " ----- MyActivity : onCreate()");

        //  在 LogCat 打印对应 Activity 活动的类名
        Log.i(TAG, "       Activity : " + getClass().getSimpleName());
        //  在 ActivityCollector.activityList 添加 Activity ,方便管理
        ActivityCollector.addActivity(this);


        //  本地广播接收器的注册
        if(mLocalBroadcastManager == null) {
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);   //  实例化本地广播管理器
            local_receiver_register();   //  注册/绑定
        }

        fPosition = 0;
        showContent(0);

        initNavigation(1);
    }

    private void showContent(int to){
        if (mFragmentManager == null){
            mFragmentManager = getSupportFragmentManager();
        }

        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

//        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        mFragmentTransaction.setCustomAnimations(R.anim.alpha_in, R.anim.alpha_out);

        hideFragment(mFragmentTransaction);

        showFragment(mFragmentTransaction, to);
    }

    private void hideFragment(FragmentTransaction fragmentTransaction){
        if (myFragment_main != null)fragmentTransaction.hide(myFragment_main);
        if (myFragment_local != null)fragmentTransaction.hide(myFragment_local);
        if (myFragment_download != null)fragmentTransaction.hide(myFragment_download);
        if (myFragment_favourite != null)fragmentTransaction.hide(myFragment_favourite);
    }

    private void showFragment(FragmentTransaction fragmentTransaction, int to){
        switch (to) {
            case 0:
                if (myFragment_main == null) {
                    myFragment_main = new MyFragment_Main();
                    fragmentTransaction.add(R.id.my_content_framelayout, myFragment_main, tabs[to]);
                } else {
                    fragmentTransaction.show(myFragment_main);
                }
                break;

            case 1:
                if (myFragment_local == null) {
                    myFragment_local = new MyFragment_Local();
                    fragmentTransaction.add(R.id.my_content_framelayout, myFragment_local, tabs[to]);
                } else {
                    fragmentTransaction.show(myFragment_local);
                }
                break;

            case 2:
                if (myFragment_download == null) {
                    myFragment_download = new MyFragment_Download();
                    fragmentTransaction.add(R.id.my_content_framelayout, myFragment_download, tabs[to]);
                } else {
                    fragmentTransaction.show(myFragment_download);
                }
                break;

            case 3:
                if (myFragment_favourite == null) {
                    myFragment_favourite = new MyFragment_Favourite();
                    fragmentTransaction.add(R.id.my_content_framelayout, myFragment_favourite, tabs[to]);
                } else {
                    fragmentTransaction.show(myFragment_favourite);
                }
                break;
        }

//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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

        fragmentTransaction.add(R.id.my_navigation_framelayout, navigationFragment, "index_navigation");

        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, " ----- MyActivity : onDestroy()");

        // 在 ActivityCollector.activityList 移除 Activity
        ActivityCollector.removeActivity(this);

        mLocalBroadcastManager.unregisterReceiver(mMYReceiver);    //  注销
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            if (fPosition == 0){
                mExit();
            }else {
                fPosition = 0;
                showContent(0);
            }
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
