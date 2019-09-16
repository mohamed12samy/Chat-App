package com.example.chatapp;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class ConversationsViewModel extends AndroidViewModel {

    private MutableLiveData<List<Pair<User , String>>> users = new MutableLiveData<>();
    private Repository mainRepository;

    public ConversationsViewModel(@NonNull Application application) {
        super(application);
        mainRepository = Repository.getInstance();

    }


    public LiveData<List<Pair<User , String>>> getConversations(){

        users = (MutableLiveData<List<Pair<User , String>>>) mainRepository.getConversations();
        if(users.getValue() != null)
            Log.d("dddddddddd",users.getValue().size()+"");
        return users;
    }
}
