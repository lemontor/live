package com.example.user.live.video;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.live.R;
import com.example.user.live.app.App;
import com.example.user.live.utils.GetVideoDataUtils;
import com.example.user.live.utils.ToastUtils;
import com.example.user.live.video.adapter.VideoListAdapter;
import com.example.user.live.video.entity.UpdateVideoBean;
import com.example.user.live.video.entity.V;
import com.example.user.live.video.entity.VideoEntity;
import com.example.user.live.video.entity.VideoTotalEntity;
import com.example.user.live.video.entity.VideoUpInfoBean;
import com.example.user.live.video.upload.VideoUpLoadActivity;
import com.example.user.live.view.LoadDialog;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2018/4/25.
 */
public class VideoList extends Activity {

    private ListView mLvVideo;
    private LoadDialog loadDialog;
    private VideoListAdapter adapter;
    private List<VideoTotalEntity> videoData;
    private Map<Integer, List<VideoEntity>> cancelData;
    private TextView tvTitle, tvUp, tvBack;
    private SwitchCompat switchCompat;
    private List<VideoUpInfoBean.VideoBean> videoUpInfoBeanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        EventBus.getDefault().register(this);
        initUI();
        initObj();
        initData();
        initListener();
    }

    private void initListener() {
        adapter.setOnSendListener(new VideoListAdapter.OnSendListener() {
            @Override
            public void send(int poi, int childPoi, boolean isChose) {
                //更新状态
                videoData.get(poi).getVideoEntities().get(childPoi).setChose(isChose);
                if (!isChose) {
                    if (videoData.get(poi).isChose()) {
                        videoData.get(poi).setChose(isChose);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    boolean chose = choseVideoAll(videoData.get(poi).getVideoEntities());
                    if (chose) {
                        videoData.get(poi).setChose(true);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void choseAll(int poi, boolean isChose) {
                if (videoData != null && videoData.size() > 0) {
                    VideoTotalEntity totalEntity = videoData.get(poi);
                    totalEntity.setChose(isChose);
                    for (VideoEntity entity : totalEntity.getVideoEntities()) {
                        entity.setChose(isChose);
                    }
                }
            }
        });
        tvUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<VideoEntity> data = getUpLoadData();
                if (data != null && data.size() > 0) {
                    VideoTotalEntity totalEntity = new VideoTotalEntity();
                    totalEntity.setVideoEntities(data);
                    totalEntity.getVideoEntities().get(0).setStatus(7);
                    Intent intent = new Intent(VideoList.this, VideoUpLoadActivity.class);
                    intent.putExtra("video", totalEntity);
                    intent.putExtra("has", 1);
                    startActivity(intent);
                } else {
                    if (App.upCount == 0) {
                        ToastUtils.showToast(VideoList.this, "请选择视频");
                    } else {
                        Intent intent = new Intent(VideoList.this, VideoUpLoadActivity.class);
                        intent.putExtra("has", 2);
                        startActivity(intent);
                    }

                }
            }
        });
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.e("tag_switch", b + "");
                if (b) {
                    adapter.getUpLoad(false);
                } else {
                    adapter.getUpLoad(true);
                }
            }
        });
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    private boolean choseVideoAll(List<VideoEntity> entityList) {
        for (VideoEntity videoEntity : entityList) {
            if (!videoEntity.isChose()) {
                return false;
            }
        }
        return true;
    }

    private void initObj() {
        tvTitle.setText("选择视频");
        tvUp.setText("上传");
        tvUp.setTextColor(getResources().getColor(R.color.color_34A446));
        videoData = new ArrayList<>();
        cancelData = new HashMap<>();
        loadDialog = new LoadDialog(this);
        adapter = new VideoListAdapter(this, videoData);
        mLvVideo.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(Map<String, List<VideoEntity>> data) {
        if (loadDialog.isShow()) {
            loadDialog.dismissDialog();
        }
        if (data != null && data.size() > 0) {
            videoData.clear();
            List<VideoTotalEntity> temp = orgData(data);
            if (videoUpInfoBeanList.size() == 0) {
                if (temp != null && temp.size() > 0) {
                    videoData.addAll(temp);
                    adapter.notifyDataSetChanged();
                }
            } else {
                List<VideoTotalEntity> findInfo = findData(temp);
                if (findInfo != null && findInfo.size() > 0) {
                    videoData.addAll(findInfo);
                    adapter.notifyDataSetChanged();
                }
            }
            Log.e("tag_videoData",videoData.toString());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceive(V v) {
        if (v != null) {
            for (VideoTotalEntity videoTotalEntity : videoData) {
                for (VideoEntity videoEntity : videoTotalEntity.getVideoEntities()) {
                    if (videoEntity.getTitle().contains(v.getFileName())) {
                        videoEntity.setUp(true);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateVideoData(UpdateVideoBean updateVideoBean){
        if(updateVideoBean != null){

        }
    }

    public void updateInfo(UpdateVideoBean updateVideoBean){
        for(VideoTotalEntity videoTotalEntity : videoData){
             for(VideoEntity videoEntity : videoTotalEntity.getVideoEntities()){
                 if(videoEntity.getTitle().equals(updateVideoBean.getFileName())){
                     videoEntity.setFail(true);
                     break;
                 }
             }
        }
    }


    private List<VideoTotalEntity> orgData(Map<String, List<VideoEntity>> data) {
        List<VideoTotalEntity> totalEntityList = new ArrayList<>();
        for (Map.Entry<String, List<VideoEntity>> entry : data.entrySet()) {
            VideoTotalEntity videoTotalEntity = new VideoTotalEntity();
            videoTotalEntity.setTitle(entry.getKey());
            videoTotalEntity.setVideoEntities(entry.getValue());
            totalEntityList.add(videoTotalEntity);
        }
        return totalEntityList;
    }


    private void initData() {
        Drawable leftDrawable = getResources().getDrawable(R.mipmap.fanhui);
        tvBack.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
        VideoUpInfoBean stickyEvent = EventBus.getDefault().getStickyEvent(VideoUpInfoBean.class);
        if (stickyEvent != null) {
            if (stickyEvent.getData() != null && stickyEvent.getData().size() > 0) {
                videoUpInfoBeanList.addAll(stickyEvent.getData());
            }
        }
        getVideoData();
    }

    private void initUI() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvUp = (TextView) findViewById(R.id.tv_set);
        tvBack = (TextView) findViewById(R.id.tv_back);
        mLvVideo = (ListView) findViewById(R.id.lv_video);
        switchCompat = (SwitchCompat) findViewById(R.id.switch_single);
        View emptyView = findViewById(R.id.layout_empty);
        mLvVideo.setEmptyView(emptyView);
    }


    private void getVideoData() {
        loadDialog.showDialog();
        new Thread() {
            @Override
            public void run() {
                super.run();
                Map<String, List<VideoEntity>> lists = GetVideoDataUtils.getList(VideoList.this);
                EventBus.getDefault().post(lists);
            }
        }.start();
    }

    private List<VideoEntity> getUpLoadData() {
        List<VideoEntity> videoEntityList = new ArrayList<>();
        for (VideoTotalEntity entity : videoData) {
            if (entity.isChose()) {
                videoEntityList.addAll(entity.getVideoEntities());
            } else {
                for (VideoEntity videoEntity : entity.getVideoEntities()) {
                    if (videoEntity.isChose()) {
                        videoEntityList.add(videoEntity);
                    }
                }
            }
        }
        return videoEntityList;
    }


    private List<VideoTotalEntity> findData(List<VideoTotalEntity> temp) {
        for (VideoUpInfoBean.VideoBean videoBean : videoUpInfoBeanList) {
            for (int i = 0; i < temp.size(); i++) {
                VideoTotalEntity videoTotalEntity = temp.get(i);
                if (findTheUpLoadVideo(videoTotalEntity, videoBean)) {
                    break;
                }
            }
        }
        return temp;
    }

    /*
    寻找已经上传的文件
     */
    private boolean findTheUpLoadVideo(VideoTotalEntity videoTotalEntity, VideoUpInfoBean.VideoBean videoBean) {
        for (int j = 0; j < videoTotalEntity.getVideoEntities().size(); j++) {
            VideoEntity videoEntity = videoTotalEntity.getVideoEntities().get(j);
            if (videoEntity.getPath().contains(videoBean.getFile_name())) {
                videoTotalEntity.getVideoEntities().get(j).setUp(true);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
