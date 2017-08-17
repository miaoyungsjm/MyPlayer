package my.com.fragment.childfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import my.com.R;

/**
 * Created by MY on 2017/8/8.
 */

public class ChildFragment_Index_Songlist extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.childfragment_index_songlist, null);

        return root;
    }
}
