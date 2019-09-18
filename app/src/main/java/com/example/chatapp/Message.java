package com.example.chatapp;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class Message {
    private String id;
    private String senderId;
    private String body;
    private Timestamp timestamp;
    private String photo_url;

    public Message() {}

    public Message(String senderId, String body, Timestamp timestamp) {
        this.senderId = senderId;
        this.body = body;
        this.timestamp = timestamp;
    }

    public Message(String id, String senderId, Timestamp timestamp, String photo_url) {
        this.id = id;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.photo_url = photo_url;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
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
