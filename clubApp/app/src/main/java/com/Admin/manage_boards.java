package com.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.Rental.Equipment;
import com.Rental.EquipmentAdapter;
import com.example.clubapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class manage_boards extends AppCompatActivity {

    private EquipmentAdapter equipmentAdapter;
    RecyclerView recyclerView;
    private CollectionReference notebookRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_boards);
        setUpRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();

        initBoards();
    }

    public void initBoards(){
        notebookRef = db.collection("equipment");

        Query first = notebookRef;

        FirestoreRecyclerOptions<Equipment> options = new FirestoreRecyclerOptions.Builder<Equipment>()
                .setQuery(first, Equipment.class)
                .build();

        equipmentAdapter = new EquipmentAdapter(options);
        recyclerView.setAdapter(equipmentAdapter);
        equipmentAdapter.startListening();



    }

    public void setUpRecyclerView() {
        recyclerView = findViewById(R.id.addBoardRecycler);
        mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);
    }
}
