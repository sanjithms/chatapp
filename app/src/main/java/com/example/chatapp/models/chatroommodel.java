package com.example.chatapp.models;

import com.google.firebase.Timestamp;

import java.util.List;

public class chatroommodel {
    String chatroomid;
    List<String> userids;
    Timestamp lastmessagesenttimestap;
    String Lastmessagesentid;

    public chatroommodel() {
    }

    public chatroommodel(String chatroomid, List<String> userids, Timestamp lastmessagesenttimestap, String lastmessagesentid) {
        this.chatroomid = chatroomid;
        this.userids = userids;
        this.lastmessagesenttimestap = lastmessagesenttimestap;
        Lastmessagesentid = lastmessagesentid;
    }

    public String getChatroomid() {
        return chatroomid;
    }

    public void setChatroomid(String chatroomid) {
        this.chatroomid = chatroomid;
    }

    public List<String> getUserids() {
        return userids;
    }

    public void setUserids(List<String> userids) {
        this.userids = userids;
    }

    public Timestamp getLastmessagesenttimestap() {
        return lastmessagesenttimestap;
    }

    public void setLastmessagesenttimestap(Timestamp lastmessagesenttimestap) {
        this.lastmessagesenttimestap = lastmessagesenttimestap;
    }

    public String getLastmessagesentid() {
        return Lastmessagesentid;
    }

    public void setLastmessagesentid(String lastmessagesentid) {
        Lastmessagesentid = lastmessagesentid;
    }
}


