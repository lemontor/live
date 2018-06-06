package com.example.user.live.video.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.live.R;
import com.example.user.live.utils.GlideUtils;
import com.example.user.live.video.entity.VideoEntity;
import com.example.user.live.view.CustomProgressBar;

import java.util.List;

/**
 * Created by user on 2018/6/5.
 */
public class VideoUpAdapter extends RecyclerView.Adapter<VideoUpAdapter.UpViewHolder> {

    private Context context;
    private List<VideoEntity> videoEntityList;
    private LayoutInflater inflater;

    public VideoUpAdapter(Context context, List<VideoEntity> videoEntityList, OnStartClickListener onStartClickListener) {
        this.context = context;
        this.videoEntityList = videoEntityList;
        this.onStartClickListener = onStartClickListener;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public UpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UpViewHolder(inflater.inflate(R.layout.item_video_upload, null));
    }

    @Override
    public void onBindViewHolder(UpViewHolder holder, final int position) {
        GlideUtils.loadLocalPic(context, videoEntityList.get(position).getThumbPath(), holder.ivPic);
        holder.tvTitle.setText(videoEntityList.get(position).getTitle());
        holder.tvTotalProgress.setText("/" + videoEntityList.get(position).getSize());
        holder.tvLoadingProgress.setText(String.valueOf(videoEntityList.get(position).getProgress()));
        holder.pbLoading.setPercent(position * 10 + 20);
        if (videoEntityList.get(position).getStatus() == 2) {//正在上传
            holder.tvLoadingStatus.setText("正在下载");
            holder.ivStart.setImageResource(R.mipmap.spsc_zhengzaishangchuan);
        } else if (videoEntityList.get(position).getStatus() == 6) {//暂停
            holder.tvLoadingStatus.setText("已暂停");
            holder.ivStart.setImageResource(R.mipmap.spsc_zanting);
        } else {//等待中
            holder.tvLoadingStatus.setText("等待中...");
            holder.ivStart.setImageResource(R.mipmap.spsc_dengdaishangchuan);
        }
        holder.ivStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoEntityList.get(position).getStatus() == 2) {
                    onStartClickListener.onStop(2, position, 6);//变为暂停
                } else if (videoEntityList.get(position).getStatus() == 6) {
                    onStartClickListener.onStart(2, position, 2);//变为开始
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoEntityList.size();
    }

    public class UpViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPic, ivStart;
        private TextView tvTitle, tvLoadingProgress, tvTotalProgress, tvLoadingStatus;
        private CustomProgressBar pbLoading;

        public UpViewHolder(View itemView) {
            super(itemView);
            ivPic = (ImageView) itemView.findViewById(R.id.iv_pic);
            ivStart = (ImageView) itemView.findViewById(R.id._iv_start);

            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvLoadingProgress = (TextView) itemView.findViewById(R.id.tv_load);
            tvTotalProgress = (TextView) itemView.findViewById(R.id.tv_total);
            tvLoadingStatus = (TextView) itemView.findViewById(R.id.tv_status);

            pbLoading = (CustomProgressBar) itemView.findViewById(R.id.pb_progress);
        }
    }

    OnStartClickListener onStartClickListener;

    public interface OnStartClickListener {
        public void onStart(int type, int position, int status);

        public void onStop(int type, int position, int status);
    }


}
