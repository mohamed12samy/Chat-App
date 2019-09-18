package com.example.chatapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class ChatRoomActivity extends AppCompatActivity implements Clicklistener {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private ChatRoomViewModel viewModel;
    private EditText messageEditText;
    private String secondUserEmail = "";
    private String secondUserPhoto;
    private String secondUserName;
    private int currentPage = 1;

    private List<Message> messages = new ArrayList<>();

    private BottomSheetBehavior mBottomSheetBehavior;
    View bottomSheet;
    ImageView userImage;
    TextView userName;
    Button deleteMessage;
    Button copyMessage;
    Button forwardMessage;
    private DrawableClickListener clickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        secondUserEmail = getIntent().getStringExtra("user_email");
        secondUserName = getIntent().getStringExtra("user_name");
        secondUserPhoto = getIntent().getStringExtra("user_url_photo");

        userName = findViewById(R.id.toolbar_title);
        userImage = findViewById(R.id.user_image);

        bottomSheet = findViewById(R.id.design_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        deleteMessage = findViewById(R.id.delete);
        copyMessage = findViewById(R.id.copy);
        forwardMessage = findViewById(R.id.forward);

        findViewById(R.id.sign_out).setVisibility(View.INVISIBLE);
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
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        viewModel = new ChatRoomViewModel(getApplication(), secondUserEmail);
        viewModel.getMessages().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> mMessages) {
                int addedSize;
                if (messages == null || messages.isEmpty()) {
                    if (mMessages == null || mMessages.isEmpty())
                        addedSize = 0;
                    else
                        addedSize = 1;
                } else {
                    addedSize = mMessages.size() - messages.size();
                }
                int offset = manager.findFirstVisibleItemPosition();
                messages  = new ArrayList<>();
                messages.addAll(mMessages);
                adapter.setData(messages);
                adapter.notifyDataSetChanged();
                if (addedSize == 1) {
                    Log.i("addedSize", addedSize+"");
                    recyclerView.scrollToPosition(messages.size() - 1);
                } else if (addedSize > 1 && addedSize <= 20){
                    Log.i("addedSize", addedSize+"");
                    Log.i("firstItemIndex", offset+"");
                    recyclerView.scrollToPosition(addedSize+offset+6);
                }
            }
        });
        adapter = new MessageAdapter(viewModel.getMyId(), viewModel.getMessages().getValue(), this);
        recyclerView.setAdapter(adapter);
        messageEditText = findViewById(R.id.messageEditText);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    viewModel.getOlderMessages();
                }
            }
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
//                    viewModel.getOlderMessages();
//                }
//            }
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (dy <=0 && recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
//                    viewModel.getOlderMessages();
//                }
//            }
        });
//        messageEditText.setDrawableC


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

    private void buttonClick(final int position) {
        deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.removeMessage(messages.get(position));
                messages.remove(position);
                adapter.notifyItemRemoved(position);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        copyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("message", messages.get(position).getBody());
                clipboard.setPrimaryClip(clip);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        forwardMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForwardMessageDialog forwadDialog = new ForwardMessageDialog(ChatRoomActivity.this,
                        ChatRoomActivity.this, secondUserEmail,
                        messages.get(position).getBody());
                Log.d("USUS", secondUserEmail);
                forwadDialog.show();
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        Message message = messages.get(position);
        if (!message.getSenderId().equals(App.getmFirebaseUser().getEmail())) {
            deleteMessage.setVisibility(View.GONE);
        } else deleteMessage.setVisibility(View.VISIBLE);

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        buttonClick(position);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                bottomSheet.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }
}
