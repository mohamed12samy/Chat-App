package com.example.chatapp;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ChatRoomViewModel extends AndroidViewModel {

    private MutableLiveData<List<Message>> messages;
    private Repository repository;

    public ChatRoomViewModel(@NonNull Application application, String secondUserId) {
        super(application);
        repository = Repository.getInstance();
        messages = repository.getMessages(secondUserId);
    }

    public void sendMessage(String body) {
        repository.sendMessage(body);
    }

    public String getMyId() {
        return repository.getMyId();
    }

    public MutableLiveData<List<Message>> getMessages() {
        return messages;
    }

    public void onChatClose() {
        repository.onChatClose();
    }
}
