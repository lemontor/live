package com.example.user.live.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;

import com.example.user.live.video.entity.VideoEntity;
import com.google.gson.Gson;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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


    public static Map<String,List<VideoEntity>> getList(Context context) {
        DecimalFormat df = new DecimalFormat("#.0");
        List<VideoEntity> sysVideoList = new ArrayList<>();
        Map<String,List<VideoEntity>>  listMap = new HashMap<>();
        // MediaStore.Video.Thumbnails.DATA:视频缩略图的文件路径
        String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID};
        // 视频其他信息的查询条件
        String[] mediaColumns = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATE_ADDED};

        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media
                        .EXTERNAL_CONTENT_URI,
                mediaColumns, null, null, null);

        if (cursor == null) {
            return listMap;
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
                String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                String titleName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String createDate = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));

                int len = 0;
                if (size != null && !size.equals("")) {
                    len = Integer.valueOf(size);
                    if (len > (1024 * 1024)) {//计算为MB
                        float MB = len / (float) (1024 * 1024);
                        info.setSize(df.format(MB) + "MB");
                    } else if (len > 1024) {//计算为KB
                        float KB = len / (float) 1024;
                        info.setSize(df.format(KB) + "KB");
                    } else {//计算为B
                        info.setSize(len + "B");
                    }
                }
                info.setLen(len);
                long creDate = 0;
                String dateType = "";
                if (createDate != null) {
                    creDate = Long.valueOf(createDate);
                    dateType = isToday(creDate);
                    info.setTimeStatus(dateType);
                }
                info.setCreateDate(creDate);
                info.setPath(path);
                info.setTitle(titleName);
                info.setDuration(getTime(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video
                        .Media.DURATION))));
                if(listMap.containsKey(dateType)){
                    List<VideoEntity>  videoEntityList = listMap.get(dateType);
                    if(videoEntityList != null){
                        videoEntityList.add(info);
                    }
                }else{
                   List<VideoEntity>  videoEntityList = new ArrayList<>();
                    videoEntityList.add(info);
                    listMap.put(dateType,videoEntityList);
                }
//                sysVideoList.add(info);
            } while (cursor.moveToNext());
            cursor.close();
//            Collections.sort(sysVideoList, new Comparator<VideoEntity>() {//降序排列
//                @Override
//                public int compare(VideoEntity videoEntity, VideoEntity t1) {
//                    if (videoEntity.getCreateDate() > t1.getCreateDate()) {
//                        return 1;
//                    } else if (videoEntity.getCreateDate() == t1.getCreateDate()) {
//                        return 0;
//                    } else {
//                        return -1;
//                    }
//                }
//            });
            String json = new Gson().toJson(listMap);
            Log.e("tag_json",json+"");
        }
        return listMap;
    }


    /*
    返回 0为当前
    返回-1为昨天
    返回-2为其他
     */
    public static String isToday(long date) {
        String day = "";
        long realDate = date * 1000;
        try {
            Calendar curCalender = Calendar.getInstance();
            Date curDate = new Date(System.currentTimeMillis());
            curCalender.setTime(curDate);


            Calendar dateCalender = Calendar.getInstance();
            Date thisDate = new Date(realDate);
            dateCalender.setTime(thisDate);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            int nowYear = curCalender.get(Calendar.YEAR);
            int createYear = dateCalender.get(Calendar.YEAR);
            if (nowYear == createYear) {
                int nowDay = curCalender.get(Calendar.DAY_OF_YEAR);
                int createDay = dateCalender.get(Calendar.DAY_OF_YEAR);
                if (nowDay == createDay) {//今天
                    return "0";
                } else if (createDay - nowDay == -1) {//昨天
                    return "-1";
                } else {
                    return simpleDateFormat.format(thisDate);
                }
            } else {
                return simpleDateFormat.format(thisDate);
            }
//            return day;
        } catch (Exception e) {
            Log.e("tag_exception", e.getMessage() + "");
            return "0";
        }
    }


    public static String getTime(long date) {
        int second = (int) (date / 1000);
        if (second > 60) {
            int minte = second / 60;
            int tempSecond = second % 60;
            String zeroMinte = "";
            if (minte < 10) {
                zeroMinte = "0" + zeroMinte + ":";
            } else {
                zeroMinte = String.valueOf(zeroMinte + ":");
            }
            String zeroSecond = "";
            if (tempSecond < 10) {
                zeroSecond = "0" + zeroSecond + "";
            }
            return zeroMinte + " " + zeroSecond;
        } else {
            if (second >= 10) {
                return "00:" + String.valueOf(second);
            } else {
                return "00:0" + String.valueOf(second);
            }
        }
    }


}
