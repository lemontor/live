package com.example.user.live.video.upload;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.user.live.R;
import com.example.user.live.utils.CacheUtils;
import com.example.user.live.video.adapter.VideoFailAdapter;
import com.example.user.live.video.adapter.VideoFinishAdapter;
import com.example.user.live.video.adapter.VideoUpAdapter;
import com.example.user.live.video.entity.FromActivityEventBean;
import com.example.user.live.video.entity.FromServiceEventBean;
import com.example.user.live.video.entity.FromUploadBean;
import com.example.user.live.video.entity.VideoEntity;
import com.example.user.live.video.entity.VideoTotalEntity;
import com.example.user.live.view.CustomProgressBar;
import com.example.user.live.view.StaticListView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2018/4/27.
 */
public class VideoUpLoadActivity extends Activity implements View.OnClickListener {

    private StaticListView lvFinish, lvFail, lvLoading;
    //    private RecyclerView rvLoading;
    private View upViewLoading;
    private TextView tvFinishNotify, tvFinishClear;
    private TextView tvFailNotify, tvFailClear;
    private TextView tvLoadNotify, tvLoadClear;
    private TextView tvTitle, tvSet;
    private View rootView;
    private VideoTotalEntity videoTotalEntity;
    private List<VideoEntity> videoEntityList, videoFinishList, videoFailList;
    private VideoUpAdapter videoUpAdapter;
    private int index = 0;
    private IntentFilter intentFilter;
    private VideoFinishAdapter videoFinishAdapter;
    private VideoFailAdapter videoFailAdapter;

    /*
    接收上传的信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void upLoadEvent(FromUploadBean fromUploadBean) {
        if (fromUploadBean != null) {
            if (fromUploadBean.getType() == 1) {//等待中
                if (videoEntityList != null) {
                    videoEntityList.get(index).setStatus(7);
                    videoUpAdapter.notifyDataSetChanged();
                }
            } else if (fromUploadBean.getType() == 2) {//开始
                Log.e("tag_event_first", "开始下载了");
                if (videoEntityList != null) {
                    Log.e("tag_event", "开始下载了");
                    videoEntityList.get(index).setStatus(2);
                    videoUpAdapter.notifyDataSetChanged();
                }
            } else if (fromUploadBean.getType() == 3) {//连接失败
                upLoadFail();
            } else if (fromUploadBean.getType() == 4) {//本地文件不存在
                if (videoEntityList != null) {
                    videoEntityList.remove(index);
                    videoUpAdapter.notifyDataSetChanged();
                }
            } else if (fromUploadBean.getType() == 5) {//暂停
                if (videoEntityList != null) {
                    videoEntityList.get(index).setStatus(6);
                    videoUpAdapter.notifyDataSetChanged();
                }
            } else if (fromUploadBean.getType() == 6) {//上传中
                updateSingle(index, fromUploadBean.getProgress(), fromUploadBean.getLen());
            } else if (fromUploadBean.getType() == 7) {//上传失败
                upLoadFail();
            } else if (fromUploadBean.getType() == 8) {//上传完成
                upLoadFinish();
            }
        }
    }

    /*
    上传完成
     */
    private void upLoadFinish() {
        if (videoEntityList != null) {
            if (lvFinish.getVisibility() == View.GONE) {
                lvFinish.setVisibility(View.VISIBLE);
            }
            VideoEntity finishEntity = videoEntityList.remove(index);
            videoFinishList.add(finishEntity);
            videoFinishAdapter.notifyDataSetChanged();
            tvFinishNotify.setText("已完成(" + videoFinishList.size() + ")");
            tvFinishClear.setText("全部清空");
            videoUpAdapter.notifyDataSetChanged();
            if (videoEntityList.size() == 0) {
                upViewLoading.setVisibility(View.GONE);
            }
        }
    }

    /*
    上传失败
     */
    private void upLoadFail() {
        if (videoEntityList != null) {
            if (lvFail.getVisibility() == View.GONE) {
                lvFail.setVisibility(View.VISIBLE);
            }
            VideoEntity finishEntity = videoEntityList.remove(index);
            finishEntity.setStatus(6);
            videoFailList.add(finishEntity);
            tvFailNotify.setText("上传失败(" + videoFailList.size() + ")");
            videoFailAdapter.notifyDataSetChanged();
            videoUpAdapter.notifyDataSetChanged();
            if (videoEntityList.size() == 0) {
                upViewLoading.setVisibility(View.GONE);
            }
        }

    }

