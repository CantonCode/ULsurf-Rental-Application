package com.example.clubapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.media.audiofx.DynamicsProcessing;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

public class CalendarActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private TextView confirm;
    private Button btn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser;
    private CollectionReference notebookRef;
    private CollectionReference newRentalRef;
    private DocumentReference equipRef;
    private DocumentReference rentRef;
    private FirebaseAuth mAuth;

    private String getEquip;
    private String getDate;
    String currentUserId;
    private int equipmentId;
    boolean found;
    CollectionReference rented;
    List<String> dateRentals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        showDatePicker();
        this.found = false;

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();

        equipmentId = getIntent().getIntExtra("selected_equipment", 0);

        confirm= findViewById(R.id.date_confirmed);

    }

    private void showDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){

        getDate= dayOfMonth + "/" + month +"/" + year;
        String confirmDate= "You have booked this piece of equipment for: " + getDate;
        confirm.setText(confirmDate);

        String message = "equipment id = " + equipmentId;
        Log.d("EQUIPID", message);

        findUser();
        //findEquipment("1");

        Toast.makeText(CalendarActivity.this, getDate, Toast.LENGTH_SHORT).show();

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
                        createEquipment();
                    }
                    else {
                        message = "No such document";
                        createUser();
                    }
                }
                else{
                    message = "get failed with " + task.getException();
                }
               Toast.makeText(CalendarActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findEquipment(final String cEquipId) {
        rented = db.collection("rented");
        DocumentReference equip = rented.document();
        equip.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    String message = "";
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            message = "DocumentSnapshot data: " + document.getData();
                            setValue(true);
                        }
                        else {
                            message = "No such document";
                        }
                    }
                    else{
                        message = "get failed with " + task.getException();
                    }
                    Toast.makeText(CalendarActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void createUser() {
        HashMap<String, Object> rent = new HashMap<>();
        rent.put("userId", currentUserId);

        rentRef = db.collection("rented").document(currentUserId);
        rentRef.set(rent);

    }

    public void createEquipment() {
        HashMap<String, Object> equipment = new HashMap<>();
        equipment.put("equipmentId", equipmentId);
        equipment.put("dateOfRental", Arrays.asList(getDate));

        equipRef = db.collection("rented").document(currentUserId).
                collection("equipment").document(Integer.toString(equipmentId));

        equipRef.update("dateOfRental", FieldValue.arrayUnion(getDate));
    }

    public void setValue(boolean value) {
        this.found = value;
    }

}
