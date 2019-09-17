package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment implements Clicklistener {

    List<User> users = new ArrayList<>();
    RecyclerView recyclerView;
    UsersAdapter usersAdapter = new UsersAdapter(getActivity(), users, this);

    UserViewModel mUserViewModel = new UserViewModel();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.user_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(usersAdapter);


        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mUserViewModel.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> user) {
                if (user != null) {
                    users.clear();
                    users.addAll(user);
                    usersAdapter.notifyDataSetChanged();
                }
            }
        });

        return view;
    }

    @Override
    public void onItemClicked(int position) {
        if (users != null) {
            Log.d("TAAGG",position + "   "+users.get(position).getId()+
                    "  "+users.get(position).getName()+"  "+users.get(position).getUrlPhoto());
            Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
            intent.putExtra("user_email",users.get(position).getEmail());
            intent.putExtra("user_name",users.get(position).getName());
            intent.putExtra("user_url_photo",users.get(position).getUrlPhoto());
            startActivity(intent);
        }
    }
}