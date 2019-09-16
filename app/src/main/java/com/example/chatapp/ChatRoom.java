package com.example.chatapp;

public class ChatRoom {

    private String UserEmail1;
    private String UserEmail2;
    private String lastMessage;

    public ChatRoom(String userEmail1, String userEmail2, String lastMessage) {
        UserEmail1 = userEmail1;
        UserEmail2 = userEmail2;
        this.lastMessage = lastMessage;
    }

    public String getUserEmail1() {
        return UserEmail1;
    }

    public void setUserEmail1(String userEmail1) {
        UserEmail1 = userEmail1;
    }

    public String getUserEmail2() {
        return UserEmail2;
    }

    public void setUserEmail2(String userEmail2) {
        UserEmail2 = userEmail2;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}