package com.example.user.live.video.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.live.R;
import com.example.user.live.utils.GlideUtils;
import com.example.user.live.video.entity.VideoEntity;

import java.util.List;

/**
 * Created by user on 2018/5/21.
 */
public class VideoFailAdapter extends BaseAdapter{

    private List<VideoEntity> entityList;
    private Context context;
    private LayoutInflater inflater;

    public VideoFailAdapter(Context context, List<VideoEntity>  entityList){
        this.context = context;
        this.entityList = entityList;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return entityList.size();
    }

    @Override
    public Object getItem(int i) {
        return entityList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolderPic viewHolderPic = null;
        if(view == null){
            view = inflater.inflate(R.layout.item_video,null);
            viewHolderPic = new ViewHolderPic(view);
            view.setTag(viewHolderPic);
        }else{
            viewHolderPic = (ViewHolderPic) view.getTag();
        }
        GlideUtils.loadLocalPic(context,entityList.get(i).getThumbPath(),viewHolderPic.ivPic);
        viewHolderPic.tvTitle.setText(entityList.get(i).getTitle());
        viewHolderPic.tvSize.setText(entityList.get(i).getSize());
        viewHolderPic.tvLen.setText(entityList.get(i).getDuration());
        viewHolderPic.tvDate.setText(entityList.get(i).getTimeStatus());
        viewHolderPic.ivReset.setVisibility(View.VISIBLE);
        viewHolderPic.ivReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onResetListener != null){
                    onResetListener.onReset(i);
                }
            }
        });
        return view;
    }



    static class ViewHolderPic{
        private ImageView ivPic;
        private TextView  tvTitle,tvSize,tvLen,tvDate;
        private ImageView ivReset;

        public ViewHolderPic(View view) {
            ivPic = (ImageView) view.findViewById(R.id.iv_pic);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvSize = (TextView) view.findViewById(R.id.tv_size);
            tvLen = (TextView) view.findViewById(R.id.tv_second);
            tvDate = (TextView) view.findViewById(R.id.tv_date);
            ivReset = (ImageView) view.findViewById(R.id.iv_reset);
        }
    }

    OnResetListener onResetListener;
    public  void setOnChoseListener(OnResetListener onResetListener){
        this.onResetListener = onResetListener;
    }

    public  interface   OnResetListener{
        public void onReset(int poi);
    }

}
