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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
    private CollectionReference newMessageRef;
    private DocumentReference chatRef;


    private ImageButton send_btn;
    private EditText text_msg;
    private TextView userName;


    private RecyclerView recyclerView;

    private String getTitle;
    private String getDate;
    private String getMessage;
    String selectedUserId;
    String currentUserId;
    String chatId;
    String chatIdOne;
    String msg;
    Boolean found;
    String chatIdTwo;
    CollectionReference chats;
    DocumentReference messageRef;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_messages);
        findViewById(R.id.sendMessage).setOnClickListener(this);
        setValue(false);
        text_msg = findViewById(R.id.newMessage);
        selectedUserId = getIntent().getStringExtra("selected_user");
//        chatId = "1";
//        setUpRecyclerView();
    }


    public void onStart(){
        super.onStart();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        Log.d("MESSAGE", "Init   currentUser: " + currentUserId + "    receiverID:"+ selectedUserId);
        chatId = currentUserId + selectedUserId;
        findChat(currentUserId,selectedUserId);
        setDate();
        Log.d("MESSAGE", "chatID new one:" + chatId);

//        findChat(currentUserId,selectedUserId);

    }

    public void setDate(){
        int getCurrentYear = Calendar.getInstance().get(Calendar.YEAR);
        int getCurrentMonth = Calendar.getInstance().get(Calendar.MONTH);
        int getCurrentDate = Calendar.getInstance().get(Calendar.DATE);
        getDate=getCurrentDate+getCurrentMonth+getCurrentYear+"";
    }

    private void create_chat(String sender,String receiver){

            HashMap<String, Object> chat = new HashMap<>();
            chat.put("chatId", chatId);
            chat.put("user1", sender);
            chat.put("user2", receiver);

            chatRef = db.collection("chats").document(chatId);
            chatRef.set(chat);

            Log.d("MESSAGE", "create_chat: " + chat);

    }

    private void findChat(final String cUserId, final String sUserId){
        chats = db.collection("chats");
        chatIdOne = cUserId + sUserId;
        chatIdTwo = sUserId + cUserId;

        DocumentReference one = chats.document(chatIdOne);
        DocumentReference two = chats.document(chatIdTwo);

        one.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("MESSAGE", "DocumentSnapshot data: " + document.getData());
                        Log.d("MESSAGE", "chatId: chat id one " + chatIdOne );
                        chatId = chatIdOne;
                        setValue(true);
                    } else {
                        Log.d("MESSAGE", "No such document");
                    }
                } else {
                    Log.d("MESSAGE", "get failed with ", task.getException());
                }
            }
        }
        );

        two.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("MESSAGE", "DocumentSnapshot data: " + document.getData());
                        Log.d("MESSAGE", "chatId: id two " + chatIdTwo );
                        chatId = chatIdTwo;
                        setValue(true);
                    } else {
                        Log.d("MESSAGE", "No such document");
                    }
                } else {
                    Log.d("MESSAGE", "get failed with ", task.getException());
                }
            }
        });


        Log.d("MESSAGE", "chatId: " + chatId );
    }


    public void setUpRecyclerView(){
        notebookRef = db.collection("chats").document(chatId).collection("messages");
        Query first = notebookRef;

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(first , Message.class)
                .build();

        messageAdapter = new MessageAdapter(options);

        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView = findViewById(R.id.messageRecyclerCon);
        recyclerView.setLayoutManager(mLayoutManager);
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
        Log.d("MESSAGE", "send_message:currentUser: " + currentUserId + "    receiverID:"+ selectedUserId);
        Log.d("MESSAGE", "chatID:"+ chatId);

        Boolean createChat = getValue();
        Log.d("MESSAGE", "Is chat already created?:  " + createChat);

        if(!createChat) {
            create_chat(sender, receiver);
        }



        newMessageRef = db.collection("chats").document(chatId).collection("messages");;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("Time", FieldValue.serverTimestamp());

        newMessageRef.add(hashMap);
    }


    public void onClick(View v){
        int i = v.getId();

        if(i == R.id.sendMessage){
             msg = text_msg.getText().toString();

            if(!msg.equals("")) {
                Log.d("MESSAGE", "Sending Message:currentUser: " + currentUserId + "    receiverID:"+ selectedUserId);
                send_Message(currentUserId,selectedUserId, msg);
            } else {
                Toast.makeText(message_between_users.this, "Can't do text", Toast.LENGTH_SHORT).show();
            }
            text_msg.setText("");
        }
    }

    public void setValue(boolean value) {
        this.found = value;
    }

    public boolean getValue() {
        return found;
    }
}

