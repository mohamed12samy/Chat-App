package com.example.chatapp;

public class Conversations {

    private String UserId1;
    private String UserId2;
    private String lastMessageId;

    public Conversations(String userId1, String userId2, String lastMessageId) {
        UserId1 = userId1;
        UserId2 = userId2;
        this.lastMessageId = lastMessageId;
    }

    public String getUserId1() {
        return UserId1;
    }

    public void setUserId1(String userId1) {
        UserId1 = userId1;
    }

    public String getUserId2() {
        return UserId2;
    }

    public void setUserId2(String userId2) {
        UserId2 = userId2;
    }

    public String getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }
}