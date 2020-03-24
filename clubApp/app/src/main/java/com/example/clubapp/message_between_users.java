package com.example.clubapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class message_between_users extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private userAdapter userAdapter;
    private FirebaseUser firebaseUser;
    private CollectionReference notebookRef = db.collection("user");

    ImageButton send_btn;
    EditText text_msg;
    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_between_users);

        mToolbar = (Toolbar) findViewById(R.id.main_appBar);
        getSupportActionBar().setTitle("Messenger");



        userName = findViewById(R.id.list_userName);
        send_btn = findViewById(R.id.send_btn);
        text_msg = findViewById(R.id.text_msg);

        Intent intent = getIntent();
        final String userID = getIntent().getStringExtra("user");

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_msg.getText().toString();

                if(!msg.equals("")) {
                    send_Message(firebaseUser.getUid(), userID, msg);
                } else {
                    Toast.makeText(message_between_users.this, "Can't do text", Toast.LENGTH_SHORT).show();
                }
                text_msg.setText("");
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.message, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.signOutButton) {

            //mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

            mAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            //sendToStart();

        }

        if (item.getItemId() == R.id.goBack) {
            Intent intent = new Intent(this, all_Members_Activity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.home) {

            Intent intent = new Intent(this, homeActivity.class);
            startActivity(intent);

        }

        return true;
    }

    private void send_Message(String sender,String receiver, String message){

        notebookRef = db.collection("chats");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        notebookRef.add(hashMap);
    }

}
