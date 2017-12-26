package com.smartspatial.sschat;

import android.util.Log;

public class ChatDTO {

    private String userName;
    private String message;

    public ChatDTO() {
    }


    public ChatDTO(String userName, String message) {
        this.userName = userName;
        Log.d("check", userName);
        this.message = message;
        Log.d("check", message);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }
}