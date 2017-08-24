package my.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import my.com.R;
import my.com.model.PlayInfo;
import my.com.service.PlayerService;
import my.com.utils.MusicUtils;

/**
 * Created by MY on 2017/8/16.
 *
 */

public class MyLocalRecyclerViewAdapter extends RecyclerView.Adapter<MyLocalRecyclerViewAdapter.ViewHolder>{

    private List<PlayInfo> mList;

    private Context mContext;

    private LocalBroadcastManager mLocalBroadcastManager;    //  本地广播管理


    private static final String TAG = "RecyclerViewAdapter";         //  调试信息 TAG 标签


    static class ViewHolder extends RecyclerView.ViewHolder{

        View itemView;

        TextView my_local_item_name_tv;
        TextView my_local_item_singer_tv;
        TextView my_local_item_size_tv;

        public ViewHolder(View view){
            super(view);
            itemView = view;
            my_local_item_name_tv = (TextView) view.findViewById(R.id.my_local_item_name_tv);
            my_local_item_singer_tv = (TextView) view.findViewById(R.id.my_local_item_singer_tv);
            my_local_item_size_tv = (TextView) view.findViewById(R.id.my_local_item_size_tv);
        }
    }

    /*
     *  构造函数
     */
    public MyLocalRecyclerViewAdapter(List<PlayInfo> list, Context context) {
        mList = list;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_my_local_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            PlayInfo tPlayInfo;
            @Override
            public void onClick(View v) {

                List<PlayInfo> tList = new ArrayList<>();
                tList = MusicUtils.scanLocalMusic(mContext);
                MusicUtils.updatePlayList(tList);

                for (int i = 0 ; i < mList.size() ; i++){
                    tPlayInfo = mList.get(i);
                    tPlayInfo.mState = false;
                }

                int position = holder.getAdapterPosition();
                MusicUtils.setPlayPosition(position);
                tPlayInfo = mList.get(position);
                tPlayInfo.mState = true;
                Log.d(TAG, "  position = " + position);
                Toast.makeText(v.getContext(), tPlayInfo.getName(), Toast.LENGTH_SHORT).show();

                //  重启服务
                Intent intent = new Intent(mContext, PlayerService.class);
                mContext.startService(intent);
                Log.d(TAG, "  startService(intent)");


                notifyDataSetChanged();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlayInfo tPlayInfo = mList.get(position);
        holder.my_local_item_name_tv.setText(tPlayInfo.getName());
        holder.my_local_item_singer_tv.setText(tPlayInfo.getSinger());
        holder.my_local_item_size_tv.setText(Long.toString(tPlayInfo.getSize()/1000000) + "M");

        if(tPlayInfo.getState()){
            holder.my_local_item_name_tv.setSelected(true);
        }else {
            holder.my_local_item_name_tv.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
