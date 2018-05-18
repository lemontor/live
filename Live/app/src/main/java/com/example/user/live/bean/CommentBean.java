package com.example.user.live.bean;

import java.util.List;

/**
 * Created by user on 2018/1/12.
 */
public class CommentBean  extends  CommonBean {

    private  DataBean data;

    public CommentBean(DataBean data) {
        this.data = data;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static  class  DataBean{
        private List<commentListBean> commentList;
        private int  peopleCount;


        public DataBean() {
        }

        public List<commentListBean> getCommentList() {
            return commentList;
        }

        public void setCommentList(List<commentListBean> commentList) {
            this.commentList = commentList;
        }

        public int getPeopleCount() {
            return peopleCount;
        }

        public void setPeopleCount(int peopleCount) {
            this.peopleCount = peopleCount;
        }
    }

    public static class commentListBean{
        private  String create_user_avatar;

        private  String create_time;
        private  String join_id;
        private  String remark;
        private  int sort;
        private  String content;
        private  int is_show;
        private  int is_used;
        private  String update_time;
        private  String update_user;
        private  int is_del;
        private  String create_user;
        private  String timeStatus;
        private  String video_id;
        private  String nameContent;


        public commentListBean() {
        }

        public String getNameContent() {
            return nameContent;
        }

        public void setNameContent(String nameContent) {
            this.nameContent = nameContent;
        }

        public String getCreate_user_avatar() {
            return create_user_avatar;
        }

        public void setCreate_user_avatar(String create_user_avatar) {
            this.create_user_avatar = create_user_avatar;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getJoin_id() {
            return join_id;
        }

        public void setJoin_id(String join_id) {
            this.join_id = join_id;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getIs_show() {
            return is_show;
        }

        public void setIs_show(int is_show) {
            this.is_show = is_show;
        }

        public int getIs_used() {
            return is_used;
        }

        public void setIs_used(int is_used) {
            this.is_used = is_used;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public String getUpdate_user() {
            return update_user;
        }

        public void setUpdate_user(String update_user) {
            this.update_user = update_user;
        }

        public int getIs_del() {
            return is_del;
        }

        public void setIs_del(int is_del) {
            this.is_del = is_del;
        }

        public String getCreate_user() {
            return create_user;
        }

        public void setCreate_user(String create_user) {
            this.create_user = create_user;
        }

        public String getTimeStatus() {
            return timeStatus;
        }

        public void setTimeStatus(String timeStatus) {
            this.timeStatus = timeStatus;
        }

        public String getVideo_id() {
            return video_id;
        }

        public void setVideo_id(String video_id) {
            this.video_id = video_id;
        }
    }



}
