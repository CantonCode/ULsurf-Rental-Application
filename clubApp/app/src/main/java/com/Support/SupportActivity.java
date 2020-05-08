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

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
            }
            else {
                Toast.makeText(SupportActivity.this,
                        "You must accept location permissions to access this feature.", Toast.LENGTH_LONG).show();
            }
        }
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
            goToMaps();
        }

        if (i == R.id.goMaps2) {
            goToMaps2();
        }
    }
}
