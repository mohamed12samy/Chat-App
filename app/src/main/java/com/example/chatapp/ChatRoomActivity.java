package com.example.chatapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatRoomActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private ChatRoomViewModel viewModel;
    private EditText messageEditText;
    private String secondUserId = "ms2519299@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        viewModel = new ChatRoomViewModel(getApplication(),secondUserId);
//        Toolbar toolbar = findViewById(R.id.app_bar);
//        toolbar.setTitle(viewModel.getMyUsername());
        viewModel.getMessages().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                adapter.setData(messages);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size() - 1);
            }
        });
        adapter = new MessageAdapter(viewModel.getMyId(), viewModel.getMessages().getValue());
        recyclerView.setAdapter(adapter);
        messageEditText = findViewById(R.id.messageEditText);
    }

    public void sendMessageButton(View view) {
        String message = messageEditText.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        messageEditText.setText("");
        viewModel.sendMessage(message);
    }
}
