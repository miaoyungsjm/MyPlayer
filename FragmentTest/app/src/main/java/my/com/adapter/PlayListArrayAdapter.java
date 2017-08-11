package my.com.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import my.com.Data.PlayInfo;
import my.com.R;

/**
 * Created by MY on 2017/8/11.
 */

public class PlayListArrayAdapter extends ArrayAdapter<PlayInfo>{

    private int resourceId;

    public PlayListArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<PlayInfo> objects) {
        super(context, resource, objects);

        resourceId = resource;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        PlayInfo playInfo = getItem(position);      //  获取当前的 PlayInfo 实例

        //  加载并缓存布局
        View view ;
        ViewHolder viewHolder;
        if(convertView == null){        //  提升 ListView 的运行效率

            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.playinfo_name_tv = (TextView) view.findViewById(R.id.playinfo_name_tv);
//            viewHolder.playinfo_singer_tv = (TextView) view.findViewById(R.id.playinfo_singer_tv);

            //  将 ViewHolder 存储在 View 中
            view.setTag(viewHolder);

        }else {
            view = convertView;

            //  重新获取 ViewHolder
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.playinfo_name_tv.setText(playInfo.getmName());
//        viewHolder.playinfo_singer_tv.setText(playInfo.getSinger());

        return view;
    }

    class ViewHolder {
        TextView playinfo_name_tv;
        TextView playinfo_singer_tv;
    }
}
