package com.example.user.live.video.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by user on 2018/4/26.
 */
public class VideoTotalEntity implements Serializable{

    private  String title;
    private  List<VideoEntity>  videoEntities;
    private  boolean  isChose;

    public boolean isChose() {
        return isChose;
    }

    public void setChose(boolean chose) {
        isChose = chose;
    }

    public VideoTotalEntity() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<VideoEntity> getVideoEntities() {
        return videoEntities;
    }

    public void setVideoEntities(List<VideoEntity> videoEntities) {
        this.videoEntities = videoEntities;
    }

    @Override
    public String toString() {
        return "VideoTotalEntity{" +
                "title='" + title + '\'' +
                ", videoEntities=" + videoEntities +
                ", isChose=" + isChose +
                '}';
    }
}
