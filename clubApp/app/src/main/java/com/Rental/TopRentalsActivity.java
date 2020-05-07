package com.Rental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnticipateInterpolator;
import android.widget.TextView;

import com.Login.userProfileActivity;
import com.example.clubapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

public class TopRentalsActivity extends AppCompatActivity {

    HashMap<String, String> equipment=new HashMap<String, String>();
    HashMap<String, Integer> numOfRentals=new HashMap<>();
    Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
    List<String> rentals = new ArrayList<>();
    ArrayList<String> keys = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    String currentUserId;

    private DecoView mDecoView;

    private int mBackIndex;
    private int mSeries1Index;
    private int mSeries2Index;
    private int mSeries3Index;

    private float mSeriesMax = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_rentals);
        mDecoView = (DecoView) findViewById(R.id.dynamicArcView);
        getEquipment();
    }

    private void getEquipment(){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
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
                } else {
                    Log.d("Check", "Error getting documents: ", task.getException());
                }
                sortRentals();
            }
        });
    }

    private void setRental(String id, List<String> dates) {
        for (Map.Entry<String, String> entry : equipment.entrySet()) {
            String key = entry.getKey();
            String name = entry.getValue();
            if (key.equals(id)) {
                Log.d("Chart", key + " => " + name);
                numOfRentals.put(name, dates.size());
            }
        }
    }

    private void sortRentals(){
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(numOfRentals.entrySet());

        //sorting the list with a comparator
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        //convert sortedMap back to Map
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            String key = entry.getKey();
            int num = entry.getValue();
            keys.add(key);
            mSeriesMax += num;
            Log.d("Check R", key + " " + num);
        }
        Log.d("Check R", ""+ sortedMap.size());
        if(sortedMap.size() >= 3){
            createBackSeries();
            createDataSeries1();
            createDataSeries2();
            createDataSeries3();
            createEvents();
        }
        else if(sortedMap.size() == 2){
            createBackSeries();
            createDataSeries1();
            createDataSeries2();
            createEvents();
        }
        else if(sortedMap.size() == 1){
            createBackSeries();
            createDataSeries1();
            createEvents();
        }
        else{
            TextView error = findViewById(R.id.error);
            Log.d("Check R", "No rentals");
            error.setText("You have no rentals to display");
        }
    }

    private void createBackSeries() {
        SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(true)
                .build();

        mBackIndex = mDecoView.addSeries(seriesItem);
    }

    private void createDataSeries1() {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF8800"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

        final TextView textPercentage = (TextView) findViewById(R.id.textPercentage);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                textPercentage.setText(" " + currentPosition);
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        final TextView textActivity1 = (TextView) findViewById(R.id.textActivity1);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                textActivity1.setText("You rented " + (keys.get(keys.size() -1)) + " " + sortedMap.get(keys.get(keys.size() -1)) + " times");
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mSeries1Index = mDecoView.addSeries(seriesItem);
    }

    private void createDataSeries2() {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF4444"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

        final TextView textPercentage = (TextView) findViewById(R.id.textPercentage);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                textPercentage.setText(" " + currentPosition);
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        final TextView textActivity2 = (TextView) findViewById(R.id.textActivity2);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                textActivity2.setText("You rented " + (keys.get(keys.size() -2)) + " " + sortedMap.get(keys.get(keys.size() -2)) + " times");
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mSeries2Index = mDecoView.addSeries(seriesItem);
    }

    private void createDataSeries3() {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FF6699FF"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

        final TextView textPercentage = (TextView) findViewById(R.id.textPercentage);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                textPercentage.setText(" " + currentPosition);
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        final TextView textActivity3 = (TextView) findViewById(R.id.textActivity3);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                textActivity3.setText("You rented " + (keys.get(keys.size() -3)) + " " + sortedMap.get(keys.get(keys.size() -3)) + " times");
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mSeries3Index = mDecoView.addSeries(seriesItem);
    }

    private void createEvents() {
        mDecoView.executeReset();
        float first;
        float second = 0.0f;
        float third = 0.0f;

        if (sortedMap.size() >= 3){
            int i = keys.size() -1;
            first = (float) sortedMap.get(keys.get(i));
            second = (float) sortedMap.get(keys.get(i -1));
            third = (float) sortedMap.get(keys.get(i - 2));
            Log.d("Check R", first  + ", " + second + ", " +  third);

            mDecoView.addEvent(new DecoEvent.Builder(mSeriesMax)
                    .setIndex(mBackIndex)
                    .setDuration(2000)
                    .setDelay(500)
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                    .setIndex(mSeries1Index)
                    .setDuration(1000)
                    .setDelay(600)
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(first)
                    .setIndex(mSeries1Index)
                    .setDelay(1125)
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                    .setIndex(mSeries2Index)
                    .setDuration(1000)
                    .setEffectRotations(1)
                    .setDelay(7000)
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(second)
                    .setIndex(mSeries2Index)
                    .setDelay(8500)
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                    .setIndex(mSeries3Index)
                    .setDuration(1000)
                    .setEffectRotations(1)
                    .setDelay(12500)
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(third).setIndex(mSeries3Index).setDelay(14000).build());

            mDecoView.addEvent(new DecoEvent.Builder(0).setIndex(mSeries3Index).setDelay(18000).build());

            mDecoView.addEvent(new DecoEvent.Builder(0).setIndex(mSeries2Index).setDelay(18000).build());

            mDecoView.addEvent(new DecoEvent.Builder(0)
                    .setIndex(mSeries1Index)
                    .setDelay(20000)
                    .setDuration(1000)
                    .setInterpolator(new AnticipateInterpolator())
                    .setListener(new DecoEvent.ExecuteEventListener() {
                        @Override
                        public void onEventStart(DecoEvent decoEvent) {

                        }

                        @Override
                        public void onEventEnd(DecoEvent decoEvent) {
                            resetText();
                        }
                    })
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_EXPLODE)
                    .setIndex(mSeries1Index)
                    .setDelay(21000)
                    .setDuration(3000)
                    .setDisplayText("Woo!")
                    .setListener(new DecoEvent.ExecuteEventListener() {
                        @Override
                        public void onEventStart(DecoEvent decoEvent) {

                        }

                        @Override
                        public void onEventEnd(DecoEvent decoEvent) {
                            createEvents();
                        }
                    })
                    .build());

            resetText();
        }
        else if (sortedMap.size() == 2){
            int i = keys.size() -1;
            first = (float) sortedMap.get(keys.get(i));
            second = (float) sortedMap.get(keys.get(i -1));
            mDecoView.addEvent(new DecoEvent.Builder(mSeriesMax)
                    .setIndex(mBackIndex)
                    .setDuration(3000)
                    .setDelay(100)
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                    .setIndex(mSeries1Index)
                    .setDuration(2000)
                    .setDelay(1250)
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(first)
                    .setIndex(mSeries1Index)
                    .setDelay(3250)
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                    .setIndex(mSeries2Index)
                    .setDuration(1000)
                    .setEffectRotations(1)
                    .setDelay(7000)
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(second)
                    .setIndex(mSeries2Index)
                    .setDelay(8500)
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                    .setIndex(mSeries3Index)
                    .setDuration(1000)
                    .setEffectRotations(1)
                    .setDelay(12500)
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(0).setIndex(mSeries3Index).setDelay(18000).build());

            mDecoView.addEvent(new DecoEvent.Builder(0).setIndex(mSeries2Index).setDelay(18000).build());

            mDecoView.addEvent(new DecoEvent.Builder(0)
                    .setIndex(mSeries1Index)
                    .setDelay(20000)
                    .setDuration(1000)
                    .setInterpolator(new AnticipateInterpolator())
                    .setListener(new DecoEvent.ExecuteEventListener() {
                        @Override
                        public void onEventStart(DecoEvent decoEvent) {

                        }

                        @Override
                        public void onEventEnd(DecoEvent decoEvent) {
                            resetText();
                        }
                    })
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_EXPLODE)
                    .setIndex(mSeries1Index)
                    .setDelay(21000)
                    .setDuration(3000)
                    .setDisplayText("Woo!")
                    .setListener(new DecoEvent.ExecuteEventListener() {
                        @Override
                        public void onEventStart(DecoEvent decoEvent) {

                        }

                        @Override
                        public void onEventEnd(DecoEvent decoEvent) {
                            createEvents();
                        }
                    })
                    .build());

            resetText();
        }
        else if (sortedMap.size() == 1){
            int i = keys.size() -1;
            first = (float) sortedMap.get(keys.get(i));
            mDecoView.addEvent(new DecoEvent.Builder(mSeriesMax)
                    .setIndex(mBackIndex)
                    .setDuration(3000)
                    .setDelay(100)
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                    .setIndex(mSeries1Index)
                    .setDuration(2000)
                    .setDelay(1250)
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(first)
                    .setIndex(mSeries1Index)
                    .setDelay(3250)
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                    .setIndex(mSeries2Index)
                    .setDuration(1000)
                    .setEffectRotations(1)
                    .setDelay(7000)
                    .build());


            mDecoView.addEvent(new DecoEvent.Builder(0).setIndex(mSeries3Index).setDelay(18000).build());

            mDecoView.addEvent(new DecoEvent.Builder(0).setIndex(mSeries2Index).setDelay(18000).build());

            mDecoView.addEvent(new DecoEvent.Builder(0)
                    .setIndex(mSeries1Index)
                    .setDelay(20000)
                    .setDuration(1000)
                    .setInterpolator(new AnticipateInterpolator())
                    .setListener(new DecoEvent.ExecuteEventListener() {
                        @Override
                        public void onEventStart(DecoEvent decoEvent) {

                        }

                        @Override
                        public void onEventEnd(DecoEvent decoEvent) {
                            resetText();
                        }
                    })
                    .build());

            mDecoView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_EXPLODE)
                    .setIndex(mSeries1Index)
                    .setDelay(6000)
                    .setDuration(2000)
                    .setDisplayText("Woo!")
                    .setListener(new DecoEvent.ExecuteEventListener() {
                        @Override
                        public void onEventStart(DecoEvent decoEvent) {

                        }

                        @Override
                        public void onEventEnd(DecoEvent decoEvent) {
                            createEvents();
                        }
                    })
                    .build());

            resetText();
        }
    }

    private void resetText() {
        ((TextView) findViewById(R.id.textActivity1)).setText("");
        ((TextView) findViewById(R.id.textActivity2)).setText("");
        ((TextView) findViewById(R.id.textActivity3)).setText("");
        ((TextView) findViewById(R.id.textPercentage)).setText("");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, userProfileActivity.class);
        startActivity(intent);
        finish();
    }
}
