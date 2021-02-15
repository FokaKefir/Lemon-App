package com.example.lemon_app.gui.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.lemon_app.R;
import com.example.lemon_app.gui.activity.MainActivity;
import com.example.lemon_app.model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    // region 1. Delc and Init

    private ArrayList<Post> posts;
    private OnPostListener onPostListener;
    private Context context;

    private List<PostViewHolder> holders;
    // endregion

    // region 2. Constructor

    public PostAdapter(ArrayList<Post> posts, OnPostListener onPostListener, Context context) {
        this.posts = posts;
        this.onPostListener = onPostListener;
        this.context = context;

        this.holders = new ArrayList<>();
    }

    // endregion

    // region 3. Adapter

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_post, parent, false);
        PostViewHolder viewHolder = new PostViewHolder(v, this.onPostListener);
        this.holders.add(viewHolder);

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
        holder.txtLikes.setText(currentPost.getNumberOfLikes() + " lemons");
        holder.txtComments.setText(currentPost.getNumberOfComments() + " comments");
        Glide.with(this.context)
                .load(currentPost.getImage())
                .into(holder.imgPost);

        if (currentPost.getAuthorId() != MainActivity.getUserId())
            holder.btnOptions.setVisibility(View.GONE);
        else
            holder.btnOptions.setVisibility(View.VISIBLE);

        if (currentPost.isLiked())
            holder.imgLike.setImageResource(R.drawable.ic_lemon_colored);
        else
            holder.imgLike.setImageResource(R.drawable.ic_lemon_gray);

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // endregion

    // region 4. Holder class

    public static class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        public int id;
        public int authorId;
        public ImageView imgPost;
        public ImageView imgLike;
        public TextView txtAuthor;
        public TextView txtDate;
        public TextView txtDescription;
        public TextView txtLikes;
        public TextView txtComments;
        public ImageButton btnOptions;
        public LinearLayout layoutMid;
        public LinearLayout layoutLike;
        public LinearLayout layoutComment;

        private OnPostListener onPostListener;

        public PostViewHolder(@NonNull View itemView, OnPostListener onPostListener) {
            super(itemView);

            this.id = 0;

            this.onPostListener = onPostListener;

            this.imgPost = itemView.findViewById(R.id.img_post);
            this.imgLike = itemView.findViewById(R.id.img_post_like);
            this.txtAuthor = itemView.findViewById(R.id.txt_post_author);
            this.txtDate = itemView.findViewById(R.id.txt_post_date);
            this.txtDescription = itemView.findViewById(R.id.txt_post_description);
            this.txtLikes = itemView.findViewById(R.id.txt_post_likes);
            this.txtComments = itemView.findViewById(R.id.txt_post_comments);
            this.btnOptions = itemView.findViewById(R.id.ib_post_options);
            this.layoutMid = itemView.findViewById(R.id.layout_post_mid);
            this.layoutLike = itemView.findViewById(R.id.layout_post_like);
            this.layoutComment = itemView.findViewById(R.id.layout_post_comment);

            this.txtAuthor.setOnClickListener(this);
            this.btnOptions.setOnClickListener(this);
            this.layoutLike.setOnClickListener(this);
            this.layoutComment.setOnClickListener(this);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.layout_post_comment) {
                this.onPostListener.onCommentListener(this.id);
            } else if (view.getId() == R.id.layout_post_like) {
                if (this.imgLike.getDrawable().getConstantState() == view.getResources().getDrawable(R.drawable.ic_lemon_gray).getConstantState()){
                    this.onPostListener.onLikeListener(this.id);
                } else {
                    this.onPostListener.onUnlikeListener(this.id);
                }
            } else if (view.getId() == R.id.txt_post_author) {
                this.onPostListener.onAuthorListener(this.authorId);
            } else if (view.getId() == R.id.ib_post_options) {
                PopupMenu options = new PopupMenu(view.getContext(), view);
                options.inflate(R.menu.popup_menu);
                options.setOnMenuItemClickListener(this);
                options.show();
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.action_popup_delete:
                    this.onPostListener.onDeleteListener(this.id);
                    return true;
                default:
                    return false;
            }
        }
    }

    // endregion

    // region 5. Listener interface

    public interface OnPostListener {
        void onCommentListener(int id);
        void onAuthorListener(int authorId);
        void onDeleteListener(int postId);
        void onLikeListener(int postId);
        void onUnlikeListener(int postId);
    }

    // endregion

    // region 6. Getters and Setters

    public PostViewHolder getMyHolder(int position) {
        return this.holders.get(position);
    }

    public void removeHolder(int position) {
        this.holders.remove(position);
    }

    // endregion

}
