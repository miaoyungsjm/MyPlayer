package com.activitytest.Fragment;

import android.content.Intent;
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

import com.activitytest.PlayerActivity;
import com.activitytest.R;
import com.activitytest.action.BroadcastAction;
import com.activitytest.adapter.MyLocalRecyclerViewAdapter;

/**
 * Created by MY on 2017/8/29.
 *
 */

public class MyFragment_Download extends Fragment implements View.OnClickListener{

    View rootview;

    ImageView my_download_playing_iv;
    ImageView my_download_back_iv;
    RecyclerView my_download_recyclerview;

    private LocalBroadcastManager mLocalBroadcastManager;    // 本地/局部广播管理器



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.fragment_my_download, null);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());

        initView(rootview);

        initRecyclerView();

        return rootview;
    }

    private void initView(View v){
        //  播放器按钮
        my_download_playing_iv = (ImageView) v.findViewById(R.id.my_download_playing_iv);
        my_download_playing_iv.setOnClickListener(this);

        my_download_back_iv = (ImageView) v.findViewById(R.id.my_download_back_iv);
        my_download_back_iv.setOnClickListener(this);

        my_download_recyclerview = (RecyclerView) v.findViewById(R.id.my_download_recyclerview);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_download_playing_iv:
                Intent intent = new Intent(getContext(), PlayerActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.translate_right_in, R.anim.alpha_out);
                break;

            case R.id.my_download_back_iv:
                Intent intent_jumpto = new Intent(BroadcastAction.MyFragmentAction);
                intent_jumpto.putExtra("jumpto", 0);

                mLocalBroadcastManager.sendBroadcast(intent_jumpto);
                break;

        }
    }

    private void initRecyclerView(){

    }


}
