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
import my.com.adapter.MyRecyclerViewAdapter;
import my.com.model.MyMenu;

/**
 * Created by MY on 2017/8/16.
 * 
 */

public class ChildFragment_My_Local extends Fragment{

    View root;

    ImageView title_my_playing_iv;


    private List<MyMenu> mMenuList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.childfragment_my_main, null);

        initView(root);

        initRV(root);

        return root;
    }

    private void initView(View v){
        title_my_playing_iv = (ImageView) v.findViewById(R.id.title_my_playing_iv);

        title_my_playing_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.translate_right_in, R.anim.translate_left_out);
            }
        });
    }

    private void initRV(View v){
        //  添加测试数据
        for (int i = 0; i < 2 ; i++){
            MyMenu Apple = new MyMenu(R.mipmap.ic_launcher, "Apple", 0);
            mMenuList.add(Apple);
            MyMenu Banana = new MyMenu(R.mipmap.ic_launcher, "Banana", 0);
            mMenuList.add(Banana);
            MyMenu Orange = new MyMenu(R.mipmap.ic_launcher, "Orange", 0);
            mMenuList.add(Orange);
            MyMenu Watermelon = new MyMenu(R.mipmap.ic_launcher, "Watermelon", 0);
            mMenuList.add(Watermelon);
            MyMenu Pear = new MyMenu(R.mipmap.ic_launcher, "Pear", 0);
            mMenuList.add(Pear);
            MyMenu Grape = new MyMenu(R.mipmap.ic_launcher, "Grape", 0);
            mMenuList.add(Grape);
            MyMenu Pineapple = new MyMenu(R.mipmap.ic_launcher, "Pineapple", 0);
            mMenuList.add(Pineapple);
            MyMenu Strawberry = new MyMenu(R.mipmap.ic_launcher, "Strawberry", 0);
            mMenuList.add(Strawberry);
            MyMenu Cherry = new MyMenu(R.mipmap.ic_launcher, "Cherry", 0);
            mMenuList.add(Cherry);
            MyMenu Mango = new MyMenu(R.mipmap.ic_launcher, "Mango", 0);
            mMenuList.add(Mango);
        }


        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview_my);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        MyRecyclerViewAdapter mMyRecyclerViewAdapter = new MyRecyclerViewAdapter(mMenuList, getContext());

        recyclerView.setAdapter(mMyRecyclerViewAdapter);

        //设置Item增加、移除动画
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
//        recyclerView.addItemDecoration(new DividerItemDecoration( getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
    }
}
