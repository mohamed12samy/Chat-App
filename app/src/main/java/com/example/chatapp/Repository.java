package com.example.chatapp;

import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private MutableLiveData<List<User>> users = new MutableLiveData<>();
    private MutableLiveData<List<Pair<User , String>>> conversations = new MutableLiveData<>();

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

    public LiveData<List<Pair<User , String>>> getConversations() {

        Task task1 = db.collection("ChatRooms")
                .whereEqualTo("UserEmail2", App.getmFirebaseUser().getEmail())
                .get();

        Task task2 = db.collection("ChatRooms")
                .whereEqualTo("UserEmail1",App.getmFirebaseUser().getEmail())
                .get();

        Task<List<QuerySnapshot>> task3 = Tasks.whenAllSuccess(task1,task2);
        task3.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            List<Pair<User , String>> list1 = new ArrayList<>();

            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
               // List<Conversations> list = new ArrayList<>();

                for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
                    for (final QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Log.d("email1", document.getId() + " => " + document.getData().get("UserEmail1"));
                        Log.d("email2", document.getId() + " => " + document.getData().get("UserEmail2"));

                        String email = App.getmFirebaseUser().getEmail().equals(document.getData().get("UserEmail1")) ?
                                document.getData().get("UserEmail2")+"" :
                                document.getData().get("UserEmail1")+"";

                        db.collection("Users")
                                .whereEqualTo("email", email)
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                User user = queryDocumentSnapshots.toObjects(User.class).get(0);
                                list1.add(new Pair<>(user,document.getData().get("lastMessage")+""));
                            }
                        });
                        /*list.add(new Conversations(document.getData().get("UserEmail1") + "",
                                document.getData().get("UserEmail2") + "",
                                document.getData().get("lastMessage") + ""
                        ));*/
                    }
                }

                conversations.postValue(list1);

            }
        }) ;


        return conversations;
    }
}
