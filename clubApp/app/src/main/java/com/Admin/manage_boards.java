package com.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.Rental.Equipment;
import com.Rental.EquipmentAdapter;
import com.example.clubapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class manage_boards extends AppCompatActivity implements View.OnClickListener {

    private adminBoardAdapter equipmentAdapter;
    RecyclerView recyclerView;
    private CollectionReference notebookRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_boards);
        setUpRecyclerView();

        findViewById(R.id.buttonBack).setOnClickListener(this);
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

        equipmentAdapter = new adminBoardAdapter(options);
        recyclerView.setAdapter(equipmentAdapter);
        equipmentAdapter.startListening();

        equipmentAdapter.setOnItemClickListener(new adminBoardAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position,String name) {
                buildDialog(name);

                Toast.makeText(manage_boards.this, name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void buildDialog(final String name){
        AlertDialog.Builder builder = new AlertDialog.Builder(manage_boards.this);

        // Set a title for alert dialog
        builder.setTitle("Delete Board");

        // Ask the final question
        builder.setMessage("Are you sure to delete this board along with all its data and rentals?");


        // Set the alert dialog yes button click listener
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when user clicked the Yes button
                // Set the TextView visibility GONE
                deleteBoard(name);
            }
        });

        // Set the alert dialog no button click listener
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when No button clicked
                Toast.makeText(getApplicationContext(),
                        "No Button Clicked",Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();

    }

    public void setUpRecyclerView() {
        recyclerView = findViewById(R.id.addBoardRecycler);
        mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);
    }

    public void deleteBoard(final String boardId){
        db.collection("equipment").document(boardId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("MANAGE", "Deleted: "+ boardId);
                        deleteAssociatedData(boardId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MANAGE", "Error deleting document", e);
                    }
                });

        }

    public void deleteAssociatedData(final String boardId){

        db.collection("rented")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("MANAGE", document.getId() + " => " + document.getData());
                                searchForRental(document.getId(),boardId);
                            }
                        } else {
                            Log.d("MANAGE", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void searchForRental(final String userId, final String boardId){
        db.collection("rented").document(userId).collection("equipment")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("MANAGE", "userId:" + userId + "equipmentId:  " + document.getId() + " => " + document.getData());
                                String equipmentId = document.getId();

                                if(equipmentId.equals(boardId)){
                                    deleteFromRental(userId,boardId);
                                }

                            }
                        } else {
                            Log.d("MANAGE", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void deleteFromRental(final String userId,final String boardId){
        db.collection("rented").document(userId).collection("equipment").document(boardId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("MANAGE", "Deteled from renatls: "+ boardId);
                        Toast.makeText(manage_boards.this, "Successfully delete board and associated data", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MANAGE", "Error deleting document", e);
                    }
                });

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.buttonBack){
            Intent intent = new Intent(this,adminActivity.class);
            startActivity(intent);
        }
    }
}
