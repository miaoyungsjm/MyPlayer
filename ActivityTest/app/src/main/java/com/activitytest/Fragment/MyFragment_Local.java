package com.activitytest.Fragment;

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

import com.activitytest.PlayerActivity;
import com.activitytest.R;
import com.activitytest.action.BroadcastAction;
import com.activitytest.adapter.MyLocalRecyclerViewAdapter;
import com.activitytest.model.PlayInfo;
import com.activitytest.service.PlayerService;
import com.activitytest.utils.MusicUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by MY on 2017/8/29.
 *
 */

public class MyFragment_Local extends Fragment implements View.OnClickListener{

    View rootview;

    ImageView my_local_playing_iv;
    ImageView my_local_back_iv;
    LinearLayout my_local_playall_ll;
    RecyclerView my_local_recyclerview;

    private List<PlayInfo> mList;
    MyLocalRecyclerViewAdapter mMyLocalRecyclerViewAdapter;

    //  本地广播接收器
    private LocalBroadcastManager mLocalBroadcastManager;    // 本地/局部广播管理器
    private MYLocalReceiver mMYLocalReceiver;       //  接收器对象，内部类编写处理方法
    boolean isChange = false;


    private static final String TAG = "MyFragment_Local";


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

        rootview = inflater.inflate(R.layout.fragment_my_local, null);


        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        local_receiver_register();

        initView(rootview);

        //  实例化播放列表
        if (mList == null){
            mList = new ArrayList<>();
        }
        mList = MusicUtils.scanLocalMusic(getContext());
        Log.d(TAG, "  MusicUtils.scanLocalMusic(this)  获取本地列表");

        initRecyclerView();

        return rootview;
    }

    private void initView(View v){
        //  播放器按钮
        my_local_playing_iv = (ImageView) v.findViewById(R.id.my_local_playing_iv);
        my_local_playing_iv.setOnClickListener(this);

        my_local_back_iv = (ImageView) v.findViewById(R.id.my_local_back_iv);
        my_local_back_iv.setOnClickListener(this);

        //  播放全部按钮
        my_local_playall_ll = (LinearLayout) v.findViewById(R.id.my_local_playall_ll);
        my_local_playall_ll.setOnClickListener(this);


        //  RecyclerView 控件
        my_local_recyclerview = (RecyclerView) v.findViewById(R.id.my_local_recyclerview);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.my_local_playing_iv:
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.translate_right_in, R.anim.alpha_out);
                break;

            case R.id.my_local_back_iv:
                Intent intent_jumpto = new Intent(BroadcastAction.MyFragmentAction);
                intent_jumpto.putExtra("jumpto", 0);

                mLocalBroadcastManager.sendBroadcast(intent_jumpto);
                break;

            case R.id.my_local_playall_ll:
                List<PlayInfo> tList = new ArrayList<>();
                tList = MusicUtils.scanLocalMusic(getActivity());
                MusicUtils.updatePlayList(tList);

                PlayInfo tPlayInfo;
                for (int i = 0; i < mList.size(); i++){
                    tPlayInfo = mList.get(i);
                    tPlayInfo.mState = false;
                }

                MusicUtils.setPlayPosition(0);
                tPlayInfo = mList.get(0);
                tPlayInfo.mState = true;

                //  重启服务
                Intent intent_1 = new Intent(getActivity(), PlayerService.class);
                getActivity().startService(intent_1);
                Log.d(TAG, "  startService(intent)");


                mMyLocalRecyclerViewAdapter.notifyDataSetChanged();
                break;
        }
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

        mLocalBroadcastManager.unregisterReceiver(mMYLocalReceiver);
    }
}
