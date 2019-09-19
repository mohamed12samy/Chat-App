package com.example.chatapp;

import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class Repository {
    private static Repository mRepository;
    private FirebaseFirestore db;
    private MutableLiveData<List<Message>> messages;
    private String chatRoomId;
    private String secondUserId;
    private DocumentSnapshot lastMessageShownId;
    private String myId = App.getmFirebaseUser().getEmail();
    private MutableLiveData<List<User>> users = new MutableLiveData<>();
    private MutableLiveData<List<Pair<User, Message>>> conversations = new MutableLiveData<>();

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

    public MutableLiveData<List<Message>> getMessages(final String secondUserId) {
        Log.d("YUYU", secondUserId);
        this.secondUserId = secondUserId;

        db.collection("ChatRooms")
                .whereArrayContains("users", App.getmFirebaseUser().getEmail())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                boolean chatRoomFound = false;
                if (queryDocumentSnapshots.size() != 0) {
                    for (int i = 0; i <= queryDocumentSnapshots.size() - 1; i++) {
                        List<String> users = (List<String>) queryDocumentSnapshots.getDocuments().get(i).get("users");
                        if (users.contains(secondUserId)) {
                            chatRoomId = queryDocumentSnapshots.getDocuments().get(i).getId();
                            chatRoomFound = true;
                            break;
                        }
                    }
                    if (!chatRoomFound) {
                        chatRoomId = null;
                        messages.setValue(new ArrayList<Message>());
                    } else {
                        db.collection("ChatRooms")
                                .document(chatRoomId)
                                .collection("messages")
                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                .limit(20)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        List<Message> data = new ArrayList<>();
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            Message message = documentSnapshot.toObject(Message.class);
                                            message.setId(documentSnapshot.getId());
                                            data.add(0,message);
                                            Log.i("lastMessageShownId:", documentSnapshot.getId());
                                            lastMessageShownId = documentSnapshot;
                                        }
                                        messages.setValue(data);
                                    }
                                });
