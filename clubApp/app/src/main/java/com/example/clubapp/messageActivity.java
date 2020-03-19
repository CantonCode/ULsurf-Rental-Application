package com.example.clubapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;



public class messageActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private pageSections mSectionsPagerAdapter;
    private TabLayout mTabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.main_appBar);
        getSupportActionBar().setTitle("Chat Room");

        findViewById(R.id.goBack).setOnClickListener(this);

        //Tabs
        mViewPager = (ViewPager) findViewById(R.id.mainView);
        mSectionsPagerAdapter = new pageSections(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.messageParts);
        mTabLayout.setupWithViewPager(mViewPager);


    }

    private void goBack() {
        Intent intent = new Intent(this, homeActivity.class);
        startActivity(intent);
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.goBack) {
            goBack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.signOutButton) {

            //mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

            mAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            //sendToStart();

        }

        if (item.getItemId() == R.id.goBack) {
            Intent intent = new Intent(this, homeActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.user_members) {

            Intent intent = new Intent(this, all_Members_Activity.class);
            startActivity(intent);

        }

        return true;
    }
}