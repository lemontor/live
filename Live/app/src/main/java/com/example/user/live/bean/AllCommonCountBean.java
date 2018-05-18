package com.example.user.live.bean;

/**
 * Created by user on 2018/1/14.
 */
public class AllCommonCountBean extends CommonBean {

    private  CommonCountBean  data;

    public AllCommonCountBean() {
    }

    public CommonCountBean getData() {
        return data;
    }

    public void setData(CommonCountBean data) {
        this.data = data;
    }

    public static  class  CommonCountBean{
        public int  peopleCount;
        public int  commentCount;

        public CommonCountBean() {
        }

        public int getPeopleCount() {
            return peopleCount;
        }

        public void setPeopleCount(int peopleCount) {
            this.peopleCount = peopleCount;
        }

        public int getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }
    }




}
