package com.example.chatapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class HostActivity extends AppCompatActivity {

    ImageView userImage;
    TextView userName;
    //    SharedPreferences sharedPreferences;
    private FirebaseUser mFirebaseUser;
    private FirebaseFirestore db;
    private String token;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        mFirebaseUser = App.getmFirebaseUser();
        db = App.getFirebaseFirestore();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        token = task.getResult().getToken();

                    }
                });
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new UsersFragment()).commit();

//        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);


        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.users:
                        selectedFragment = new UsersFragment();
                        break;

                    case R.id.conversations:
                        selectedFragment = new ConversationsFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, selectedFragment).commit();
                return true;
            }
        });
        userName = findViewById(R.id.toolbar_title);
        userImage = findViewById(R.id.user_image);

        userName.setText(App.getmFirebaseUser().getDisplayName());


        findViewById(R.id.sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                App.setmFirebaseUser(null);
//                sharedPreferences.edit().clear();
                startActivity(new Intent(HostActivity.this, MainActivity.class));
                finish();
            }
        });
        Glide.with(userImage.getContext()).load(App.getmFirebaseUser().getPhotoUrl())
                .placeholder(R.drawable.user)
                .listener(new RequestListener<Uri, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        userImage.setImageDrawable(resource);
                        return false;
                    }
                })
                .into(userImage);

//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.nav_host_fragment);
//        NavigationUI.setupWithNavController(bottomNavigationView,
//                navHostFragment.getNavController());

        db.collection("Users").whereEqualTo(App.EMAIL, mFirebaseUser.getEmail())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String refernce = queryDocumentSnapshots.getDocuments().get(0).getId();
                db.collection("Users").document(refernce).update(App.TOKEN, token);
                Log.d("Notification_LOL",mFirebaseUser.getEmail()+"22");
                Log.d("Notification_LOL",token+"22");


            }
        });

    }
}
