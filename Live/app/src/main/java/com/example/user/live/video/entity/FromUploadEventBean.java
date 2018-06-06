package com.example.user.live.video.entity;

/**
 * Created by user on 2018/5/31.
 */
public class FromUploadEventBean {
    private  int  eventType;//2：来自Upload;
    private  int  progress;//进度
    private  int  type;//1:开始；2：上传中；3：完成；4：失败；5：终止；
    private  long len;

    public FromUploadEventBean() {
    }

    public long getLen() {
        return len;
    }

    public void setLen(long len) {
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
