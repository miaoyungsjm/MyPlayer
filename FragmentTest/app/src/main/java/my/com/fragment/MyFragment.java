package my.com.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import my.com.PlayerActivity;
import my.com.R;

/**
 * Created by MY on 2017/7/20.
 */

public class MyFragment extends Fragment {

    private View rootview;

    private ImageView title_my_playing;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_my,null);

        initView(rootview);

        return rootview;
    }

    private void initView(View v){
        title_my_playing = (ImageView) v.findViewById(R.id.title_my_playing);

        title_my_playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }
}
