package com.example.user.live.video;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.user.live.R;
import com.example.user.live.video.adapter.VideoUpAdapter;
import com.example.user.live.video.entity.FromActivityEventBean;
import com.example.user.live.video.entity.FromServiceEventBean;
import com.example.user.live.video.entity.FromUploadEventBean;
import com.example.user.live.video.entity.VideoEntity;
import com.example.user.live.video.entity.VideoTotalEntity;
import com.example.user.live.view.StaticListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2018/4/27.
 */
public class VideoUpLoad extends Activity implements View.OnClickListener {

    private StaticListView lvFinish, lvFail;
    private RecyclerView rvLoading;
    private TextView tvFinishNotify, tvFinishClear;
    private TextView tvFailNotify, tvFailClear;
    private TextView tvLoadNotify, tvLoadClear;
    private TextView tvTitle, tvSet;
    private View rootView;
    private VideoTotalEntity videoTotalEntity;
    private List<VideoEntity> videoEntityList;
    private VideoUpAdapter videoUpAdapter;
    private int index = 0;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void upLoadEvent(FromUploadEventBean fromUploadEventBean) {
        if (fromUploadEventBean != null) {
            if (fromUploadEventBean.getType() == 1) {//开始
                videoEntityList.get(index).setStatus(2);
                videoUpAdapter.notifyItemChanged(index);
            } else if (fromUploadEventBean.getType() == 2) {//上传中
                videoEntityList.get(index).setStatus(2);
                videoEntityList.get(index).setProgress(fromUploadEventBean.getProgress());
                videoUpAdapter.notifyItemChanged(index);
            } else if (fromUploadEventBean.getType() == 3) {//完成:将已完成的数据放到完成模块
                videoUpAdapter.notifyItemRemoved(index);
            } else if (fromUploadEventBean.getType() == 4) {//失败
            } else if (fromUploadEventBean.getType() == 5) {//终止
            } else if (fromUploadEventBean.getType() == 6) {//暂停
            } else if (fromUploadEventBean.getType() == 7) {//等待中
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fromServiceEvent(FromServiceEventBean serviceEventBean) {
        if (serviceEventBean != null) {
            if (serviceEventBean.getEvent() == 3) {//提示
                index = serviceEventBean.getPoi();
            } else if (serviceEventBean.getEvent() == 2) {//暂停

            } else if (serviceEventBean.getEvent() == 1) {//开始

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = View.inflate(this, R.layout.activity_video_upload, null);
        setContentView(rootView);
        initUI();
        initData();
        initListener();
    }

    private void initData() {
        videoEntityList = new ArrayList<>();
        videoTotalEntity = (VideoTotalEntity) getIntent().getSerializableExtra("video");
        if (videoTotalEntity != null) {
            videoEntityList.addAll(videoTotalEntity.getVideoEntities());
            Log.e("tag_data_activity", videoTotalEntity.toString() + "");
            EventBus.getDefault().postSticky(videoTotalEntity);
//            Intent intent = new Intent(this,UpLoadService.class);
//            startService(intent);
        }
    }

    private void initUI() {
        lvFinish = (StaticListView) findViewById(R.id.lv_finish);
        lvFail = (StaticListView) findViewById(R.id.lv_fail);
        tvLoadNotify = (TextView) findViewById(R.id.tv_notify);
        tvLoadClear = (TextView) findViewById(R.id.tv_clear);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSet = (TextView) findViewById(R.id.tv_set);
        rvLoading = (RecyclerView) findViewById(R.id.rv_load);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvLoading.setLayoutManager(linearLayoutManager);
        View finishView = LayoutInflater.from(this).inflate(R.layout.layout_head_upload, null);
        View failView = LayoutInflater.from(this).inflate(R.layout.layout_head_upload, null);
        tvFinishNotify = (TextView) finishView.findViewById(R.id.tv_notify);
        tvFinishClear = (TextView) finishView.findViewById(R.id.tv_clear);
        tvFailNotify = (TextView) failView.findViewById(R.id.tv_notify);
        tvFailClear = (TextView) failView.findViewById(R.id.tv_clear);
        lvFinish.addHeaderView(finishView);
        lvFail.addHeaderView(failView);
        tvLoadNotify.setText("进行中（1）");
        tvLoadClear.setText("全部暂停");
        tvLoadClear.setTextColor(getResources().getColor(R.color.color_34A446));
        tvTitle.setText("任务列表");
        tvSet.setText("");
        Drawable drawable = getResources().getDrawable(R.mipmap.spsc_gengduo);
        tvSet.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

    }

    private void initListener() {
        videoUpAdapter = new VideoUpAdapter(this, videoEntityList, new VideoUpAdapter.OnStartClickListener() {
            @Override
            public void onStart(int type, int position, int status) {
                partVideoStop(position,index);
                FromActivityEventBean fromActivityEventBean = new FromActivityEventBean();
                fromActivityEventBean.setEvent(1);
                fromActivityEventBean.setPoi(position);
                EventBus.getDefault().post(fromActivityEventBean);
            }

            @Override
            public void onStop(int type, int position, int status) {
                try{
                    FromActivityEventBean fromActivityEventBean = new FromActivityEventBean();
                    int nextPoi = position + 1;
                    int targetPoi = 0;
                    if(videoEntityList.size() == nextPoi){
                        index = 0;
                        videoEntityList.get(0).setStatus(2);
//                        videoUpAdapter.notifyItemChanged(0);
                        targetPoi = 0;
                    }else{
                        index = nextPoi;
                        videoEntityList.get(nextPoi).setStatus(2);
//                        videoUpAdapter.notifyItemChanged(0);
                        targetPoi = nextPoi;
                    }
                    videoEntityList.get(position).setStatus(6);
//                    videoUpAdapter.notifyItemChanged(position);
                    videoUpAdapter.notifyDataSetChanged();
                    fromActivityEventBean.setEvent(1);
                    fromActivityEventBean.setPoi(targetPoi);
                    EventBus.getDefault().post(fromActivityEventBean);
                }catch (Exception e){}

            }
        });
        rvLoading.setAdapter(videoUpAdapter);
        tvSet.setOnClickListener(this);
    }

    private PopupWindow mPopNotify;

    private void initPop() {
        if (mPopNotify == null) {
            mPopNotify = new PopupWindow(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            View popView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_op, null);
            TextView tvClearIng = (TextView) popView.findViewById(R.id.tv_clear_ing);
            TextView tvReset = (TextView) popView.findViewById(R.id.tv_reset);
            TextView tvClearFail = (TextView) popView.findViewById(R.id.tv_clear_fail);
            TextView tvCancel = (TextView) popView.findViewById(R.id.tv_cancel);
            tvClearIng.setOnClickListener(this);
            tvReset.setOnClickListener(this);
            tvClearFail.setOnClickListener(this);
            tvCancel.setOnClickListener(this);
            mPopNotify.setContentView(popView);
            mPopNotify.setFocusable(true);
            mPopNotify.setBackgroundDrawable(new PaintDrawable(getResources().getColor(R.color.color_44000000)));
            mPopNotify.setAnimationStyle(R.style.popwin_anim_style);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_clear_ing:
                break;
            case R.id.tv_reset:
                break;
            case R.id.tv_clear_fail:
                break;
            case R.id.tv_cancel:
                mPopNotify.dismiss();
                break;
            case R.id.tv_set:
                initPop();
                mPopNotify.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                break;
        }
    }

    private void partVideoStop(int startPoi,int stopPoi) {
        index = startPoi;
        videoEntityList.get(stopPoi).setStatus(6);
//        videoUpAdapter.notifyItemChanged(stopPoi);
        videoEntityList.get(startPoi).setStatus(2);
//        videoUpAdapter.notifyItemChanged(startPoi);
        videoUpAdapter.notifyDataSetChanged();
    }


}
