package com.example.chatapp;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Repository {
    private static Repository mRepository;
    private FirebaseFirestore db;

    private Repository(){
        db = App.getFirebaseFirestore();
    }

    public static synchronized Repository getInstance(){

        if(mRepository == null){
            mRepository = new Repository();
        }
        return mRepository;
    }


    public void AddNewUser(FirebaseUser user){
        String name = user.getDisplayName();
        String email = user.getEmail();
        String photo = String.valueOf(user.getPhotoUrl());
        Map<String, Object> mUser = new HashMap<>();
        mUser.put(App.NAME, name);
        mUser.put(App.EMAIL, email);
        mUser.put(App.URL_PHOTO, photo);
        db.collection("Users").document().set(mUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
}