//                        final CollectionReference docRef = db.collection("ChatRooms").document(chatRoomId)
//                                .collection("messages");
//
//                        docRef.orderBy("timestamp", Query.Direction.ASCENDING)
//                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//
//                                        List<Message> data = new ArrayList<>();
//                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                            Message message = documentSnapshot.toObject(Message.class);
//                                            message.setId(documentSnapshot.getId());
//                                            data.add(message);
//                                            Log.i("MessageSenderId:", message.getSenderId());
//                                        }
//                                        messages.setValue(data);
//                                    }
//                                });
                    }
                }
            }
        });

        return messages;
    }

    public void getOlderMessages() {
        if (chatRoomId != null) {
            db.collection("ChatRooms")
                    .document(chatRoomId)
                    .collection("messages")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(20)
                    .startAfter(lastMessageShownId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<Message> data = messages.getValue();
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Message message = documentSnapshot.toObject(Message.class);
                                message.setId(documentSnapshot.getId());
                                data.add(0, message);
                                Log.i("lastMessageShownId:", documentSnapshot.getId());
                                lastMessageShownId = documentSnapshot;
                            }
                            messages.setValue(data);
                        }
                    });
        }
    }

    List<Message> data = new ArrayList<>();

    public void sendMessage(final String body, final String image) {

        if (chatRoomId == null) {
            List<String> users = new ArrayList<>();
            users.add(myId);
            users.add(secondUserId);

            db.collection("ChatRooms")
                    .add(new ChatRoom(users))
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            chatRoomId = documentReference.getId();

                            final Message message = new Message(myId, body,image, Timestamp.now());

                            db.collection("ChatRooms").document(chatRoomId).collection("messages")
                                    .add(message)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
//                                            data = new ArrayList<>();
                                            message.setId(documentReference.getId());
                                            getMessages(secondUserId);
                                           /* data.add(message);
                                            messages.setValue(data);*/
                                        }
                                    });
                        }
                    });
        } else {
            final Message message = new Message(myId, body,image, Timestamp.now());
            db.collection("ChatRooms").document(chatRoomId).collection("messages")
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            message.setId(documentReference.getId());
                            /*data.add(message);
                            messages.setValue(data);*/
                        }
                    });
        }
    }

    String chatRoomReceiver;

    public void forwardMessage(final String body,final String image, final String receiverEmail) {

        db.collection("ChatRooms")
                .whereArrayContains("users", App.getmFirebaseUser().getEmail())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                boolean chatRoomFound = false;
                if (queryDocumentSnapshots.size() != 0) {
                    for (int i = 0; i <= queryDocumentSnapshots.size() - 1; i++) {
                        List<String> users = (List<String>) queryDocumentSnapshots.getDocuments().get(i).get("users");
                        if (users.contains(receiverEmail)) {
                            chatRoomReceiver = queryDocumentSnapshots.getDocuments().get(i).getId();
                            chatRoomFound = true;
                            break;
                        }
                    }
                    if (!chatRoomFound) {
                        chatRoomReceiver = null;
                        List<String> users = new ArrayList<>();
                        users.add(myId);
                        users.add(receiverEmail);

                        db.collection("ChatRooms")
                                .add(new ChatRoom(users))
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        chatRoomReceiver = documentReference.getId();
                                        final Message message = new Message(myId,body, image, Timestamp.now());
                                        db.collection("ChatRooms").document(chatRoomReceiver)
                                                .collection("messages")
                                                .add(message)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                    }
                                                });
                                    }
                                });
                    } else {
                        final Message message = new Message(myId, body,image, Timestamp.now());
                        db.collection("ChatRooms").document(chatRoomReceiver)
                                .collection("messages")
                                .add(message)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                    }
                                });
                    }
                }
            }
        });
    }

    public LiveData<List<User>> getUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<User> list = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Log.d("TAG", document.getId() + " => " + document.getData().get("email"));

                            if (!document.getData().get("email").equals(App.getmFirebaseUser().getEmail())) {

                                Log.d("asdd", document.getData().get("email") + "   " + App.getmFirebaseUser().getEmail());

                                list.add(new User(document.getId(), document.getData().get("name") + "",
                                        document.getData().get("url_photo") + "", document.getData().get("email") + ""));
                            }
                        }
                        users.postValue(list);

                    }
                });
        return users;
    }

    public LiveData<List<Pair<User, Message>>> getConversations() {

        db.collection("ChatRooms")
                .whereArrayContains("users", App.getmFirebaseUser().getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    List<Pair<User, Message>> list1 = new ArrayList<>();

                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (final QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            List<String> users = (List<String>) document.getData().get("users");
                            Log.d("emails", document.getId() + " => " + users.get(0));

                            String email = App.getmFirebaseUser().getEmail().equals(users.get(0)) ?
                                    users.get(1) + "" :
                                    users.get(0) + "";

                            db.collection("Users")
                                    .whereEqualTo("email", email)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    for (QueryDocumentSnapshot document1 : task.getResult()) {

                                        final User user = new User(document1.getId(), document1.getData().get("name") + "",
                                                document1.getData().get("url_photo") + "", document1.getData().get("email") + "");

                                        db.collection("ChatRooms").document(document.getId()).collection("messages")
                                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                        Message message;
                                                        if (queryDocumentSnapshots.size() > 0) {
                                                            message = queryDocumentSnapshots.getDocuments().get(0).toObject(Message.class);

                                                            Message message1;
                                                            if (queryDocumentSnapshots.size() > 1) {
                                                                message1 = queryDocumentSnapshots.getDocuments().get(1).toObject(Message.class);
                                                                for(int i=0 ; i<list1.size() ; i++){
                                                                    if(list1.get(i).first.getEmail().equals(user.getEmail())){
                                                                        list1.remove(i);
                                                                        break;
                                                                    }
                                                                }
                                                               // list1.remove(new Pair<>(user, message1));
                                                            }
                                                            list1.add(0, new Pair<>(user, message));

                                                            Log.d("rtrt", list1.size() + "");
                                                            conversations.postValue(list1);
                                                        }
                                                    }
                                                });

                                    }
                                }
                            });
                        }
                    }
                });

        return conversations;
    }

//    MutableLiveData<List<Message>> newMesssage = new MutableLiveData<>();

    public void removeMessage(String id) {
        db.collection("ChatRooms").document(chatRoomId).collection("messages").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error deleting document", e);
                    }
                });
    }

    public void onChatClose() {
        messages = new MutableLiveData<>();
        chatRoomId = null;
        secondUserId = null;
    }

    public void storeImageToStorage(Uri imagUri){
        final StorageReference photoRef = App.getPhotoReference().child(imagUri.getLastPathSegment());
        photoRef.putFile(imagUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot/*the key to getting thr\e URL of the file that was just sent to the storage*/) {
                photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        sendMessage(null,imageUrl);

                    }
                });
            }
        });
    }
}
