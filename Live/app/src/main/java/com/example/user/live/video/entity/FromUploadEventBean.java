package com.example.user.live.video.entity;

/**
 * Created by user on 2018/5/31.
 */
public class FromUploadEventBean {
    private  int  eventType;//2：来自Upload;
    private  int  progress;//进度
    private  int  type;//1:开始；2：上传中；3：完成；4：失败；5：终止；10：网络不可用；11：不是wifi环境；12：是wifi环境
    private  String len;//已经下载的数据
    private  int  poi;

    public FromUploadEventBean() {
    }

    public int getPoi() {
        return poi;
    }

    public void setPoi(int poi) {
        this.poi = poi;
    }

    public String getLen() {
        return len;
    }

    public void setLen(String len) {
        this.len = len;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

}
