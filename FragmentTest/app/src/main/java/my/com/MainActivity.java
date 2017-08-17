package my.com;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import my.com.action.BroadcastAction;
import my.com.fragment.FriendFragment;
import my.com.fragment.IndexFragment;
import my.com.fragment.MyFragment;
import my.com.fragment.PersonFragment;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    //  底部导航栏的控件
    private LinearLayout navigation_index_ll, navigation_my_ll, navigation_friend_ll, navigation_person_ll;
    private ImageView navigation_index_iv, navigation_my_iv, navigation_friend_iv, navigation_person_iv;
    private TextView navigation_index_tv, navigation_my_tv, navigation_friend_tv, navigation_person_tv;

    //  Fragment 碎片
    private FragmentManager mFragmentManager;
    private Fragment indexfragment, myfragment, friendfragment, personfragment;     //  Fragment 对象
    private String[] tabs = new String[]{"index", "my", "friend", "person"};           //  Fragment 标签

    //  记录底部导航栏跳转的位置
    int fPosition;

    //  重写“返回键”所需变量
    private long mExitTime;     //  用于计算双击“返回键”的间隔时长

    //  本地广播管理器
    private LocalBroadcastManager mLocalBroadcastManager;


    private static final String TAG = "MainActivity";         // 调试信息 TAG 标签


    /*
     *   ----- MainActivity : onCreate
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, " ----- MainActivity : onCreate");

        //  在 LogCat 打印对应 Activity 活动的类名
        Log.i(TAG, "       Activity : " + getClass().getSimpleName());
        //  在 ActivityCollector.activityList 添加 Activity ,方便管理
        ActivityCollector.addActivity(this);


        //  实例化本地广播管理器，用途在重写返回键的位置
        if(mLocalBroadcastManager == null) {
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        }


        //  初始化页面，绑定控件
        initView();

        //  显示底部导航栏第一个 Fragment ，并更新底部导航栏状态
        navigation_index_iv.setSelected(true);
        navigation_index_tv.setSelected(true);
        showContent(0);     //  显示 Fragment
        fPosition = 0;      //  记录位置
    }

    /*
     *  初始化 MainActivity 的控件，并设置对应的控件的点击事件
     */
    private void initView(){
        navigation_index_ll = (LinearLayout) findViewById(R.id.navigation_index_ll);
        navigation_my_ll = (LinearLayout) findViewById(R.id.navigation_my_ll);
        navigation_friend_ll = (LinearLayout) findViewById(R.id.navigation_friend_ll);
        navigation_person_ll = (LinearLayout) findViewById(R.id.navigation_person_ll);

        navigation_index_ll.setOnClickListener(this);
        navigation_my_ll.setOnClickListener(this);
        navigation_friend_ll.setOnClickListener(this);
        navigation_person_ll.setOnClickListener(this);

        navigation_index_iv = (ImageView) findViewById(R.id.navigation_index_iv);
        navigation_my_iv = (ImageView) findViewById(R.id.navigation_my_iv);
        navigation_friend_iv = (ImageView) findViewById(R.id.navigation_friend_iv);
        navigation_person_iv = (ImageView) findViewById(R.id.navigation_person_iv);

        navigation_index_tv = (TextView) findViewById(R.id.navigation_index_tv);
        navigation_my_tv = (TextView) findViewById(R.id.navigation_my_tv);
        navigation_friend_tv = (TextView) findViewById(R.id.navigation_friend_tv);
        navigation_person_tv = (TextView) findViewById(R.id.navigation_person_tv);

    }

    /*
     *  重置底部导航栏的控件状态（被选中）的函数
     *
     *  使其恢复默认状态（灰色）
     *  详细状态看 drawable - navigation_index_xxx.xml 文件
     */
    private void resetSelect(){
        navigation_index_iv.setSelected(false);
        navigation_my_iv.setSelected(false);
        navigation_friend_iv.setSelected(false);
        navigation_person_iv.setSelected(false);

        navigation_index_tv.setSelected(false);
        navigation_my_tv.setSelected(false);
        navigation_friend_tv.setSelected(false);
        navigation_person_tv.setSelected(false);
    }

    /*
     *  点击事件监听器
     */
    @Override
    public void onClick(View v){
        switch (v.getId())
        {
            case R.id.navigation_index_ll :
                resetSelect();
                navigation_index_iv.setSelected(true);
                navigation_index_tv.setSelected(true);
                showContent(0);
                break;

            case R.id.navigation_my_ll :
                resetSelect();
                navigation_my_iv.setSelected(true);
                navigation_my_tv.setSelected(true);
                showContent(1);
                break;

            case R.id.navigation_friend_ll :
                resetSelect();
                navigation_friend_iv.setSelected(true);
                navigation_friend_tv.setSelected(true);
                showContent(2);
                break;

            case R.id.navigation_person_ll :
                resetSelect();
                navigation_person_iv.setSelected(true);
                navigation_person_tv.setSelected(true);
                showContent(3);
                break;
        }
    }

    /*
     *  使用 FragmentManager 显示 Fragment 的函数
     *  显示第 to 个 Fragment 去 <Framelayout>
     */
    private void showContent(int to){
        if(mFragmentManager == null){
            //  实例化 FragmentManager ，注意是：getSupportFragmentManager()
            mFragmentManager = this.getSupportFragmentManager();
        }

        //  开启一个事务
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

        hideFragment(mFragmentTransaction);

        showFragment(mFragmentTransaction, to);


        //  替换 Fragment
//        mFragmentTransaction.replace(R.id.framelayout_main, getFragment(to),TAGS[to]);

        //  把 Fragment 返回栈
//        mFragmentTransaction.addToBackStack(null);

        //  提交事务
//        mFragmentTransaction.commit();
    }

    private void hideFragment(FragmentTransaction fragmentTransaction){
        if (indexfragment != null)fragmentTransaction.hide(indexfragment);
        if (myfragment != null)fragmentTransaction.hide(myfragment);
        if (friendfragment != null)fragmentTransaction.hide(friendfragment);
        if (personfragment != null)fragmentTransaction.hide(personfragment);
    }

    private void showFragment(FragmentTransaction fragmentTransaction, int to) {
        switch (to) {
            case 0:
                fPosition = 0;
                if (indexfragment == null){         //  首次创建

                    indexfragment = new IndexFragment();
                    fragmentTransaction.add(R.id.framelayout_main, indexfragment, tabs[to]);
                }else {         //  重新显示
                    fragmentTransaction.show(indexfragment);
                }
                break;

            case 1:
                fPosition = 1;
                if (myfragment == null){
                    myfragment = new MyFragment();
                    fragmentTransaction.add(R.id.framelayout_main, myfragment, tabs[to]);
                }else {
                    fragmentTransaction.show(myfragment);
                }
                break;

            case 2:
                fPosition = 2;
                if (friendfragment == null){
                    friendfragment = new FriendFragment();
                    fragmentTransaction.add(R.id.framelayout_main, friendfragment, tabs[to]);
                }else {
                    fragmentTransaction.show(friendfragment);
                }
                break;

            case 3:
                fPosition = 3;
                if (personfragment == null){
                    personfragment = new PersonFragment();
                    fragmentTransaction.add(R.id.framelayout_main, personfragment, tabs[to]);
                }else {
                    fragmentTransaction.show(personfragment);
                }
                break;
        }
        //  把 Fragment 返回栈
//        fragmentTransaction.addToBackStack(null);
        //  事务提交
        fragmentTransaction.commit();
    }


    /*
     *  重写“返回键”的方法
     *  双击退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){        //  当按下返回键，并且不是长按
            if(fPosition == 1){         //  如果 Fragment 页面是在“我的”那，则不使用双击退出程序
                /*
                 *  这里使用本地广播管理器 LocalBroadcastManager 发送本地广播，
                 *  目的是为了使 MyFragment 里的 ChildFragment 能跳转
                 *  当按下返回键时，使其发送内容为要跳转的 ChildFragment 的地址
                 */
                int jumpto = 0;     //  跳转地址
                Intent intent = new Intent(BroadcastAction.MyFragmentAction);
                intent.putExtra("jumpto", jumpto);
                mLocalBroadcastManager.sendBroadcast(intent);
                Log.i(TAG," -- MainActivity  onKeyDown() : mLocalBroadcastManager.sendBroadcast(intent)  " +
                        "  Jump To :" + jumpto );

            }else {
                mExit();        //  开启双击退出
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void mExit(){
        if((System.currentTimeMillis() - mExitTime) > 2000){
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        }
        else {
            //  调用 ActivityCollector.finishAll() 销毁所有 Activity
            ActivityCollector.finishAll();

            //  加了这个所有的 Activity 的 onDestroy() 都没执行
//            System.exit(0);
        }
    }


    /*
     *   ----- MainActivity : onDestroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, " ----- MainActivity : onDestroy");

        // 在 ActivityCollector.activityList 移除 Activity
        ActivityCollector.removeActivity(this);


    }
}
