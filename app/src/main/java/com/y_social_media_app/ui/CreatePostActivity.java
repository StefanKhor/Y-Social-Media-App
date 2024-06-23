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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.UploadTask;
import com.y_social_media_app.databinding.ActivityCreatePostBinding;

import java.util.HashMap;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity {

    StorageReference storageReference, ref;

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
        storageReference = FirebaseStorage.getInstance("gs://y-social-media-app.appspot.com").getReference();

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        binding.uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreatePostActivity.this, "Pick An Image", Toast.LENGTH_SHORT).show();
                openFileChooser();
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
        if(imageUri != null && !imageUri.equals(Uri.EMPTY)){
            String randomUUID = UUID.randomUUID().toString();
            ref = storageReference.child("PostImages/" + firebaseUser.getUid()+ "/" + randomUUID);
            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(CreatePostActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            hashMap.put("postImage", uri.toString());
                            savePostToDatabase(hashMap);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hashMap.put("postImage", "NoImage");
                    Toast.makeText(CreatePostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            hashMap.put("postImage", "NoImage");
            savePostToDatabase(hashMap);
        }
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

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1000 && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();
            binding.uploadImage.setVisibility(View.GONE);
            binding.postImageResult.setVisibility(View.VISIBLE);
            Glide.with(this).load(imageUri).into(binding.postImageResult);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void savePostToDatabase(HashMap<Object, String> hashMap){
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
}