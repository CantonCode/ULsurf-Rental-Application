package com.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clubapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class userProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private TextView userName;
    private TextView userEmail;
    private TextView userNumber;
    private ImageView userPicture;
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        findViewById(R.id.goBack).setOnClickListener(this);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        userName = findViewById(R.id.pUserName);
        userEmail = findViewById(R.id.pEmail);
        userPicture = findViewById(R.id.userProfilePic);
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        setCurrentUserText(currentUser);
        setUserEmail();
        setProfilePic();
    }

    private void setProfilePic(){
        String path ="images/" + currentUser.getUid();
        storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("PROFILE", "SUCESS" + uri);
                Picasso.get().load(uri).fit().centerCrop().transform(new RoundedCornersTransformation(200,0)).into(userPicture);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void setCurrentUserText(FirebaseUser user){
        if(user.getDisplayName() == null) {
            userName.setText(user.getEmail());
        }else{
            userName.setText(user.getDisplayName());
        }
    }

    private void setUserEmail(){
        if(currentUser.getEmail() != null){
            userEmail.setText(currentUser.getEmail());
        }
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
