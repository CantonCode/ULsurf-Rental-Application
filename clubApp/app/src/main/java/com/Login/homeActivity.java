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

import com.Chats.messageActivity;
import com.Rental.RentalMainActivity;
import com.Support.SupportActivity;
import com.example.clubapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class homeActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextView userTextView;
    private ImageView userPic;
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        findViewById(R.id.signOutButton).setOnClickListener(this);
        userTextView = findViewById(R.id.currentUser);
        userPic = findViewById(R.id.userProfile);
        findViewById(R.id.goToRental).setOnClickListener(this);
        findViewById(R.id.goToSupport).setOnClickListener(this);
        findViewById(R.id.userProfile).setOnClickListener(this);
        findViewById(R.id.goToMessage).setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        setCurrentUserText(currentUser);
        setProfilePic(currentUser);
    }

    private void setCurrentUserText(FirebaseUser user){
        if(user.getDisplayName() == null) {
            userTextView.setText(user.getEmail());
        }else{
            userTextView.setText(user.getDisplayName());
        }
    }

    private void setProfilePic(final FirebaseUser user){
        String path ="images/" + user.getUid();
        storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(userPic);


                if(!user.getPhotoUrl().equals(uri.toString())) {
                    updatePhotoUrl(uri,user);
                    setProfileDetails(uri, user);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void updatePhotoUrl(Uri uri,FirebaseUser user){
        DocumentReference userRef = db.collection("users").document(user.getUid());
        userRef.update("photoUrl", uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("PHOTO", "DocumentSnapshot successfully updated!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("PHOTO", "Error updating document", e);
                    }
                });
    }

    private void setProfileDetails(Uri uri, FirebaseUser user){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("USER", "User profile updated.");
                        }
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.signOutButton){
            signOut();
        }

        if(i == R.id.goToRental){
            Intent intent = new Intent(this, RentalMainActivity.class);
            startActivity(intent);
        }

        if(i == R.id.goToSupport){
            Intent intent = new Intent(this, SupportActivity.class);
            startActivity(intent);
        }
        if(i==R.id.goToMessage){
            Intent intent = new Intent(this, messageActivity.class);
            startActivity(intent);
        }

        if(i==R.id.userProfile){
            Intent intent = new Intent(this, userProfileActivity.class);
            startActivity(intent);
        }
    }
}
