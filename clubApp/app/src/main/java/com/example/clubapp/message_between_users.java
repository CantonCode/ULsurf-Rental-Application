package com.example.clubapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class message_between_users extends AppCompatActivity implements View.OnClickListener{

    private ListenerRegistration listenerRegistration;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private userAdapter userAdapter;
    private MessageAdapter messageAdapter;
    private FirebaseUser firebaseUser;
    private CollectionReference notebookRef;

    private ImageButton send_btn;
    private EditText text_msg;
    private TextView userName;


    private RecyclerView recyclerView;

    private  List<Chat> mChat;
    private String getTitle;
    private String getDate;
    private String getMessage;
    String selectedUserId;
    String currentUserId;
    String chatId;
    DocumentReference messageRef;
    Boolean found = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_between_users);
        mToolbar = (Toolbar) findViewById(R.id.main_appBar);
        getSupportActionBar().setTitle("Messenger");

        userName = findViewById(R.id.list_userName);
        text_msg = findViewById(R.id.text_msg);

        findViewById(R.id.send_btn).setOnClickListener(this);

        recyclerView = findViewById(R.id.recyclerEquipment);

        selectedUserId = getIntent().getStringExtra("selected_user");

    }

    public void onStart(){
        super.onStart();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        setDate();
        findChat(currentUserId,selectedUserId);

    }

    public void setDate(){
        int getCurrentYear = Calendar.getInstance().get(Calendar.YEAR);
        int getCurrentMonth = Calendar.getInstance().get(Calendar.MONTH);
        int getCurrentDate = Calendar.getInstance().get(Calendar.DATE);
        getDate=getCurrentDate+getCurrentMonth+getCurrentYear+"";
    }




    private void findChat(final String currentUserId, final String selectedUserId){
        CollectionReference chats = db.collection("chats");

        chats.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("USER", document.getId() + " => " + document.getData());

                        if((document.getId().contains(currentUserId)) && document.getId().contains(selectedUserId)){
                            Log.d("USER", "SUCCESS MATCH FOUND");
                            found = true;
                            chatId = document.getId();
                            setUpRecyclerView();
                            break;
                        }else {
                            Log.d("USER", "NO MATCH FOUND: "+ found);
                        }

                    }
                } else {
                    Log.d("USER", "Error getting documents: ", task.getException());
                }
            }
        });


    }


    public void setUpRecyclerView(){
        messageAdapter = new MessageAdapter(mChat,getTitle,currentUserId);
        notebookRef = db.collection("chats").document(chatId).collection("messages");
        Query first = notebookRef;
        first.addSnapshotListener(message_between_users.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(!documentSnapshots.isEmpty())
                {
                    for(DocumentChange doc:documentSnapshots.getDocumentChanges())
                    {
                        if(doc.getType()==DocumentChange.Type.ADDED)
                        {
                            Chat obj = doc.getDocument().toObject(Chat.class);
                            mChat.add(obj);
                            DocumentSnapshot lastVisible = documentSnapshots.getDocuments()
                                    .get(documentSnapshots.size() -1);
                            Query next = notebookRef.startAfter(lastVisible);

                            messageAdapter.notifyDataSetChanged();
                        }
                        if(doc.getType()==DocumentChange.Type.MODIFIED)
                        {
                            String docID = doc.getDocument().getId();
                            Chat obj = doc.getDocument().toObject(Chat.class);
                            if(doc.getOldIndex() == doc.getNewIndex())
                            {
                                mChat.set(doc.getOldIndex(),obj);
                            }
                            else
                            {
                                mChat.remove(doc.getOldIndex());
                                mChat.add(doc.getNewIndex(),obj);
                                messageAdapter.notifyItemMoved(doc.getOldIndex(),doc.getNewIndex());
                            }
                            messageAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(message_between_users.this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messageAdapter);
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

        chatId = sender+receiver;

        notebookRef = db.collection("chats").document(chatId).collection("messages");;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("Time", FieldValue.serverTimestamp());

        notebookRef.add(hashMap);
    }

    public void onClick(View v){
        int i = v.getId();

        if(i == R.id.send_btn){
            String msg = text_msg.getText().toString();

            if(!msg.equals("")) {
                send_Message(currentUserId,selectedUserId, msg);
            } else {
                Toast.makeText(message_between_users.this, "Can't do text", Toast.LENGTH_SHORT).show();
            }
            text_msg.setText("");
        }
    }
}

