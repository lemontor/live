package com.example.user.live.video.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.user.live.R;
import com.example.user.live.utils.GlideUtils;
import com.example.user.live.video.entity.VideoEntity;

import java.io.File;
import java.util.List;

/**
 * Created by user on 2018/4/26.
 */
public class VideoGvAdapter extends BaseAdapter {

    private List<VideoEntity> entityList;
    private Context context;
    private LayoutInflater inflater;
    static int mGridWidth;
    private boolean all;

    public VideoGvAdapter(Context context, List<VideoEntity> entityList, boolean all) {
        this.all = all;
        Log.e("tag_all", all + ":" + entityList.toString());
        this.context = context;
        this.entityList = entityList;
        inflater = LayoutInflater.from(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        } else {
            width = wm.getDefaultDisplay().getWidth();
        }
        mGridWidth = width / 4;
    }


    @Override
    public int getCount() {
        Log.e("tag_grid_view",entityList.size()+"");
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
        if (view == null) {
            view = inflater.inflate(R.layout.item_pic_gv, null);
            viewHolderPic = new ViewHolderPic(view);
            view.setTag(viewHolderPic);
        } else {
            viewHolderPic = (ViewHolderPic) view.getTag();
        }
        GlideUtils.loadLocalBitmap(context, entityList.get(i).getPath(), viewHolderPic.ivPic, mGridWidth, mGridWidth);
//        viewHolderPic.ivPic.setImageBitmap(getVideoThumbnail(entityList.get(i).getPath(),mGridWidth,mGridWidth, MediaStore.Video.Thumbnails.MINI_KIND));
//        if (!all) {
//            if (entityList.get(i).isUp()) {
//                viewHolderPic.layout.setVisibility(View.GONE);
//            } else {
//                viewHolderPic.layout.setVisibility(View.VISIBLE);
//            }
//        } else {
//            viewHolderPic.layout.setVisibility(View.VISIBLE);
//        }
        Log.e("tag_up",i+" : "+entityList.get(i).isUp());
        if (all) {
            viewHolderPic.layout.setVisibility(View.VISIBLE);
        } else {
            if (entityList.get(i).isUp()) {
                viewHolderPic.layout.setVisibility(View.GONE);
            } else {
                viewHolderPic.layout.setVisibility(View.VISIBLE);
            }
        }

//        if (entityList.get(i).isUp()) {
//            if (all) {
//                viewHolderPic.layout.setVisibility(View.VISIBLE);
//            } else {
//                viewHolderPic.layout.setVisibility(View.GONE);
//            }
//        } else {
//            viewHolderPic.layout.setVisibility(View.VISIBLE);
//        }


        viewHolderPic.tvLen.setText(entityList.get(i).getDuration());
        if (entityList.get(i).isUp()) {
            viewHolderPic.ivIsUp.setVisibility(View.VISIBLE);
        } else {
            viewHolderPic.ivIsUp.setVisibility(View.GONE);
        }
        if (entityList.get(i).isChose()) {
            viewHolderPic.ivChose.setImageResource(R.mipmap.group);
        } else {
            viewHolderPic.ivChose.setImageResource(R.mipmap.oval);
        }
        final ViewHolderPic finalViewHolderPic = viewHolderPic;
        viewHolderPic.ivPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (entityList.get(i).isChose()) {//已选过
                    finalViewHolderPic.ivChose.setImageResource(R.mipmap.oval);
                    entityList.get(i).setChose(false);
                    if (onChoseListener != null) {
                        onChoseListener.onChose(i, false);
                    }
                } else {//未选过
                    finalViewHolderPic.ivChose.setImageResource(R.mipmap.group);
                    entityList.get(i).setChose(true);
                    if (onChoseListener != null) {
                        onChoseListener.onChose(i, true);
                    }
                }
            }
        });
        return view;
    }

    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        if(bitmap!= null){
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }


    static class ViewHolderPic {
        private ImageView ivPic;
        private ImageView ivChose;
        private RelativeLayout layout;
        private TextView tvLen;
        private ImageView ivIsUp;

        public ViewHolderPic(View view) {
            ivPic = (ImageView) view.findViewById(R.id.iv_pic);
            ivChose = (ImageView) view.findViewById(R.id.cb_chose);
            layout = (RelativeLayout) view.findViewById(R.id.layout_up);
            tvLen = (TextView) view.findViewById(R.id.tv_len);
            ivIsUp = (ImageView) view.findViewById(R.id.iv_is_up);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();
            layoutParams.width = mGridWidth;
            layoutParams.height = mGridWidth;
            layout.setLayoutParams(layoutParams);
        }
    }

    OnChoseListener onChoseListener;

    public void setOnChoseListener(OnChoseListener onChoseListener) {
        this.onChoseListener = onChoseListener;
    }

    public interface OnChoseListener {
        public void onChose(int poi, boolean isChose);
    }

    public void notifyDataChange() {
        notifyDataSetChanged();
    }

}
