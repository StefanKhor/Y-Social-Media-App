package com.y_social_media_app;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.y_social_media_app.ui.EditProfileActivity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {



    private final Context context;
    private final ArrayList<ModalPost> posts;

    // Constructor
    public PostAdapter(Context context, ArrayList<ModalPost> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_rows, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        // to set data to textview and imageview of each card layout
        ModalPost model = posts.get(position);
        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());
        holder.timestamp.setText(getTimeDate(model.getTimestamp()));

        DatabaseReference userRef = FirebaseDatabase.getInstance("https://y-social-media-app-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        userRef.child(model.getUid()).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue(String.class);
                holder.username.setText(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                holder.username.setText("?????");
            }
        });

        userRef.child(model.getUid()).child("profileImageURL").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String url = snapshot.getValue(String.class);
                Glide.with(context).load(url).into(holder.profile_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return posts.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView profile_image, post_image;
        private final TextView title, description, timestamp, username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.post_profile_image);
            post_image = itemView.findViewById(R.id.post_profile_image);
            username = itemView.findViewById(R.id.post_username);
            title = itemView.findViewById(R.id.post_title);
            description = itemView.findViewById(R.id.post_description);
            timestamp = itemView.findViewById(R.id.post_timestamp);
        }
    }

    private String getTimeDate(String timestamp){
        try{
            long data = Long.parseLong(timestamp);
            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            Date netDate = (new Date(data));
            return dateFormat.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }

    public static class UserClass{
        public static String username;
    }
}