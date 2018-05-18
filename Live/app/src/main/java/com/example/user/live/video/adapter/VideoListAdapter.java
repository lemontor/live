package com.example.user.live.video.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.user.live.R;
import com.example.user.live.video.entity.VideoEntity;
import com.example.user.live.video.entity.VideoTotalEntity;
import com.example.user.live.view.NoScroolGridView;
import java.util.List;

/**
 * Created by user on 2018/4/26.
 */
public class VideoListAdapter extends BaseAdapter {
    private List<List<VideoEntity>> videoTotalEntities;
    private Context context;
    private LayoutInflater inflater;

    public VideoListAdapter(Context context, List<List<VideoEntity>> videoTotalEntities) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.videoTotalEntities = videoTotalEntities;
    }

    @Override
    public int getCount() {
        return videoTotalEntities.size();
    }

    @Override
    public Object getItem(int i) {
        return videoTotalEntities.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder vh = null;
        if (view == null) {
            view = inflater.inflate(R.layout.item_video_list, null);
            vh = new ViewHolder(view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        if (videoTotalEntities.get(i).get(0).getTimeStatus() == 0) {
            vh.tvTitle.setText("今天");
        } else if (videoTotalEntities.get(i).get(0).getTimeStatus() == -1) {
            vh.tvTitle.setText("昨天");
        } else {
            vh.tvTitle.setText("其他");
        }
        VideoGvAdapter gvAdapter = new VideoGvAdapter(context, videoTotalEntities.get(i));
        vh.gvPic.setAdapter(gvAdapter);
        gvAdapter.setOnChoseListener(new VideoGvAdapter.OnChoseListener() {
            @Override
            public void onChose(int poi, boolean isChose) {
                if(onSendListener != null){
                    onSendListener.send(i,poi,isChose);
                }
            }
        });
        return view;
    }


    class ViewHolder {
        public TextView tvTitle;
        public NoScroolGridView gvPic;

        public ViewHolder(View view) {
            tvTitle = (TextView) view.findViewById(R.id.tv_date);
            gvPic = (NoScroolGridView) view.findViewById(R.id.gv_pic);
        }
    }

    OnSendListener onSendListener;
    public void  setOnSendListener(OnSendListener onSendListener){
        this.onSendListener = onSendListener;
    }


    public interface  OnSendListener{
        public void send(int poi,int childPoi,boolean isChose);
    }


}
