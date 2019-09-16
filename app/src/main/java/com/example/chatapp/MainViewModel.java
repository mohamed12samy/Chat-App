package com.example.chatapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

public class MainViewModel extends AndroidViewModel {

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public void addNewUser(FirebaseUser user){
        Repository.getInstance().AddNewUser(user);
    }
}
