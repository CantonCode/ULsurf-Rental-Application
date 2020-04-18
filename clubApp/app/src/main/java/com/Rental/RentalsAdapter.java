package com.Rental;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clubapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class RentalsAdapter extends RecyclerView.Adapter<RentalsAdapter.ViewHolder> {
    private ArrayList<Rentals> mRentalsList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
        }
    }

    public RentalsAdapter(ArrayList<Rentals> exampleList) {
        mRentalsList = exampleList;
    }

    @Override
    public RentalsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rentals, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RentalsAdapter.ViewHolder holder, int position) {
        Rentals currentItem = mRentalsList.get(position);
        Log.d("Adapt", currentItem.toString());
        Picasso.get().load(currentItem.getImageUrl()).fit().centerCrop().into(holder.mImageView);
        holder.mTextView1.setText(currentItem.getBoardName());
        holder.mTextView2.setText(currentItem.getDate());
    }



    @Override
    public int getItemCount() {
        return mRentalsList.size();
    }
}
