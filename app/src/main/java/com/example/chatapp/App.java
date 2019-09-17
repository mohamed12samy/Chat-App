package com.example.chatapp;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class App extends Application {
    private static App myApp;

    private static FirebaseFirestore mFirebaseFirestore;
    private static FirebaseUser mFirebaseUser;

    public final static String NAME = "name";
    public final static String EMAIL = "email";
    public final static String URL_PHOTO = "url_photo";

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
    }

    public static App getInstance() {
        return myApp;
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
}
