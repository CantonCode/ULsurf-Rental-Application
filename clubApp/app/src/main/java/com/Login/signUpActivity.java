package com.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clubapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class signUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailField;
    private EditText passwordField;
    private EditText studentNumField;
    private EditText userNameField;
    private TextView uploadText;
    private ImageView userPicture;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final int PICK_IMAGE_REQUEST = 22;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViewById(R.id.signUpButton).setOnClickListener(this);
        findViewById(R.id.uploadPicture).setOnClickListener(this);

        emailField = findViewById(R.id.signUpEmailField);
        passwordField = findViewById(R.id.signUpPasswordField);
        studentNumField = findViewById(R.id.signUpStudentNumber);
        userNameField = findViewById(R.id.signUpUserName);
        userPicture = findViewById(R.id.signUpPicture);
        uploadText = findViewById(R.id.uploadText);
        Picasso.get().load("http://s3.amazonaws.com/37assets/svn/765-default-avatar.png").fit().centerCrop().into(userPicture);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


    }

    private void createAccount(final String email, final String password,final String number, final String username, Uri filePath) {
        if (!validateForm(email, password, number, username,filePath)) {
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
                            createUserInDataBase(number,username,user);
                            uploadPic(user);
                            setProfileUserName(user,username);
                            Toast.makeText(signUpActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
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

    private void setProfileUserName(final FirebaseUser user, final String username){


        Log.d("USER", "User profile updated." + user );
        if( user != null) {
            final String email = user.getEmail();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("USER", "User profile updated." + email);
                                    }
                                }
                            });
                }



        }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadPic(FirebaseUser user){
        if(filePath != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + user.getUid());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(signUpActivity.this, "Uploaded", Toast.LENGTH_LONG).show();
                    success();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(signUpActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            Picasso.get().load(filePath).centerCrop().fit().into(userPicture);
        }
    }

    private boolean validateForm(String email,String password, String number, String username,Uri filePath){
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
        }else if(password.length()< 4){
            passwordField.setError("Must be greater than 4 characters");
            valid = false;
        }

        else {
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

        if(filePath == null){
            valid = false;
            uploadText.setError("required");
        }

        return valid;
    }

    private void createUserInDataBase(String number,String username,FirebaseUser user){
        Log.d("USER", "enterdatabase");
        String id = user.getUid();
        User newUser = new User(username,id,number,"",false);

        Log.d("USER","this is a new user" + newUser.toString());

        db.collection("users").document(id).set(newUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Log.d("USER", "User added to data base");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("USER", "User failed to add to data base");
            }
        });

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if(i==R.id.signUpButton){
            createAccount(emailField.getText().toString(),passwordField.getText().toString(),
                    studentNumField.getText().toString(),userNameField.getText().toString(),filePath);
        }

        if(i == R.id.uploadPicture){
            selectImage();
        }
    }
}
