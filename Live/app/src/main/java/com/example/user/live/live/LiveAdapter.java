package com.example.user.live.live;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.live.R;

/**
 * Created by user on 2018/4/24.
 */
public class LiveAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;

    public LiveAdapter(Context context){
           inflater = LayoutInflater.from(context);
    }



    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh = null;
        if(view == null){
            view = inflater.inflate(R.layout.layout_live_item,null);
            vh = new ViewHolder();
            vh.ivPic = (ImageView) view.findViewById(R.id.iv_pic);

            vh.tvTitle = (TextView) view.findViewById(R.id.tv_title);
            vh.tvOrg = (TextView) view.findViewById(R.id.tv_org);
            vh.tvCount = (TextView) view.findViewById(R.id.tv_count);
            vh.tvStatus = (TextView) view.findViewById(R.id.tv_status);
            view.setTag(vh);
        }else{
            vh = (ViewHolder) view.getTag();
        }
        return view;
    }


    static class ViewHolder{
        private ImageView ivPic;
        private TextView  tvTitle,tvOrg,tvCount,tvStatus;
    }


}
