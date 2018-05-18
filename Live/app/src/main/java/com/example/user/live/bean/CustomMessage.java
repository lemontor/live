package com.example.user.live.bean;

/**
 * Created by user on 2018/1/14.
 */
public class CustomMessage {
    private  String  message;


    public CustomMessage() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CustomMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
