package com.activitytest.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.activitytest.PlayerActivity;
import com.activitytest.R;
import com.activitytest.adapter.MyMainRecyclerViewAdapter;
import com.activitytest.model.MyMain;
import com.activitytest.utils.MusicUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MY on 2017/8/29.
 *
 */

public class MyFragment_Main extends Fragment implements View.OnClickListener{

    View rootview;

    ImageView my_main_playing_iv;       //  ImageView
    TextView my_main_more_tv;
    RecyclerView my_main_recyclerview;      //  RecyclerView

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.fragment_my_main, null);

        initView(rootview);

        initRecyclerView();

        return rootview;
    }

    private void initView(View v){
        my_main_playing_iv = (ImageView) v.findViewById(R.id.my_main_playing_iv);
        my_main_playing_iv.setOnClickListener(this);

        my_main_more_tv = (TextView) v.findViewById(R.id.my_main_more_tv);
        my_main_more_tv.setOnClickListener(this);

        my_main_recyclerview = (RecyclerView) v.findViewById(R.id.my_main_recyclerview);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.my_main_playing_iv:
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.translate_right_in, R.anim.alpha_out);
                break;

            case R.id.my_main_more_tv:


                //  popupwindow    管理歌单


                break;
        }
    }

    private void initRecyclerView(){
        List<MyMain> mMyMainList = new ArrayList<>();
        MyMain local = new MyMain(R.drawable.music_red, "本地音乐", MusicUtils.getListSize());
        mMyMainList.add(local);
        MyMain download = new MyMain(R.mipmap.ic_launcher, "我的下载", 0);
        mMyMainList.add(download);
        MyMain favourite = new MyMain(R.mipmap.ic_launcher, "我的收藏", 0);
        mMyMainList.add(favourite);

//        RecyclerView my_local_recyclerview = (RecyclerView) v.findViewById(R.id.my_local_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        my_main_recyclerview.setLayoutManager(layoutManager);

        MyMainRecyclerViewAdapter mMyMainRecyclerViewAdapter = new MyMainRecyclerViewAdapter(mMyMainList, getContext());

        my_main_recyclerview.setAdapter(mMyMainRecyclerViewAdapter);

        //设置Item增加、移除动画
//        my_local_recyclerview.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
//        my_local_recyclerview.addItemDecoration(new DividerItemDecoration( getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
    }

}
