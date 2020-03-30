package com.Chats;


import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clubapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        TextView mContentForSender , mContentForReceiver;

        CardView display_chat;


        public MessageHolder(View itemView) {
            super(itemView);

            //My variable initialization
            mContentForSender = itemView.findViewById(R.id.show_message);
            mContentForReceiver = itemView.findViewById(R.id.show_message1);
            //display_chat = itemView.findViewById(R.id.user_chatBox);

//            mContent =itemView.findViewById(R.id.list_studentNumber);

        }
    }
}