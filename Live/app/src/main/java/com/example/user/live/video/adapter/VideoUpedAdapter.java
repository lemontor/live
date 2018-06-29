package com.example.user.live.video.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.live.R;
import com.example.user.live.utils.GlideUtils;
import com.example.user.live.video.entity.VideoEntity;
import com.example.user.live.video.entity.VideoUpInfoBean;

import java.util.List;

/**
 * Created by user on 2018/5/21.
 */
public class VideoUpedAdapter extends BaseAdapter{

    private List<VideoUpInfoBean.VideoBean> entityList;
    private Context context;
    private LayoutInflater inflater;
    static int mGridWidth;

    public VideoUpedAdapter(Context context, List<VideoUpInfoBean.VideoBean>  entityList){
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
        GlideUtils.loadLocalPic(context,entityList.get(i).getPath(),viewHolderPic.ivPic);
//        viewHolderPic.ivPic.setImageBitmap(getVideoThumbnail(entityList.get(i).getPath(),mGridWidth,mGridWidth, MediaStore.Video.Thumbnails.MINI_KIND));

        viewHolderPic.tvTitle.setText(entityList.get(i).getFile_name());
        viewHolderPic.tvSize.setText(entityList.get(i).getShow_size());
        viewHolderPic.tvLen.setText(getTime(entityList.get(i).getVideo_time()));
        viewHolderPic.tvDate.setText(entityList.get(i).getCreate_time());
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

    public static String getTime(int second) {
        if (second > 60) {
            int minte = second / 60;
            int tempSecond = second % 60;
            String zeroMinte = "";
            if (minte < 10) {
                zeroMinte = "0" + minte + ":";
            } else {
                zeroMinte = String.valueOf(minte + ":");
            }
            String zeroSecond = "";
            if (tempSecond < 10) {
                zeroSecond = "0" + tempSecond + "";
            }else{
                zeroSecond = String.valueOf(tempSecond);
            }
            return zeroMinte + " " + zeroSecond;
        } else {
            if (second >= 10) {
                return "00:" + String.valueOf(second);
            } else {
                return "00:0" + String.valueOf(second);
            }
        }
    }


    static class ViewHolderPic{
        private ImageView ivPic;
        private TextView  tvTitle,tvSize,tvLen,tvDate;

        public ViewHolderPic(View view) {
            ivPic = (ImageView) view.findViewById(R.id.iv_pic);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvSize = (TextView) view.findViewById(R.id.tv_size);
            tvLen = (TextView) view.findViewById(R.id.tv_second);
            tvDate = (TextView) view.findViewById(R.id.tv_date);

        }
    }

    OnChoseListener onChoseListener;
    public  void setOnChoseListener(OnChoseListener onChoseListener){
        this.onChoseListener = onChoseListener;
    }

    public  interface   OnChoseListener{
        public void onChose(int poi, boolean isChose);
    }

}
