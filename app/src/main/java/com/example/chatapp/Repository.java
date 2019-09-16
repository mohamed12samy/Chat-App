package com.example.chatapp;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repository {
    private static Repository mRepository;
    private FirebaseFirestore db;

    private Repository() {
        db = App.getFirebaseFirestore();
    }

    public static synchronized Repository getInstance() {

        if (mRepository == null) {
            mRepository = new Repository();
        }
        return mRepository;
    }


    public void AddNewUser(FirebaseUser user) {
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

    private MutableLiveData<List<User>> users = new MutableLiveData<>();
    private MutableLiveData<List<Conversations>> conversations = new MutableLiveData<>();

    public LiveData<List<User>> getUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<User> list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData().get("email"));

                                if (!document.getData().get("email").equals( App.getmFirebaseUser().getEmail())) {

                                    Log.d("asdd",document.getData().get("email") +"   "+ App.getmFirebaseUser().getEmail());

                                    list.add(new User(document.getId(), document.getData().get("name") + "",
                                            document.getData().get("url_photo") + "", document.getData().get("email") + ""));
                                }
                            }
                            users.postValue(list);
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
        return users;
    }

    public LiveData<List<Conversations>> getConversations() {

        db.collection("ChatRooms")
                .whereEqualTo("UserEmail2", App.getmFirebaseUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Conversations> list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("2 email1", document.getId() + " => " + document.getData().get("UserEmail1"));
                                Log.d("2 email2", document.getId() + " => " + document.getData().get("UserEmail2"));

                                list.add(new Conversations(document.getData().get("UserEmail1")+"",
                                        document.getData().get("UserEmail2")+"",
                                        document.getData().get("lastMessageId")+""
                                ));
                            }
                            //conversations.postValue(list);
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });

        db.collection("ChatRooms")
                .whereEqualTo("UserEmail1", App.getmFirebaseUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Conversations> list = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("1 email1", document.getId() + " => " + document.getData().get("UserEmail1"));
                                Log.d("1 email2", document.getId() + " => " + document.getData().get("UserEmail2"));

                                list.add(new Conversations(document.getData().get("UserEmail1")+"",
                                        document.getData().get("UserEmail2")+"",
                                        document.getData().get("lastMessageId")+""
                                ));
                            }
                            conversations.postValue(list);
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });

        return conversations;
    }
}
