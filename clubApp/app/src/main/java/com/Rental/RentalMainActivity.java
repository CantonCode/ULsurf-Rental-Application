package com.Rental;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import com.Login.MainActivity;
import com.example.clubapp.R;
import com.Login.homeActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RentalMainActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("equipment");
    private EquipmentAdapter equipmentAdapter;
    private EquipmentAdapter equipmentAdapter1;
    private EquipmentAdapter equipmentAdapter2;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Dialog sortByDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_main);

        findViewById(R.id.goBack).setOnClickListener(this);
        findViewById(R.id.sortBy).setOnClickListener(this);
        findViewById(R.id.floatingActionButton).setOnClickListener(this);
        SearchView searchView = findViewById(R.id.searchView); // inititate a search view


        setUpRecyclerView();
        setUpDialog();
        setUpSearch(searchView);
    }

    public void setUpRecyclerView(){
        Query query = notebookRef.orderBy("rented", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Equipment> options = new FirestoreRecyclerOptions.Builder<Equipment>()
                .setQuery(query, Equipment.class)
                .build();

        equipmentAdapter = new EquipmentAdapter(options);

        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView = findViewById(R.id.recyclerEquipment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(equipmentAdapter);

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        equipmentAdapter.startListening();
    }
    protected void onStop(){
        super.onStop();
        equipmentAdapter.stopListening();
    }

    private void goBack(){
        Intent intent = new Intent(this, homeActivity.class);
        startActivity(intent);
    }

    private void setUpDialog() {
        sortByDialog = new Dialog(this);
        sortByDialog.setContentView(R.layout.sort_by_equipment);
        sortByDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sortByDialog.findViewById(R.id.availablitiy).setOnClickListener(this);
        sortByDialog.findViewById(R.id.level).setOnClickListener(this);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.message, popup.getMenu());
        popup.show();
    }

    private void setUpSearch(SearchView searchView){
        // perform set on query text listener event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Pattern pattern = Pattern.compile("\\s");
                Matcher matcher = pattern.matcher(query);
                boolean found = matcher.find();

                Log.d("RENTAL", "onQueryTextSubmit: " + query);
                Log.d("SEARCH", "is there a space? " + found + " "+ query);

                if(found){
                    query = query.replace(" ","-");
                    search(query);
                    Log.d("SEARCH", "replaced spaces " + found + " "+ query);
                }else{
                    search(query);
                    Log.d("SEARCH", "no spaces " + found + " "+ query);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
// do something when text changes
                return false;
            }
        });
        }

    private void changeView(){
        Query query = notebookRef.orderBy("size", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Equipment> options1 = new FirestoreRecyclerOptions.Builder<Equipment>()
                .setQuery(query, Equipment.class)
                .build();

        equipmentAdapter1 = new EquipmentAdapter(options1);
        recyclerView.setAdapter(equipmentAdapter1);
        equipmentAdapter1.startListening();
        sortByDialog.dismiss();
//        equipmentAdapter1.stopListening();
    }

    private void resetView(){
        Query query = notebookRef.orderBy("rented", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Equipment> options1 = new FirestoreRecyclerOptions.Builder<Equipment>()
                .setQuery(query, Equipment.class)
                .build();

        equipmentAdapter1 = new EquipmentAdapter(options1);
        recyclerView.setAdapter(equipmentAdapter1);
        equipmentAdapter1.startListening();
        sortByDialog.dismiss();
//        equipmentAdapter1.stopListening();
    }

    private void search(String s){
        Log.d("RENTAL", "search: " + s);
        String desc = s;
        Query query = notebookRef.whereArrayContainsAny("description",Arrays.asList(desc.trim().toLowerCase()));
        FirestoreRecyclerOptions<Equipment> options2 = new FirestoreRecyclerOptions.Builder<Equipment>()
                .setQuery(query, Equipment.class)
                .build();

        equipmentAdapter2 = new EquipmentAdapter(options2);
        recyclerView.setAdapter(equipmentAdapter2);
        equipmentAdapter2.startListening();
        sortByDialog.dismiss();
//        equipmentAdapter1.stopListening();
    }

    private void popUp(){
        sortByDialog.show();
    }

    public void onClick(View v){
        int i = v.getId();
        if (i == R.id.goBack) {
            goBack();
        }
        if (i == R.id.sortBy) {
            popUp();
        }

        if (i == R.id.floatingActionButton) {
            Toast.makeText(RentalMainActivity.this, "Resetting View", Toast.LENGTH_SHORT).show();
            resetView();
        }

        if (i == R.id.availablitiy) {
            changeView();
            Toast.makeText(this, "Showing by size", Toast.LENGTH_SHORT).show();
        }

        if( i == R.id.level){
            PopupMenu popup = new PopupMenu(RentalMainActivity.this, v);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.beginner:
                            Toast.makeText(RentalMainActivity.this, "Showing Beginner Boards", Toast.LENGTH_SHORT).show();
                            search("beginner");
                            return true;
                        case R.id.intermediate:
                            Toast.makeText(RentalMainActivity.this, "Showing Intermediate Boards", Toast.LENGTH_SHORT).show();
                            search("intermediate");
                            return true;
                        case R.id.advanced:
                            Toast.makeText(RentalMainActivity.this, "Showing Advanced Boards", Toast.LENGTH_SHORT).show();
                            search("advanced");
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.inflate(R.menu.message);
            popup.show();
        }



    }


    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.beginner:
                search("beginner");
            case R.id.intermediate:
                search("intermediate");
            case R.id.advanced:
                search("advanced");


            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, homeActivity.class);
        startActivity(intent);
        finish();
    }

}
