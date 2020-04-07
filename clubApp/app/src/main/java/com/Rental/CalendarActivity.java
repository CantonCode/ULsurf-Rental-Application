package com.Rental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import com.example.clubapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class CalendarActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private TextView confirm;
    private TextView result;
    private Button btn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser;
    private CollectionReference notebookRef;
    private CollectionReference newRentalRef;
    private DocumentReference equipRef;
    private DocumentReference rentRef;
    private DocumentReference equipRentRef;
    private FirebaseAuth mAuth;

    private String getEquip;
    private String getDate;
    String currentUserId;
    private String equipmentId;
    boolean found;
    boolean equipmentFound;
    boolean notAvail = false;
    CollectionReference rented;
    ArrayList<String> dateRentals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        showDatePicker();
        this.found = false;

        equipmentId = getIntent().getStringExtra("selected_equipment");
        getDates();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();

        confirm= findViewById(R.id.date_confirmed);
        result = findViewById(R.id.confirmation);

    }

    private void showDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePicker.invalidateOptionsMenu();
        datePicker.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){

        getDate= dayOfMonth + "/" + (month+1) +"/" + year;

        String message = "equipment id = " + equipmentId;
        Log.d("EQUIPID", message);

        findUser();

        btn= findViewById(R.id.back);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(CalendarActivity.this, RentalMainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void findUser() {
        checkEquipment();
        rented = db.collection("rented");
        DocumentReference user = rented.document(currentUserId);
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String message = "";
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        message = "DocumentSnapshot data: " + document.getData();
                        setValue(true);
                        checkEquipment();
                    }
                    else {
                        message = "No such document";
                        createUser();
                    }
                }
                else{
                    message = "get failed with " + task.getException();
                }
               //Toast.makeText(CalendarActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkEquipment() {
        for(String date : dateRentals) {
            if(getDate.equals(date)) {
                Log.d("Check", "Date wanted = " + getDate + " Date check = " + date);
                equipmentFound = true;
                notAvail = true;
            }
            else {
                equipmentFound = false;
                notAvail=false;
            }
        }
        dateUnavailable();
    }

    public void createUser() {
        HashMap<String, Object> rent = new HashMap<>();
        rent.put("userId", currentUserId);

        rentRef = db.collection("rented").document(currentUserId);
        rentRef.set(rent);

        if(!equipmentFound) {
            notAvail = false;
            Log.d("Check", "Equipment Not found create equipment");
        }
        else {
            Log.d("Check", "Equipment unavailable");
            notAvail = true;
        }

        checkEquipment();
    }

    public void dateUnavailable() {
        if(notAvail) {
            String confirmDate= "ERROR: Equipment you selected is not available on " + getDate;
            Log.d("Check", confirmDate);
            Toast.makeText(CalendarActivity.this, confirmDate, Toast.LENGTH_SHORT).show();
            confirm.setText(confirmDate);
            result.setText("ERROR");
        }
        else {
            String confirmDate= "You have booked this piece of equipment for: " + getDate;
            confirm.setText(confirmDate);
            result.setText("Confirmation");
            createEquipment();
        }
    }

    public void createEquipment() {
        final HashMap<String, Object> equipment = new HashMap<>();
        equipment.put("dateOfRental", Arrays.asList(getDate));

        equipRentRef = db.collection("equipment").document(equipmentId).collection("rentalDates").document(equipmentId);
        Log.d("Check", equipmentId);

        equipRef = db.collection("rented").document(currentUserId).
                collection("equipment").document(equipmentId);

        equipRentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot != null && documentSnapshot.exists()) {
                        equipRentRef.update("dateOfRental", FieldValue.arrayUnion(getDate));
                    }else{
                        equipRentRef.set(equipment);
                    }

                }
            }
        });

        equipRentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot != null && documentSnapshot.exists()) {
                        equipRef.update("dateOfRental", FieldValue.arrayUnion(getDate));
                    }else{
                        equipRef.set(equipment);
                    }

                }
            }
        });

    }

    public void getDates() {
        DocumentReference docRef = db.collection("equipment").document(equipmentId).collection("rentalDates").document(equipmentId);
        Log.d("Check", equipmentId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(task.isSuccessful()) {
                    Log.d("Check", "made it to here");
                    Log.d("Check", document.get("dateOfRental").toString());
                    dateRentals = (ArrayList<String>) document.get("dateOfRental");
                }
                else {
                    dateRentals.add("no Values");
                    Log.d("ERROR", "No Equipment");
                }
            }
        });
        Log.d("Check", dateRentals.toString());
    }

    public void setValue(boolean value) {
        this.found = value;
    }

}