    /*
    接收service传过来的信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fromServiceEvent(FromServiceEventBean serviceEventBean) {
        if (serviceEventBean != null) {
            if (serviceEventBean.getEvent() == 3) {//提示
                index = serviceEventBean.getPoi();
            } else if (serviceEventBean.getEvent() == 2) {//暂停
                videoEntityList.get(serviceEventBean.getPoi()).setStatus(6);
                videoUpAdapter.notifyDataSetChanged();
            } else if (serviceEventBean.getEvent() == 1) {//开始
                videoEntityList.get(serviceEventBean.getPoi()).setStatus(2);
                videoUpAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        rootView = View.inflate(this, R.layout.activity_video_upload, null);
        setContentView(rootView);
        initUI();
        initData();
        initListener();
    }

    private void initData() {
        videoEntityList = new ArrayList<>();
        videoFinishList = new ArrayList<>();
        videoFailList = new ArrayList<>();
        videoTotalEntity = (VideoTotalEntity) getIntent().getSerializableExtra("video");
        if (videoTotalEntity != null) {
//            new CacheUtils().init().putCacheForVideoInfo(videoTotalEntity);
            videoEntityList.addAll(videoTotalEntity.getVideoEntities());
            EventBus.getDefault().postSticky(videoTotalEntity);
            Intent intent = new Intent(this, UpLoadVideoFileService.class);
            startService(intent);
            tvLoadNotify.setText("进行中（" + videoEntityList.size() + ")");
        }
    }

    private void initUI() {
        lvFinish = (StaticListView) findViewById(R.id.lv_finish);
        lvFail = (StaticListView) findViewById(R.id.lv_fail);
        tvLoadNotify = (TextView) findViewById(R.id.tv_notify);
        tvLoadClear = (TextView) findViewById(R.id.tv_clear);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSet = (TextView) findViewById(R.id.tv_set);
        upViewLoading = findViewById(R.id.layout_loading);
        lvLoading = (StaticListView) findViewById(R.id.lv_loading);
        View finishView = LayoutInflater.from(this).inflate(R.layout.layout_head_upload, null);
        View failView = LayoutInflater.from(this).inflate(R.layout.layout_head_upload, null);
        tvFinishNotify = (TextView) finishView.findViewById(R.id.tv_notify);
        tvFinishClear = (TextView) finishView.findViewById(R.id.tv_clear);
        tvFailNotify = (TextView) failView.findViewById(R.id.tv_notify);
        tvFailClear = (TextView) failView.findViewById(R.id.tv_clear);
        lvFinish.addHeaderView(finishView);
        lvFail.addHeaderView(failView);
        tvLoadClear.setText("全部暂停");
        tvLoadClear.setTextColor(getResources().getColor(R.color.color_34A446));
        tvTitle.setText("任务列表");
        tvSet.setText("");
        Drawable drawable = getResources().getDrawable(R.mipmap.spsc_gengduo);
        tvSet.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    NetworkChangeReceiver networkChangeReceiver;

    private void initListener() {
        //注册网络变化监听事件
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
        videoUpAdapter = new VideoUpAdapter(this, videoEntityList, new VideoUpAdapter.OnStartClickListener() {
            @Override
            public void onStart(int type, int position, int status) {
                try {
                    partVideoStop(position, index);
                    FromActivityEventBean fromActivityEventBean = new FromActivityEventBean();
                    fromActivityEventBean.setEvent(1);
                    fromActivityEventBean.setPoi(position);
                    EventBus.getDefault().post(fromActivityEventBean);
                } catch (Exception e) {

                }

            }

            @Override
            public void onStop(int type, int position, int status) {
                try {
                    FromActivityEventBean fromActivityEventBean = new FromActivityEventBean();
                    videoEntityList.get(position).setStatus(6);
                    fromActivityEventBean.setEvent(2);
                    fromActivityEventBean.setPoi(position);
                    EventBus.getDefault().post(fromActivityEventBean);
                } catch (Exception e) {
                }

            }
        });
        lvLoading.setAdapter(videoUpAdapter);
        tvSet.setOnClickListener(this);
        tvLoadClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        tvFinishClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoFinishList.clear();
                lvFinish.setVisibility(View.GONE);
            }
        });
        tvFailClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoFailList.clear();
                lvFail.setVisibility(View.GONE);
            }
        });
        videoFinishAdapter = new VideoFinishAdapter(this, videoFinishList);
        lvFinish.setAdapter(videoFinishAdapter);
        videoFailAdapter = new VideoFailAdapter(this, videoFailList);
        videoFailAdapter.setOnChoseListener(new VideoFailAdapter.OnResetListener() {
            @Override
            public void onReset(int poi) {
                reUploadData(poi);
//                VideoEntity videoEntity = videoFailList.remove(poi);
//                if (videoEntityList.size() == 0) {
//                    videoEntityList.add(videoEntity);
//                } else {
//                    videoEntityList.add(videoEntityList.size() - 1, videoEntity);
//                }
//                if (videoFailList.size() == 0) {
//                    lvFail.setVisibility(View.GONE);
//                } else {
//                    videoFailAdapter.notifyDataSetChanged();
//                }
//                videoUpAdapter.notifyDataSetChanged();
//                upViewLoading.setVisibility(View.VISIBLE);
//                tvLoadNotify.setText("进行中（" + videoEntityList.size() + ")");
                FromActivityEventBean fromActivityEventBean = new FromActivityEventBean();
                fromActivityEventBean.setEvent(1);
                fromActivityEventBean.setPoi(index);
                EventBus.getDefault().post(fromActivityEventBean);
            }
        });
        lvFail.setAdapter(videoFailAdapter);
    }

    AlertDialog alertDialog;

    private void initDialog() {
        if (alertDialog == null) {
            View view = View.inflate(this, R.layout.dialog_net, null);
            TextView tvCancel = (TextView) view.findViewById(R.id.cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            TextView tvSure = (TextView) view.findViewById(R.id.sure);
            tvSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);
            alertDialog = builder.create();
        }
        alertDialog.show();
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
                //先终止正在上传的视频
                videoEntityList.clear();
                videoUpAdapter.notifyDataSetChanged();
                mPopNotify.dismiss();
                break;
            case R.id.tv_reset:
                if (videoEntityList.size() == 0) {
                    videoEntityList.addAll(videoFailList);
                    videoUpAdapter.notifyDataSetChanged();
                } else {
                    videoEntityList.addAll(videoEntityList.size() - 1, videoFailList);
                    videoUpAdapter.notifyDataSetChanged();
                    lvFail.setVisibility(View.GONE);
                }
                mPopNotify.dismiss();
                break;
            case R.id.tv_clear_fail:
                videoFailList.clear();
                lvFail.setVisibility(View.GONE);
                mPopNotify.dismiss();
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

    boolean  isCloseWifi = false;

    /*
    监听网络变化
     */
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                switch (networkInfo.getType()) {
                    case ConnectivityManager.TYPE_MOBILE:
                        Log.e("tag_info","手机网络");
                        initDialog();
                        videoEntityList.get(index).setStatus(9);
                        videoUpAdapter.notifyDataSetChanged();
                        FromActivityEventBean fromActivityEventBean = new FromActivityEventBean();
                        fromActivityEventBean.setPoi(index);
                        fromActivityEventBean.setEvent(2);
                        EventBus.getDefault().post(fromActivityEventBean);
                        break;
                    case ConnectivityManager.TYPE_WIFI:
                        if(isCloseWifi){
                            reUploadData(index);
                            FromActivityEventBean reStartActivityEventBean = new FromActivityEventBean();
                            reStartActivityEventBean.setEvent(1);
                            reStartActivityEventBean.setPoi(index);
                            EventBus.getDefault().post(reStartActivityEventBean);
//                            videoEntityList.get(index).setStatus(2);
                            isCloseWifi = false;
                        }
                        break;
                    default:
                        break;
                }
            } else {
                isCloseWifi = true;
                Log.e("tag_info","网络不可用");
                initDialog();
                FromActivityEventBean fromActivityEventBean = new FromActivityEventBean();
                fromActivityEventBean.setPoi(index);
                fromActivityEventBean.setEvent(2);
                EventBus.getDefault().post(fromActivityEventBean);
            }
        }
    }

    /**
     * 第一种方法 更新对应view的内容
     *
     * @param position 要更新的位置
     */
    private void updateSingle(int position, int progress, String loadProgress) {
        /**第一个可见的位置**/
        int firstVisiblePosition = lvLoading.getFirstVisiblePosition();
        /**最后一个可见的位置**/
        int lastVisiblePosition = lvLoading.getLastVisiblePosition();
        /**在看见范围内才更新，不可见的滑动后自动会调用getView方法更新**/
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            /**获取指定位置view对象**/
            View view = lvLoading.getChildAt(position - firstVisiblePosition);
            CustomProgressBar pbar = (CustomProgressBar) view.findViewById(R.id.pb_progress);
            TextView tvPercent = (TextView) view.findViewById(R.id.tv_load);
            tvPercent.setText(loadProgress);
            ImageView ivStatus = (ImageView) view.findViewById(R.id._iv_start);
            ivStatus.setImageResource(R.mipmap.spsc_zhengzaishangchuan);
            TextView tvStatus = (TextView) view.findViewById(R.id.tv_status);
            videoEntityList.get(index).setStatus(2);
            tvStatus.setText("正在上传");
            pbar.setPercent(progress);
        }
    }


    /*
    改变视频上传状态
     */
    private void partVideoStop(int startPoi, int stopPoi) {
        index = startPoi;
        videoEntityList.get(stopPoi).setStatus(6);
        videoEntityList.get(startPoi).setStatus(2);
        videoUpAdapter.notifyDataSetChanged();
    }

    private void  reUploadData(int poi){
        VideoEntity videoEntity = videoFailList.remove(poi);
        if (videoEntityList.size() == 0) {
            videoEntityList.add(videoEntity);
        } else {
            videoEntityList.add(videoEntityList.size() - 1, videoEntity);
        }
        if (videoFailList.size() == 0) {
            lvFail.setVisibility(View.GONE);
        } else {
            videoFailAdapter.notifyDataSetChanged();
        }
        videoUpAdapter.notifyDataSetChanged();
        upViewLoading.setVisibility(View.VISIBLE);
        tvLoadNotify.setText("进行中（" + videoEntityList.size() + ")");
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
        EventBus.getDefault().unregister(this);
    }


}
