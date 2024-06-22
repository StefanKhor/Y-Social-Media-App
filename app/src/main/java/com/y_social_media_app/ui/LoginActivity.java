package com.y_social_media_app.ui;

import static android.view.View.VISIBLE;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.y_social_media_app.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding loginBinding;
    private EditText email, password;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        email = loginBinding.emailInput;
        password = loginBinding.passwordInput;
        Button loginButton = loginBinding.loginButton;
        Button createAccButton = loginBinding.createAccountButton;

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBinding.errorTextView.setVisibility(View.INVISIBLE);
            }
        });

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBinding.errorTextView.setVisibility(View.INVISIBLE);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(email.getText().toString(), password.getText().toString());

            }
        });

        createAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    private boolean validateInput(String email, String password) {
        boolean valid = true;
        if (TextUtils.isEmpty(email)) {
            loginBinding.emailInput.setError("Email Required");
            valid = false;
        } else {
            loginBinding.emailInput.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            loginBinding.passwordInput.setError("Password Required");
            valid = false;
        } else {
            loginBinding.passwordInput.setError(null);
        }

        return valid;

    }

    private void signIn(String email, String password) {
        if (!validateInput(email, password)) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // If sign in success, redirect user to the dashboard
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fail, display error message
                            Toast.makeText(LoginActivity.this, "Failed to sign in",
                                    Toast.LENGTH_SHORT).show();
                            TextView errorTextView = loginBinding.errorTextView;
                            errorTextView.setVisibility(VISIBLE);
                        }
                    }
                });
    }
}