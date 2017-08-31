package com.activitytest.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activitytest.FriendActivity;
import com.activitytest.IndexActivity;
import com.activitytest.MyActivity;
import com.activitytest.PersonActivity;
import com.activitytest.R;

/**
 * Created by MY on 2017/8/29.
 *
 */

public class NavigationFragment extends Fragment{

    View rootview;

    private LinearLayout navigation_index_ll, navigation_my_ll, navigation_friend_ll, navigation_person_ll;
    private ImageView navigation_index_iv, navigation_my_iv, navigation_friend_iv, navigation_person_iv;
    private TextView navigation_index_tv, navigation_my_tv, navigation_friend_tv, navigation_person_tv;

    private int position = -1;


    private static final String TAG = "NavigationFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, " ----- NavigationFragment : onCreateView()");

        rootview = inflater.inflate(R.layout.fragment_navigation, null);

        initView(rootview);

        position = getArguments().getInt("position");
        navigationSetSelected(position);
        Log.i(TAG, "    navigationSetSelected(position)  position = " + position);

        return rootview;
    }

    private void initView(View v){
        navigation_index_ll = (LinearLayout) v.findViewById(R.id.navigation_index_ll);
        navigation_my_ll = (LinearLayout) v.findViewById(R.id.navigation_my_ll);
        navigation_friend_ll = (LinearLayout) v.findViewById(R.id.navigation_friend_ll);
        navigation_person_ll = (LinearLayout) v.findViewById(R.id.navigation_person_ll);

        navigation_index_ll.setOnClickListener(mOnClickListener);
        navigation_my_ll.setOnClickListener(mOnClickListener);
        navigation_friend_ll.setOnClickListener(mOnClickListener);
        navigation_person_ll.setOnClickListener(mOnClickListener);

        navigation_index_iv = (ImageView) v.findViewById(R.id.navigation_index_iv);
        navigation_my_iv = (ImageView) v.findViewById(R.id.navigation_my_iv);
        navigation_friend_iv = (ImageView) v.findViewById(R.id.navigation_friend_iv);
        navigation_person_iv = (ImageView) v.findViewById(R.id.navigation_person_iv);

        navigation_index_tv = (TextView) v.findViewById(R.id.navigation_index_tv);
        navigation_my_tv = (TextView) v.findViewById(R.id.navigation_my_tv);
        navigation_friend_tv = (TextView) v.findViewById(R.id.navigation_friend_tv);
        navigation_person_tv = (TextView) v.findViewById(R.id.navigation_person_tv);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        Intent intent;
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.navigation_index_ll:
                    if (position == 0)break;
                    intent = new Intent(getActivity(), IndexActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                    break;

                case R.id.navigation_my_ll:
                    if (position == 1)break;
                    intent = new Intent(getActivity(), MyActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                    break;

                case R.id.navigation_friend_ll:
                    if (position == 2)break;
                    intent = new Intent(getActivity(), FriendActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                    break;

                case R.id.navigation_person_ll:
                    if (position == 3)break;
                    intent = new Intent(getActivity(), PersonActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                    break;
            }
        }
    };


    private void navigationSetSelected(int to){
        if (to == 0){
            navigation_index_iv.setSelected(true);
            navigation_index_tv.setSelected(true);
        }
        if (to == 1){
            navigation_my_iv.setSelected(true);
            navigation_my_tv.setSelected(true);
        }
        if (to == 2){
            navigation_friend_iv.setSelected(true);
            navigation_friend_tv.setSelected(true);
        }
        if (to == 3){
            navigation_person_iv.setSelected(true);
            navigation_person_tv.setSelected(true);
        }
    }

}
