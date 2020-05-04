package com.Admin;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Rental.Equipment;
import com.Rental.EquipmentAdapter;
import com.example.clubapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class adminBoardAdapter extends FirestoreRecyclerAdapter<Equipment, adminBoardAdapter.adminBoardHolder>{

    public adminBoardAdapter(FirestoreRecyclerOptions<Equipment> options) {
        super(options);
    }

        @Override
        protected void onBindViewHolder(@NonNull adminBoardHolder holder, int position, @NonNull Equipment model) {
                holder.boardName.setText(model.getEquipmentName());
        }


        @Override
        public adminBoardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_board_card,parent,false);
            final adminBoardHolder holder = new adminBoardHolder(v);



            return holder;
        }



class adminBoardHolder extends RecyclerView.ViewHolder{
        TextView boardName;
        ImageView boardPic;

        public adminBoardHolder(View itemView){
            super(itemView);

            boardName = itemView.findViewById(R.id.boardName);

        }
    }
}