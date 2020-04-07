package com.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.Login.homeActivity;
import com.example.clubapp.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class adminActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        findViewById(R.id.addBoards).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.addBoards){
            Intent intent = new Intent(this,addBoard.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, homeActivity.class);
        startActivity(intent);
        finish();
    }
}
