package com.y_social_media_app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.y_social_media_app.databinding.ActivityDashboardBinding;

import com.y_social_media_app.R;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    String uid;
    ActionBar actionBar;
    ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.navigationView.setSelectedItemId(R.id.navigation_home);
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction homeFragmentTransaction = getSupportFragmentManager().beginTransaction();
        homeFragmentTransaction.replace(R.id.content, homeFragment);
        homeFragmentTransaction.commit();

        binding.navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.navigation_home){
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentTransaction homeFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    homeFragmentTransaction.replace(R.id.content, homeFragment);
                    homeFragmentTransaction.commit();
                    return true;
                }
                else if (menuItem.getItemId() == R.id.navigation_post){
                    Intent intent = new Intent(DashboardActivity.this, CreatePostActivity.class);
                    startActivity(intent);
                    return false;
                }
                else if (menuItem.getItemId() == R.id.navigation_profile) {
                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentTransaction profileFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    profileFragmentTransaction.replace(R.id.content, profileFragment);
                    profileFragmentTransaction.commit();
                    return true;
                }
                return false;
            }
        });

//      Use home as default when the application start
    }

}