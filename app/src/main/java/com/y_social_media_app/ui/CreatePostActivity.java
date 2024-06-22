package com.y_social_media_app.ui;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.y_social_media_app.databinding.ActivityCreatePostBinding;

import java.util.HashMap;

public class CreatePostActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String[] cameraPermission;
    String[] storagePermission;

    Uri imageUri = null;

    private ActivityCreatePostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        binding.uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreatePostActivity.this, "Pick An Image", Toast.LENGTH_SHORT).show();
                pickImageDialog();
            }
        });

        binding.cancelPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
    }

    private void submitPost(){
        String title = binding.postTitleInput.getText().toString();
        String description = binding.postDescriptionInput.getText().toString();
        if(!validateForm(title, description)){
            return;
        }

        final String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("uid", firebaseUser.getUid());
        hashMap.put("title", title);
        hashMap.put("description", description);
        hashMap.put("timestamp", timestamp);
        hashMap.put("like", "0");
        hashMap.put("comment", "0");

        Toast.makeText(CreatePostActivity.this, "Publishing Post", Toast.LENGTH_SHORT).show();

        firebaseDatabase = FirebaseDatabase.getInstance("https://y-social-media-app-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseReference = firebaseDatabase.getReference("Posts");

        String postID = databaseReference.push().getKey();
        if (postID != null) {
            databaseReference
                    .child(postID)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(CreatePostActivity.this, "Post Successfully Uploaded", Toast.LENGTH_SHORT).show();
                            binding.postTitleInput.setText("");
                            binding.postDescriptionInput.setText("");
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(CreatePostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void pickImageDialog(){
        String[] option = {"Camera", "Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(CreatePostActivity.this);
        dialog.setTitle("Please select an image");
        dialog.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    if (checkCameraPermission() && checkStoragePermission()){
                        Toast.makeText(CreatePostActivity.this, "Getting Camera Ready", Toast.LENGTH_SHORT).show();
                        getCamera();
                    }
                    else {
                        requestPermissions(cameraPermission, 100);
                        requestPermissions(storagePermission, 200);
                    }
                }
                else {
                    if (checkStoragePermission()){
                    }
                }
            }
        });
        dialog.create().show();
    }



    ///  Check camera and storage permission if user choose to use camera
    private Boolean checkCameraPermission(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA ) == (PackageManager.PERMISSION_GRANTED);
    }

    ///  Check camera and storage permission if user choose to use camera
    private Boolean checkStoragePermission(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) == (PackageManager.PERMISSION_GRANTED);
    }

    //    get the image and pass to startactivityforresult
    private void getCamera(){
        Toast.makeText(CreatePostActivity.this, "Camera", Toast.LENGTH_SHORT).show();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp Image");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Image To Upload");
        imageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(intent);
    }

    private Boolean validateForm(String title, String description){
        if (title.isEmpty()){
            Toast.makeText(CreatePostActivity.this, "Title Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (description.isEmpty()){
            Toast.makeText(CreatePostActivity.this, "Description Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}