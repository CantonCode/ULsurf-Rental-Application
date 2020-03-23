package com.example.clubapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailField;
    private EditText passwordField;
    private EditText studentNumField;
    private EditText userNameField;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViewById(R.id.signUpButton).setOnClickListener(this);

        emailField = findViewById(R.id.signUpEmailField);
        passwordField = findViewById(R.id.signUpPasswordField);
        studentNumField = findViewById(R.id.signUpStudentNumber);
        userNameField = findViewById(R.id.signUpUserName);

        mAuth = FirebaseAuth.getInstance();
    }

    private void createAccount(String email, String password, String number, String username) {
        if (!validateForm(email, password, number, username)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("USER", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(signUpActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                            success();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("USER", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(signUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            ;
                        }

                        // [START_EXCLUDE]
//                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void success(){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }


    private boolean validateForm(String email,String password, String number, String username){
        boolean valid = true;


        if(TextUtils.isEmpty(email)) {
            emailField.setError("Required");
            valid = false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailField.setError("Invalid Format");
        }
        else{
            emailField.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        if(TextUtils.isEmpty(number)){
            studentNumField.setError("required");
            valid = false;
        }else if(number.length()!=8){
            studentNumField.setError("Only 8 Digits Required");
            valid = false;
        }
        else{
            studentNumField.setError(null);
        }

        if(TextUtils.isEmpty(username)){
           userNameField.setError("required");
            valid = false;
        }else if(username.length()< 5 || username.length()>32 ){
            userNameField.setError("Must be between 5 and 32 characters");
            valid = false;
        }
        else{
            studentNumField.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if(i==R.id.signUpButton){
            createAccount(emailField.getText().toString(),passwordField.getText().toString(),
                    studentNumField.getText().toString(),userNameField.getText().toString());
        }
    }
}
