package com.example.clubapp;


import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class userAdapter extends FirestoreRecyclerAdapter<User,userAdapter.UserViewHolder> {

    public userAdapter( FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder,final int position, @NonNull User model) {
        holder.list_studentNumber.setText(model.getStudentNumber());
        holder.list_userName.setText(model.getUserName());
        holder.list_admin.setText(String.valueOf(model.isAdmin()));
        Picasso.get().load(model.getPhotoUrl()).fit().centerCrop().into(holder.list_photo);
        Log.d("USERIDaaaaa", model.getUserName());

    }



    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.format_users_value, parent, false);
        return new UserViewHolder(v);
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        TextView list_admin;
        ImageView list_photo;
        TextView list_studentNumber;
        TextView list_userName;

        public UserViewHolder(final View itemView) {
            super(itemView);

            list_admin = itemView.findViewById(R.id.list_admin);
            list_photo = itemView.findViewById(R.id.list_photo);
            list_studentNumber = itemView.findViewById(R.id.list_studentNumber);
            list_userName = itemView.findViewById(R.id.list_userName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), message_between_users.class);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}

