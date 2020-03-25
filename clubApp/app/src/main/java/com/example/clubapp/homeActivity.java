package com.example.clubapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class homeActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextView userTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.signOutButton).setOnClickListener(this);
        userTextView = findViewById(R.id.currentUser);
        findViewById(R.id.goToRental).setOnClickListener(this);
        findViewById(R.id.goToSupport).setOnClickListener(this);
        findViewById(R.id.userProfile).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        setCurrentUserText(currentUser);
    }

    private void setCurrentUserText(FirebaseUser user){
        if(user.getDisplayName() == null) {
            userTextView.setText(user.getEmail());
        }else{
            userTextView.setText(user.getDisplayName());
        }
    }


    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.signOutButton){
            signOut();
        }

        if(i == R.id.goToRental){
            Intent intent = new Intent(this,RentalMainActivity.class);
            startActivity(intent);
        }

        if(i == R.id.goToSupport){
            Intent intent = new Intent(this,SupportActivity.class);
            startActivity(intent);
        }

        if(i==R.id.userProfile){
            Intent intent = new Intent(this,userProfileActivity.class);
            startActivity(intent);
        }
    }
}
