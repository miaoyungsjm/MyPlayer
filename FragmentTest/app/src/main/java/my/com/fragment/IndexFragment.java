package my.com.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import my.com.PlayerActivity;
import my.com.R;
import my.com.adapter.IndexFragmentPagerAdapter;
import my.com.fragment.childfragment.ChildFragment_Index_Songlist;
import my.com.fragment.childfragment.ChildFragment_Index_Ranking;
import my.com.fragment.childfragment.ChildFragment_Index_Recommend;

/**
 * Created by MY on 2017/7/20.
 *
 */

public class IndexFragment extends Fragment implements View.OnClickListener{

    private View root;

    private ImageView title_index_playing_iv;
    private TextView title_index_songlist_tv;
    private TextView title_index_recommend_tv;
    private TextView title_index_ranking_tv;

    private ViewPager mViewPager;
//    private FragmentPagerAdapter indexFragmentPagerAdapter;
    private IndexFragmentPagerAdapter indexFragmentPagerAdapter;
    private List<Fragment> mList;


    private static final String TAG = "IndexFragment";         // 调试信息 TAG 标签

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        Log.i(TAG, " ----- IndexFragment : onCreateView()");


        if(root == null){
            //通过 LayoutInflater 的 inflate() 方法将 fragment_index 布局动态加载进来
//            View view = inflater.inflate(R.layout.fragment_index, container, false);
            root = inflater.inflate(R.layout.fragment_index, null);

            initView(root);     //  初始化控件

            init_ViewPager(root);   //  初始化 ViewPager

        }

        /*
         * 底部导航栏切换后 由于没有销毁顶部设置导致如果没有重新设置view
         * 导致底部切换后切回顶部页面数据会消失等bug
         * 以下设置每次重新创建view即可
         */
//        ViewGroup parent = (ViewGroup) root.getParent();
//        if (parent != null) {
//            parent.removeView(root);
//        }


        return root;
    }

    private void initView(View v){
        title_index_playing_iv = (ImageView) v.findViewById(R.id.title_index_playing_iv);
        title_index_playing_iv.setOnClickListener(this);

        title_index_songlist_tv = (TextView) v.findViewById(R.id.title_index_songlist_tv);
        title_index_songlist_tv.setOnClickListener(this);
        title_index_recommend_tv = (TextView) v.findViewById(R.id.title_index_recommend_tv);
        title_index_recommend_tv.setOnClickListener(this);
        title_index_ranking_tv = (TextView) v.findViewById(R.id.title_index_ranking_tv);
        title_index_ranking_tv.setOnClickListener(this);

    }

    private void init_ViewPager(View v){
        mViewPager = (ViewPager) v.findViewById(R.id.viewpager_index);

        mList = new ArrayList<Fragment>();
        ChildFragment_Index_Songlist childFragment_index_songlist = new ChildFragment_Index_Songlist();
        ChildFragment_Index_Recommend childFragment_index_recommend = new ChildFragment_Index_Recommend();
        ChildFragment_Index_Ranking childFragment_index_ranking = new ChildFragment_Index_Ranking();
        mList.add(childFragment_index_songlist);
        mList.add(childFragment_index_recommend);
        mList.add(childFragment_index_ranking);


//        indexFragmentPagerAdapter = new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
//            @Override
//            public Fragment getItem(int position) {
//                return mList.get(position);
//            }
//
//            @Override
//            public int getCount() {
//                return mList.size();
//            }
//        };

        //  实例化 FragmentPagerAdapter 适配器，
//        indexFragmentPagerAdapter = new IndexFragmentPagerAdapter(getActivity().getSupportFragmentManager(), mList);
        indexFragmentPagerAdapter = new IndexFragmentPagerAdapter(getChildFragmentManager(), mList);

        //  绑定适配器
        mViewPager.setAdapter(indexFragmentPagerAdapter);

        mViewPager.setCurrentItem(1);       //  设置默认显示页面

        mViewPager.setOffscreenPageLimit(3);    //  缓存页面数量

//        mViewPager.setOnPageChangeListener();     //  页面切换监听器
    }

    /*
     *  点击事件监听器
     */
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.title_index_playing_iv :
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                getActivity().startActivity(intent);
                //getActivity().startActivity(new Intent().setClass(getActivity(), PlayerActivity.class));
                getActivity().overridePendingTransition(R.anim.translate_right_in, R.anim.alpha_out);
                break;

            case R.id.title_index_songlist_tv:
                mViewPager.setCurrentItem(0);
                break;

            case R.id.title_index_recommend_tv :
                mViewPager.setCurrentItem(1);
                break;

            case R.id.title_index_ranking_tv :
                mViewPager.setCurrentItem(2);
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, " ----- IndexFragment : onDestroyView()");
    }
}
