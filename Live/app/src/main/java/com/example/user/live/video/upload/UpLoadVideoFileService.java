package com.example.user.live.video.upload;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.VolleyError;
import com.example.user.live.R;
import com.example.user.live.app.App;
import com.example.user.live.utils.CacheUtils;
import com.example.user.live.utils.ConstantUtils;
import com.example.user.live.utils.FtpUpLoadUtils;
import com.example.user.live.utils.FtpUtils;
import com.example.user.live.utils.SharedPreferencesUtils;
import com.example.user.live.utils.VolleyUtils;
import com.example.user.live.video.entity.FromActivityEventBean;
import com.example.user.live.video.entity.FromServiceEventBean;
import com.example.user.live.video.entity.FromUploadBean;
import com.example.user.live.video.entity.FromUploadEventBean;
import com.example.user.live.video.entity.FtpUploadInfoEntity;
import com.example.user.live.video.entity.UpLoadingBean;
import com.example.user.live.video.entity.V;
import com.example.user.live.video.entity.VideoEntity;
import com.example.user.live.video.entity.VideoTotalEntity;
import com.example.user.live.video.entity.VideoUpInfoBean;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by user on 2018/5/29.
 */
public class UpLoadVideoFileService extends Service {

    private static final String START_UP = "start";
    private static final String STOP_UP = "stop";
    private static final String CANCEL = "cancel";
    NotificationManager notificationManager;
    private int index = 0;
    private int videoSize;
    private String upLoadFileName;
    private List<VideoEntity> videoEntityList;
    private long size;
    private String fileName;
    private String duration;
    private int intDuration;
    private String thumb;
    private String path;
    private boolean isFail = false;
    private FtpUploadInfoEntity entity;
    private FtpUtils ftpUtils;
    private VolleyUtils volleyUtils;
    private SharedPreferencesUtils sharedPreferencesUtils;
    private List<VideoEntity> failVideo = new ArrayList<>();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fromActivityEvent(FromActivityEventBean activityEventBean) {
        if (activityEventBean != null) {
            if (activityEventBean.getEvent() == 1) {//开始
                notification.contentView.setProgressBar(R.id.pro, 100, 0, false);//重新初始化notification的显示
                notification.contentView.setTextViewText(R.id.tv_start, "暂停");
                index = activityEventBean.getPoi();//记录当前正在上传的视频下标
                upLoadFileName = videoEntityList.get(index).getPath();
                size = videoEntityList.get(index).getLen();
                fileName = videoEntityList.get(index).getTitle();
                duration = videoEntityList.get(index).getDuration();
                intDuration = videoEntityList.get(index).getLongDuration();
                thumb = videoEntityList.get(index).getThumbPath();
                isFail = videoEntityList.get(index).isFail();
                Log.e("tag_fileName", fileName + "");
                if (videoEntityList.size() > 1) {
                    upVideo(false);
                } else {
                    upVideo(true);
                }
            } else if (activityEventBean.getEvent() == 2) {//暂停
                Log.e("tag_stop", "暂停");
                ftpUtils.stop();
            } else if (activityEventBean.getEvent() == 3) {//无网络
                ftpUtils.abort();
                videoEntityList.get(index).setFail(true);
            } else if (activityEventBean.getEvent() == 4) {//重新上传
//                VideoEntity videoEntity = activityEventBean.getVideoEntityList().get(0);
//                if (videoEntity != null) {
                    if (videoEntityList != null) {
                        if (videoEntityList.size() > 0) {
                            videoEntityList.addAll(activityEventBean.getVideoEntityList());
                        } else {
                            videoEntityList.addAll(activityEventBean.getVideoEntityList());
                        }
                    }
//                }
                Log.e("tag_video", videoEntityList.toString() + "");
                App.upCount = videoEntityList.size();
                if (!activityEventBean.isUp()) {//正在上传任务，
                    upLoadFileName = videoEntityList.get(index).getPath();
                    size = videoEntityList.get(index).getLen();
                    fileName = videoEntityList.get(index).getTitle();
                    duration = videoEntityList.get(index).getDuration();
                    intDuration = videoEntityList.get(index).getLongDuration();
                    isFail = videoEntityList.get(index).isFail();
                    thumb = videoEntityList.get(index).getThumbPath();
                    Log.e("tag_fileName", fileName + "");
                    FromServiceEventBean fromServiceEventBean = new FromServiceEventBean();
                    fromServiceEventBean.setEvent(3);
                    fromServiceEventBean.setPoi(index);
                    EventBus.getDefault().post(fromServiceEventBean);
                    if (videoEntityList.size() > 1) {
                        upVideo(false);
                    } else {
                        upVideo(true);
                    }
                }
//                if(App.upCount == 0){//已经没有上传任务了
//                    upLoadNextVideo(false);
//
//                }
            } else if (activityEventBean.getEvent() == 5) {//添加上传任务
                if (activityEventBean.getVideoEntityList() != null && activityEventBean.getVideoEntityList().size() > 0) {
                    videoEntityList.addAll(activityEventBean.getVideoEntityList());
                    List<VideoEntity> temp = removeDuplicteUsers(videoEntityList);
                    videoEntityList.clear();
                    videoEntityList.addAll(temp);
                    Log.e("tag_all", videoEntityList.toString() + "");
                    App.upCount = videoEntityList.size();
                    if (!App.isRunning) {//正在上传任务，
                        upLoadFileName = videoEntityList.get(index).getPath();
                        size = videoEntityList.get(index).getLen();
                        fileName = videoEntityList.get(index).getTitle();
                        duration = videoEntityList.get(index).getDuration();
                        intDuration = videoEntityList.get(index).getLongDuration();
                        isFail = videoEntityList.get(index).isFail();
                        thumb = videoEntityList.get(index).getThumbPath();
                        Log.e("tag_fileName", fileName + "");
                        FromServiceEventBean fromServiceEventBean = new FromServiceEventBean();
                        fromServiceEventBean.setEvent(3);
                        fromServiceEventBean.setPoi(index);
                        EventBus.getDefault().post(fromServiceEventBean);
                        if (videoEntityList.size() > 1) {
                            upVideo(false);
                        } else {
                            upVideo(true);
                        }
                    }
                }
            }
            notificationManager.notify(1, notification);
        }
    }

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

