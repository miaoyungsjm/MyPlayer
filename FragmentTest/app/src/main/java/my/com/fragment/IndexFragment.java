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

public class IndexFragment extends Fragment implements View.OnClickListener{

    private View rootview;

    private ImageView title_index_playing;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_index,null);

        initView(rootview);

        return rootview;
    }

    private void initView(View v){
        title_index_playing = (ImageView) v.findViewById(R.id.title_index_playing);
        title_index_playing.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.title_index_playing :
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                getActivity().startActivity(intent);
                //getActivity().startActivity(new Intent().setClass(getActivity(), PlayerActivity.class));
                break;
        }
    }
}
