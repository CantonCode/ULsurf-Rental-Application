package com.Rental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.example.clubapp.NotifyService;
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
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class CalendarActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private int notificationId = 1;

    private TextView confirm;
    private ImageView result;
    private TextView display;
    private Button btn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference equipRef;
    private DocumentReference rentRef;
    private DocumentReference equipRentRef;
    private FirebaseAuth mAuth;

    private String getDate;
    String currentUserId;
    private String equipmentId;
    private String equipmentName;
    boolean found;
    CollectionReference rented;
    ArrayList<String> dateRentals = new ArrayList<>();
    Calendar[] disabledDays;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    DatePickerDialog datePickerDialog ;
    int Year, Month, Day, hour, min, sec;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        equipmentId = getIntent().getStringExtra("selected_equipment");
        equipmentName = getIntent().getStringExtra("selected_equipmentName");

        getDates();
        showDatePicker();
        this.found = false;

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();

        confirm = findViewById(R.id.date_confirmed);
        result = findViewById(R.id.image);
        display = findViewById(R.id.display);

    }

    private void showDatePicker(){
        datePickerDialog = DatePickerDialog.newInstance(CalendarActivity.this, Year, Month, Day);
        datePickerDialog.setThemeDark(true);
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

        Toast.makeText(CalendarActivity.this, getDate, Toast.LENGTH_SHORT).show();


        btn= findViewById(R.id.back);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(CalendarActivity.this, RentalMainActivity.class);
                startActivity(intent);
            }
        });

        findUser();
        String confirmDate= "Booking confirmed for " + equipmentName + " on the " + getDate;
        confirm.setText(confirmDate);
        result.setText("Confirmation");

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.HOUR, 10);
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.set(Calendar.MONTH, Month);
        cal.set(Calendar.DAY_OF_MONTH, Day);
        cal.set(Calendar.YEAR, Year);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        Log.d("Check", cal.getTimeInMillis() + " -> " + cal.getTime());

        long alarmTime = cal.getTimeInMillis();

        // Set notificationId & text.
        Intent intent = new Intent(CalendarActivity.this, NotifyService.class);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("todo", "Upcoming Rental - Tomorrow");

        // getBroadcast(context, requestCode, intent, flags)
        PendingIntent alarmIntent = PendingIntent.getBroadcast(CalendarActivity.this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        // Set alarm.
        // set(type, milliseconds, intent)
        alarm.set(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent);
        display.setText("Confirmation");
        Picasso.get().load(R.drawable.tick2).transform(new RoundedCornersTransformation(50,0)).into(result);
    }

    private void disableDates() {

        disabledDays = new Calendar[dateRentals.size()];

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
        }

        datePickerDialog.setDisabledDays(disabledDays);
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
                        setValue(true);
                        createEquipment();
                    }
                    else {
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

        equipRef = db.collection("rented").document(currentUserId).collection("equipment").document(equipmentId);

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

        equipRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
