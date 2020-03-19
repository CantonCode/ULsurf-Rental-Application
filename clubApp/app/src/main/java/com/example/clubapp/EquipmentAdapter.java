package com.example.clubapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class EquipmentAdapter extends FirestoreRecyclerAdapter<Equipment,EquipmentAdapter.EquipmentHolder> {

    Dialog equipmentDialog;
    FirestoreRecyclerOptions<Equipment> model;

    public EquipmentAdapter(@NonNull FirestoreRecyclerOptions<Equipment> options) {
        super(options);

        this.model = options;
    }

    @Override
    protected void onBindViewHolder(@NonNull EquipmentHolder holder, final int position, @NonNull final Equipment model){
        holder.textViewEquipmentName.setText(model.getEquipmentName());
        holder.textViewEquipmentDescription.setText(model.getDescription());
        holder.textViewEquipmentSize.setText(model.getSize());
        Picasso.get().load(model.getImageUrl()).fit().centerCrop().into(holder.imageViewEquipmentPic);

        equipmentDialog.setContentView(R.layout.equipment_detail_page);
        equipmentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        holder.cardViewEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "test"+String.valueOf(position), Toast.LENGTH_SHORT).show();
                TextView textViewEquipmentName = equipmentDialog.findViewById(R.id.equipmentName);
                ImageView imageViewEquipmentImage = equipmentDialog.findViewById(R.id.equipmentImage);
                textViewEquipmentName.setText(model.getEquipmentName());
                Picasso.get().load(model.getImageUrl()).fit().centerCrop().into(imageViewEquipmentImage);

                equipmentDialog.show();
            }
        });
    }

    @NonNull
    @Override
    public EquipmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.equipment_item,parent,false);
        final EquipmentHolder holder = new EquipmentHolder(v);

        equipmentDialog = new Dialog(parent.getContext());

        return holder;
    }

    class EquipmentHolder extends RecyclerView.ViewHolder{
        TextView textViewEquipmentName;
        TextView textViewEquipmentDescription;
        TextView textViewEquipmentSize;
        ImageView imageViewEquipmentPic;
        CardView cardViewEquipment;

        public EquipmentHolder(View itemView){
            super(itemView);

            textViewEquipmentName = itemView.findViewById(R.id.equipmentName);
            textViewEquipmentDescription = itemView.findViewById(R.id.equipmentDescription);
            textViewEquipmentSize = itemView.findViewById(R.id.equipmentSize);
            imageViewEquipmentPic = itemView.findViewById(R.id.boardImageView);
            cardViewEquipment = itemView.findViewById(R.id.equipmentCard);

        }

    }
}
