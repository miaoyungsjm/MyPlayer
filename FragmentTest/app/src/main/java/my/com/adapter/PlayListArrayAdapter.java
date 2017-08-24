package my.com.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import my.com.model.PlayInfo;
import my.com.R;
import my.com.utils.MusicUtils;

/**
 * Created by MY on 2017/8/11.
 *
 */

public class PlayListArrayAdapter extends ArrayAdapter<PlayInfo>{

    private int resourceId;     //  记录布局 ID

    private List<PlayInfo> mList;       //  记录传入的 List<PlayInfo>

    //  内部类，用于提高 ListView 的性能：记录被缓冲的 View 绑定的控件，无需重新查找
    private class ViewHolder {
        TextView playinfo_name_tv;
        TextView playinfo_singer_tv;
        ImageView playinfo_cancel_iv;
    }


    private static final String TAG = "PlayListArrayAdapter";         // 调试信息 TAG 标签


    //  构造函数
    public PlayListArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<PlayInfo> objects) {
        super(context, resource, objects);

        resourceId = resource;

        mList = objects;

    }

    /*
     *  “标准写法”
     *
     *  重写 ListView - item 的获取方法，ListView 的滚动、删除项、更新项内容 都需调用这函数
     */
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.d(TAG, " ----- PlayListArrayAdapter : getView()  " + position+ "  " + convertView);

        //  获取当前的 PlayInfo 实例（有点疑问）？？？
        PlayInfo playInfo = getItem(position);

        View view;
        ViewHolder viewHolder;

        //  加载并缓存布局，提升 ListView 的运行效率
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.playinfo_name_tv = (TextView) view.findViewById(R.id.playinfo_name_tv);
            viewHolder.playinfo_singer_tv = (TextView) view.findViewById(R.id.playinfo_singer_tv);
            viewHolder.playinfo_cancel_iv = (ImageView) view.findViewById(R.id.playinfo_cancel_iv);

            view.setTag(viewHolder);

        }else {
            view = convertView;     //  重获缓冲的 view

            viewHolder = (ViewHolder) view.getTag();        //  重获 view 中绑定的控件 ViewHolder
        }

        //  从 model 的 PlayInfo 类获取对应 playInfo 对象的数据，修改 item 的信息
        viewHolder.playinfo_name_tv.setText(playInfo.getName());
        viewHolder.playinfo_singer_tv.setText(playInfo.getSinger());

        //  依据 playInfo 对象的 mState 值，判定是否被选中。如被选中，字体 background 改变
        if(playInfo.getState()){
            viewHolder.playinfo_name_tv.setSelected(true);
        }else {
            viewHolder.playinfo_name_tv.setSelected(false);
        }

        //  为取消按钮设置点击事件，点击则将其在传入的数据源 mList 中移除，并刷新适配器
        viewHolder.playinfo_cancel_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, " -------------------------------------- Delete position : " + position);

                int mPlayPosition = MusicUtils.getPlayPosition();
                if(position <= mPlayPosition)MusicUtils.setPlayPosition(mPlayPosition-1);      //  更新对应的播放位置
                mList.remove(position);
                mList = MusicUtils.updatePlayList(mList);       //  更新 MusicUtils 类播放列表

                notifyDataSetChanged();     //  更新适配器
            }
        });

        return view;
    }

}
