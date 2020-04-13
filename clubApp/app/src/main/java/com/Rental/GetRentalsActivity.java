package com.Rental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.clubapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GetRentalsActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    String currentUserId;
    List<List<String>> allRentals = new ArrayList<>();
    List<List<String>> RentalsWithName = new ArrayList<>();
    ArrayList<String> rentals = new ArrayList<>();
    List<String> ids = new ArrayList<>();
    List<String> names = new ArrayList<>();
    TextView display;
    String equipmentName = "";
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_rentals);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();

        getUserRentals();

        display= findViewById(R.id.textView4);
    }

    private void getUserRentals(){
        CollectionReference colRef = db.collection("rented").document(currentUserId).collection("equipment");

        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        rentals = (ArrayList<String>) document.get("dateOfRental");
                        ids.add(document.getId());
                        String name = getEquipmentName(document.getId(), rentals);
                        Log.d("Check", name);
                        allRentals.add(rentals);
                    }
                    Log.d("Check", "" + allRentals);
                    Log.d("Check", "" + ids);

                    for(int i =0; i < allRentals.size(); i++){
                        Log.d("Check", "" + ids.get(i) +" "+ allRentals.get(i));
                    }

                } else {
                    Log.d("Check", "Error getting documents: ", task.getException());
                }
            }
        });
        barChart();
    }

    private String getEquipmentName(String id, final ArrayList<String> dates){
        equipmentName= "";
        DocumentReference docRef = db.collection("equipment").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                equipmentName = document.getString("equipmentName");
                display(equipmentName, dates);
            }
        });
        return equipmentName;
    }

    private void display(String name, ArrayList<String> dates){
        boolean upcomingDate = false;
        boolean nameDisplayed = false;
        names.add(name);
        SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");
        for(String date : dates) {
            try {
                Date now = new Date();
                Date d=dateFormat.parse(date);
                System.out.println("DATE"+d);
                System.out.println("Formated"+dateFormat.format(d));
                if(now.before(d)) {
                    upcomingDate=true;
                    if(upcomingDate && !nameDisplayed){
                        //display.setTextColor(Color.parseColor("#2144F3"));
                        display.append("\n" + name + ": ");
                        //display.setTextColor(Color.parseColor("F6050000"));
                        display.append(date + "\n   ");
                        nameDisplayed=true;
                    }
                    else {
                        display.append(date + "\n   " + "   ");
                    }
                }
            }
            catch(Exception e) {
                //java.text.ParseException: Unparseable date: Geting error
                System.out.println("Excep"+e);
            }
        }
    }

    private void barChart() {
        ArrayList labels= new ArrayList<>();
        ArrayList entries = new ArrayList<>();
        BarChart chart = findViewById(R.id.barchart);

        for(int i =0; i < allRentals.size(); i++){
            int num =0;
            for(int j=0; j < allRentals.get(i).size(); j++) {
                Log.d("Check", allRentals.get(i).get(j));
                num += 1;
            }
            Log.d("Check", Integer.toString(num));
            entries.add(new BarEntry(Float.valueOf(num), i));
        }

        BarDataSet bardataset = new BarDataSet(entries, "Cells");

        for(String na: names){
            labels.add(na);
        }

        BarData data = new BarData(labels, bardataset);
        chart.setData(data);

        chart.setDescription("Board Rental Summary");  // set the description
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.animateY(5000);
    }
}
