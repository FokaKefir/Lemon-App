package com.example.lemon_app.gui.recyclerview;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lemon_app.R;
import com.example.lemon_app.gui.activity.MainActivity;
import com.example.lemon_app.gui.fragment.CommentsFragment;
import com.example.lemon_app.model.Comment;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    // region 1. Decl and Init

    private ArrayList<Comment> comments;
    private OnCommentListener onCommentListener;

    // endregion

    // region 2. Constructor

    public CommentAdapter(ArrayList<Comment> comments, OnCommentListener onCommentListener) {
        this.comments = comments;
        this.onCommentListener = onCommentListener;
    }

    // endregion

    // region 3. Adapter

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_comment, parent, false);
        CommentViewHolder viewHolder = new CommentViewHolder(v, this.onCommentListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment currentComment = this.comments.get(position);

        holder.id = currentComment.getId();
        holder.postId = currentComment.getPostId();
        holder.authorId = currentComment.getAuthorId();
        holder.txtAuthor.setText(currentComment.getAuthor());
        holder.txtText.setText(currentComment.getText());
        if (holder.authorId != MainActivity.getUserId() && MainActivity.getUserId() != CommentsFragment.getAuthorId()) {
            holder.btnOptions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    // endregion

    // region 4. Holder class

    public static class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        public int id;
        public int postId;
        public int authorId;
        public TextView txtAuthor;
        public TextView txtText;
        public ImageButton btnOptions;

        private OnCommentListener onCommentListener;

        public CommentViewHolder(View itemView, OnCommentListener onCommentListener) {
            super(itemView);

            this.onCommentListener = onCommentListener;

            this.txtAuthor = itemView.findViewById(R.id.txt_comment_author);
            this.txtText = itemView.findViewById(R.id.txt_comment_text);
            this.btnOptions = itemView.findViewById(R.id.ib_comment_options);

            this.txtAuthor.setOnClickListener(this);
            this.btnOptions.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.txt_comment_author) {
                this.onCommentListener.onAuthorListener(this.authorId);
            } else if (view.getId() == R.id.ib_comment_options) {
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
                    this.onCommentListener.onDeleteListener(this.id);
                    return true;

                default:
                    return false;
            }
        }
    }

    // endregion

    // region 5. Listener interface

    public interface OnCommentListener {
        void onAuthorListener(int authorId);
        void onDeleteListener(int commentId);
    }

    // endregion

}
