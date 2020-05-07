package com.Support;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.clubapp.R;
import com.Login.homeActivity;

public class SupportActivity extends AppCompatActivity implements View.OnClickListener {
    private final static int LOCATION_REQUEST_CODE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        findViewById(R.id.goBack).setOnClickListener(this);
        findViewById(R.id.goMaps).setOnClickListener(this);
        findViewById(R.id.goMaps2).setOnClickListener(this);
    }

    private void goBack() {
        Intent intent = new Intent(this, homeActivity.class);
        startActivity(intent);
    }

    private void goToMaps() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("Map code", "boathouse");
        startActivity(intent);
    }

    private void goToMaps2() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("Map code", "pool");
        startActivity(intent);
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.goBack) {
            goBack();
        }

        if (i == R.id.goMaps) {
            if(locationEnabled()) {
                if(ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                    LOCATION_REQUEST_CODE);
                            if(ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_COARSE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {
                                goToMaps();
                            }
                            else {
                                Toast.makeText(SupportActivity.this,
                                        "You must accept location permissions to access this feature.", Toast.LENGTH_LONG).show();
                            }
                }
                else {
                    goToMaps();
                }
            }
            else {
                Toast.makeText(SupportActivity.this,
                        "Your location services are turned off.", Toast.LENGTH_LONG).show();
            }

        }

        if (i == R.id.goMaps2) {
            if(locationEnabled()) {
                if(ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_REQUEST_CODE);
                    if(ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        goToMaps2();
                    }
                    else {
                        Toast.makeText(SupportActivity.this,
                                "You must accept location permissions to access this feature.", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    goToMaps2();
                }
            }
            else {
                Toast.makeText(SupportActivity.this,
                        "Your location services are turned off.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean locationEnabled(){
        LocationManager lm = (LocationManager)getSystemService(Context. LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        Log.d("gps: ", "status" + gps_enabled);

        if(gps_enabled) {
            return true;
        }
        else {
            return false;
        }
    }
}
