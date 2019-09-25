package com.example.chatapp;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class App extends Application {
    private static App myApp;

    private static FirebaseFirestore mFirebaseFirestore;
    private static FirebaseUser mFirebaseUser;
    private static FirebaseStorage storage;
    private static StorageReference photoReference ;
    private static FirebaseAuth mAuth;


    public final static String NAME = "name";
    public final static String EMAIL = "email";
    public final static String URL_PHOTO = "url_photo";
    public final static String TOKEN = "token";
    private static String token;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        photoReference = storage.getReference().child("images");
        mAuth = FirebaseAuth.getInstance();

    }

    public static App getInstance() {
        return myApp;
    }

    public static FirebaseAuth getmAuth() {
        return mAuth;
    }

    public static void setmAuth(FirebaseAuth mAuth) {
        App.mAuth = mAuth;
    }

    public static FirebaseFirestore getFirebaseFirestore(){
        return mFirebaseFirestore;
    }

    public static FirebaseUser getmFirebaseUser() {
        return mFirebaseUser;
    }

    public static void setmFirebaseUser(FirebaseUser mFirebaseUser) {
        App.mFirebaseUser = mFirebaseUser;
    }

    public static StorageReference getPhotoReference() {
        return photoReference;
    }

    public static void setToken (String mToken)
    {
        mToken = token;
    }

    public static String getToken()
    {
        return token;
    }
}
