package my.com.fragment.childfragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import my.com.PlayerActivity;
import my.com.R;
import my.com.action.BroadcastAction;
import my.com.adapter.MyLocalRecyclerViewAdapter;
import my.com.fragment.MyFragment;
import my.com.model.PlayInfo;
import my.com.service.PlayerService;
import my.com.utils.MusicUtils;

/**
 * Created by MY on 2017/8/16.
 *
 */


public class ChildFragment_My_Local extends Fragment{

    View root;

    ImageView my_local_playing_iv;
    LinearLayout my_local_playall_ll;
    RecyclerView my_local_recyclerview;

    private List<PlayInfo> mList;
    MyLocalRecyclerViewAdapter mMyLocalRecyclerViewAdapter;


    //  本地广播接收器
    private LocalBroadcastManager mLocalBroadcastManager;    // 本地/局部广播管理器
    private MYLocalReceiver mMYLocalReceiver;       //  接收器对象，内部类编写处理方法
    boolean isChange = false;


    private static final String TAG = "ChildFragment_My_Local";


    /*
     *  本地广播接收器
     */
    private class MYLocalReceiver extends BroadcastReceiver {
        PlayInfo tPlayInfo;
        String musicname;
        @Override
        public void onReceive(Context context, Intent intent) {
            isChange = intent.getBooleanExtra("isChange", false);
            if(isChange){
                musicname = intent.getStringExtra("musicname");
                for (int i = 0 ; i < mList.size() ; i++){
                    tPlayInfo = mList.get(i);
                    if (tPlayInfo.getName().equals(musicname)){
                        tPlayInfo.mState = true;
                    }else {
                        tPlayInfo.mState = false;
                    }
                }

                mMyLocalRecyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }
    //  注册 - 本地广播接收器
    private void local_receiver_register(){
        IntentFilter intentFilter = new IntentFilter();         //  过滤器
        intentFilter.addAction(BroadcastAction.PlayInfoProgressAction);

        mMYLocalReceiver = new MYLocalReceiver();            //  实例化广播接收器对象
        mLocalBroadcastManager.registerReceiver(mMYLocalReceiver, intentFilter);        //  绑定/注册广播接收器
        Log.i(TAG," --  mLocalBroadcastManager.registerReceiver(mMYLocalReceiver, intentFilter)");
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, " ----- ChildFragment_My_Local : onCreateView()");

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        local_receiver_register();

        root = inflater.inflate(R.layout.childfragment_my_loacl, null);

        initView(root);

        //  实例化播放列表
        if (mList == null){
            mList = new ArrayList<>();
        }
        mList = MusicUtils.scanLocalMusic(getActivity());
        Log.d(TAG, "  MusicUtils.scanLocalMusic(this)  获取本地列表");

        initRecyclerView();

        return root;
    }

    private void initView(View v){
        //  播放器按钮
        my_local_playing_iv = (ImageView) v.findViewById(R.id.my_local_playing_iv);
        my_local_playing_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.translate_right_in, R.anim.alpha_out);
            }
        });

        //  播放全部按钮
        my_local_playall_ll = (LinearLayout) v.findViewById(R.id.my_local_playall_ll);
        my_local_playall_ll.setOnClickListener(new View.OnClickListener() {
            PlayInfo tPlayInfo;
            @Override
            public void onClick(View v) {

                List<PlayInfo> tList = new ArrayList<>();
                tList = MusicUtils.scanLocalMusic(getActivity());
                MusicUtils.updatePlayList(tList);


                for (int i = 0; i < mList.size(); i++){
                    tPlayInfo = mList.get(i);
                    tPlayInfo.mState = false;
                }

                MusicUtils.setPlayPosition(0);
                tPlayInfo = mList.get(0);
                tPlayInfo.mState = true;

                //  重启服务
                Intent intent = new Intent(getActivity(), PlayerService.class);
                getActivity().startService(intent);
                Log.d(TAG, "  startService(intent)");


                mMyLocalRecyclerViewAdapter.notifyDataSetChanged();
            }
        });

        //  RecyclerView 控件
        my_local_recyclerview = (RecyclerView) v.findViewById(R.id.my_local_recyclerview);
    }

    /*
     *  初始化 RecyclerView
     */
    private void initRecyclerView(){
//        RecyclerView my_local_recyclerview = (RecyclerView) v.findViewById(R.id.my_local_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        my_local_recyclerview.setLayoutManager(layoutManager);

        mMyLocalRecyclerViewAdapter = new MyLocalRecyclerViewAdapter(mList, getContext());

        my_local_recyclerview.setAdapter(mMyLocalRecyclerViewAdapter);

        //设置Item增加、移除动画
//        my_local_recyclerview.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
//        my_local_recyclerview.addItemDecoration(new DividerItemDecoration( getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, " ----- ChildFragment_My_Local : onDestroyView()");

        mLocalBroadcastManager.unregisterReceiver(mMYLocalReceiver);
    }
}
