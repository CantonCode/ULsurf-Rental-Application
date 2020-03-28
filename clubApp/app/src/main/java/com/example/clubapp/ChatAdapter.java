package com.example.clubapp;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;


public class ChatAdapter extends FirestoreRecyclerAdapter<Chat,ChatAdapter.ChatHolder> {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private String currentUserId;

    public ChatAdapter(FirestoreRecyclerOptions<Chat>options){
        super(options);
    }


    protected void onBindViewHolder(ChatHolder holder,int position,final Chat model){

        final String selectedUser;

        currentUserId = user.getUid();

        if(currentUserId.equals(model.getUser1())){
           selectedUser = model.getUser2();

        }else{
            selectedUser = model.getUser1();
        }

        holder.chatUserName.setText(selectedUser);

        holder.chat_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( v.getContext(), message_between_users.class);
                intent.putExtra("selected_user",selectedUser);
                v.getContext().startActivity(intent);
            }
        });;

        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/clubapp-surf.appspot.com/o/images%2FxcDaQgFDEaQKlyExvaV9THLFPaj1?alt=media&token=bc45f854-58f8-40b4-8dc4-ac4db2c4528e").fit().centerCrop().into(holder.chatUserImage);
        Log.d("CHAT", "user1:" + model.getUser1());
        Log.d("CHAT", "user2:" + model.getUser2());
        Log.d("CHAT", "userChatId:" + model.getChatId());


    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_card,parent,false);
        final ChatAdapter.ChatHolder holder = new ChatAdapter.ChatHolder(v);

        return holder;
    }

    class ChatHolder extends RecyclerView.ViewHolder{

        TextView chatUserName;
        ImageView chatUserImage;
        CardView chat_selected;

        public ChatHolder(final View itemView){
            super(itemView);

            chatUserName = itemView.findViewById(R.id.chatUserName);
            chatUserImage = itemView.findViewById(R.id.chatUserImage);
            chat_selected = itemView.findViewById(R.id.chat_card);
        }
    }
}
