package com.example.chatapp;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class Message {
    private String id;
    private String senderId;
    private String body;
    private Timestamp timestamp;

    public Message() {}

    public Message(String senderId, String body, Timestamp timestamp) {
        this.senderId = senderId;
        this.body = body;
        this.timestamp = timestamp;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
