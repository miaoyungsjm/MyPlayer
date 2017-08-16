package my.com.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by MY on 2017/8/11.
 */

public class MFragmentPagerAdapter extends FragmentPagerAdapter{

    List<Fragment> mlist;

    public MFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.mlist = list;
    }

    @Override
    public Fragment getItem(int position) {
        //  根据Item的位置返回对应位置的Fragment，绑定item和Fragment
        return mlist.get(position);
    }

    @Override
    public int getCount() {
        //  设置Item的数量
        return mlist.size();
    }
}
