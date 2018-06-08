package com.example.user.live.video;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.live.R;
import com.example.user.live.utils.GetVideoDataUtils;
import com.example.user.live.utils.ToastUtils;
import com.example.user.live.video.adapter.VideoListAdapter;
import com.example.user.live.video.entity.VideoEntity;
import com.example.user.live.video.entity.VideoTotalEntity;
import com.example.user.live.view.LoadDialog;

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
    private TextView tvTitle, tvUp;
    private SwitchCompat  switchCompat;


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
                if(!isChose){
                    if(videoData.get(poi).isChose()){
                        videoData.get(poi).setChose(isChose);
                        adapter.notifyDataSetChanged();
                    }
                }else{
                    boolean chose = choseVideoAll(videoData.get(poi).getVideoEntities());
                    if(chose){
                        videoData.get(poi).setChose(true);
                        adapter.notifyDataSetChanged();
                    }
                }
                Log.e("tag_send",videoData.toString()+"");
            }

            @Override
            public void choseAll(int poi, boolean isChose) {
                if(videoData != null && videoData.size() > 0){
                   VideoTotalEntity totalEntity =  videoData.get(poi);
                   totalEntity.setChose(isChose);
                   for(VideoEntity entity : totalEntity.getVideoEntities()){
                       entity.setChose(isChose);
                   }
                }
                Log.e("tag_send_choseAll",videoData.toString()+"");
            }
        });
        tvUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<VideoEntity>  data = getUpLoadData();
                if(data != null && data.size() > 0){
                     VideoTotalEntity totalEntity = new VideoTotalEntity();
                    totalEntity.setVideoEntities(data);
                    totalEntity.getVideoEntities().get(0).setStatus(2);
                    if(totalEntity.getVideoEntities().size() >=2){
                        totalEntity.getVideoEntities().get(1).setStatus(6);
                    }
                    Intent intent = new Intent(VideoList.this,VideoUpLoad.class);
                    intent.putExtra("video",totalEntity);
                    startActivity(intent);
                }else{
                    ToastUtils.showToast(VideoList.this,"请选择上传的视频");
                }
            }
        });
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.e("tag_switch",b+"");
            }
        });
    }


    private boolean  choseVideoAll(List<VideoEntity>  entityList){
        for(VideoEntity videoEntity : entityList){
            if(!videoEntity.isChose()){
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
            videoData.addAll(orgData(data));
            adapter.notifyDataSetChanged();
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
        getVideoData();
    }

    private void initUI() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvUp = (TextView) findViewById(R.id.tv_set);
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

    private List<VideoEntity>  getUpLoadData(){
        List<VideoEntity> videoEntityList = new ArrayList<>();
        for(VideoTotalEntity entity : videoData){
             if(entity.isChose()){
                videoEntityList.addAll(entity.getVideoEntities());
             }else{
                 for(VideoEntity videoEntity : entity.getVideoEntities()){
                     if(videoEntity.isChose()){
                         videoEntityList.add(videoEntity);
                     }
                 }
             }
        }
        Log.e("tag_up_data",videoEntityList.toString()+"");
        return  videoEntityList;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
