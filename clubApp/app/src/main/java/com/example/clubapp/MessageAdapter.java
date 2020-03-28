package com.example.clubapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

public class MessageAdapter extends FirestoreRecyclerAdapter<Message,MessageAdapter.MessageHolder> {


    private static final int SENT = 0;
    private static final int RECEIVED = 1;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    public MessageAdapter( FirestoreRecyclerOptions<Message> options){
        super(options);
    }


    protected void onBindViewHolder(final MessageHolder holder, int position,Message model) {
//        message = model;
        holder.mContent.setText(model.getMessage());

        Log.d("MessageAdapter", model.getMessage());

        if(user.getUid().equals(model.getSender())){
            holder.mContent.setTextColor(Color.RED);
        }else{
            holder.mContent.setTextColor(Color.BLUE);
        }
    }


    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_card,parent,false);
        final MessageHolder holder = new MessageHolder(v);
//        if(viewType == SENT)
//        {
//            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
//        }
//        if(viewType == RECEIVED)
//        {
//            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
//        }

        return holder;

    }




//    @Override
//    public int getItemViewType(int position) {
//        if (message.getSender().equals(userID))
//            return SENT;
//        else
//            return RECEIVED;
//    }

    class MessageHolder extends RecyclerView.ViewHolder {


        //Variables for my chat lists...
        TextView mContent;


        public MessageHolder(View itemView) {
            super(itemView);

            //My variable initialization
            mContent = itemView.findViewById(R.id.messageText);
//            mContent =itemView.findViewById(R.id.list_studentNumber);

        }
    }
}