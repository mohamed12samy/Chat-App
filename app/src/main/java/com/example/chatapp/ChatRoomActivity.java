package com.example.chatapp;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class ChatRoomActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private ChatRoomViewModel viewModel;
    private EditText messageEditText;
    private String secondUserEmail = "";
    private String secondUserPhoto;
    private String secondUserName;

    ImageView userImage;
    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        secondUserEmail = getIntent().getStringExtra("user_email");
        secondUserName = getIntent().getStringExtra("user_name");
        secondUserPhoto = getIntent().getStringExtra("user_url_photo");

        userName = findViewById(R.id.toolbar_title);
        userImage = findViewById(R.id.user_image);

        findViewById(R.id.sign_out).setVisibility(View.GONE);
        userName.setText(secondUserName);
        Glide.with(userImage.getContext()).load(secondUserPhoto)
                .placeholder(R.drawable.user)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        userImage.setImageDrawable(resource);
                        return false;
                    }
                })
                .into(userImage);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        viewModel = new ChatRoomViewModel(getApplication(),secondUserEmail);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onChatClose();
    }
}
