package com.y_social_media_app.ui;

import static android.content.ContentValues.TAG;
import static android.util.Log.println;
import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.y_social_media_app.R;
import com.y_social_media_app.databinding.ActivityEditProfileBinding;

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
    ProgressDialog progressDialog;


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
        progressDialog = new ProgressDialog(this);

        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference("Users");

        listenerSetup();

    }

    @Override
    protected void onPause() {
        super.onPause();
        listenerSetup();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        listenerSetup();
    }


    private void listenerSetup() {
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

        editPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Update Password");
                displayUpdatePasswordDialog();
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

    private void displayUpdatePasswordDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.change_password_dialog, null);
        final EditText currentPassword = view.findViewById(R.id.current_password_edit_text);
        final EditText newPassword = view.findViewById(R.id.new_password_edit_text);
        final EditText confirmPassword = view.findViewById(R.id.confirm_password_edit_text);
        Button updatePasswordBtn = view.findViewById(R.id.update_password_button);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        updatePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid = true;
                String currentPass = currentPassword.getText().toString();
                String newPass = newPassword.getText().toString();
                String confirmPass = confirmPassword.getText().toString();


                if (currentPass.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    currentPassword.setError("Please enter your current password");
                    isValid = false;

                }

                if (newPass.length() < 8) {
                    newPassword.setError("Password must be at least 8 characters");
                    isValid = false;
                }

                if (!newPass.equals(confirmPass)) {
                    confirmPassword.setError("Passwords do not match");
                    isValid = false;
                }

                if (confirmPass.isEmpty()) {
                    confirmPassword.setError("Password must be at least 8 characters");
                    isValid = false;
                }

                if (isValid) {
                    ProgressBar progressBar = view.findViewById(R.id.progressBar_cyclic);
                    progressBar.setVisibility(View.VISIBLE);
                    updatePassword(currentPass, newPass);
                    progressBar.setVisibility(View.INVISIBLE);
                    dialog.dismiss();
                }


            }
        });

    }

    private void updatePassword(String currentPass, String newPass) {
        firebaseUser = firebaseAuth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), currentPass);
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Password Updated Successfully", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Update Password Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Update Password Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}