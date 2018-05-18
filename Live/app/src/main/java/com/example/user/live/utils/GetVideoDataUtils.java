package com.example.user.live.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;

import com.example.user.live.video.entity.VideoEntity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2018/4/24.
 */
public class GetVideoDataUtils {


    public static List<VideoEntity> getVideoInfo(Context context) {
        List<VideoEntity> lists = new ArrayList<>();
        String[] projection = new String[]{
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION
        };
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
            File file = new File(path);
            long date = 0;
            String title = "";
            if (path != null) {
                int start = path.lastIndexOf("/");
                int end = path.lastIndexOf(".");
                title = path.substring(start + 1, end);
            }
            if (file.exists()) {
                date = file.lastModified();
                long len = file.length();
                Log.e("tag_len", len + "");
            }
            Log.e("tag_video", "path:" + path + "  duration:" + duration + "  title:" + title + "  thumb:" + "thumb" + "  date:" + date + "  displayName:" + "displayName"
                    + "  size:" + "size");
        }
        cursor.close();
        return lists;
    }


    public static List<List<VideoEntity>> getList(Context context) {
        DecimalFormat df = new DecimalFormat("#.0");
        List<VideoEntity> sysVideoList = new ArrayList<>();
        List<List<VideoEntity>> totalListVideo = new ArrayList<>();
        // MediaStore.Video.Thumbnails.DATA:视频缩略图的文件路径
        String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID};
        // 视频其他信息的查询条件
        String[] mediaColumns = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION};

        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media
                        .EXTERNAL_CONTENT_URI,
                mediaColumns, null, null, null);

        if (cursor == null) {
            return totalListVideo;
        }
        if (cursor.moveToFirst()) {
            do {
                VideoEntity info = new VideoEntity();
                int id = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Video.Media._ID));
                Cursor thumbCursor = context.getContentResolver().query(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
                                + "=" + id, null, null);
                if (thumbCursor.moveToFirst()) {
                    //获取缩略图
                    info.setThumbPath(thumbCursor.getString(thumbCursor
                            .getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
                }
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                File file = new File(path);
                long date = 0;
                String title = "";
                if (path != null) {
                    int start = path.lastIndexOf("/");
                    int end = path.lastIndexOf(".");
                    title = path.substring(start + 1, end);
                }
                if (file.exists()) {
                    date = file.lastModified();
                    long len = file.length();
                    if (len > (1024 * 1024)) {//计算为MB
                        float MB = len / (float) (1024 * 1024);
                        info.setSize(df.format(MB) + "MB");
                    } else if (len > 1024) {//计算为KB
                        float KB = len / (float) 1024;
                        info.setSize(df.format(KB) + "KB");
                    } else {//计算为B
                        info.setSize(len + "B");
                    }
                    info.setLen(len);
                    if (date != 0) {
                        info.setTimeStatus(isToday(date));
                        String str_date = String.valueOf(date);
                        String use_date = str_date.substring(0, str_date.length() - 3);
                        date = Long.valueOf(use_date);
                    }
                    info.setCreateDate(date);
                }
                info.setPath(path);
                info.setTitle(title);
                info.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video
                        .Media.DURATION)));
                sysVideoList.add(info);
            } while (cursor.moveToNext());
            cursor.close();
            Collections.sort(sysVideoList, new Comparator<VideoEntity>() {//降序排列
                @Override
                public int compare(VideoEntity videoEntity, VideoEntity t1) {
                    if (videoEntity.getCreateDate() > t1.getCreateDate()) {
                        return 1;
                    } else if (videoEntity.getCreateDate() == t1.getCreateDate()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
            List<VideoEntity> today = new ArrayList<>();
            List<VideoEntity> yest = new ArrayList<>();
            List<VideoEntity> other = new ArrayList<>();
            for (VideoEntity videoEntity : sysVideoList) {
                if (videoEntity.getTimeStatus() == 0) {
                    today.add(videoEntity);
                } else if (videoEntity.getTimeStatus() == -1) {
                    yest.add(videoEntity);
                } else if (videoEntity.getTimeStatus() == -2) {
                    other.add(videoEntity);
                }
            }
            if (today.size() > 0) {
                totalListVideo.add(today);
            }
            if (yest.size() > 0) {
                totalListVideo.add(yest);
            }
            if (other.size() > 0) {
                totalListVideo.add(other);
            }
        }
        return totalListVideo;
    }


    /*
    返回 0为当前
    返回-1为昨天
    返回-2为其他
     */
    public static int isToday(long date) {
        int day = -2;
        try {
            Calendar curCalender = Calendar.getInstance();
            Date curDate = new Date(System.currentTimeMillis());
            curCalender.setTime(curDate);

            Calendar dateCalender = Calendar.getInstance();
            Date thisDate = new Date(date);
            dateCalender.setTime(thisDate);

            if (curCalender.get(Calendar.YEAR) == dateCalender.get(Calendar.YEAR)) {
                day = dateCalender.get(Calendar.DAY_OF_YEAR) - curCalender.get(Calendar.DAY_OF_YEAR);
            }
            return day;
        } catch (Exception e) {
            return day;
        }

    }


}
