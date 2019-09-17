package com.example.chatapp;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {

    private List<String> users = new ArrayList<>();

    public ChatRoom(List<String> users) {
        this.users = users;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}