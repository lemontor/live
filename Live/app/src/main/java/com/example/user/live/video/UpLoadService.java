package com.example.user.live.video;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.user.live.R;
import com.example.user.live.video.entity.VideoEntity;
import com.example.user.live.video.entity.VideoTotalEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by user on 2018/5/29.
 */
public class UpLoadService extends Service{

    private static final String  START_UP = "start";
    private static final String  STOP_UP = "stop";
    private static final String  CANCEL = "cancel";
    NotificationManager notificationManager;
    private int index = 0;
    private int videoSize;
    private String  upLoadFileName;
    private List<VideoEntity>  videoEntityList;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void  fromActivityEvent(){

    }



    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        initData();
        notificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        buildNotify();
    }

    private void initData() {
        VideoTotalEntity stickyEvent = EventBus.getDefault().getStickyEvent(VideoTotalEntity.class);
        if(stickyEvent != null){
            videoSize = stickyEvent.getVideoEntities().size();
            upLoadFileName = stickyEvent.getVideoEntities().get(0).getPath();
            videoEntityList = stickyEvent.getVideoEntities();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    Notification notification;
    private void  buildNotify(){
        notification = new Notification(R.mipmap.icon,
                "HUATEC", System.currentTimeMillis());
        RemoteViews view = new RemoteViews(getPackageName(),R.layout.layout_notification);
        notification.contentView = view;
        IntentFilter filter_click = new IntentFilter();
        filter_click.addAction(START_UP);
        filter_click.addAction(CANCEL);
        registerReceiver(receiver_onclick, filter_click);//注册广播
        Intent Intent_pre = new Intent(START_UP);
        PendingIntent pendIntent_click = PendingIntent.getBroadcast(this, 0, Intent_pre, 0);//得到PendingIntent
        notification.contentView.setOnClickPendingIntent(R.id.tv_start,pendIntent_click); //设置监听
        Intent intent_cancel = new Intent(CANCEL);
        PendingIntent pendingIntent_cancel = PendingIntent.getBroadcast(this,2,intent_cancel,0);
        notification.contentView.setOnClickPendingIntent(R.id.tv_dismiss,pendingIntent_cancel);
        startForeground(1, notification);//前台运行
    }

    private BroadcastReceiver receiver_onclick = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("tag_action",action+"");
        }
    };

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }
}
