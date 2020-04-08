package com.Rental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

//import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

public class CalendarActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView confirm;
    private TextView result;
    private Button btn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference equipRef;
    private DocumentReference rentRef;
    private DocumentReference equipRentRef;
    private FirebaseAuth mAuth;

    private String getDate;
    String currentUserId;
    private String equipmentId;
    boolean found;
    CollectionReference rented;
    ArrayList<String> dateRentals = new ArrayList<>();
    Calendar[] disabledDays;

    DatePickerDialog datePickerDialog ;
    int Year, Month, Day;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        equipmentId = getIntent().getStringExtra("selected_equipment");
        getDates();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        showDatePicker();
        this.found = false;

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();

        confirm= findViewById(R.id.date_confirmed);
        result = findViewById(R.id.confirmation);

    }

    private void showDatePicker(){
        datePickerDialog = DatePickerDialog.newInstance(CalendarActivity.this, Year, Month, Day);
        datePickerDialog.setThemeDark(false);
        datePickerDialog.showYearPickerFirst(false);
        datePickerDialog.setTitle("Date Picker");
        datePickerDialog.setCancelColor(getResources().getColor(R.color.colorPrimaryDark));
        datePickerDialog.setOkColor(getResources().getColor(R.color.colorPrimary));
        datePickerDialog.setAccentColor(getResources().getColor(R.color.colorPrimary));

        Calendar c = Calendar.getInstance();
        datePickerDialog.setMinDate(c);
        c.add(Calendar.YEAR, 2);
        datePickerDialog.setMaxDate(c);
        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Intent intent = new Intent(CalendarActivity.this, RentalMainActivity.class);
                startActivity(intent);
                Toast.makeText(CalendarActivity.this, "Datepicker Canceled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int Year, int Month, int Day) {

        getDate = Day+"/"+(Month+1)+"/"+Year;

        Toast.makeText(CalendarActivity.this, getDate, Toast.LENGTH_LONG).show();


        btn= findViewById(R.id.back);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(CalendarActivity.this, RentalMainActivity.class);
                startActivity(intent);
            }
        });

        findUser();
        String confirmDate= "You have booked this piece of equipment for: " + getDate;
        confirm.setText(confirmDate);
        result.setText("Confirmation");

    }

    private void disableDates() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        disabledDays = new Calendar[dateRentals.size()];

        Log.d("Check", dateRentals.toString());
        for(int i = 0; i < dateRentals.size(); i++) {
            String date =dateRentals.get(i);
            try {
                Date newDate = format.parse(date);
                Log.d("datesDisabled", newDate.toString());
                Calendar cal = Calendar.getInstance();
                cal.setTime(newDate);
                disabledDays[i] = cal;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d("datesDisabled", disabledDays.toString());
        }
        datePickerDialog.setDisabledDays(disabledDays);
        for(Calendar date: disabledDays ){
            Log.d("datesDisabled", date.toString());
        }

        datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
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
               //Toast.makeText(CalendarActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void createUser() {
        HashMap<String, Object> rent = new HashMap<>();
        rent.put("userId", currentUserId);

        rentRef = db.collection("rented").document(currentUserId);
        rentRef.set(rent);
        createEquipment();
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
        Log.d("Check", equipmentId);
        DocumentReference docRef = db.collection("equipment").document(equipmentId).collection("rentalDates").document(equipmentId);
        Log.d("Check", equipmentId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                Log.d("Check", document.toString());
                if(task.isSuccessful()) {
                    if(task.getResult().getData() != null) {
                        Log.d("Check", "made it to here");
                        Log.d("Check", document.get("dateOfRental").toString());
                        dateRentals = (ArrayList<String>) document.get("dateOfRental");
                    }
                    disableDates();
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
