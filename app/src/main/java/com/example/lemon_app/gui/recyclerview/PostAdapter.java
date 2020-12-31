package com.example.lemon_app.gui.recyclerview;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lemon_app.R;
import com.example.lemon_app.model.Post;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private ArrayList<Post> posts;

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView txtAuthor;
        public TextView txtDate;
        public TextView txtDescription;
        public TextView txtLikes;
        public TextView txtComments;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            this.image = itemView.findViewById(R.id.img_post);
            this.txtAuthor = itemView.findViewById(R.id.txt_author);
            this.txtDate = itemView.findViewById(R.id.txt_date);
            this.txtDescription = itemView.findViewById(R.id.txt_description);
            this.txtLikes = itemView.findViewById(R.id.txt_likes);
            this.txtComments = itemView.findViewById(R.id.txt_comments);
        }
    }

    public PostAdapter(ArrayList<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_post, parent, false);
        PostViewHolder viewHolder = new PostViewHolder(v);

        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post currentPost = this.posts.get(position);

        holder.image.setImageResource(currentPost.getImage());
        holder.txtAuthor.setText(currentPost.getAuthor());
        holder.txtDate.setText(currentPost.getDate());
        holder.txtDescription.setText(currentPost.getDescription());
        holder.txtLikes.setText(currentPost.getNumberOfLikes() + " likes");
        holder.txtComments.setText(currentPost.getNumberOfComments() + " comments");
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
