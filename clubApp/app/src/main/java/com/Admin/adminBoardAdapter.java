package com.Admin;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Rental.Equipment;
import com.Rental.EquipmentAdapter;
import com.example.clubapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class adminBoardAdapter extends FirestoreRecyclerAdapter<Equipment, adminBoardAdapter.adminBoardHolder>{

    private OnItemClickListener mListener;



    public interface  OnItemClickListener{
        void onDeleteClick(int position,String name);
    }

    public void setOnItemClickListener(OnItemClickListener listener){mListener = listener;}

    public adminBoardAdapter(FirestoreRecyclerOptions<Equipment> options) {
        super(options);
    }

        @Override
        protected void onBindViewHolder(@NonNull final adminBoardHolder holder, int position, @NonNull Equipment model) {
            final OnItemClickListener listener = mListener;
                holder.boardName.setText(model.getEquipmentName());
                holder.id = model.getEquipmentId();

                Picasso.get().load(model.getImageUrl()).fit().centerCrop().into(holder.boardPic);

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null ){
                        int position = holder.getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position,holder.id);
                        }
                    }

                }
            });


        }


        @Override
        public adminBoardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_board_card,parent,false);
            final adminBoardHolder holder = new adminBoardHolder(v,mListener);



            return holder;
        }



class adminBoardHolder extends RecyclerView.ViewHolder{
        TextView boardName;
        ImageView boardPic, delete;
        String id;



        public adminBoardHolder(View itemView,final OnItemClickListener listener){
            super(itemView);

            boardName = itemView.findViewById(R.id.adBoardName);
            boardPic = itemView.findViewById(R.id.adBoardImage);
            delete = itemView.findViewById(R.id.board_delete);
            id = "";



        }
    }
}