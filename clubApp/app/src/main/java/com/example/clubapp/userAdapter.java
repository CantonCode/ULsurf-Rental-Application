package com.example.clubapp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class userAdapter extends FirestoreRecyclerAdapter<User,userAdapter.UsersViewHolder> {

    public userAdapter( FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder( UsersViewHolder holder, int position, User model) {
        holder.list_userID.setText(model.getUserId());
        holder.list_studentNumber.setText(model.getStudentNumber());
        holder.list_photo.setText(model.getPhotoUrl());
        holder.list_userName.setText(model.getUserName());
        holder.list_admin.setText(String.valueOf(model.isAdmin()));
    }




    @Override
    public UsersViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.format_users_value, parent, false);
        return new UsersViewHolder(v);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        private TextView list_admin;
        private TextView list_photo;
        private TextView list_studentNumber;
        private TextView list_userID;
        private TextView list_userName;

        public UsersViewHolder(View itemView) {
            super(itemView);

            list_admin = itemView.findViewById(R.id.list_photo);
            list_photo = itemView.findViewById(R.id.list_photo);
            list_studentNumber = itemView.findViewById(R.id.list_studentNumber);
            list_userID = itemView.findViewById(R.id.list_userID);
            list_userName = itemView.findViewById(R.id.list_userName);
        }
    }
}


