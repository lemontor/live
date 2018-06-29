package com.example.user.live.video.upload;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.user.live.app.App;
import com.example.user.live.utils.CacheUtils;
import com.example.user.live.utils.SharedPreferencesUtils;
import com.example.user.live.utils.ToastUtils;
import com.example.user.live.video.adapter.VideoFailAdapter;
import com.example.user.live.video.adapter.VideoFinishAdapter;
import com.example.user.live.video.adapter.VideoUpAdapter;
import com.example.user.live.video.entity.FromActivityEventBean;
import com.example.user.live.video.entity.FromServiceEventBean;
import com.example.user.live.video.entity.FromUploadBean;
import com.example.user.live.video.entity.UpLoadingBean;
import com.example.user.live.video.entity.VideoEntity;
import com.example.user.live.video.entity.VideoTotalEntity;
import com.example.user.live.view.CustomProgressBar;
import com.example.user.live.view.StaticListView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

/**
 * Created by user on 2018/4/27.
 */
public class VideoUpLoadActivity extends Activity implements View.OnClickListener {

    private static final String serviceName = "com.example.user.live.video.upload.UpLoadVideoFileService";
    private StaticListView lvFinish, lvFail, lvLoading;
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
    private boolean hasUpLoading = false;
    private TextView tvBack;
    private SharedPreferencesUtils shareUtils;
    private boolean isUp = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void upLoadingMethod(UpLoadingBean bean) {
        if (bean != null) {
            isUp = true;
            updateSingle(index, bean.getProgress(), bean.getLen());
            bean.recycle();
        }
    }

