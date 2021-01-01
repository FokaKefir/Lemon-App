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

    // region 1. Delc and Init

    private ArrayList<Post> posts;
    private OnPostListener onPostListener;

    // endregion

    // region 2. Constructor

    public PostAdapter(ArrayList<Post> posts, OnPostListener onPostListener) {
        this.posts = posts;
        this.onPostListener = onPostListener;
    }

    // endregion

    // region 3. Adapter

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_post, parent, false);
        PostViewHolder viewHolder = new PostViewHolder(v, this.onPostListener);

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

    // endregion

    // region 4. Holder class

    public static class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView image;
        public TextView txtAuthor;
        public TextView txtDate;
        public TextView txtDescription;
        public TextView txtLikes;
        public TextView txtComments;

        private OnPostListener onPostListener;

        public PostViewHolder(@NonNull View itemView, OnPostListener onPostListener) {
            super(itemView);

            this.image = itemView.findViewById(R.id.img_post);
            this.txtAuthor = itemView.findViewById(R.id.txt_author);
            this.txtDate = itemView.findViewById(R.id.txt_date);
            this.txtDescription = itemView.findViewById(R.id.txt_description);
            this.txtLikes = itemView.findViewById(R.id.txt_likes);
            this.txtComments = itemView.findViewById(R.id.txt_comments);

            this.onPostListener = onPostListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            this.onPostListener.onPostListener(getAdapterPosition());
        }
    }

    // endregion

    // region 5. Listener interface

    public interface OnPostListener {
        void onPostListener(int position);
    }

    // endregion

}
