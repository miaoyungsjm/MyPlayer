package my.com.fragment.childfragment;

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

import java.util.ArrayList;
import java.util.List;

import my.com.PlayerActivity;
import my.com.R;
import my.com.adapter.MyMainRecyclerViewAdapter;
import my.com.model.MyMain;

/**
 * Created by MY on 2017/8/16.
 *
 */

public class ChildFragment_My_Main extends Fragment{

    View root;

    private LocalBroadcastManager mLocalBroadcastManager;    //  本地广播管理

    ImageView my_main_playing_iv;       //  ImageView
    RecyclerView my_main_recyclerview;      //  RecyclerView

    private List<MyMain> mMyMainList;


    private static final String TAG = "ChildFragment_My_Main";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, " ----- ChildFragment_My_Main : onCreateView()");

        root = inflater.inflate(R.layout.childfragment_my_main, null);

        if(mLocalBroadcastManager == null) {
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        }

        initView(root);

        initRecyclerView();

        return root;
    }

    private void initView(View v){
        my_main_playing_iv = (ImageView) v.findViewById(R.id.my_main_playing_iv);

        my_main_playing_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.translate_right_in, R.anim.alpha_out);
            }
        });


        my_main_recyclerview = (RecyclerView) v.findViewById(R.id.my_main_recyclerview);
    }

    private void initRecyclerView(){
        mMyMainList = new ArrayList<>();
        MyMain local = new MyMain(R.drawable.music_red, "本地音乐", 0);
        mMyMainList.add(local);
        MyMain download = new MyMain(R.mipmap.ic_launcher, "我的下载", 0);
        mMyMainList.add(download);
        MyMain favourite = new MyMain(R.mipmap.ic_launcher, "我的收藏", 0);
        mMyMainList.add(favourite);

//        RecyclerView my_local_recyclerview = (RecyclerView) v.findViewById(R.id.my_local_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());      //  getContext()？？？
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        my_main_recyclerview.setLayoutManager(layoutManager);

        MyMainRecyclerViewAdapter mMyMainRecyclerViewAdapter = new MyMainRecyclerViewAdapter(mMyMainList, mLocalBroadcastManager);

        my_main_recyclerview.setAdapter(mMyMainRecyclerViewAdapter);

        //设置Item增加、移除动画
//        my_local_recyclerview.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
//        my_local_recyclerview.addItemDecoration(new DividerItemDecoration( getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, " ----- ChildFragment_My_Main : onDestroyView()");
    }
}
