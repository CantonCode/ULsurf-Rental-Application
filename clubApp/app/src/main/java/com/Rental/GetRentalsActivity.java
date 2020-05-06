package com.Rental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.Login.MainActivity;
import com.Login.userProfileActivity;
import com.example.clubapp.R;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetRentalsActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    String currentUserId;
    List<List<String>> allRentals = new ArrayList<>();
    List<String> rentals = new ArrayList<>();
    List<String> names = new ArrayList<>();
    HashMap<String, String> equipment=new HashMap<String, String>();
    HashMap<String, String> equipmentPics=new HashMap<String, String>();
    ArrayList<Rentals> allRentaldates = new ArrayList<>();
    TextView noRentals;
    CollectionReference notebookRef;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_rentals);
        noRentals = findViewById(R.id.noRentals);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        notebookRef = db.collection("rented").document(currentUserId).collection("equipment");

        getEquipment();
        Button btn = findViewById(R.id.overview);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetRentalsActivity.this, RentalsOverviewActivity.class);
                startActivity(intent);
            }
        });

        Button btn2 = findViewById(R.id.topRentals);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetRentalsActivity.this, TopRentalsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getEquipment(){
        CollectionReference docRef = db.collection("equipment");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        equipment.put(document.getId(), document.getString("equipmentName"));
                        equipmentPics.put(document.getId(), document.getString("imageUrl"));
                    }

                } else {
                    Log.d("Chart", "Error getting documents: ", task.getException());
                }
                Log.d("Chart", equipment.toString());
                getUserRentals();

            }
        });
    }

    private void getUserRentals(){
        CollectionReference colRef = db.collection("rented").document(currentUserId).collection("equipment");

        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        rentals = (ArrayList<String>) document.get("dateOfRental");
                        allRentals.add(rentals);
                        setRental(document.getId(), rentals);
                    }
                    buildRecyclerView();
                } else {
                    Log.d("Check", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void setRental(String id, List<String> dates) {
        String boardName ="";
        String img ="";
        for (Map.Entry<String,String> entry : equipment.entrySet()) {
            String key = entry.getKey();
            String name = entry.getValue();
            if(key.equals(id)){
                Log.d("Chart", key + " => " + name);
                boardName = name;
                names.add(name);
            }
        }
        for (Map.Entry<String,String> entry : equipmentPics.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            if(key.equals(id)){
                img = val;
            }
        }
        for(String date: dates){
            SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date now = new Date();
                Date d=dateFormat.parse(date);

                if(now.before(d)) {
                    Rentals rent = new Rentals(img, boardName, date);
                    allRentaldates.add(rent);
                }
            }
            catch(Exception e) {
                //java.text.ParseException: Unparseable date: Geting error
                System.out.println("Excep"+e);
            }

        }
        Collections.sort(allRentaldates, new Comparator<Rentals>() {
            @Override
            public int compare(Rentals u1, Rentals u2) {
                Date u1Date = new Date (u1.getDate());
                Date u2Date = new Date(u2.getDate());
                return u1Date.compareTo(u2Date);
            }
        });
    }

    public void buildRecyclerView() {
        if(allRentaldates.size() == 0){
            noRentals.setText("You have no upcoming rentals");
        }
        Log.d("Adapt", "in build");
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RentalsAdapter(allRentaldates);
        Log.d("Adapt", allRentaldates.toString());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, userProfileActivity.class);
        startActivity(intent);
        finish();
    }

}
