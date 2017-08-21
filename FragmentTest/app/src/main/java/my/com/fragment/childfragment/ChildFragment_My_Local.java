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
import my.com.adapter.MyLocalRecyclerViewAdapter;
import my.com.adapter.MyMainRecyclerViewAdapter;
import my.com.model.MyMain;
import my.com.model.PlayInfo;

/**
 * Created by MY on 2017/8/16.
 *
 */

public class ChildFragment_My_Local extends Fragment{

    View root;

    ImageView my_local_playing_iv;
    RecyclerView my_local_recyclerview;

    private List<PlayInfo> mList;


    private static final String TAG = "ChildFragment_My_Local";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, " ----- ChildFragment_My_Local : onCreateView()");

        root = inflater.inflate(R.layout.childfragment_my_loacl, null);

        initView(root);

        initRV(root);

        return root;
    }

    private void initView(View v){
        my_local_playing_iv = (ImageView) v.findViewById(R.id.my_local_playing_iv);

        my_local_playing_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.translate_right_in, R.anim.alpha_out);
            }
        });


        my_local_recyclerview = (RecyclerView) v.findViewById(R.id.my_local_recyclerview);
    }

    private void initRV(View v){
        mList =  new ArrayList<>();
        //  添加测试数据
        for (int i = 0; i < 2 ; i++){
            PlayInfo Apple = new PlayInfo("Apple", "singer", 10);
            mList.add(Apple);
            PlayInfo Banana = new PlayInfo("Banana", "singer", 10);
            mList.add(Banana);
            PlayInfo Orange = new PlayInfo("Orange", "singer", 10);
            mList.add(Orange);
            PlayInfo Watermelon = new PlayInfo("Watermelon", "singer", 10);
            mList.add(Watermelon);
            PlayInfo Pear = new PlayInfo("Pear", "singer", 10);
            mList.add(Pear);
            PlayInfo Grape = new PlayInfo("Grape", "singer", 10);
            mList.add(Grape);
            PlayInfo Pineapple = new PlayInfo("Pineapple", "singer", 10);
            mList.add(Pineapple);
            PlayInfo Strawberry = new PlayInfo("Strawberry", "singer", 10);
            mList.add(Strawberry);
            PlayInfo Cherry = new PlayInfo("Cherry", "singer", 10);
            mList.add(Cherry);
            PlayInfo Mango = new PlayInfo("Mango", "singer", 10);
            mList.add(Mango);
        }


//        RecyclerView my_local_recyclerview = (RecyclerView) v.findViewById(R.id.my_local_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        my_local_recyclerview.setLayoutManager(layoutManager);

        MyLocalRecyclerViewAdapter mMyLocalRecyclerViewAdapter = new MyLocalRecyclerViewAdapter(mList, getContext());

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
    }
}
