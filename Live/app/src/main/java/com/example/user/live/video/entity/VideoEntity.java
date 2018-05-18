package com.example.user.live.video.entity;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by user on 2018/4/24.
 */
public class VideoEntity implements Serializable ,Comparator<VideoEntity>{
    private String  title;
    private String  path;
    private int duration;
    private String thumbPath;
    private long len;
    private long  createDate;
    private String size;
    private int   timeStatus;// 0:当日，-1：昨天，-2：其他日子
    private boolean  isChose;


    public boolean isChose() {
        return isChose;
    }

    public void setChose(boolean chose) {
        isChose = chose;
    }

    public VideoEntity(){

    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getLen() {
        return len;
    }

    public void setLen(long len) {
        this.len = len;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public int getTimeStatus() {
        return timeStatus;
    }

    public void setTimeStatus(int timeStatus) {
        this.timeStatus = timeStatus;
    }

    @Override
    public String toString() {
        return "VideoEntity{" +
                "title='" + title + '\'' +
                ", path='" + path + '\'' +
                ", duration=" + duration +
                ", thumbPath='" + thumbPath + '\'' +
                ", len=" + len +
                ", createDate=" + createDate +
                ", size='" + size + '\'' +
                ", timeStatus=" + timeStatus +
                '}';
    }

    @Override
    public int compare(VideoEntity videoEntity, VideoEntity t1) {
        long date = videoEntity.getCreateDate() - t1.getCreateDate();
        if(date == 0){
           return 0;
        }
        return 1;
    }
}
