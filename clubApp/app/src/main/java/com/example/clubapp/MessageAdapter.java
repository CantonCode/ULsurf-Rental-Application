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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    private List<Chat> mChat;
    private String imageUrl;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private Context mContext;
    private FirebaseUser firebaseUser;
    private String title;
    private String userID;

    private static final int SENT = 0;
    private static final int RECEIVED = 1;


    public MessageAdapter(List<Chat> mChat,String title,String userID) {
        this.mChat = mChat;
        this.title = title;
        this.userID = userID;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mContext = parent.getContext();

        View view = null;
        if(viewType == SENT)
        {
            view =
                    LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
        }
        if(viewType == RECEIVED)
        {
            view =
                    LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
        }

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        String getSenderId = mChat.get(position).getSender();
        final String getSenderMessage = mChat.get(position).getMessage();
        final String getID = mChat.get(position).getReceiver();
        //final String theType = mChat.get(position).getType();

            //To get name...
            mFirestore.collection("chats").document(getSenderId).get()
                    .addOnCompleteListener((Activity) mContext, new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                //String getFirstName = task.getResult().getString("Firstname");


                               // holder.mName.setText(getFirstName.charAt(0) + getFirstName.substring(1).toLowerCase());
                            }
                        }
                    });
            holder.mContent.setText(getSenderMessage);
        }


    @Override
    public int getItemCount() {
        return mChat.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (mChat.get(position).getSender().equals(userID))
            return SENT;
        else
            return RECEIVED;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        //Variables for my chat lists...
        private TextView mName, mContent;
        private RelativeLayout mBox;


        public ViewHolder(View itemView) {
            super(itemView);

            //My variable initialization
            mName = (TextView) itemView.findViewById(R.id.list_userName);
            mContent = (TextView) itemView.findViewById(R.id.show_message);
            mBox = (RelativeLayout) itemView.findViewById(R.id.user_chatBox);
        }
    }
}

