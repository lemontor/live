package com.example.user.live.video.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.user.live.R;
import com.example.user.live.video.entity.VideoEntity;
import com.example.user.live.video.entity.VideoTotalEntity;
import com.example.user.live.view.NoScroolGridView;
import java.security.cert.PolicyNode;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2018/4/26.
 */
public class VideoListAdapter extends BaseAdapter {
    private List<VideoTotalEntity> videoTotalEntities;
    private Context context;
    private LayoutInflater inflater;

    public VideoListAdapter(Context context, List<VideoTotalEntity> videoTotalEntities) {
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
        if (videoTotalEntities.get(i).getTitle().equals("0")) {
            vh.tvTitle.setText("今天");
        } else if (videoTotalEntities.get(i).getTitle().equals("-1")) {
            vh.tvTitle.setText("昨天");
        } else {
            vh.tvTitle.setText(videoTotalEntities.get(i).getTitle());
        }

        if (videoTotalEntities.get(i).isChose()) {
            Drawable leftDrawable = context.getResources().getDrawable(R.mipmap.group);
            vh.tvTitle.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
        } else {
            Drawable leftDrawable = context.getResources().getDrawable(R.mipmap.oval);
            vh.tvTitle.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
        }

        final VideoGvAdapter gvAdapter = new VideoGvAdapter(context, videoTotalEntities.get(i).getVideoEntities());
        vh.gvPic.setAdapter(gvAdapter);
        gvAdapter.setOnChoseListener(new VideoGvAdapter.OnChoseListener() {
            @Override
            public void onChose(int poi, boolean isChose) {
                if (onSendListener != null) {
                    onSendListener.send(i, poi, isChose);
                }
            }
        });
        final ViewHolder finalVh = vh;
        vh.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoTotalEntities.get(i).isChose()) {//已经选中
                    Drawable leftDrawable = context.getResources().getDrawable(R.mipmap.oval);
                    finalVh.tvTitle.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
                    if (onSendListener != null) {
                        onSendListener.choseAll(i, false);
                    }
                } else {
                    Drawable leftDrawable = context.getResources().getDrawable(R.mipmap.group);
                    finalVh.tvTitle.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
                    if (onSendListener != null) {
                        onSendListener.choseAll(i, true);
                    }
                }
                gvAdapter.notifyDataChange();
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

    public void setOnSendListener(OnSendListener onSendListener) {
        this.onSendListener = onSendListener;
    }


    public interface OnSendListener {
        public void send(int poi, int childPoi, boolean isChose);

        public void choseAll(int poi, boolean isChose);
    }


}