    private VideoEntity findVideo(String fileName) {
        for (VideoEntity videoEntity : failVideo) {
            if (fileName.equals(videoEntity.getTitle())) {
                return videoEntity;
            }
        }
        return null;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void upLoadingMethod(UpLoadingBean bean) {
//        if (bean != null) {
//            notification.contentView.setTextViewText(R.id.tv_start, "暂停");
//            notification.contentView.setProgressBar(R.id.pro, 100, bean.getProgress(), false);
//            notificationManager.notify(1, notification);
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fromUpLoadEvent(FromUploadBean fromUploadEventBean) {
        try {
            if (fromUploadEventBean != null) {
                if (fromUploadEventBean.getType() == 1) {//等待中
                    notification.contentView.setTextViewText(R.id.tv_start, "等待中");
                    clickStatus = 0;
                    notificationManager.notify(1, notification);
                } else if (fromUploadEventBean.getType() == 2) {//开始
                    notification.contentView.setTextViewText(R.id.tv_start, "暂停");
                    notification.contentView.setProgressBar(R.id.pro, 100, fromUploadEventBean.getProgress(), false);
                    notificationManager.notify(1, notification);
                } else if (fromUploadEventBean.getType() == 3) {//连接失败
                    App.upCount--;
                    VideoEntity videoEntity = videoEntityList.get(index);
                    if (videoEntity != null) {
                        failVideo.add(videoEntity);
                    }
                    upLoadNextVideo(true);
                } else if (fromUploadEventBean.getType() == 4) {//本地文件不存在
                    App.upCount--;
                } else if (fromUploadEventBean.getType() == 5) {//暂停
                } else if (fromUploadEventBean.getType() == 6) {//上传中
                    notification.contentView.setTextViewText(R.id.tv_start, "暂停");
                    notification.contentView.setProgressBar(R.id.pro, 100, fromUploadEventBean.getProgress(), false);
                    notificationManager.notify(1, notification);
                } else if (fromUploadEventBean.getType() == 7) {//上传失败
                    VideoEntity videoEntity = videoEntityList.get(index);
                    if (videoEntity != null) {
                        failVideo.add(videoEntity);
                    }
                    App.upCount--;
                    upLoadNextVideo(true);
                } else if (fromUploadEventBean.getType() == 8) {//上传完成
                    App.upCount--;
                    updateVideo(fileName);
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            upInfo();
                        }
                    }.start();
                } else if (fromUploadEventBean.getType() == 9) {//删除服务器文件
                    videoEntityList.get(index).setFail(false);
                    isFail = false;
                } else if (fromUploadEventBean.getType() == 10) {//已经上传过了
                    App.upCount--;
                    upLoadNextVideo(false);
                } else if (fromUploadEventBean.getType() == 11) {
                    VideoEntity videoEntity = videoEntityList.get(index);
                    if (videoEntity != null) {
                        failVideo.add(videoEntity);
                    }
                    videoEntityList.remove(index);
                    App.upCount--;
                    if (videoEntityList.size() == 0) {
                        stopForeground(true);
                        stopSelf();
                    }
//                    upLoadNextVideo(true);
                }
            }
        } catch (Exception e) {
        }
    }


    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
        super.onCreate();
        initData();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        buildNotify();
    }

    private void initData() {
        volleyUtils = new VolleyUtils();
        ftpUtils = new FtpUtils();
        sharedPreferencesUtils = new SharedPreferencesUtils(this);
        VideoTotalEntity stickyEvent = EventBus.getDefault().getStickyEvent(VideoTotalEntity.class);
        if (stickyEvent != null && stickyEvent.getVideoEntities().size() > 0) {
            App.upCount = stickyEvent.getVideoEntities().size();
            videoSize = stickyEvent.getVideoEntities().size();
            upLoadFileName = stickyEvent.getVideoEntities().get(0).getPath();
            size = stickyEvent.getVideoEntities().get(0).getLen();
            fileName = stickyEvent.getVideoEntities().get(0).getTitle();
            duration = stickyEvent.getVideoEntities().get(0).getDuration();
            intDuration = stickyEvent.getVideoEntities().get(0).getLongDuration();
            thumb = stickyEvent.getVideoEntities().get(0).getThumbPath();
            isFail = stickyEvent.getVideoEntities().get(0).isFail();
            Log.e("tag_fileName", fileName + "");
            videoEntityList = stickyEvent.getVideoEntities();
            index = 0;
            FromServiceEventBean fromServiceEventBean = new FromServiceEventBean();
            fromServiceEventBean.setEvent(3);
            fromServiceEventBean.setPoi(0);
            EventBus.getDefault().post(fromServiceEventBean);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("tag_onStartCommand", "onStartCommand");
        clickStatus = 1;//初始化状态为暂停
        if (videoEntityList != null) {
            if (videoEntityList.size() > 1) {
                upVideo(false);
            } else {
                upVideo(true);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    Notification notification;

    private void buildNotify() {
        notification = new Notification(R.mipmap.icon,
                "HUATEC", System.currentTimeMillis());
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.layout_notification);
        notification.contentView = view;
        IntentFilter filter_click = new IntentFilter();
        filter_click.addAction(START_UP);
        filter_click.addAction(CANCEL);
        registerReceiver(receiver_onclick, filter_click);//注册广播
        Intent Intent_pre = new Intent(START_UP);
        PendingIntent pendIntent_click = PendingIntent.getBroadcast(this, 0, Intent_pre, 0);//得到PendingIntent
        notification.contentView.setOnClickPendingIntent(R.id.tv_start, pendIntent_click); //设置监听
        Intent intent_cancel = new Intent(CANCEL);
        PendingIntent pendingIntent_cancel = PendingIntent.getBroadcast(this, 2, intent_cancel, 0);
        notification.contentView.setOnClickPendingIntent(R.id.tv_dismiss, pendingIntent_cancel);
        startForeground(1, notification);//前台运行
    }

    int clickStatus;//0：显示开始（还没有上传）； 1：显示暂停（正在上传中）；

    private BroadcastReceiver receiver_onclick = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("tag_action", action + "");
            if (action.equals(START_UP)) {
                FromServiceEventBean fromServiceEventBean = new FromServiceEventBean();
                if (clickStatus == 0) {
                    clickStatus = 1;
                    notification.contentView.setTextViewText(R.id.tv_start, "暂停");
                    fromServiceEventBean.setEvent(1);
                    fromServiceEventBean.setPoi(index);
                    EventBus.getDefault().post(fromServiceEventBean);
                } else if (clickStatus == 1) {
                    clickStatus = 0;
                    notification.contentView.setTextViewText(R.id.tv_start, "开始");
                    fromServiceEventBean.setEvent(2);
                    fromServiceEventBean.setPoi(index);
                    EventBus.getDefault().post(fromServiceEventBean);
                }
                notificationManager.notify(1, notification);
            } else {
                //关闭前台服务之前需要关闭上传操作
                stopForeground(true);
                stopSelf();
            }
        }
    };

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        Log.e("tag_service", "onDestroy");
        unregisterReceiver(receiver_onclick);
        stopForeground(true);
    }


    private void upInfo() {
        volleyUtils.upVideoInfo(fileName+ ".mp4", ConstantUtils.SERVERPATH + fileName + ".mp4", sharedPreferencesUtils.getString(ConstantUtils.USER_ID), size, String.valueOf(intDuration), upLoadFileName, new VolleyUtils.OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                Log.e("tag_onResponse", response);
                VideoUpInfoBean.VideoBean videoBean = new VideoUpInfoBean.VideoBean();
                videoBean.setFile_name(fileName);
                videoBean.setType(1);
                EventBus.getDefault().post(videoBean);
                //通知是否还有文件上传
                V v = new V();
                v.setFileName(fileName);
                EventBus.getDefault().post(v);
                upLoadNextVideo(false);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                upLoadNextVideo(false);
            }
        });
    }

    /*
    上传下一个视频
     */
    private void upLoadNextVideo(boolean upFail) {
        if (videoEntityList != null) {
            if (index != videoEntityList.size() - 1) {
                if (videoEntityList != null && videoEntityList.size() > 0) {
                    VideoEntity videoEntity = videoEntityList.remove(index);
                }
                upLoadFileName = videoEntityList.get(index).getPath();
                size = videoEntityList.get(index).getLen();
                fileName = videoEntityList.get(index).getTitle();
                duration = videoEntityList.get(index).getDuration();
                intDuration = videoEntityList.get(index).getLongDuration();
                isFail = videoEntityList.get(index).isFail();
                thumb = videoEntityList.get(index).getThumbPath();
                Log.e("tag_fileName", fileName + "");
                FromServiceEventBean fromServiceEventBean = new FromServiceEventBean();
                fromServiceEventBean.setEvent(3);
                fromServiceEventBean.setPoi(index);
                EventBus.getDefault().post(fromServiceEventBean);
                if (videoEntityList.size() > 1) {
                    upVideo(false);
                } else {
                    upVideo(true);
                }
            } else {
//                new CacheUtils().init().remove("v");
                sharedPreferencesUtils.clear("v");
                Log.e("tag_stop", "stopSelf");
                stopForeground(true);
                stopSelf();
            }
        }
    }


    private void upVideo(final boolean isClose) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                int status = ftpUtils.upLoad(upLoadFileName, fileName, index, isClose, isFail);
                Log.e("tag_status", status + "");
                if (status == 2) {
                    ftpUtils.upLoad(upLoadFileName, fileName, index, isClose, isFail);
                }
            }
        }.start();
    }

    /*
    更新正在上传的视频缓存
     */
    private void updateVideo(final String fileName) {
        new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    String info = sharedPreferencesUtils.getString("v");
                    if (info != null && !info.equals("")) {
                        VideoTotalEntity videoTotalEntity = new Gson().fromJson(info, VideoTotalEntity.class);
                        if (videoTotalEntity != null && videoTotalEntity.getVideoEntities().size() > 0) {
                            for (int i = 0; i < videoTotalEntity.getVideoEntities().size(); i++) {
                                if (fileName.equals(videoTotalEntity.getVideoEntities().get(i).getTitle())) {
                                    videoTotalEntity.getVideoEntities().remove(i);
                                    break;
                                }
                            }
                        }
                        if (videoTotalEntity != null && videoTotalEntity.getVideoEntities().size() != 0) {
                            String in = new Gson().toJson(videoTotalEntity);
                            sharedPreferencesUtils.putString("v",in);
                        }
                    }
                } catch (Exception e) {
                }
            }
        }.start();
    }


}
