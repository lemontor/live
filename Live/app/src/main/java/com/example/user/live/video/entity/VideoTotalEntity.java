package com.example.user.live.video.entity;

import java.util.List;

/**
 * Created by user on 2018/4/26.
 */
public class VideoTotalEntity {

    private  String title;
    private  List<VideoEntity>  videoEntities;


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
}