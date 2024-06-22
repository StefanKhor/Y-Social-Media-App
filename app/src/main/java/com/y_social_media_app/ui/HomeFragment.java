package com.y_social_media_app.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.Firebase;
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
import com.y_social_media_app.R;
import com.y_social_media_app.databinding.FragmentHomeBinding;
import com.y_social_media_app.databinding.FragmentProfileBinding;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    ArrayList<ModalPost> allPosts;

    private FragmentHomeBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        // Initialize RecyclerView and set LayoutManager
        RecyclerView recyclerView = binding.homeRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allPosts = new ArrayList<>();
        loadPost();
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
    private void loadPost(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://y-social-media-app-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference postsRef = firebaseDatabase.getReference("Posts");

        Query query = postsRef.orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot post: snapshot.getChildren()){
                            System.out.println(post.toString());
                            ModalPost modalPost = post.getValue(ModalPost.class);
                            allPosts.add(modalPost);
                            binding.homeRecyclerView.setAdapter(
                                    new PostAdapter(getActivity(), allPosts) {
                                    }
                            );
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                });

    }
}