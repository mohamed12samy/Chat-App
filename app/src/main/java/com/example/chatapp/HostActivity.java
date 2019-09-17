package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HostActivity extends AppCompatActivity {

    ImageView userImage;
    TextView userName;
//    SharedPreferences sharedPreferences;

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,new UsersFragment()).commit();

//        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId())
                {
                    case R.id.users:
                        selectedFragment =new UsersFragment();
                        break;

                    case R.id.conversations:
                        selectedFragment =new ConversationsFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,selectedFragment).commit();
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

    }
}
