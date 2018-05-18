package com.example.user.live.live;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.user.live.R;
import com.example.user.live.bean.CommentBean;

import java.util.List;

/**
 * Created by user on 2018/1/12.
 */
public class CommentAdapter extends BaseAdapter{

    private Context  mContext;
    private LayoutInflater  inflater;
    private List<CommentBean.commentListBean>   mData;
    private String teacherName;

    public CommentAdapter(Context  context,List<CommentBean.commentListBean>   data,String teacherName){
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.mData = data;
        this.teacherName = teacherName;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemViewHolder  viewHolder;
        if(view == null){
            view = inflater.inflate(R.layout.item_content,null);
            viewHolder = new ItemViewHolder();
            viewHolder.tvContent = (TextView) view.findViewById(R.id.tv_content);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ItemViewHolder) view.getTag();
        }

        viewHolder.tvContent.setText(mData.get(i).getNameContent());
        if("".equals(teacherName)){
            if(teacherName.equals(mData.get(i).getCreate_user())){
                viewHolder.tvContent.setBackgroundResource(R.drawable.bg_live_content_me);
            }else{
                viewHolder.tvContent.setBackgroundResource(R.drawable.bg_live_content);
            }
        }else{
            viewHolder.tvContent.setBackgroundResource(R.drawable.bg_live_content);
        }
        return view;
    }


    public static  class  ItemViewHolder{
        public TextView  tvContent;
    }

}
