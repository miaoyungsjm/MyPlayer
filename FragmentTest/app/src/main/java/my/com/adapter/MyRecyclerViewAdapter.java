package my.com.adapter;

import android.content.Context;
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
import my.com.model.MyMenu;

/**
 * Created by MY on 2017/8/16.
 *
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>{

    private List<MyMenu> mlist;

    private Context mcontext;

    private LocalBroadcastManager mLocalBroadcastManager;    //  本地广播管理


    private static final String TAG = "MyRecyclerViewAdapter";         //  调试信息 TAG 标签



    static class ViewHolder extends RecyclerView.ViewHolder{

        View itemView;

        ImageView my_item_pic_iv;
        TextView my_item_title_tv;
        TextView my_item_count_tv;

        public ViewHolder(View view){
            super(view);
            itemView = view;
            my_item_pic_iv = (ImageView) view.findViewById(R.id.my_item_pic_iv);
            my_item_title_tv = (TextView) view.findViewById(R.id.my_item_title_tv);
            my_item_count_tv = (TextView) view.findViewById(R.id.my_item_count_tv);
        }
    }

    /*
     *  构造函数
     */
    public MyRecyclerViewAdapter(List<MyMenu> list, Context context) {
        mlist = list;
        mcontext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_my_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                MyMenu mMyMenu = mlist.get(position);
                Toast.makeText(v.getContext(), mMyMenu.getTitle(), Toast.LENGTH_SHORT).show();


                // 实例化本地广播管理器，使用本地广播发送播放信息
                if(mLocalBroadcastManager == null) {
                    mLocalBroadcastManager = LocalBroadcastManager.getInstance(mcontext);
                }

                int jumpto = position+1;
                Intent intent = new Intent(BroadcastAction.MyFragmentAction);
                intent.putExtra("jumpto", jumpto);
                mLocalBroadcastManager.sendBroadcast(intent);
                Log.i(TAG," -- MyRecyclerViewAdapter : mLocalBroadcastManager.sendBroadcast(intent)\n" +
                        "    Jump To :" + jumpto );

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyMenu myMenu = mlist.get(position);
        holder.my_item_pic_iv.setImageResource(myMenu.getImageId());
        holder.my_item_title_tv.setText(myMenu.getTitle());
        holder.my_item_count_tv.setText(Integer.toString(myMenu.getCount()));
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }
}