    /*
    接收上传的信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void upLoadEvent(FromUploadBean fromUploadBean) {
        try {
            if (fromUploadBean != null) {
                if (fromUploadBean.getType() == 1) {//等待中
                    isUp = false;
                    if (videoEntityList != null) {
                        videoEntityList.get(index).setStatus(7);
                        videoUpAdapter.notifyDataSetChanged();
                    }
                } else if (fromUploadBean.getType() == 2) {//开始
                    isUp = true;
                    if (videoEntityList != null) {
                        videoEntityList.get(index).setStatus(2);
                        videoUpAdapter.notifyDataSetChanged();
                    }
                } else if (fromUploadBean.getType() == 3) {//连接失败
                    isUp = false;
                    upLoadFail(false);
                } else if (fromUploadBean.getType() == 4) {//本地文件不存在
                    if (videoEntityList != null) {
                        isUp = false;
                        videoEntityList.remove(index);
                        videoUpAdapter.notifyDataSetChanged();
                    }
                } else if (fromUploadBean.getType() == 5) {//暂停
                    if (videoEntityList != null) {
                        isUp = false;
                        videoEntityList.get(index).setStatus(6);
                        videoUpAdapter.notifyDataSetChanged();
                    }
                } else if (fromUploadBean.getType() == 6) {//上传中
                    updateSingle(index, fromUploadBean.getProgress(), fromUploadBean.getLen());
                } else if (fromUploadBean.getType() == 7) {//上传失败
                    isUp = false;
                    upLoadFail(false);
                } else if (fromUploadBean.getType() == 8) {//上传完成
                    isUp = false;
                    upLoadFinish();
                    int size = videoEntityList.size();
                    if (size > 0) {
                        tvLoadNotify.setText("进行中（" + size + "）");
                    }
                    updateSingleNoNet(index, 0, "0");
                } else if (fromUploadBean.getType() == 10) {//已经上传过了
                    isUp = false;
                    upLoadFinish();
                    int size = videoEntityList.size();
                    if (size > 0) {
                        tvLoadNotify.setText("进行中（" + size + "）");
                    }
                } else if (fromUploadBean.getType() == 11) {
                    isUp = false;
                    VideoEntity videoEntity = videoEntityList.get(index);
                    videoEntity.setStatus(7);
                    App.videoEntityList.add(videoEntity);
                    upLoadFail(true);
                    updateSingleNoNet(index, 0, "0");
                }
            }
        } catch (Exception e) {
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
            updateVideoCache(finishEntity);
            videoFinishList.add(finishEntity);
            videoFinishAdapter.notifyDataSetChanged();
            tvFinishNotify.setText("已完成(" + videoFinishList.size() + ")");
            tvFinishClear.setText("全部清空");
            tvFinishClear.setTextColor(getResources().getColor(R.color.color_E75B5B));
            videoUpAdapter.notifyDataSetChanged();
            if (videoEntityList.size() == 0) {
                upViewLoading.setVisibility(View.GONE);
            }
        }
    }

    private void updateVideoCache(VideoEntity videoEntity) {//更新信息
        if (App.videoEntityList.size() > 0) {
            for (int i = 0; i < App.videoEntityList.size(); i++) {
                if (videoEntity.getTitle().equals(App.videoEntityList.get(i).getTitle())) {
                    App.videoEntityList.remove(i);
                    break;
                }
            }
        }
    }


    //关键代码 运行序列化和反序列化  进行深度拷贝
    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    /*
    上传失败
     */
    private void upLoadFail(boolean fail) {
        if (videoEntityList != null) {
            if (lvFail.getVisibility() == View.GONE) {
                lvFail.setVisibility(View.VISIBLE);
            }
            VideoEntity finishEntity = videoEntityList.remove(index);
            finishEntity.setFail(fail);
            finishEntity.setStatus(7);
            int size = videoEntityList.size();
            if (size > 0) {
                tvLoadNotify.setText("进行中（" + size + "）");
            }
            finishEntity.setStatus(7);
            videoFailList.add(finishEntity);
            hasUpLoading = true;
            tvFailNotify.setText("上传失败(" + videoFailList.size() + ")");
            tvFailClear.setText("全部清除");
            tvFailClear.setTextColor(getResources().getColor(R.color.color_E75B5B));
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
//            serviceEventBean.recycle();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EventBus.getDefault().register(this);
        rootView = View.inflate(this, R.layout.activity_video_upload, null);
        setContentView(rootView);
        initUI();
        initData();
        initListener();
    }

    private void initData() {
        shareUtils = new SharedPreferencesUtils(this);
        videoEntityList = new ArrayList<>();
        videoFinishList = new ArrayList<>();
        videoFailList = new ArrayList<>();
        int has = getIntent().getIntExtra("has", 0);
        String info = shareUtils.getString("v");
        String finishInfo = shareUtils.getString("finish");
        String errorInfo = shareUtils.getString("f");
        initFinishAndFailInfo(finishInfo,errorInfo);
        if (has == 1) {//有数据
            if (info != null && !info.equals("")) {
                VideoTotalEntity videoTotalTemp = new Gson().fromJson(info, VideoTotalEntity.class);
                if (videoTotalTemp != null && videoTotalTemp.getVideoEntities().size() > 0) {
                    videoEntityList.addAll(videoTotalTemp.getVideoEntities());
                }
            }
            videoTotalEntity = (VideoTotalEntity) getIntent().getSerializableExtra("video");
            initVideoData();
        } else {
            Log.e("tag_cache", info + "");
            if (info != null && !info.equals("")) {
                videoTotalEntity = new Gson().fromJson(info, VideoTotalEntity.class);
                initVideoData();
            }
        }
    }

    private void initVideoData() {
        if (videoTotalEntity != null) {
            videoEntityList.addAll(videoTotalEntity.getVideoEntities());
            List<VideoEntity> temp = removeDuplicteUsers(videoEntityList);//去重
            Log.e("tag_quchong", temp.toString() + "");
            videoEntityList.clear();
            videoEntityList.addAll(temp);
            try {
                List<VideoEntity> deepVideoData = deepCopy(videoEntityList);
                if (videoFailList.size() > 0) {
                    for (VideoEntity videoEntity : videoFailList) {
                        if (videoEntity.isFail()) {
                            for (VideoEntity mVideoEntity : deepVideoData) {
                                if (videoEntity.getTitle().equals(mVideoEntity.getTitle())) {
                                    mVideoEntity.setFail(true);
                                }
                            }
                        }
                    }
                }
                if (isServiceRunning(this, serviceName)) {
                    FromActivityEventBean fromActivityEventBean = new FromActivityEventBean();
                    fromActivityEventBean.setEvent(5);
                    fromActivityEventBean.setVideoEntityList(deepVideoData);
                    EventBus.getDefault().post(fromActivityEventBean);
                } else {
                    VideoTotalEntity tempVideo = new VideoTotalEntity();
                    tempVideo.setVideoEntities(deepVideoData);
                    EventBus.getDefault().postSticky(tempVideo);
                    Intent intent = new Intent(this, UpLoadVideoFileService.class);
                    startService(intent);
                }
                tvLoadNotify.setText("进行中（" + videoEntityList.size() + "）");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 将列表中重复的用户移除，重复指的是title相同
     *
     * @param userList
     * @return
     */
    public static ArrayList<VideoEntity> removeDuplicteUsers(List<VideoEntity> userList) {
        Set<VideoEntity> s = new TreeSet<VideoEntity>(new Comparator<VideoEntity>() {

            @Override
            public int compare(VideoEntity o1, VideoEntity o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
        s.addAll(userList);
        return new ArrayList<VideoEntity>(s);
    }

    private void initFinishAndFailInfo(String finishInfo, String failInfo) {
        if (finishInfo != null && !finishInfo.equals("")) {
            VideoTotalEntity videoTotalTemp = new Gson().fromJson(finishInfo, VideoTotalEntity.class);
            if (videoTotalTemp != null && videoTotalTemp.getVideoEntities().size() > 0) {
                videoFinishList.addAll(videoTotalTemp.getVideoEntities());
            }
            tvFinishNotify.setText("已完成(" + videoFinishList.size() + ")");
            tvFinishClear.setText("全部清空");
            lvFinish.setVisibility(View.VISIBLE);
        }
        if (failInfo != null && !failInfo.equals("")) {
            VideoTotalEntity videoTotalTemp = new Gson().fromJson(failInfo, VideoTotalEntity.class);
            if (videoTotalTemp != null && videoTotalTemp.getVideoEntities().size() > 0) {
                videoFailList.addAll(videoTotalTemp.getVideoEntities());
            }
            tvFailNotify.setText("上传失败(" + videoFailList.size() + ")");
            tvFailClear.setText("全部清除");
            lvFail.setVisibility(View.VISIBLE);
        }
    }


    private void initUI() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        Drawable backDrawable = getResources().getDrawable(R.mipmap.fanhui);
        tvBack.setCompoundDrawablesWithIntrinsicBounds(backDrawable, null, null, null);
        tvBack.setOnClickListener(this);
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
                    if (netType == 2) {
                        partVideoStop(position, index);
                        FromActivityEventBean fromActivityEventBean = new FromActivityEventBean();
                        fromActivityEventBean.setEvent(1);
                        fromActivityEventBean.setPoi(position);
                        EventBus.getDefault().post(fromActivityEventBean);
                    } else {
                        ToastUtils.showToast(VideoUpLoadActivity.this, "请连接wifi网络");
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onStop(int type, int position, int status) {
                stopVideo();
                videoUpAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNetChange(int type, int position, int status) {
                if (netType == 2) {
                    try {
                        partVideoStop(position, index);
                        FromActivityEventBean fromActivityEventBean = new FromActivityEventBean();
                        fromActivityEventBean.setEvent(1);
                        fromActivityEventBean.setPoi(position);
                        EventBus.getDefault().post(fromActivityEventBean);
                    } catch (Exception e) {

                    }
                }
            }
        });
        lvLoading.setAdapter(videoUpAdapter);
        tvSet.setOnClickListener(this);
        tvLoadClear.setOnClickListener(this);
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
                if (netType != 2) {
                    ToastUtils.showToast(VideoUpLoadActivity.this, "请连接wifi网络");
                    return;
                }
                try {
                    String fileName = videoFailList.get(poi).getTitle();
                    List<VideoEntity> temp = new ArrayList<VideoEntity>();
                    temp.add(videoFailList.get(poi));
                    reUploadData(poi);
                    if (isServiceRunning(VideoUpLoadActivity.this, serviceName)) {
                        FromActivityEventBean fromActivityEventBean = new FromActivityEventBean();
                        fromActivityEventBean.setEvent(4);
                        fromActivityEventBean.setPoi(index);
                        fromActivityEventBean.setFileName(fileName);
                        fromActivityEventBean.setVideoEntityList(temp);
                        fromActivityEventBean.setUp(isUp);
                        EventBus.getDefault().post(fromActivityEventBean);
                    } else {
                        VideoTotalEntity videoToTal = new VideoTotalEntity();
                        videoToTal.setVideoEntities(temp);
                        EventBus.getDefault().postSticky(videoToTal);
                        Intent intent = new Intent(VideoUpLoadActivity.this, UpLoadVideoFileService.class);
                        startService(intent);
                    }
                } catch (Exception e) {
                }

            }
        });
        lvFail.setAdapter(videoFailAdapter);
    }

    /*
    停止上传视频
     */
    private void stopVideo() {
        try {
            FromActivityEventBean fromActivityEventBean = new FromActivityEventBean();
            videoEntityList.get(index).setStatus(6);
            fromActivityEventBean.setEvent(2);
            fromActivityEventBean.setPoi(index);
            EventBus.getDefault().post(fromActivityEventBean);
        } catch (Exception e) {
        }
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
            case R.id.tv_clear_ing: //先终止正在上传的视频
                stopVideo();
                videoEntityList.clear();
                videoUpAdapter.notifyDataSetChanged();
                mPopNotify.dismiss();
                upViewLoading.setVisibility(View.GONE);
                App.upCount = 0;
//                shareUtils.clear("v");
                //关闭service
                Intent intent = new Intent(VideoUpLoadActivity.this, UpLoadVideoFileService.class);
                stopService(intent);
                break;
            case R.id.tv_reset:
                if (netType != 2) {
                    ToastUtils.showToast(VideoUpLoadActivity.this, "请连接wifi网络");
                    return;
                }
                if (videoEntityList.size() == 0) {
                    videoEntityList.addAll(videoFailList);
                    videoUpAdapter.notifyDataSetChanged();
                } else {
                    videoEntityList.addAll(videoFailList);
                    videoUpAdapter.notifyDataSetChanged();
                }
                lvFail.setVisibility(View.GONE);
                if (upViewLoading.getVisibility() != View.VISIBLE) {
                    upViewLoading.setVisibility(View.VISIBLE);
                }
                tvLoadNotify.setText("进行中（" + videoEntityList.size() + "）");
                mPopNotify.dismiss();
                try {
                    List<VideoEntity> deepVideo = deepCopy(videoFailList);//深复制
                    if (isServiceRunning(VideoUpLoadActivity.this, serviceName)) {
                        FromActivityEventBean fromActivityEventBean = new FromActivityEventBean();
                        fromActivityEventBean.setEvent(4);
                        fromActivityEventBean.setPoi(index);
                        fromActivityEventBean.setUp(isUp);
                        fromActivityEventBean.setVideoEntityList(deepVideo);
                        EventBus.getDefault().post(fromActivityEventBean);
                    } else {
                        VideoTotalEntity videoToTal = new VideoTotalEntity();
                        videoToTal.setVideoEntities(deepVideo);
                        EventBus.getDefault().postSticky(videoToTal);
                        Intent intents = new Intent(VideoUpLoadActivity.this, UpLoadVideoFileService.class);
                        startService(intents);
                    }
                    videoFailList.clear();
                    videoFailAdapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.tv_clear_fail://清空失败项
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
            case R.id.tv_clear://全部暂停
                stopVideo();
                break;
            case R.id.tv_back:
                finish();
                break;
        }
    }

    boolean isCloseWifi = false;
    private int netType;

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
                        netType = 1;
                        initDialog();
                        videoEntityList.get(index).setStatus(9);
                        videoUpAdapter.notifyDataSetChanged();
                        FromActivityEventBean fromActivityEventBean = new FromActivityEventBean();
                        fromActivityEventBean.setPoi(index);
                        fromActivityEventBean.setEvent(2);
                        EventBus.getDefault().post(fromActivityEventBean);
                        break;
                    case ConnectivityManager.TYPE_WIFI:
                        netType = 2;
                        if (isCloseWifi) {
                            isCloseWifi = false;
                        }
                        break;
                    default:
                        break;
                }
            } else {
                netType = 3;
                isCloseWifi = true;
                Log.e("tag_info", "网络不可用");
                initDialog();
                FromActivityEventBean fromActivityEventBean = new FromActivityEventBean();
                fromActivityEventBean.setPoi(index);
                fromActivityEventBean.setEvent(3);
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


    private void updateSingleNoNet(int position, int progress, String loadProgress) {
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
            ivStatus.setImageResource(R.mipmap.spsc_dengdaishangchuan);
            TextView tvStatus = (TextView) view.findViewById(R.id.tv_status);
            videoEntityList.get(index).setStatus(6);
            tvStatus.setText("等到中...");
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

    private void reUploadData(int poi) {
        VideoEntity videoEntity = videoFailList.remove(poi);
        if (videoEntityList.size() == 0) {
            videoEntityList.add(videoEntity);
        } else {
            videoEntityList.add(videoEntity);
        }
        if (videoFailList.size() == 0) {
            lvFail.setVisibility(View.GONE);
        } else {
            videoFailAdapter.notifyDataSetChanged();
        }
        videoUpAdapter.notifyDataSetChanged();
        upViewLoading.setVisibility(View.VISIBLE);
        tvLoadNotify.setText("进行中（" + videoEntityList.size() + ")");
        tvFailNotify.setText("上传失败(" + videoFailList.size() + ")");
    }

    @Override
    protected void onStop() {
        super.onStop();
        new Thread() {
            @Override
            public void run() {
                super.run();
                cacheVideoInfo();//缓存信息
            }
        }.start();
    }


    private void cacheVideoInfo() {
        if (videoEntityList != null && videoEntityList.size() != 0) {
            videoEntityList.get(0).setStatus(7);
            VideoTotalEntity videoTotalEntity = new VideoTotalEntity();
            videoTotalEntity.setVideoEntities(videoEntityList);
            String info = new Gson().toJson(videoTotalEntity);
            shareUtils.putString("v", info);
        } else {
            shareUtils.putString("v", "");
        }
        if (videoFinishList != null && videoFinishList.size() != 0) {
            VideoTotalEntity videoTotalEntity = new VideoTotalEntity();
            videoTotalEntity.setVideoEntities(videoFinishList);
            String info = new Gson().toJson(videoTotalEntity);
            shareUtils.putString("finish", info);
        } else {
            shareUtils.clear("finish");
        }
        if (videoFailList != null && videoFailList.size() != 0) {
            VideoTotalEntity videoTotalEntity = new VideoTotalEntity();
            videoTotalEntity.setVideoEntities(videoFailList);
            String info = new Gson().toJson(videoTotalEntity);
            shareUtils.putString("f", info);
        } else {
            shareUtils.clear("f");
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
        EventBus.getDefault().unregister(this);
    }


    /**
     * 判断服务是否开启
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null)
            return false;
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }

}
