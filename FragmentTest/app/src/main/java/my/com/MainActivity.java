package my.com;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import my.com.fragment.FriendFragment;
import my.com.fragment.IndexFragment;
import my.com.fragment.MyFragment;
import my.com.fragment.PersonFragment;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout navigation_index_ll, navigation_my_ll, navigation_friend_ll, navigation_person_ll;
    private ImageView navigation_index_iv, navigation_my_iv, navigation_friend_iv, navigation_person_iv;
    private TextView navigation_index_tv, navigation_my_tv, navigation_friend_tv, navigation_person_tv;

    private FragmentManager fManager;
    private String[] TAGS = new String[]{"index", "my", "friend", "person"};

    private ViewPager viewpager_index;

    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void exit(){
        if((System.currentTimeMillis() - mExitTime) > 2000){
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        }
        else {
            ActivityCollector.finishAll();
            System.exit(0);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        navigation_index_iv.setSelected(true);
        navigation_index_tv.setSelected(true);
        showContent(0);
    }

    private void bindViews(){
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

        viewpager_index = (ViewPager) findViewById(R.id.viewpager_index);
    }

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

    @Override
    public void onClick(View v){
        switch (v.getId())
        {
            case R.id.navigation_index_ll :
                //navigation_index_iv.setImageResource(R.drawable.index_red);
                //navigation_index_tv.setTextColor(getResources().getColor(R.color.colorAccent));
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

    private void showContent(int to){
        if(fManager == null){
            fManager = this.getFragmentManager();
        }
        FragmentTransaction fTransaction = fManager.beginTransaction();

        fTransaction.replace(R.id.framelayout_main, getFragment(to),TAGS[to]);
        //fTransaction.addToBackStack(null);
        fTransaction.commit();
    }

    private Fragment getFragment(int index) {

        switch (index) {
            case 0:
                Fragment indexfragment = new IndexFragment();
                return indexfragment;

            case 1:
                Fragment myfragment = new MyFragment();
                return myfragment;

            case 2:
                Fragment friendfragment = new FriendFragment();
                return friendfragment;

            case 3:
                Fragment personfragment = new PersonFragment();
                return personfragment;
        }
        return null;
    }
}
