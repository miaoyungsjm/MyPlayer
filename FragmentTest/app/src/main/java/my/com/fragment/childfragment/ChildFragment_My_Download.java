package my.com.fragment.childfragment;

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

import java.util.ArrayList;
import java.util.List;

import my.com.PlayerActivity;
import my.com.R;
import my.com.adapter.MyLocalRecyclerViewAdapter;
import my.com.adapter.MyMainRecyclerViewAdapter;
import my.com.model.PlayInfo;

/**
 * Created by MY on 2017/8/16.
 *
 */

public class ChildFragment_My_Download extends Fragment{

    View root;

    ImageView my_download_playing_iv;


    private List<PlayInfo> mPlayInfoList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.childfragment_my_download, null);

        initView(root);

//        initRV(root);

        return root;
    }

    private void initView(View v){
        my_download_playing_iv = (ImageView) v.findViewById(R.id.my_download_playing_iv);

        my_download_playing_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.translate_right_in, R.anim.alpha_out);
            }
        });
    }

    private void initRV(View v){
        //  添加测试数据
        for (int i = 0; i < 2 ; i++){
            PlayInfo Apple = new PlayInfo("Apple","Singer", 0);
            mPlayInfoList.add(Apple);
            PlayInfo Banana = new PlayInfo("Banana","Singer", 0);
            mPlayInfoList.add(Banana);
            PlayInfo Orange = new PlayInfo("Orange","Singer", 0);
            mPlayInfoList.add(Orange);
            PlayInfo Watermelon = new PlayInfo("Watermelon","Singer", 0);
            mPlayInfoList.add(Watermelon);
            PlayInfo Pear = new PlayInfo("Pear","Singer", 0);
            mPlayInfoList.add(Pear);
            PlayInfo Grape = new PlayInfo("Grape","Singer", 0);
            mPlayInfoList.add(Grape);
            PlayInfo Pineapple = new PlayInfo("Pineapple","Singer", 0);
            mPlayInfoList.add(Pineapple);
            PlayInfo Strawberry = new PlayInfo("Strawberry","Singer", 0);
            mPlayInfoList.add(Strawberry);
            PlayInfo Cherry = new PlayInfo("Cherry","Singer", 0);
            mPlayInfoList.add(Cherry);
            PlayInfo Mango = new PlayInfo("Mango","Singer", 0);
            mPlayInfoList.add(Mango);
        }


        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.my_download_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        MyLocalRecyclerViewAdapter mMyLocalRecyclerViewAdapter = new MyLocalRecyclerViewAdapter(mPlayInfoList, getContext());

        recyclerView.setAdapter(mMyLocalRecyclerViewAdapter);

        //设置Item增加、移除动画
//        my_local_recyclerview.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
//        my_local_recyclerview.addItemDecoration(new DividerItemDecoration( getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
    }
}
