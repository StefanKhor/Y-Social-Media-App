package com.y_social_media_app.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.y_social_media_app.ModalPost;
import com.y_social_media_app.PostAdapter;
import com.y_social_media_app.databinding.FragmentProfileBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private FragmentProfileBinding binding;

    ArrayList<ModalPost> allPosts;

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
                        if (getActivity() != null) {
                            Glide.with(getActivity())
                                    .load(profileImageURL)
                                    .into(binding.profileFragmentProfileImage);
                        }
                    }

                    if (!coverImageURL.isEmpty()) {
                        if (getActivity() != null) {
                            Glide.with(getActivity())
                                    .load(coverImageURL)
                                    .into(binding.profileFragmentProfileCoverImage);
                        }
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

        binding.logoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                firebaseAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        // Initialize RecyclerView and set LayoutManager
        RecyclerView recyclerView = binding.ownPostRecyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allPosts = new ArrayList<>();
        getOwnPost();

        return binding.getRoot();
    }


    private void getOwnPost(){
        binding.ownPostProgressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://y-social-media-app-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference postsRef = firebaseDatabase.getReference("Posts");
        String uid = firebaseUser.getUid();

        Query query = postsRef.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ModalPost> allPosts = new ArrayList<>();

                // Iterate through the filtered results and add to allPosts
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ModalPost modalPost = postSnapshot.getValue(ModalPost.class);
                    allPosts.add(modalPost);
                }

//                Sort manually
                allPosts.sort(new Comparator<ModalPost>() {
                    @Override
                    public int compare(ModalPost post1, ModalPost post2) {
                        // Parse timestamp strings to long
                        long timestamp1 = Long.parseLong(post1.getTimestamp());
                        long timestamp2 = Long.parseLong(post2.getTimestamp());

                        // Compare timestamps in descending order (latest first)
                        return Long.compare(timestamp2, timestamp1);                    }
                });

                // Set the adapter to your RecyclerView
                PostAdapter adapter = new PostAdapter(getActivity(), allPosts);
                binding.ownPostProgressBar.setVisibility(View.GONE);
                binding.ownPostRecyclerview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.ownPostProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}