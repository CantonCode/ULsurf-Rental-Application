package com.example.clubapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class messageActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("chats");
    private ChatAdapter chatAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.newChatButton).setOnClickListener(this);
        setUpRecyclerView();
    }

    public void setUpRecyclerView(){
        Query query = notebookRef;
        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query, Chat.class)
                .build();

        chatAdapter = new ChatAdapter(options);

        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView = findViewById(R.id.currentChatView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(chatAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        chatAdapter.startListening();
    }
    protected void onStop(){
        super.onStop();

        chatAdapter.stopListening();
    }

    private void goBack() {
        Intent intent = new Intent(this, homeActivity.class);
        startActivity(intent);
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.goBack) {
            goBack();
        }

        if(i == R.id.newChatButton){
            Intent intent = new Intent(this, all_Members_Activity.class);
            startActivity(intent);
        }
    }

}