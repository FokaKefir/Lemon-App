package com.example.lemon_app.gui.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lemon_app.R;
import com.example.lemon_app.model.Post;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    // region 1. Delc and Init

    private ArrayList<Post> posts;
    private OnPostListener onPostListener;
    private Context context;

    // endregion

    // region 2. Constructor

    public PostAdapter(ArrayList<Post> posts, OnPostListener onPostListener, Context context) {
        this.posts = posts;
        this.onPostListener = onPostListener;
        this.context = context;
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

        holder.id = currentPost.getId();
        holder.authorId = currentPost.getAuthorId();
        holder.txtAuthor.setText(currentPost.getAuthor());
        holder.txtDate.setText(currentPost.getDate());
        holder.txtDescription.setText(currentPost.getDescription());
        holder.txtLikes.setText(currentPost.getNumberOfLikes() + " likes");
        holder.txtComments.setText(currentPost.getNumberOfComments() + " comments");
        Glide.with(this.context).load(currentPost.getImage()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // endregion

    // region 4. Holder class

    public static class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public int id;
        public int authorId;
        public ImageView image;
        public TextView txtAuthor;
        public TextView txtDate;
        public TextView txtDescription;
        public TextView txtLikes;
        public TextView txtComments;

        private View itemView;

        private OnPostListener onPostListener;

        public PostViewHolder(@NonNull View itemView, OnPostListener onPostListener) {
            super(itemView);

            this.itemView = itemView;
            this.onPostListener = onPostListener;

            this.image = itemView.findViewById(R.id.img_post);
            this.txtAuthor = itemView.findViewById(R.id.txt_post_author);
            this.txtDate = itemView.findViewById(R.id.txt_post_date);
            this.txtDescription = itemView.findViewById(R.id.txt_post_description);
            this.txtLikes = itemView.findViewById(R.id.txt_post_likes);
            this.txtComments = itemView.findViewById(R.id.txt_post_comments);

            this.itemView.setOnClickListener(this);
            this.txtAuthor.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == this.itemView) {
                this.onPostListener.onPostListener(this.id);
            } else if (view.getId() == R.id.txt_post_author) {
                this.onPostListener.onAuthorListener(this.authorId);
            }
        }
    }

    // endregion

    // region 5. Listener interface

    public interface OnPostListener {
        void onPostListener(int id);
        void onAuthorListener(int authorId);
    }

    // endregion

}
