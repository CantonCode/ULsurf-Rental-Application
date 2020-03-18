package com.example.clubapp;

import android.net.Uri;
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

public class EquipmentAdapter extends FirestoreRecyclerAdapter<Equipment,EquipmentAdapter.EquipmentHolder> {

    public EquipmentAdapter(@NonNull FirestoreRecyclerOptions<Equipment> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EquipmentHolder holder,int position, @NonNull Equipment model){
        holder.textViewEquipmentName.setText(model.getEquipmentName());
        holder.textViewEquipmentDescription.setText(model.getDescription());
        holder.textViewEquipmentSize.setText(model.getSize());
        Picasso.get().load(model.getImageUrl()).fit().centerCrop().into(holder.imageViewEquipmentPic);
    }

    @NonNull
    @Override
    public EquipmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.equipment_item,parent,false);
        return new EquipmentHolder(v);
    }

    class EquipmentHolder extends RecyclerView.ViewHolder{
        TextView textViewEquipmentName;
        TextView textViewEquipmentDescription;
        TextView textViewEquipmentSize;
        ImageView imageViewEquipmentPic;

        public EquipmentHolder(View itemView){
            super(itemView);

            textViewEquipmentName = itemView.findViewById(R.id.equipmentName);
            textViewEquipmentDescription = itemView.findViewById(R.id.equipmentDescription);
            textViewEquipmentSize = itemView.findViewById(R.id.equipmentSize);
            imageViewEquipmentPic = itemView.findViewById(R.id.boardImageView);

        }

    }
}
