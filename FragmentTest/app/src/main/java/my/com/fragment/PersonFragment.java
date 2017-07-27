package my.com.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import my.com.PlayerActivity;
import my.com.R;

/**
 * Created by MY on 2017/7/20.
 */

public class PersonFragment extends Fragment{

    private View rootview;

    private ImageView title_person_playing;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_person,null);

        initView(rootview);

        return rootview;
    }

    private void initView(View v){
        title_person_playing = (ImageView) v.findViewById(R.id.title_person_playing);
        title_person_playing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }
}
