package com.Login;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.clubapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import androidx.annotation.VisibleForTesting;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;

    @VisibleForTesting
    public ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById(R.id.signUp).setOnClickListener(this);
        ImageView logo = findViewById(R.id.logo);
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/clubapp-surf.appspot.com/o/stockPhotos%2Fulsurfclub.png?alt=media&token=922ce976-e572-43b1-aeac-42c4f99da6b9").transform(new RoundedCornersTransformation(50,0)).into(logo);
        isUserLoggedIn();
    }

    public void isUserLoggedIn(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null){
//            Intent intent = new Intent(this, ViewPosts.class);
//            startActivity(intent);
        }

    }

    public void setProgressBar(int resId) {
        mProgressBar = findViewById(resId);
    }

    public void showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressBar();
    }

    public void onClick(View v){
        int i = v.getId();

        if (i == R.id.loginButton){
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }

        if(i == R.id.signUp){
            Intent intent1 = new Intent(MainActivity.this, signUpActivity.class);
            startActivity(intent1);
        }
    }

}