package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private final int RC_SIGN_IN = 101;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private LoginButton loginButton;
    private CallbackManager mCallbackManager;

    private ProgressBar progressBar;
    private MainViewModel mainViewModel = new MainViewModel(App.getInstance());

    SharedPreferences sharedPreferences;
    Button googleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        if(sharedPreferences.contains("is_logged") && sharedPreferences.getBoolean("is_logged",false)){
            startActivity(new Intent(MainActivity.this,HostActivity.class));
            finish();
        }*/

        //facebook
        progressBar = findViewById(R.id.progress_bar);
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_facebook);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");

        googleSignIn = findViewById(R.id.login_google);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        mAuth = FirebaseAuth.getInstance();

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                signInGoogle();
            }
        });

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("facebookLogin", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                progressBar.setVisibility(View.INVISIBLE);

                Log.d("facebookLogin", "facebook:onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                progressBar.setVisibility(View.INVISIBLE);
                Log.d("facebookLogin", "facebook:onError", error);

            }
        });

    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        progressBar.setVisibility(View.INVISIBLE);
        Log.d("facebookLogin", "handleFacebookAccessToken:" + accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNew;
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("facebookLogin", "signInWithCredential:success");
                            App.setmFirebaseUser(mAuth.getCurrentUser());
                            isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if(isNew){
                                mainViewModel.addNewUser(App.getmFirebaseUser());
                            }
                            /*SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("is_logged", true).apply();
                            editor.commit();*/
                            startActivity(new Intent(MainActivity.this,HostActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("facebookLogin", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }
        else
        {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        progressBar.setVisibility(View.INVISIBLE);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNew;
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            App.setmFirebaseUser(mAuth.getCurrentUser());
                            isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if(isNew){
                                mainViewModel.addNewUser(App.getmFirebaseUser());
                            }
                           /* SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("is_logged", true).apply();
                            editor.commit();*/
                            startActivity(new Intent(MainActivity.this,HostActivity.class));
                            finish();
                            //Log.d("RRR",user.getDisplayName()+"   "+user.getEmail()+"  "+user.getPhotoUrl());
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            App.setmFirebaseUser(currentUser);
            startActivity(new Intent(MainActivity.this,HostActivity.class));
            finish();
        }
//        updateUI(currentUser);
    }

}
