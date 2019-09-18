package com.example.chatapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ForwardMessageDialog extends Dialog implements Clicklistener{

    List<User> users = new ArrayList<>();
    RecyclerView recyclerView;
    UsersAdapter usersAdapter ;

    UserViewModel mUserViewModel = new UserViewModel();

    Context mContext;
    LifecycleOwner mLifecycleOwner;

    String secondUserEmail;
    String mMessage;

    public ForwardMessageDialog(Context context, LifecycleOwner lifecycleOwner, String userEmail, String message) {
        super(context);
        setContentView(R.layout.fowward_message_layout);
        mContext = context;
        mLifecycleOwner = lifecycleOwner;
        secondUserEmail = userEmail;
        mMessage = message;

        usersAdapter = new UsersAdapter(1, users, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerView = findViewById(R.id.user_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(usersAdapter);

        this.getWindow().setBackgroundDrawableResource(R.drawable.background_dialog);

        mUserViewModel.getUsers().observe(mLifecycleOwner, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> user) {
                if (user != null) {
                    users.clear();
                    users.addAll(user);
                    for (int i = 0 ; i <= users.size()-1 ;i++){
                        if( users.get(i).getEmail().equals(secondUserEmail)){
                            users.remove(users.get(i));
                            break;
                        }
                    }
                    /*
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                        users.removeIf(new Predicate<User>() {
                            @Override
                            public boolean test(User mUser) {
                                return mUser.getEmail().equals(secondUserEmail);
                            }
                        });
                    }else {
                        for (int i = 0 ; i<users.size()-1 ;i++){
                            if( users.get(i).getEmail().equals(secondUserEmail)){
                                Log.d("UYUY",users.get(i).getEmail());
                                users.remove(users.get(i));
                                break;
                            }
                        }
                    }
*/
                    usersAdapter.notifyDataSetChanged();
                }
            }
        });


        findViewById(R.id.dismiss_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmiss();
            }
        });
    }

    private void dissmiss(){
        this.dismiss();
    }
    @Override
    public void onItemClicked(int position) {
        Log.d("TYY",users.get(position).getEmail());
        mUserViewModel.forqardMessage(mMessage,users.get(position).getEmail());
        //dismiss();
    }
}
