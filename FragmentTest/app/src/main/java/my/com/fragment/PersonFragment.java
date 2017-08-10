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

public class PersonFragment extends Fragment {

    private View rootview;

    private ImageView title_person_playing_iv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_person,null);

        initView(rootview);

        return rootview;
    }

    private void initView(View v){
        title_person_playing_iv = (ImageView) v.findViewById(R.id.title_person_playing_iv);
        title_person_playing_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
            }
        });
    }
}
