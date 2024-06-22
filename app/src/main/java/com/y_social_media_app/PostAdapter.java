package com.y_social_media_app;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

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
        holder.timestamp.setText(model.getTimestamp());
//        holder.profile_image.setImageResource(model.getAuthorImage());
//        holder.post_image.setImageResource(model.getPost_image());
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number of card items in recycler view
        return posts.size();
    }

    // View holder class for initializing of your views such as TextView and Imageview
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView profile_image, post_image;
        private final TextView title, description, timestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.post_profile_image);
            post_image = itemView.findViewById(R.id.post_profile_image);
            title = itemView.findViewById(R.id.post_title);
            description = itemView.findViewById(R.id.post_description);
            timestamp = itemView.findViewById(R.id.post_timestamp);
        }
    }
}