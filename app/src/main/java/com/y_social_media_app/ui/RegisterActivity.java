package com.y_social_media_app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.y_social_media_app.R;
import com.y_social_media_app.databinding.ActivityRegisterBinding;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        binding.loginText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });
    }

    private void registerUser(){
        String username = binding.usernameInput.getText().toString();
        String email = binding.emailInput.getText().toString();
        String password = binding.passwordInput.getText().toString();

        if (!validateForm(username, email, password)){
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        // User is null, handle accordingly
                        String email = user.getEmail();
                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("email", email);
                        hashMap.put("uid", user.getUid());
                        hashMap.put("username", username);
                        hashMap.put("handler", username);
                        hashMap.put("bio", "");
                        hashMap.put("profileImageURL", "");
                        hashMap.put("coverImageURL", "");
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("Users");
                        reference.child(user.getUid()).setValue(hashMap);
                        Toast.makeText(RegisterActivity.this, "Register Successful", Toast.LENGTH_SHORT).show();

                        FirebaseAuth.getInstance().signOut();
                        navigateToLogin();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Boolean validateForm(String username, String email, String password){
        String error = null;
        if (username.isEmpty()){
            // Username is empty
            Toast.makeText(RegisterActivity.this, "Username Must Not be Empty", Toast.LENGTH_SHORT).show();
            binding.usernameInput.setError("Username Must Not be Empty");
            return false;
        }
        if (email.isEmpty()){
            // Email is empty
            Toast.makeText(RegisterActivity.this, "Email Must Not be Empty", Toast.LENGTH_SHORT).show();
            binding.emailInput.setError("Email Must Not be Empty");
            return false;
        }
        if (password.isEmpty()){
            // Password is empty
            Toast.makeText(RegisterActivity.this, "Password Must Not be Empty", Toast.LENGTH_SHORT).show();
            binding.passwordInput.setError("Password Must Not be Empty");
            return false;
        }

        if (password.length() < 8) {
            // Password is too short
            Toast.makeText(RegisterActivity.this,  " Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            binding.passwordInput.setError("Password must be at least 8 characters long");
            return false;
        }


        return true;
    }

    private void navigateToLogin(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}