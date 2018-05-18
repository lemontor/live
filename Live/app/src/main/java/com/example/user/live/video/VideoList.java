package com.example.user.live.video;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.example.user.live.R;
import com.example.user.live.utils.GetVideoDataUtils;
import com.example.user.live.video.adapter.VideoListAdapter;
import com.example.user.live.video.entity.VideoEntity;
import com.example.user.live.view.LoadDialog;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2018/4/25.
 */
public class VideoList extends Activity {

    private ListView mLvVideo;
    private LoadDialog loadDialog;
    private VideoListAdapter adapter;
    private List<List<VideoEntity>> videoData;

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
                videoData.get(poi).get(childPoi).setChose(isChose);
            }
        });
    }

    private void initObj() {
        videoData = new ArrayList<>();
        loadDialog = new LoadDialog(this);
        adapter = new VideoListAdapter(this, videoData);
        mLvVideo.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(List<List<VideoEntity>> data) {
        Log.e("tag_data", data.toString() + "");
        if (loadDialog.isShow()) {
            loadDialog.dismissDialog();
        }
        if (data != null && data.size() > 0) {
            videoData.addAll(data);
            adapter.notifyDataSetChanged();
        }


    }

    private void initData() {
        getVideoData();
    }

    private void initUI() {
        mLvVideo = (ListView) findViewById(R.id.lv_video);
        View emptyView = findViewById(R.id.layout_empty);
        mLvVideo.setEmptyView(emptyView);
    }


    private void getVideoData() {
        loadDialog.showDialog();
        new Thread() {
            @Override
            public void run() {
                super.run();
                List<List<VideoEntity>> lists = GetVideoDataUtils.getList(VideoList.this);
                EventBus.getDefault().post(lists);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
