package com.example.clubapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message,MessageAdapter.MessageViewHolder> {


    private FirebaseFirestore mFirestore;
    private Context mContext;
    private FirebaseUser firebaseUser;
    private String title;
    private String userID;
//    Message message;

    private static final int SENT = 0;
    private static final int RECEIVED = 1;

    public MessageAdapter( FirestoreRecyclerOptions<Message> options){
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MessageViewHolder holder, int position,@NonNull Message model) {
//        message = model;
        holder.mContent.setText(model.getMessage());

        Log.d("USER", model.getMessage());
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_card,parent,false);;
//        if(viewType == SENT)
//        {
//            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
//        }
//        if(viewType == RECEIVED)
//        {
//            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
//        }

        return new MessageViewHolder(view);

    }




//    @Override
//    public int getItemViewType(int position) {
//        if (message.getSender().equals(userID))
//            return SENT;
//        else
//            return RECEIVED;
//    }

    class MessageViewHolder extends RecyclerView.ViewHolder {


        //Variables for my chat lists...
        TextView mName, mContent;


        public MessageViewHolder(final View itemView) {
            super(itemView);

            //My variable initialization
            mContent = itemView.findViewById(R.id.messageText);
//            mContent =itemView.findViewById(R.id.list_studentNumber);

        }
    }
}

