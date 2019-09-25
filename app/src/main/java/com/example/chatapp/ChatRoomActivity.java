package com.example.chatapp;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class ChatRoomActivity extends AppCompatActivity implements Clicklistener {

    private static final int PICK_IMAGE_CAMERA = 101;
    private static final int PICK_IMAGE_GALLERY = 102;
    private static final int REQUEST_CODE = 1;

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private ChatRoomViewModel viewModel;
    private EditText messageEditText;
    private String secondUserEmail = "";
    private String secondUserPhoto;
    private String secondUserName;
    private boolean queryInProgress = false;

    private List<Message> messages = new ArrayList<>();

    private BottomSheetBehavior mBottomSheetBehavior;
    View bottomSheet;
    ImageView userImage;
    ImageView imageToSend;
    TextView userName;
    Button deleteMessage;
    Button copyMessage;
    Button forwardMessage;
    ImageButton pickImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        secondUserEmail = getIntent().getStringExtra("user_email");
        secondUserName = getIntent().getStringExtra("user_name");
        secondUserPhoto = getIntent().getStringExtra("user_url_photo");

        userName = findViewById(R.id.toolbar_title);
        userImage = findViewById(R.id.user_image);
        imageToSend = findViewById(R.id.image_to_send);

        bottomSheet = findViewById(R.id.design_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        deleteMessage = findViewById(R.id.delete);
        copyMessage = findViewById(R.id.copy);
        forwardMessage = findViewById(R.id.forward);
        pickImage = findViewById(R.id.get_image);

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
        recyclerView.setHasFixedSize(false);
        viewModel = new ChatRoomViewModel(getApplication(), secondUserEmail);
        viewModel.getMessages().observe(this, new Observer<List<Pair<Message, String>>>() {
            @Override
            public void onChanged(List<Pair<Message, String>> mMessages) {
                if (mMessages != null) {
                    List<Integer> toRemove = new ArrayList<>();
                    int offset = manager.findFirstVisibleItemPosition();
                    for (int i = 0; i < mMessages.size(); i++) {
                        if (mMessages.get(i).second.equals("ADDEND")) {
                            boolean found = false;
                            for (Message message : messages) {
                                if (mMessages.get(i).first.getId().equals(message.getId())) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                messages.add(mMessages.get(i).first);
                                adapter.addEnd(mMessages.get(i).first);
//                                adapter.notifyItemInserted(messages.size() - 1);
//                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(messages.size() - 1);
                            }
                        } else if (mMessages.get(i).second.equals("ADDSTART")) {
                            queryInProgress = false;
                            messages.add(0, mMessages.get(i).first);
                            adapter.setData(messages);
//                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(offset + 1);
                            recyclerView.scrollToPosition(mMessages.size() + offset + 6);
                        } else if (mMessages.get(i).second.equals("REMOVE")) {
                            for (Message message : messages) {
                                if (mMessages.get(i).first.getId().equals(message.getId())) {
                                    toRemove.add(messages.indexOf(message));
                                    Log.d("toRemove", message.getId());
                                }
                            }
                        }
                        if (!toRemove.isEmpty()) {
                            for (Integer index : toRemove) {
                                messages.remove(index);
                                adapter.removeItemAt(index);
//                                adapter.notifyItemRemoved(index);
//                                adapter.setData(messages);
                            }
                        }
                    }
                }
            }
        });
        adapter = new MessageAdapter(viewModel.getMyId(), new ArrayList<Message>() /*viewModel.getMessages().getValue().first*/, this);
        recyclerView.setAdapter(adapter);
        messageEditText = findViewById(R.id.messageEditText);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!queryInProgress) {
                        viewModel.getOlderMessages();
                        queryInProgress = true;
                    }
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

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(ChatRoomActivity.this, new String[]
                                {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);

                        return;
                    }
                }*/
                /*Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);*/

                selectImage();
            }
        });

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
        viewModel.getMessages().removeObservers(this);
    }

    private void buttonClick(final int position) {
        deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.removeMessage(messages.get(position));
//                messages.remove(position);
//                adapter.notifyItemRemoved(position);
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

                        messages.get(position).getBody(), messages.get(position).getPhoto_url());

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

        if (message.getBody() == null) {
            copyMessage.setVisibility(View.GONE);
        } else copyMessage.setVisibility(View.VISIBLE);


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CAMERA) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Uri tempUri = getImageUri(getApplicationContext(), photo);

            photoViewing(tempUri, photo);
        } else if (requestCode == PICK_IMAGE_GALLERY && resultCode == RESULT_OK) {
            final Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                Log.e("Activity", "Pick from Gallery::>>> ");


                String imgPath = getRealPathFromURI(selectedImage);

                photoViewing(selectedImage, bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void photoViewing(final Uri selectedImage, Bitmap bitmap) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        imageToSend.setImageBitmap(bitmap);
        findViewById(R.id.photo_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.buttons_layout).setVisibility(View.GONE);
        findViewById(R.id.send_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.addImageToStorage(selectedImage);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                imageToSend.setImageBitmap(null);
                findViewById(R.id.photo_layout).setVisibility(View.GONE);
                findViewById(R.id.buttons_layout).setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("sdsd", "++++++++++++++++");

        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

            }
        } else if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, PICK_IMAGE_CAMERA);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void selectImage() {

        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (true) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatRoomActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            Log.d("sdsd", "*8888881");

                            dialog.dismiss();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    Log.d("sdsd", "*1111111");

                                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                                } else {
                                    Log.d("sdsd", "********");
                                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(cameraIntent, PICK_IMAGE_CAMERA);
                                }
                            } else {
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, PICK_IMAGE_CAMERA);
                            }

                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.create();
                builder.show();
            } else
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
