package com.Rental;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.clubapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

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
        holder.textViewEquipmentSize.setText(model.getSize());
        Picasso.get().load(model.getImageUrl()).fit().centerCrop().into(holder.imageViewEquipmentPic);
        Log.d("USERIDbbbbb", model.getEquipmentName());

        equipmentDialog.setContentView(R.layout.equipment_detail_page);
        equipmentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        holder.cardViewEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textViewEquipmentName = equipmentDialog.findViewById(R.id.equipmentName);
                ImageView imageViewEquipmentImage = equipmentDialog.findViewById(R.id.equipmentImage);
                textViewEquipmentName.setText(model.getEquipmentName());
                Picasso.get().load(model.getImageUrl()).fit().centerCrop().into(imageViewEquipmentImage);

                equipmentDialog.show();

                Button btn = (Button) equipmentDialog.findViewById(R.id.rentThisBoard);
                btn.setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), CalendarActivity.class);
                        intent.putExtra("selected_equipment",model.getEquipmentId());
                        v.getContext().startActivity(intent);
                    }
                });

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
            textViewEquipmentSize = itemView.findViewById(R.id.equipmentSize);
            imageViewEquipmentPic = itemView.findViewById(R.id.boardImageView);
            cardViewEquipment = itemView.findViewById(R.id.equipmentCard);

        }
    }

}
