package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.ConversationAction;

import java.util.ArrayList;
import java.util.List;


public class ConversationsFragment extends Fragment implements Clicklistener{
    List<Pair<User , Message>> conversationss = new ArrayList<>();
    RecyclerView recyclerView;
    ConversationsAdapter conversationsAdapter = new ConversationsAdapter(getActivity(), conversationss, this);

    ConversationsViewModel mConversationsViewModel = new ConversationsViewModel(App.getInstance());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);

        recyclerView = view.findViewById(R.id.conversations_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(conversationsAdapter);

        mConversationsViewModel.getConversations().observe(this, new Observer<List<Pair<User , Message>>>() {
            @Override
            public void onChanged(List<Pair<User , Message>> conversation) {
                if(conversation != null) {
                    conversationss.clear();
                    conversationss.addAll(conversation);
                    conversationsAdapter.notifyDataSetChanged();
                }
            }
        });
        return view;
    }
    @Override
    public void onItemClicked(int position) {
        if (conversationss != null) {
            Log.d("TAAGG",position + "   "+conversationss.get(position).first.getId()+
                    "  "+conversationss.get(position).first.getName()+"  "+conversationss.get(position).first.getUrlPhoto());

            Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
            intent.putExtra("user_email",conversationss.get(position).first.getEmail());
            intent.putExtra("user_name",conversationss.get(position).first.getName());
            intent.putExtra("user_url_photo",conversationss.get(position).first.getUrlPhoto());
            startActivity(intent);
        }
    }
}