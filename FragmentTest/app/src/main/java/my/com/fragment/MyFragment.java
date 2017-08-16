package my.com.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import my.com.R;
import my.com.action.BroadcastAction;
import my.com.adapter.MyRecyclerViewAdapter;
import my.com.fragment.childfragment.ChildFragment_My_Local;
import my.com.fragment.childfragment.ChildFragment_My_Main;
import my.com.model.MyMenu;

/**
 * Created by MY on 2017/7/20.
 */

public class MyFragment extends Fragment {

    private View rootview;

    private FrameLayout framelayout_my;

    private FragmentManager mFragmentManager;
    private Fragment childFragment_my_main, childFragment_my_local, childFragment_my_download, childFragment_my_favourite;
    private String[] tabs = new String[]{"main", "local", "download", "favourite"};        //  Fragment 标签


    // PlayService 本地广播接收器（内部类对象）
    private LocalBroadcastManager mLocalBroadcastManager;    // 本地/局部广播管理器
    private MYLocalReceiver mMYLocalReceiver;


    private static final String TAG = "MyFragment";         // 调试信息 TAG 标签

    
    private class MYLocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int to = intent.getIntExtra("jumpto", 0);
            Log.d(TAG, " -- MYLocalReceiver : onReceive()\n" +
                    "    jumpto = " + to);

            showContent(to);
        }
    }
    //  注册本地广播接收器
    private void myfragment_local_receiver_register(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastAction.MyFragmentAction);// "com.my.broadcast.PLAYINFO_LOCAL_ACTION"
        mMYLocalReceiver = new MYLocalReceiver();
        mLocalBroadcastManager.registerReceiver(mMYLocalReceiver, intentFilter);
        Log.i(TAG," -- mLocalBroadcastManager.registerReceiver(mMYLocalReceiver, intentFilter);\n");
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_my,null);

        //  本地广播接收器的注册
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        myfragment_local_receiver_register();

        showContent(0);

        return rootview;
    }

    private void showContent(int to){
        if(mFragmentManager == null){
            mFragmentManager = getChildFragmentManager();
        }

        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

        hideFragment(mFragmentTransaction);

        showFragment(mFragmentTransaction, to);
    }

    private void hideFragment(FragmentTransaction fragmentTransaction){
        if (childFragment_my_main != null)fragmentTransaction.hide(childFragment_my_main);
        if (childFragment_my_local != null)fragmentTransaction.hide(childFragment_my_local);
        if (childFragment_my_download != null)fragmentTransaction.hide(childFragment_my_download);
        if (childFragment_my_favourite != null)fragmentTransaction.hide(childFragment_my_favourite);
    }

    private void showFragment(FragmentTransaction fragmentTransaction, int to){
        switch (to) {
            case 0:
                if (childFragment_my_main == null) {
                    //  首次创建
                    childFragment_my_main = new ChildFragment_My_Main();
                    fragmentTransaction.add(R.id.framelayout_my, childFragment_my_main, tabs[to]);
                } else {
                    //  重新显示
                    fragmentTransaction.show(childFragment_my_main);
                }
                break;

            case 1:
                if (childFragment_my_local == null) {
                    //  首次创建
                    childFragment_my_local = new ChildFragment_My_Local();
                    fragmentTransaction.add(R.id.framelayout_my, childFragment_my_local, tabs[to]);
                } else {
                    //  重新显示
                    fragmentTransaction.show(childFragment_my_local);
                }
                break;

            case 2:
                if (childFragment_my_download == null) {
                    //  首次创建
                    childFragment_my_download = new ChildFragment_My_Local();
                    fragmentTransaction.add(R.id.framelayout_my, childFragment_my_download, tabs[to]);
                } else {
                    //  重新显示
                    fragmentTransaction.show(childFragment_my_download);
                }
                break;

            case 3:
                if (childFragment_my_favourite == null) {
                    //  首次创建
                    childFragment_my_favourite = new ChildFragment_My_Local();
                    fragmentTransaction.add(R.id.framelayout_my, childFragment_my_favourite, tabs[to]);
                } else {
                    //  重新显示
                    fragmentTransaction.show(childFragment_my_favourite);
                }
                break;
        }
        //  把 Fragment 返回栈
        fragmentTransaction.addToBackStack(null);
        //  事务提交
        fragmentTransaction.commit();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, " ----- onDestroyView : " + TAG);

        mLocalBroadcastManager.unregisterReceiver(mMYLocalReceiver);
    }
}
