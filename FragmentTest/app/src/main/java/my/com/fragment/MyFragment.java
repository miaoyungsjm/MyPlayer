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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import my.com.R;
import my.com.action.BroadcastAction;
import my.com.fragment.childfragment.ChildFragment_My_Download;
import my.com.fragment.childfragment.ChildFragment_My_Favourite;
import my.com.fragment.childfragment.ChildFragment_My_Local;
import my.com.fragment.childfragment.ChildFragment_My_Main;

/**
 * Created by MY on 2017/7/20.
 *
 */

public class MyFragment extends Fragment {

    private View root;

    private FragmentManager mFragmentManager;
    private Fragment childFragment_my_main, childFragment_my_local, childFragment_my_download, childFragment_my_favourite;
    private String[] tabs = new String[]{"main", "local", "download", "favourite"};        //  Fragment 标签


    //  本地广播接收器
    private LocalBroadcastManager mLocalBroadcastManager;    // 本地/局部广播管理器
    private MYReceiver mMYReceiver;       //  接收器对象，内部类编写处理方法


    private static final String TAG = "MyFragment";         // 调试信息 TAG 标签


    /*
     *  本地广播接收器
     *
     *  当收到 BroadcastAction.MyFragmentAction 广播，解析广播内容：jumpto
     *  实现 ChildFragment 的页面跳转 showContent(to)
     */
    private class MYReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int to = intent.getIntExtra("jumpto", 0);
            Log.d(TAG, " -- MYReceiver : onReceive()  " +
                    "  jumpto = " + to);

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



    /*
     *   ----- MyFragment : onCreateView()
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        Log.i(TAG, " ----- MyFragment : onCreateView()");

        root = inflater.inflate(R.layout.fragment_my, null);

        //  本地广播接收器的注册
        if(mLocalBroadcastManager == null) {
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());   //  实例化本地广播管理器
            local_receiver_register();   //  注册/绑定
        }

        //  显示 ChildFragment_My_Main
        showContent(0);

        return root;
    }

    private void showContent(int to){
        if(mFragmentManager == null){
            //  实例化 FragmentManager ，注意是：getChildFragmentManager()
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
                    childFragment_my_local = new ChildFragment_My_Local();
                    fragmentTransaction.add(R.id.framelayout_my, childFragment_my_local, tabs[to]);
                } else {
                    fragmentTransaction.show(childFragment_my_local);
                }
                break;

            case 2:
                if (childFragment_my_download == null) {
                    childFragment_my_download = new ChildFragment_My_Download();
                    fragmentTransaction.add(R.id.framelayout_my, childFragment_my_download, tabs[to]);
                } else {
                    fragmentTransaction.show(childFragment_my_download);
                }
                break;

            case 3:
                if (childFragment_my_favourite == null) {
                    childFragment_my_favourite = new ChildFragment_My_Favourite();
                    fragmentTransaction.add(R.id.framelayout_my, childFragment_my_favourite, tabs[to]);
                } else {
                    fragmentTransaction.show(childFragment_my_favourite);
                }
                break;
        }
        //  把 Fragment 返回栈
//        fragmentTransaction.addToBackStack(null);
        //  事务提交
        fragmentTransaction.commit();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, " ----- MyFragment : onDestroyView()");

        mLocalBroadcastManager.unregisterReceiver(mMYReceiver);    //  注销
    }
}
