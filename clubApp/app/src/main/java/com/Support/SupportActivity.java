package com.Support;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.clubapp.R;
import com.Login.homeActivity;

public class SupportActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        findViewById(R.id.goBack).setOnClickListener(this);
        findViewById(R.id.goMaps).setOnClickListener(this);
        findViewById(R.id.goMaps2).setOnClickListener(this);
    }

    private void goBack(){
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

    public void onClick(View v){
        int i = v.getId();
        if (i == R.id.goBack) {
            goBack();
        }

        if (i == R.id.goMaps) {
            goToMaps();
        }

        if(i == R.id.goMaps2) {
            goToMaps2();
        }
    }
}
