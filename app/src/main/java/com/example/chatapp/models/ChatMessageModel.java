package com.example.chatapp.models;

import com.google.firebase.Timestamp;

public class ChatMessageModel {
    private String message;
    private String senderId;
    private Timestamp timestamp;
    private String messageId;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String messageid,String message, String senderId, Timestamp timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.messageId=messageid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
