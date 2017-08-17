package my.com.adapter;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import my.com.R;
import my.com.action.BroadcastAction;
import my.com.model.MyMain;

/**
 * Created by MY on 2017/8/16.
 *
 */

public class MyMainRecyclerViewAdapter extends RecyclerView.Adapter<MyMainRecyclerViewAdapter.ViewHolder>{

    private List<MyMain> mlist;

    private LocalBroadcastManager mLocalBroadcastManager;    //  本地广播管理


    private static final String TAG = "RecyclerViewAdapter";         //  调试信息 TAG 标签


    static class ViewHolder extends RecyclerView.ViewHolder{

        View itemView;

        ImageView my_main_item_pic_iv;
        TextView my_main_item_title_tv;
        TextView my_main_item_count_tv;

        public ViewHolder(View view){
            super(view);
            itemView = view;
            my_main_item_pic_iv = (ImageView) view.findViewById(R.id.my_main_item_pic_iv);
            my_main_item_title_tv = (TextView) view.findViewById(R.id.my_main_item_title_tv);
            my_main_item_count_tv = (TextView) view.findViewById(R.id.my_main_item_count_tv);
        }
    }

    /*
     *  构造函数
     */
    public MyMainRecyclerViewAdapter(List<MyMain> list, LocalBroadcastManager localBroadcastManager) {
        mlist = list;
        mLocalBroadcastManager = localBroadcastManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_my_main_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                MyMain mMyMain = mlist.get(position);
                Toast.makeText(v.getContext(), mMyMain.getTitle(), Toast.LENGTH_SHORT).show();


                int jumpto = position+1;
                Intent intent = new Intent(BroadcastAction.MyFragmentAction);
                intent.putExtra("jumpto", jumpto);

                mLocalBroadcastManager.sendBroadcast(intent);
                Log.i(TAG, " -- MyMainRecyclerViewAdapter : mLocalBroadcastManager.sendBroadcast(intent)  " +
                        "  Jump To :" + jumpto );

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyMain mMyMain = mlist.get(position);
        holder.my_main_item_pic_iv.setImageResource(mMyMain.getImageId());
        holder.my_main_item_title_tv.setText(mMyMain.getTitle());
        holder.my_main_item_count_tv.setText(Integer.toString(mMyMain.getCount()));
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }
}
