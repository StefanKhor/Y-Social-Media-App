package com.y_social_media_app.ui;

import static java.security.AccessController.getContext;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.y_social_media_app.R;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
//    StorageRefence storageRefence;
    String storagepath = "";
    ImageView profileImage, coverImgage;
    EditText username, bio;
    Button editPasswordBtn, saveBtn;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;
    String cameraPermission[];
    String storagePermission[];
    Uri profileImageUri, coverImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://y-social-media-app-default-rtdb.asia-southeast1.firebasedatabase.app");

        profileImage = findViewById(R.id.profile_image);
        coverImgage = findViewById(R.id.profile_cover_image);
        username = findViewById(R.id.edit_username);
        bio = findViewById(R.id.edit_bio);

        editPasswordBtn = findViewById(R.id.update_password_button);
        saveBtn = findViewById(R.id.edit_profile_button);


        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference("Users");
        Query query = databaseReference.child(firebaseUser.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
//                  //  Get data from database
                    String profileImageURL = "" + dataSnapshot.child("profileImageURL").getValue();
                    String coverImageURL = "" + dataSnapshot.child("coverImageURL").getValue();

                    username.setText(dataSnapshot.child("username").getValue().toString());
                    bio.setText(dataSnapshot.child("bio").getValue().toString());
                    if (!profileImageURL.isEmpty()) {
                        Glide.with(EditProfileActivity.this)
                                .load(profileImageURL)
                                .into(profileImage);
                    }

                    if (!coverImageURL.isEmpty()) {
                        Glide.with(EditProfileActivity.this)
                                .load(coverImageURL)
                                .into(coverImgage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                handle any error
                Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Choose a profile image
            }
        });

        coverImgage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

}