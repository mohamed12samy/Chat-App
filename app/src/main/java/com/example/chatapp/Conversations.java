package com.example.chatapp;

public class Conversations {

    private String UserId1;
    private String UserId2;
    private String lastMessage;

    public Conversations(String userId1, String userId2, String lastMessageId) {
        UserId1 = userId1;
        UserId2 = userId2;
        this.lastMessage = lastMessageId;
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessageId) {
        this.lastMessage = lastMessageId;
    }
}