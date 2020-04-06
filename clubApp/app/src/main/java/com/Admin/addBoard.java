package com.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.Login.signUpActivity;
import com.Rental.Equipment;
import com.example.clubapp.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class addBoard extends AppCompatActivity implements View.OnClickListener{

    EditText descriptionText;
    ChipGroup chipGroup;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final int PICK_IMAGE_REQUEST = 22;
    ImageView boardPic;
    String uniqueID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_board);

        chipGroup = findViewById(R.id.chipView);
        descriptionText = findViewById(R.id.newBoardDescription);
        boardPic = findViewById(R.id.boardPicture);

        findViewById(R.id.addDescription).setOnClickListener(this);
        findViewById(R.id.uploadPicture).setOnClickListener(this);
        findViewById(R.id.addBoardButton).setOnClickListener(this);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        uniqueID = UUID.randomUUID().toString();


    }

    private void addEquip(String url){
        Equipment newEquipment = new Equipment(uniqueID,);
    }

    private void addChip(String desc){
        final Chip chip = new Chip(this);
        chip.setText(desc);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip);
            }
        });
        chipGroup.addView(chip);
    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadPic(){
        if(filePath != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/equipment/boards/" + uniqueID);
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    String url = ref.getDownloadUrl().toString();
                    Log.d("ADD", "onSuccess: url: " + url);
                    Toast.makeText(addBoard.this, "Uploaded", Toast.LENGTH_LONG).show();
                    addEquip(url);

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(addBoard.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            Picasso.get().load(filePath).centerCrop().fit().into(boardPic);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.addDescription){
            addChip(descriptionText.getText().toString());
            descriptionText.setText("");
        }
        if(i == R.id.uploadPicture){
            selectImage();
        }

        if(i == R.id.addBoardButton){
            uploadPic();
        }
    }
}
