package com.example.chatapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.ConversationAction;

import java.util.ArrayList;
import java.util.List;


public class ConversationsFragment extends Fragment {
    List<Pair<User , String>> conversationss = new ArrayList<>();
    RecyclerView recyclerView;
    ConversationsAdapter conversationsAdapter = new ConversationsAdapter(getActivity(), conversationss);

    ConversationsViewModel mConversationsViewModel = new ConversationsViewModel(App.getInstance());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);

        recyclerView = view.findViewById(R.id.conversations_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(conversationsAdapter);

        mConversationsViewModel.getConversations().observe(this, new Observer<List<Pair<User , String>>>() {
            @Override
            public void onChanged(List<Pair<User , String>> conversation) {
                if(conversation != null) {
                    conversationss.clear();
                    conversationss.addAll(conversation);
                    conversationsAdapter.notifyDataSetChanged();
                }
            }
        });
        return view;
    }

}