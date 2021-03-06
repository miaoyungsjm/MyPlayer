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
import my.com.adapter.MyMainRecyclerViewAdapter;
import my.com.model.MyMain;

/**
 * Created by MY on 2017/8/16.
 *
 */

public class ChildFragment_My_Favourite extends Fragment{

    View root;

    ImageView my_favourite_playing_iv;


    private List<MyMain> mMyMainList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.childfragment_my_favourite, null);

        initView(root);

//        initRV(root);

        return root;
    }

    private void initView(View v){
        my_favourite_playing_iv = (ImageView) v.findViewById(R.id.my_favourite_playing_iv);

        my_favourite_playing_iv.setOnClickListener(new View.OnClickListener() {
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
            MyMain Apple = new MyMain(R.mipmap.ic_launcher, "Apple", 0);
            mMyMainList.add(Apple);
            MyMain Banana = new MyMain(R.mipmap.ic_launcher, "Banana", 0);
            mMyMainList.add(Banana);
            MyMain Orange = new MyMain(R.mipmap.ic_launcher, "Orange", 0);
            mMyMainList.add(Orange);
            MyMain Watermelon = new MyMain(R.mipmap.ic_launcher, "Watermelon", 0);
            mMyMainList.add(Watermelon);
            MyMain Pear = new MyMain(R.mipmap.ic_launcher, "Pear", 0);
            mMyMainList.add(Pear);
            MyMain Grape = new MyMain(R.mipmap.ic_launcher, "Grape", 0);
            mMyMainList.add(Grape);
            MyMain Pineapple = new MyMain(R.mipmap.ic_launcher, "Pineapple", 0);
            mMyMainList.add(Pineapple);
            MyMain Strawberry = new MyMain(R.mipmap.ic_launcher, "Strawberry", 0);
            mMyMainList.add(Strawberry);
            MyMain Cherry = new MyMain(R.mipmap.ic_launcher, "Cherry", 0);
            mMyMainList.add(Cherry);
            MyMain Mango = new MyMain(R.mipmap.ic_launcher, "Mango", 0);
            mMyMainList.add(Mango);
        }


        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.my_favourite_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        MyMainRecyclerViewAdapter mMyMainRecyclerViewAdapter = new MyMainRecyclerViewAdapter(mMyMainList, getContext());

        recyclerView.setAdapter(mMyMainRecyclerViewAdapter);

        //设置Item增加、移除动画
//        my_local_recyclerview.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
//        my_local_recyclerview.addItemDecoration(new DividerItemDecoration( getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
    }
}
