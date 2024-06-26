package com.example.chatapp.models;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phonenumber;
    private String username;
    private Timestamp createdtimestap;
    private String userId;
    private boolean isOnline;
    private  String fcmtoken;

    public UserModel() {
    }

    public UserModel(String phonenumber, String username, Timestamp createdtimestap, String userId) {
        this.phonenumber = phonenumber;
        this.username = username;
        this.createdtimestap = createdtimestap;
        this.userId=userId;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedtimestap() {
        return createdtimestap;
    }

    public void setCreatedtimestap(Timestamp createdtimestap) {
        this.createdtimestap = createdtimestap;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getFcmtoken() {
        return fcmtoken;
    }

    public void setFcmtoken(String fcmtoken) {
        this.fcmtoken = fcmtoken;
    }
}
