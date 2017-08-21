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

import java.util.List;

import my.com.R;
import my.com.action.BroadcastAction;
import my.com.model.MyMain;
import my.com.model.PlayInfo;

/**
 * Created by MY on 2017/8/16.
 *
 */

public class MyLocalRecyclerViewAdapter extends RecyclerView.Adapter<MyLocalRecyclerViewAdapter.ViewHolder>{

    private List<PlayInfo> mlist;

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
        mlist = list;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_my_local_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                PlayInfo playInfo = mlist.get(position);
                Toast.makeText(v.getContext(), playInfo.getName(), Toast.LENGTH_SHORT).show();


//                int jumpto = position+1;
//                Intent intent = new Intent(BroadcastAction.MyFragmentAction);
//                intent.putExtra("jumpto", jumpto);
//
//
//                if(mLocalBroadcastManager == null){
//                    mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
//                }
//                mLocalBroadcastManager.sendBroadcast(intent);
//                Log.i(TAG, " -- MyMainRecyclerViewAdapter : mLocalBroadcastManager.sendBroadcast(intent)  " +
//                        "  Jump To :" + jumpto );

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlayInfo playInfo = mlist.get(position);
        holder.my_local_item_name_tv.setText(playInfo.getName());
        holder.my_local_item_singer_tv.setText(playInfo.getSinger());
        holder.my_local_item_size_tv.setText(Long.toString(playInfo.getSize()) + "M");
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }
}
