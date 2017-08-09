package my.com.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import my.com.R;

/**
 * Created by MY on 2017/7/20.
 */

public class FriendFragment extends Fragment {

    private View rootview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_friend,null);
        return rootview;
    }
}
