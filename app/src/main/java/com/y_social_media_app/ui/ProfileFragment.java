package com.y_social_media_app.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.y_social_media_app.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private FragmentProfileBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance("https://y-social-media-app-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseReference = firebaseDatabase.getReference("Users");

//        Query and get information from firebase database
        Query query = databaseReference.child(firebaseUser.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
//                  //  Get data from database
                    String username = "" + dataSnapshot.child("username").getValue();
                    String handler = "" + dataSnapshot.child("handler").getValue();
                    String bio = "" + dataSnapshot.child("bio").getValue();
                    String profileImageURL = "" + dataSnapshot.child("profileImageURL").getValue();
                    String coverImageURL = "" + dataSnapshot.child("coverImageURL").getValue();

                    binding.profileUsername.setText(username);
                    binding.profileHandler.setText(handler);

                    if (!bio.isEmpty()){
                        binding.profileBio.setText(bio);
                    }
                    if (!profileImageURL.isEmpty()) {
                        Glide.with(ProfileFragment.this)
                                .load(profileImageURL)
                                .into(binding.profileFragmentProfileImage);
                    }

                    if (!coverImageURL.isEmpty()) {
                        Glide.with(ProfileFragment.this)
                                .load(coverImageURL)
                                .into(binding.profileFragmentProfileCoverImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                handle any error
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }
}