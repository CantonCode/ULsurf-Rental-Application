package com.Chats;


import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.Login.User;
import com.example.clubapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message,MessageAdapter.MessageHolder> {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public MessageAdapter( FirestoreRecyclerOptions<Message> options){
        super(options);
    }

    protected void onBindViewHolder(final MessageHolder holder, int position,Message model) {

        /*
        String chatId = user.getUid();
        final ArrayList<String> userName = model.getUsers();



        DocumentReference docRef = db.collection("users").document(chatId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User nameDetail = documentSnapshot.toObject(User.class);
                holder.chatUserName.setText(nameDetail.getUserName());
                Log.d("CHAT", "onSuccess: " + nameDetail.getPhotoUrl());
                Picasso.get().load(nameDetail.getPhotoUrl()).transform(new RoundedCornersTransformation(50,0)).fit().centerCrop().into(holder.chatUserImage);
            }
        });
        */

        Log.d("MessageAdapter", model.getMessage());

        if(user.getUid().equals(model.getSender())){
            holder.mContentForReceiver.setTextColor(Color.WHITE);
            holder.mContentForReceiver.setText(model.getMessage());
            holder.mContentForSender.setVisibility(View.GONE);
        }else{
            holder.mContentForSender.setTextColor(Color.WHITE);
            holder.mContentForSender.setText(model.getMessage());
            holder.mContentForReceiver.setVisibility(View.GONE);
        }
    }


    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_card,parent,false);
        final MessageHolder holder = new MessageHolder(v);

        return holder;

    }
    class MessageHolder extends RecyclerView.ViewHolder {


        //Variables for my chat lists...
        TextView mContentForSender , mContentForReceiver;
        TextView chatUserName;
        ImageView chatUserImage;
        CardView chat_selected;

        public MessageHolder(View itemView) {
            super(itemView);

            //My variable initialization
            mContentForSender = itemView.findViewById(R.id.show_message);
            mContentForReceiver = itemView.findViewById(R.id.show_message1);
            chatUserName = itemView.findViewById(R.id.chatUserName);
            chatUserImage = itemView.findViewById(R.id.chatUserImage);
            chat_selected = itemView.findViewById(R.id.chat_card_for_user_page);
        }
    }
}