package my.com.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import my.com.PlayerActivity;
import my.com.R;
import my.com.adapter.MFragmentPagerAdapter;
import my.com.fragment.childfragment.ChildFragment_Index_Musiclist;
import my.com.fragment.childfragment.ChildFragment_Index_Ranking;
import my.com.fragment.childfragment.ChildFragment_Index_Recommend;

/**
 * Created by MY on 2017/7/20.
 */

public class IndexFragment extends Fragment implements View.OnClickListener{

    private View rootview;

    private ImageView title_index_playing_iv;
    private TextView title_index_musiclist_tv;
    private TextView title_index_recommend_tv;
    private TextView title_index_ranking_tv;

    private ViewPager mViewPager;
//    private FragmentPagerAdapter mFragmentPagerAdapter;
    private MFragmentPagerAdapter mFragmentPagerAdapter;
    private List<Fragment> mList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if(rootview == null){
//            //通过 LayoutInflater 的 inflate() 方法将 fragment_index 布局动态加载进来
//            View view = inflater.inflate(R.layout.fragment_index, container, false);
            rootview = inflater.inflate(R.layout.fragment_index, null);

            initView(rootview);

            init_ViewPager(rootview);

        }

        /*
         * 底部导航栏切换后 由于没有销毁顶部设置导致如果没有重新设置view
         * 导致底部切换后切回顶部页面数据会消失等bug
         * 以下设置每次重新创建view即可
         */
//        ViewGroup parent = (ViewGroup) rootview.getParent();
//        if (parent != null) {
//            parent.removeView(rootview);
//        }


        return rootview;
    }

    private void initView(View v){
        title_index_playing_iv = (ImageView) v.findViewById(R.id.title_index_playing_iv);
        title_index_playing_iv.setOnClickListener(this);

        title_index_musiclist_tv = (TextView) v.findViewById(R.id.title_index_musiclist_tv);
        title_index_musiclist_tv.setOnClickListener(this);
        title_index_recommend_tv = (TextView) v.findViewById(R.id.title_index_recommend_tv);
        title_index_recommend_tv.setOnClickListener(this);
        title_index_ranking_tv = (TextView) v.findViewById(R.id.title_index_ranking_tv);
        title_index_ranking_tv.setOnClickListener(this);

    }

    private void init_ViewPager(View v){
        mViewPager = (ViewPager) v.findViewById(R.id.viewpager_index);

        mList = new ArrayList<Fragment>();
        ChildFragment_Index_Musiclist childFragment_index_musiclist = new ChildFragment_Index_Musiclist();
        ChildFragment_Index_Recommend childFragment_index_recommend = new ChildFragment_Index_Recommend();
        ChildFragment_Index_Ranking childFragment_index_ranking = new ChildFragment_Index_Ranking();
        mList.add(childFragment_index_musiclist);
        mList.add(childFragment_index_recommend);
        mList.add(childFragment_index_ranking);

        //  配置适配器，两种写法选一种
//        mFragmentPagerAdapter = new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
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

//        mFragmentPagerAdapter = new MFragmentPagerAdapter(getActivity().getSupportFragmentManager(), mList);
        mFragmentPagerAdapter = new MFragmentPagerAdapter(getChildFragmentManager(), mList);
        //  两种 FragmentManager() 区别有待发现！！！

        mViewPager.setAdapter(mFragmentPagerAdapter);

        mViewPager.setCurrentItem(1);

        mViewPager.setOffscreenPageLimit(3);

//        mViewPager.setOnPageChangeListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.title_index_playing_iv :
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                getActivity().startActivity(intent);
                //getActivity().startActivity(new Intent().setClass(getActivity(), PlayerActivity.class));
                getActivity().overridePendingTransition(R.anim.translate_right_in, R.anim.translate_left_out);
                break;

            case R.id.title_index_musiclist_tv :
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
}
