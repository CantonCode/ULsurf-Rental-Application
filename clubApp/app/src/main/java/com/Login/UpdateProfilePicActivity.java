package com.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.clubapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class UpdateProfilePicActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView profilePic;
    private Button changePic;
    private final int PICK_IMAGE_REQUEST = 22;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int PERMISSION_CODE = 1000;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_pic);


        findViewById(R.id.uploadPic).setOnClickListener(UpdateProfilePicActivity.this);
        findViewById(R.id.takePic).setOnClickListener(UpdateProfilePicActivity.this);
        findViewById(R.id.changePic).setOnClickListener(UpdateProfilePicActivity.this);

        changePic= findViewById(R.id.changePic);
        profilePic = findViewById(R.id.profilePicture);

        if(!validate())
            changePic.setEnabled(false);
    }

    /*private void takePicture(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Log.d("Check","get bitmap");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }*/

    private void selectPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void success(){
        Intent intent = new Intent(this, userProfileActivity.class);
        startActivity(intent);
    }

    private void uploadPic(FirebaseUser user){
        if(filePath != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + user.getUid());
            Log.d("Check", ""+ ref);
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(UpdateProfilePicActivity.this, "Uploaded", Toast.LENGTH_LONG).show();
                    success();
                }
            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) { progressDialog.dismiss();
                        Toast.makeText(UpdateProfilePicActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
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
            Picasso.get().load(filePath).centerCrop().fit().into(profilePic);
        }
        /*if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Log.d("Check"," checking activity");
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            Log.d("Check"," " + photo);
            profilePic.setImageBitmap(photo);
            Uri tempUri = getImageUri(getApplicationContext(), photo);
            filePath = tempUri;
            Log.d("Check"," " + tempUri);
        }*/

        if(validate())
            changePic.setEnabled(true);
    }

    private boolean validate(){
        boolean valid = true;

        if(filePath == null){
            valid = false;
        }

        return valid;
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.uploadPic) {
            selectPicture();
        }
        if (i == R.id.takePic){
           // takePicture(v);
        }
        if(i == R.id.changePic){
            FirebaseUser user = mAuth.getCurrentUser();
            uploadPic(user);
        }
    }

}
