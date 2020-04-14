package com.Chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Login.MainActivity;
import com.Login.User;
import com.example.clubapp.R;
import com.Login.homeActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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
import com.squareup.picasso.Picasso;


import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class message_between_users extends AppCompatActivity implements View.OnClickListener {

    private ListenerRegistration listenerRegistration;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private com.Chats.userAdapter userAdapter;
    private MessageAdapter messageAdapter;
    private ChatAdapter chatAdapter;
    private FirebaseUser firebaseUser;
    private CollectionReference notebookRef;
    private CollectionReference newMessageRef;
    private DocumentReference chatRef;


    private ImageButton send_btn;
    private EditText text_msg;
    private TextView userName;
    private ImageView userPic;


    RecyclerView recyclerView;


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
    LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_messages);
        findViewById(R.id.sendMessage).setOnClickListener(this);
        setValue(false);
        text_msg = findViewById(R.id.newMessage);
        selectedUserId = getIntent().getStringExtra("selected_user");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();

        setUpRecyclerView();

        chatId = currentUserId + selectedUserId;

        findChat(currentUserId, selectedUserId);
        setText();




    }

    private void setText(){

        userName = findViewById(R.id.username);
        userPic = findViewById(R.id.userPic);


        DocumentReference docRef = db.collection("users").document(selectedUserId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User nameDetail = documentSnapshot.toObject(User.class);
                userName.setText(nameDetail.getUserName());
                Picasso.get().load(nameDetail.getPhotoUrl()).transform(new RoundedCornersTransformation(50,0)).fit().centerCrop().into(userPic);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        Log.d("MESSAGE", "Init   currentUser: " + currentUserId + "    receiverID:" + selectedUserId);
        setDate();
        Log.d("MESSAGE", "chatID new one:" + chatId);

//        findChat(currentUserId,selectedUserId);
    }





    public void setDate() {
        int getCurrentYear = Calendar.getInstance().get(Calendar.YEAR);
        int getCurrentMonth = Calendar.getInstance().get(Calendar.MONTH);
        int getCurrentDate = Calendar.getInstance().get(Calendar.DATE);
        getDate = getCurrentDate + getCurrentMonth + getCurrentYear + "";
    }

    private void create_chat(String sender, String receiver) {

        HashMap<String, Object> chat = new HashMap<>();
        chat.put("chatId", chatId);
        chat.put("users", Arrays.asList(sender,receiver));

        chatRef = db.collection("chats").document(chatId);
        chatRef.set(chat);

        Log.d("MESSAGE", "create_chat: " + chat);

    }

    private void findChat(final String cUserId, final String sUserId) {


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
                                                        Log.d("MESSAGE", "chatId: chat id one " + chatIdOne);
                                                        setId(chatIdOne);
                                                        setValue(true);
                                                        initChat();
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
                        Log.d("MESSAGE", "chatId: id two " + chatIdTwo);
                        setId(chatIdTwo);
                        setValue(true);
                        initChat();
                    } else {
                        Log.d("MESSAGE", "No such document");
                    }
                } else {
                    Log.d("MESSAGE", "get failed with ", task.getException());
                }
            }
        });


        Log.d("MESSAGE", "chatId: " + chatId);
    }

    public void initChat(){
        String id = getId();
        Log.d("MESSAGE", "chat init:" + id);

        notebookRef = db.collection("chats").document(id).collection("messages");

        Query first = notebookRef.orderBy("Time", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(first, Message.class)
                .build();

        messageAdapter = new MessageAdapter(options);
        messageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            }
        });
        recyclerView.setAdapter(messageAdapter);
        messageAdapter.startListening();

    }


    public void setUpRecyclerView() {
        recyclerView = findViewById(R.id.messageRecyclerCon);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
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

    private void send_Message(String sender, String receiver, String message) {
        Log.d("MESSAGE", "send_message:currentUser: " + currentUserId + "    receiverID:" + selectedUserId);
        Log.d("MESSAGE", "chatID:" + chatId);

        Boolean createChat = getValue();
        Log.d("MESSAGE", "Is chat already created?:  " + createChat);

        if (!createChat) {
            create_chat(sender, receiver);
            initChat();
        }


        newMessageRef = db.collection("chats").document(chatId).collection("messages");
        ;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("Time", FieldValue.serverTimestamp());

        newMessageRef.add(hashMap);

        recyclerView.scrollToPosition(messageAdapter.getItemCount());
    }


    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.sendMessage) {
            msg = text_msg.getText().toString();

            if (!msg.equals("")) {
                Log.d("MESSAGE", "Sending Message:currentUser: " + currentUserId + "    receiverID:" + selectedUserId);
                send_Message(currentUserId, selectedUserId, msg);
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

    public void setId(String value) {
        this.chatId = value;
    }

    public String getId() {
        return chatId;
    }


    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        messageAdapter.stopListening();
        Intent intent = new Intent(this, messageActivity.class);
        startActivity(intent);
        finish();
    }
}


