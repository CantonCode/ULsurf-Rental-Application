package com.Support;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.clubapp.R;
import com.Login.homeActivity;

public class SupportActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        findViewById(R.id.goBack).setOnClickListener(this);
    }

    private void goBack(){
        Intent intent = new Intent(this, homeActivity.class);
        startActivity(intent);
    }

    public void onClick(View v){
        int i = v.getId();
        if (i == R.id.goBack) {
            goBack();
        }
    }
}
