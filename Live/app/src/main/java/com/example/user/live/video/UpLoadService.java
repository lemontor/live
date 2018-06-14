//package com.example.user.live.video;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AlertDialog;
//import android.util.Log;
//import android.view.View;
//import android.widget.RemoteViews;
//
//import com.example.user.live.R;
//import com.example.user.live.utils.FtpUpLoadUtils;
//import com.example.user.live.video.entity.FromActivityEventBean;
//import com.example.user.live.video.entity.FromServiceEventBean;
//import com.example.user.live.video.entity.FromUploadEventBean;
//import com.example.user.live.video.entity.FtpUploadInfoEntity;
//import com.example.user.live.video.entity.VideoEntity;
//import com.example.user.live.video.entity.VideoTotalEntity;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.util.List;
//
///**
// * Created by user on 2018/5/29.
// */
//public class UpLoadService extends Service {
//
//    private static final String START_UP = "start";
//    private static final String STOP_UP = "stop";
//    private static final String CANCEL = "cancel";
//    NotificationManager notificationManager;
//    private int index = 0;
//    private int videoSize;
//    private String upLoadFileName;
//    private List<VideoEntity> videoEntityList;
//    private FtpUpLoadUtils ftpUpLoadUtils;
//    private long size;
//    private String fileName;
//    private FtpUploadInfoEntity entity;
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void fromActivityEvent(FromActivityEventBean activityEventBean) {
//        if (activityEventBean != null) {
//            if (activityEventBean.getEvent() == 1) {//开始
//                notification.contentView.setProgressBar(R.id.pro, 100, 0, false);//重新初始化notification的显示
//                notification.contentView.setTextViewText(R.id.tv_start, "暂停");
//                //当前的index暂停
//                index = activityEventBean.getPoi();//记录当前正在上传的视频下标
//                Log.e("tag_index", index + "");
//                ftpUpLoadUtils.reUpload(UpLoadService.this,entity);
//            } else if (activityEventBean.getEvent() == 2) {//暂停
////                notification.contentView.setTextViewText(R.id.tv_start, "开始");
//                ftpUpLoadUtils.abortDataTransfer();
//                stopForeground(true);
//                stopSelf();
//            }
//            notificationManager.notify(1, notification);
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void fromUpLoadEvent(FromUploadEventBean fromUploadEventBean) {
//        if (fromUploadEventBean != null) {
//            if (fromUploadEventBean.getType() == 1) {//开始
//                notification.contentView.setTextViewText(R.id.tv_start, "暂停");
//                clickStatus = 0;
//            } else if (fromUploadEventBean.getType() == 2) {//上传中
//                notification.contentView.setTextViewText(R.id.tv_start, "暂停");
//                notification.contentView.setProgressBar(R.id.pro, 100, fromUploadEventBean.getProgress(), false);
//            } else if (fromUploadEventBean.getType() == 3) {//完成
////                notification.contentView.setProgressBar(R.id.pro, 100, 0, false);
////                notification.contentView.setTextViewText(R.id.tv_start, "开始");
////                clickStatus = 1;
//                stopForeground(true);
//                stopSelf();
//            } else if (fromUploadEventBean.getType() == 4) {//失败
////                notification.contentView.setTextViewText(R.id.tv_start, "开始");
////                clickStatus = 1;
//                stopForeground(true);
//                stopSelf();
//            } else if (fromUploadEventBean.getType() == 5) {//终止
////                notification.contentView.setTextViewText(R.id.tv_start, "开始");
////                clickStatus = 1;
//                stopForeground(true);
//                stopSelf();
//            }
//            notificationManager.notify(1, notification);
//        }
//    }
//
//
//    @Override
//    public void onCreate() {
//        EventBus.getDefault().register(this);
//        super.onCreate();
//        initData();
//        notificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
//        buildNotify();
//    }
//
//    private void initData() {
//        ftpUpLoadUtils = new FtpUpLoadUtils();
//        VideoTotalEntity stickyEvent = EventBus.getDefault().getStickyEvent(VideoTotalEntity.class);
//        if (stickyEvent != null) {
//            videoSize = stickyEvent.getVideoEntities().size();
//            upLoadFileName = stickyEvent.getVideoEntities().get(0).getPath();
//            size = stickyEvent.getVideoEntities().get(0).getLen();
//            fileName = stickyEvent.getVideoEntities().get(0).getTitle();
//            videoEntityList = stickyEvent.getVideoEntities();
//            index = 0;
//            FromServiceEventBean fromServiceEventBean = new FromServiceEventBean();
//            fromServiceEventBean.setEvent(3);
//            fromServiceEventBean.setPoi(0);
//            EventBus.getDefault().post(fromServiceEventBean);
//        }
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        final FtpUploadInfoEntity infoEntity = new FtpUploadInfoEntity();
//        infoEntity.ip = "49.4.15.103";
//        infoEntity.userName = "study1";
//        infoEntity.password = "study1";
//        infoEntity.port = 21;
//        infoEntity.path = upLoadFileName;
////        infoEntity.serverPath = "/video/dev/android/";
//        infoEntity.serverPath = "/mnt/sdc/storage/video/dev/android/";
//        infoEntity.len = size;
//        infoEntity.fileName = fileName;
//        clickStatus = 1;//初始化状态为暂停
//        this.entity = infoEntity;
////        new Thread(){
////            @Override
////            public void run() {
////                super.run();
//        ftpUpLoadUtils.uploadFile(UpLoadService.this, infoEntity);
////            }
////        }.start();
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//
//    Notification notification;
//
//    private void buildNotify() {
//        notification = new Notification(R.mipmap.icon,
//                "HUATEC", System.currentTimeMillis());
//        RemoteViews view = new RemoteViews(getPackageName(), R.layout.layout_notification);
//        notification.contentView = view;
//        IntentFilter filter_click = new IntentFilter();
//        filter_click.addAction(START_UP);
//        filter_click.addAction(CANCEL);
//        registerReceiver(receiver_onclick, filter_click);//注册广播
//        Intent Intent_pre = new Intent(START_UP);
//        PendingIntent pendIntent_click = PendingIntent.getBroadcast(this, 0, Intent_pre, 0);//得到PendingIntent
//        notification.contentView.setOnClickPendingIntent(R.id.tv_start, pendIntent_click); //设置监听
//        Intent intent_cancel = new Intent(CANCEL);
//        PendingIntent pendingIntent_cancel = PendingIntent.getBroadcast(this, 2, intent_cancel, 0);
//        notification.contentView.setOnClickPendingIntent(R.id.tv_dismiss, pendingIntent_cancel);
//        startForeground(1, notification);//前台运行
//    }
//
//    int clickStatus;//0：显示开始（还没有上传）； 1：显示暂停（正在上传中）；
//
//    private BroadcastReceiver receiver_onclick = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            Log.e("tag_action", action + "");
//            if (action.equals(START_UP)) {
//                FromServiceEventBean fromServiceEventBean = new FromServiceEventBean();
//                if (clickStatus == 0) {
//                    clickStatus = 1;
//                    notification.contentView.setTextViewText(R.id.tv_start, "暂停");
//                    fromServiceEventBean.setEvent(1);
//                    fromServiceEventBean.setPoi(index);
//                    EventBus.getDefault().post(fromServiceEventBean);
//                } else if (clickStatus == 1) {
//                    clickStatus = 0;
//                    notification.contentView.setTextViewText(R.id.tv_start, "开始");
//                    fromServiceEventBean.setEvent(2);
//                    fromServiceEventBean.setPoi(index);
//                    EventBus.getDefault().post(fromServiceEventBean);
//                }
//                notificationManager.notify(1, notification);
//            } else {
//                //关闭前台服务之前需要关闭上传操作
//                stopForeground(true);
//                stopSelf();
//            }
//        }
//    };
//
//    @Override
//    public void onDestroy() {
//        EventBus.getDefault().unregister(this);
//        super.onDestroy();
//        Log.e("tag_service","onDestroy");
//        unregisterReceiver(receiver_onclick);
//        stopForeground(true);
//    }
//
//
//}
