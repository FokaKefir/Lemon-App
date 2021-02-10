package com.example.lemon_app.gui.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lemon_app.R;
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

        holder.id = currentComment.getPostId();
        holder.authorId = currentComment.getAuthorId();
        holder.txtAuthor.setText(currentComment.getAuthor());
        holder.txtText.setText(currentComment.getText());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    // endregion

    // region 4. Holder class

    public static class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public int id;
        public int authorId;
        public TextView txtAuthor;
        public TextView txtText;

        private OnCommentListener onCommentListener;

        public CommentViewHolder(View itemView, OnCommentListener onCommentListener) {
            super(itemView);

            this.onCommentListener = onCommentListener;

            this.txtAuthor = itemView.findViewById(R.id.txt_comment_author);
            this.txtText = itemView.findViewById(R.id.txt_comment_text);

            this.txtAuthor.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.txt_comment_author) {
                this.onCommentListener.onAuthorListener(this.authorId);
            }
        }
    }

    // endregion

    // region 5. Listener interface

    public interface OnCommentListener {
        void onAuthorListener(int authorId);
    }

    // endregion

}
