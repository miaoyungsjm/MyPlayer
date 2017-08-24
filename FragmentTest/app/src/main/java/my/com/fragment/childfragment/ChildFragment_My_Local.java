package my.com.fragment.childfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import my.com.adapter.MyLocalRecyclerViewAdapter;
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


    private static final String TAG = "ChildFragment_My_Local";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, " ----- ChildFragment_My_Local : onCreateView()");

        root = inflater.inflate(R.layout.childfragment_my_loacl, null);

        initView(root);

        //  实例化播放列表
        if (mList == null){
            mList = new ArrayList<>();
        }
        mList = MusicUtils.scanLocalMusic(getActivity());       //  注意 MusicUtils.scanLocalMusic(getActivity()) 的返回值（并不是静态变量）
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
                mList = MusicUtils.scanLocalMusic(getActivity());
                mList = MusicUtils.updatePlayList(mList); //  注意 MusicUtils.updatePlayList(mList) 的返回值（是静态变量）
                Log.d(TAG, "  MusicUtils.updatePlayList(mList);");

                int mPlayPosition = MusicUtils.getPlayPosition();//  获取前播放位置

                if (mPlayPosition >= 0 ){//  重置前播放状态
                    tPlayInfo = mList.get(mPlayPosition);
                    tPlayInfo.mState = false;
                    Log.d(TAG, " tPlayInfo.mState = false    mPlayPosition = " + mPlayPosition);
                }

                MusicUtils.setPlayPosition(0);
                tPlayInfo = mList.get(0);
                tPlayInfo.mState = true;

                //  重新初始化 RecyclerView
                initRecyclerView();


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
    }
}
