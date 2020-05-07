package com.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Login.signUpActivity;
import com.Rental.Equipment;
import com.example.clubapp.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class addBoard extends AppCompatActivity implements View.OnClickListener {

    EditText descriptionText;
    ChipGroup chipGroup;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final int PICK_IMAGE_REQUEST = 22;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int PERMISSION_CODE = 1000;

    ImageView boardPic;
    EditText boardName;
    EditText boardSize;
    TextView uploadText;
    String uniqueID;
    ArrayList<String> boardDesc;
    String name;
    String size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_board);

        chipGroup = findViewById(R.id.chipView);
        descriptionText = findViewById(R.id.newBoardDescription);
        boardPic = findViewById(R.id.boardPicture);
        boardName = findViewById(R.id.newBoardName);
        boardSize = findViewById(R.id.newBoardSize);
        uploadText = findViewById(R.id.uploadText);

        findViewById(R.id.addDescription).setOnClickListener(this);
        findViewById(R.id.uploadPicture).setOnClickListener(this);
        findViewById(R.id.addBoardButton).setOnClickListener(this);
        findViewById(R.id.takePicture).setOnClickListener(this);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        uniqueID = UUID.randomUUID().toString();
        boardDesc = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(addBoard.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(addBoard.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(addBoard.this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, PERMISSION_CODE);
        }

    }

    private Boolean wordCount(String text){
        String[] words = text.split("\\s+");
        Log.d("ADDBOARD", "wordCount: " + words.length);

        if(words.length > 2) {
            return false;
        }
        else {
            return true;
        }

    }

    private void addToArray(String desc) {
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(desc);
        boolean found = matcher.find();

        Log.d("ADD", "onQueryTextSubmit: " + desc);
        Log.d("Add", "is there a space? " + found + " " + desc);

        if (found) {
            desc = desc.replace(" ", "-");
            boardDesc.add(desc);
            Log.d("ADD", "replaced spaces " + found + " " + desc + " " + boardDesc);

        } else {
            boardDesc.add(desc);
            Log.d("ADD", "no spaces " + found + " " + desc + " " + boardDesc);
        }
    }

    private void addEquip(String url) {
        name = boardName.getText().toString();
        size = boardSize.getText().toString();


            final Equipment newEquipment = new Equipment(uniqueID, name, boardDesc, size, false, url);

            db.collection("equipment").document(uniqueID).set(newEquipment)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Log.d("ADD", "Equipment added to data base:" + newEquipment);

                            Toast.makeText(addBoard.this, "Board Added", Toast.LENGTH_LONG).show();

                            goBack();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("ADD", "failed to add to data base");
                }
            });

    }

    private void addChip(final String desc) {

        if(!wordCount(desc)){
            descriptionText.setError("Max 2 Words");
        }else {


            final Chip chip = new Chip(this);
            chip.setText(desc);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chipGroup.removeView(chip);
                    boardDesc.remove(desc);
                    Log.d("ADD", "item removed" + " " + boardDesc);
                }
            });
            chipGroup.addView(chip);
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void takePicture(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Log.d("Check","get bitmap");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void uploadPic() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            final String path = "images/equipment/boards/" + uniqueID;

            final StorageReference ref = storageReference.child(path);
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();

                    storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("ADD", "SUCCESS" + uri);
                            Toast.makeText(addBoard.this, "Uploaded", Toast.LENGTH_LONG).show();

                            addEquip(uri.toString());

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(addBoard.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            Picasso.get().load(filePath).centerCrop().fit().into(boardPic);
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Log.d("Check"," checking activity");
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            Log.d("Check"," " + photo);
            Uri tempUri = getImageUri(getApplicationContext(), photo);
            filePath = tempUri;
            Picasso.get().load(filePath).centerCrop().fit().into(boardPic);
            Log.d("Check"," " + tempUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case PERMISSION_CODE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(addBoard.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(addBoard.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean validateForm(String name, String size, ArrayList<String> desc, Uri filePath) {
        boolean valid = true;

        if (TextUtils.isEmpty(name)) {
            boardName.setError("Required.");
            valid = false;
        }

        if (TextUtils.isEmpty(size)) {
            boardSize.setError("Required.");
            valid = false;
        }

        if (desc.isEmpty()) {
            descriptionText.setError("Required.");
            valid = false;
        }

        if (filePath == null) {
            uploadText.setError("Required.");
            valid = false;
        }

        return valid;
    }

    private void goBack(){
        Intent intent = new Intent(this,adminActivity.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.addDescription){
            addChip(descriptionText.getText().toString());
            addToArray(descriptionText.getText().toString());
            descriptionText.setText("");
        }
        if(i == R.id.uploadPicture){
            selectImage();
        }

        if(i == R.id.takePicture){
            takePicture(v);
        }

        if(i == R.id.addBoardButton){
            name = boardName.getText().toString();
            size = boardSize.getText().toString();

            if (!validateForm(name, size, boardDesc, filePath)) {
                return;
            }else {
                uploadPic();
            }
        }
    }
}

