package com.example.chatapp;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
    private MutableLiveData<List<Message>> messages;
    private String chatRoomId;
    private String secondUserId;
    private String myId = "23uXCKmDE2ajwE2WU6wG";

    private Repository() {
        db = App.getFirebaseFirestore();
        messages = new MutableLiveData<>();
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

    public String getMyId() {
        return myId;
    }

    public MutableLiveData<List<Message>> getMessages(String secondUserId) {
        this.secondUserId = secondUserId;
        Task task1 = db.collection("ChatRooms")
                .whereEqualTo("UserEmail1", myId)
                .whereEqualTo("UserEmail2", secondUserId)
                .get();

        Task task2 = db.collection("ChatRooms")
                .whereEqualTo("UserEmail1", secondUserId)
                .whereEqualTo("UserEmail2", myId)
                .get();

        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);
        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
                    if (queryDocumentSnapshots.size() != 0) {
                        chatRoomId = queryDocumentSnapshots.getDocuments().get(0).getId();
                    } else {
//                        db.collection("ChatRooms")
                    }
                }
            }
        });
        db.collection("Messages")
                .whereEqualTo("chatRoomId", chatRoomId)
//                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.i("Size", queryDocumentSnapshots.size() + "");
                        List<Message> data = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Message message = documentSnapshot.toObject(Message.class);
                            message.setId(documentSnapshot.getId());
                            data.add(message);
                            Log.i("MessageSenderId:", message.getSenderId());
                        }
                        messages.setValue(data);
                        Log.i("Messages", data.toString());
                    }
                });
        return messages;
    }

    public void sendMessage(String body) {
        final Message message = new Message(chatRoomId, myId, body, Timestamp.now());
        db.collection("Messages")
                .add(message)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        message.setId(documentReference.getId());
                        List<Message> data = messages.getValue();
                        data.add(message);
                        messages.setValue(data);
                    }
                });
    }
}
