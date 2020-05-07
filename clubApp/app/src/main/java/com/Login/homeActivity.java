package com.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.Admin.adminActivity;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class homeActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextView userTextView;
    private ImageView userPic;
    private ImageView adminSection,messaging,support,rental;
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.goToRental).setOnClickListener(this);
        findViewById(R.id.goToSupport).setOnClickListener(this);
        findViewById(R.id.userProfile).setOnClickListener(this);
        findViewById(R.id.goToMessage).setOnClickListener(this);
        findViewById(R.id.goToAdmin).setOnClickListener(this);

        adminSection = findViewById(R.id.goToAdmin);
        messaging = findViewById(R.id.goToMessage);
        support = findViewById(R.id.goToSupport);
        rental = findViewById(R.id.goToRental);
        userTextView = findViewById(R.id.currentUser);
        userPic = findViewById(R.id.userProfile);
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/clubapp-surf.appspot.com/o/stockPhotos%2Frental_icon.png?alt=media&token=4b3770cc-b22b-4193-82aa-a0c0b79c53e7").fit().centerCrop().transform(new RoundedCornersTransformation(10,0)).into(rental);
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/clubapp-surf.appspot.com/o/stockPhotos%2Fmessaging_icon.png?alt=media&token=9b143190-a997-40fe-a686-e24230ec19dc").fit().centerCrop().transform(new RoundedCornersTransformation(10,0)).into(messaging);
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/clubapp-surf.appspot.com/o/stockPhotos%2FIinfo_icon.png?alt=media&token=93c05c0d-3b89-4b3b-810d-296ef0daf27e").fit().centerCrop().transform(new RoundedCornersTransformation(10,0)).into(support);
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/clubapp-surf.appspot.com/o/stockPhotos%2Fadmin_icon.png?alt=media&token=a1792b39-317d-4b25-abe6-fbe427a9bfb9").fit().centerCrop().transform(new RoundedCornersTransformation(10,0)).into(adminSection);

        adminSection.setVisibility(View.GONE);
        
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        setCurrentUserText(currentUser);
        setProfilePic();
        isAdmin();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        setCurrentUserText(currentUser);
        setProfilePic();
        isAdmin();
    }

    private void isAdmin(){
        DocumentReference userRef = db.collection("users").document(currentUser.getUid());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("ADMIN", "DocumentSnapshot data: " + document.getData());
                        Boolean isAdmin = document.getBoolean("admin");
                        Log.d("ADMIN", "onComplete: is user an admin:"+ isAdmin);

                        if(isAdmin){
                            adminSection.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Log.d("ADMIN", "No such document");
                    }
                } else {
                    Log.d("ADMIN", "get failed with ", task.getException());
                }
            }
        });

    }

    private void setCurrentUserText(FirebaseUser user){
        if(user.getDisplayName() == null) {
            userTextView.setText(user.getEmail());
        }else{
            userTextView.setText(user.getDisplayName());
        }
    }

    private void setProfilePic(){
        String path ="images/" + currentUser.getUid();
        storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().transform(new RoundedCornersTransformation(200,0)).into(userPic);


                if( currentUser.getPhotoUrl() != uri) {
                    updatePhotoUrl(uri,currentUser);
                    setProfileDetails(uri, currentUser);
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

        if(i==R.id.goToAdmin){
            Intent intent = new Intent(this, adminActivity.class);
            startActivity(intent);
        }


    }


    @Override
    public void onBackPressed() {
    }
}
