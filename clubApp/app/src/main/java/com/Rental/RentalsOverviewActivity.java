package com.Rental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.Login.userProfileActivity;
import com.example.clubapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentalsOverviewActivity extends AppCompatActivity {

    ArrayList lab= new ArrayList<>();
    List<Integer> nums = new ArrayList<>();
    List<String> names = new ArrayList<>();
    HashMap<String, String> equipment=new HashMap<String, String>();
    List<String> rentals = new ArrayList<>();
    ArrayList number = new ArrayList<>();
    TextView noRentals;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rentals_overview);
        noRentals = findViewById(R.id.noData);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        getEquipment();
    }

    private void getEquipment(){
        CollectionReference docRef = db.collection("equipment");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        equipment.put(document.getId(), document.getString("equipmentName"));
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
                        setRental(document.getId(), rentals);
                    }
                    if(rentals.size() > 0)
                        barChart();
                    else{
                        noRentals.setText("You have no rentals to display");
                    }
                } else {
                    Log.d("Check", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void setRental(String id, List<String> dates) {
        nums.add(dates.size());
        for (Map.Entry<String, String> entry : equipment.entrySet()) {
            String key = entry.getKey();
            String name = entry.getValue();
            if (key.equals(id)) {
                Log.d("Chart", key + " => " + name);
                names.add(name);
                lab.add(name);
            }
        }
    }

    private void barChart() {
        BarChart chart = findViewById(R.id.barchart);

        Log.d("Chart", nums.toString());
        Log.d("Chart", lab.toString());

        for(int i =0; i < nums.size(); i++){
            int numberOfDates = nums.get(i);
            float num = (float) numberOfDates;
            number.add((new BarEntry(num, i)));
        }

        Log.d("Chart", number.toString());
        BarDataSet bardataset = new BarDataSet(number ,"Cells");

        BarData data = new BarData(names, bardataset);
        chart.setData(data);
        chart.setDescription("Board Rental Summary");  // set the description
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.animateY(3000);


        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                int index = e.getXIndex();
                String name = names.get(index);
                Toast.makeText(RentalsOverviewActivity.this, "Board = " + name + " \nNumber of times rented = " + e.getVal(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, userProfileActivity.class);
        startActivity(intent);
        finish();
    }
}
