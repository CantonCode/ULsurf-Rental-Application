package com.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
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

public class manage_boards extends AppCompatActivity {

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
                deleteBoard(name);
                Toast.makeText(manage_boards.this, name, Toast.LENGTH_SHORT).show();
            }
        });
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


}
