package com.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import com.Login.homeActivity;
import com.Rental.Equipment;
import com.Rental.EquipmentAdapter;
import com.example.clubapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class adminActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView add,manage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        findViewById(R.id.addBoards).setOnClickListener(this);
        findViewById(R.id.manageBoards).setOnClickListener(this);

        add = findViewById(R.id.addBoards);
        manage = findViewById(R.id.manageBoards);
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/clubapp-surf.appspot.com/o/stockPhotos%2Fadd_board.png?alt=media&token=15d67592-4e5c-4889-828e-102b3e03f987").fit().centerCrop().transform(new RoundedCornersTransformation(10,0)).into(add);
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/clubapp-surf.appspot.com/o/stockPhotos%2Fmanage_board.png?alt=media&token=78a4f24f-55af-4d6a-971f-e6cc96ea3623").fit().centerCrop().transform(new RoundedCornersTransformation(10,0)).into(manage);

    }




    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.addBoards){
            Intent intent = new Intent(this,addBoard.class);
            startActivity(intent);
        }

        if (i == R.id.manageBoards){
            Intent intent = new Intent(this,manage_boards.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, homeActivity.class);
        startActivity(intent);
        finish();
    }
}
