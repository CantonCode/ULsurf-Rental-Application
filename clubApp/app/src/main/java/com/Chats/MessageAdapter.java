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

import org.ocpsoft.prettytime.PrettyTime;
import androidx.recyclerview.widget.RecyclerView;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message,MessageAdapter.MessageHolder> {


    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int MULTIPLE = 2;
    private static final int NONMULT = 3;

    PrettyTime p = new PrettyTime();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String userName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MessageAdapter( FirestoreRecyclerOptions<Message> options){
        super(options);
    }


    protected void onBindViewHolder(final MessageHolder holder,final int position,final Message model) {

        final Boolean isEqual;

        if(isMultiple(position, model.getSender()) == MULTIPLE){
            Log.d("MESSAGE", "EQUALS ");
          isEqual = true;
        }
        else{
            isEqual = false;
        }

        DocumentReference docRef = db.collection("users").document(model.getSender());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.sender.setVisibility(View.VISIBLE);
                holder.pic.setVisibility(View.VISIBLE);

                holder.message.setText(model.getMessage());
                holder.sender.setText(documentSnapshot.get("userName").toString());
                holder.timestamp.setText(p.format(model.getTime()));
                Picasso.get().load(documentSnapshot.get("photoUrl").toString()).transform(new RoundedCornersTransformation(50,0)).fit().centerCrop().into(holder.pic);

                if( isEqual){

                    holder.sender.setVisibility(View.GONE);
                    holder.pic.setVisibility(View.GONE);
                }

            }
        });

    }


    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(viewType == RIGHT)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_right, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_left, parent, false);

        return new MessageHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position).getSender().equals(user.getUid()))
            return RIGHT;
        else
            return LEFT;

    }

    public int isMultiple(int position,String id) {

        int currentMessagePos = position;
        int previousMessPos = position - 1;

        if(previousMessPos < 0)
            previousMessPos = 0;

        String currentSender = getItem(currentMessagePos).getSender();
        String previousSender = getItem(previousMessPos).getSender()  ;

        String currentMess = getItem(currentMessagePos).getMessage();
        String previousMess= getItem(previousMessPos).getMessage()  ;



        Log.d("MESSAGE", "currentMesId" + currentMessagePos);
        Log.d("MESSAGE", "prevMesId" + previousMessPos);
        Log.d("MESSAGE", "currentMesSender" + currentSender);
        Log.d("MESSAGE", "prevMesSender" + previousSender);

        if(currentSender.equals(previousSender)) {
            Log.d("MESSAGE", "Is a Match: \n" + currentSender + ": "+ currentMess + "\n" + previousSender + ": "+ previousMess);
            return MULTIPLE;
        }
        else {
            Log.d("MESSAGE", "NOT a Match: \n" + currentSender + ": "+ currentMess + "\n" + previousSender + ": "+ previousMess);
            return NONMULT;
        }

    }

    class MessageHolder extends RecyclerView.ViewHolder {


        //Variables for my chat lists...
        TextView sender , message, timestamp;
        ImageView pic;




        public MessageHolder(View itemView) {
            super(itemView);

            //My variable initialization
            sender = itemView.findViewById(R.id.sendername);
            message = itemView.findViewById(R.id.textmessage);
            timestamp = itemView.findViewById(R.id.timestamp);
            pic = itemView.findViewById(R.id.userPic);

        }
    }
}


