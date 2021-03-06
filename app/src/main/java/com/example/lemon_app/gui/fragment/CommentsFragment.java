package com.example.lemon_app.gui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lemon_app.R;
import com.example.lemon_app.constants.Constants;
import com.example.lemon_app.database.DataRequest;
import com.example.lemon_app.database.DatabaseManager;
import com.example.lemon_app.gui.activity.MainActivity;
import com.example.lemon_app.gui.recyclerview.CommentAdapter;
import com.example.lemon_app.model.Comment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.lemon_app.constants.Constants.COMMENTS_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.DELETE_COMMENT_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.UPLOAD_COMMENT_REQUEST_URL;


public class CommentsFragment extends Fragment implements CommentAdapter.OnCommentListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, DatabaseManager.CommentsManager.OnResponseListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private MainActivity activity;

    private View view;

    private DatabaseManager.CommentsManager databaseManager;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextInputLayout txtInputComment;
    private FloatingActionButton fabSend;

    private Comment newComment;
    private ArrayList<Comment> comments;
    private int postId;
    private int authorId;
    private int userId;
    private String strName;

    // endregion

    // region 2. Lifecycle and Constructor

    public CommentsFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_comments, container, false);

        this.postId = getArguments().getInt("id");
        this.authorId = getArguments().getInt("author_id");
        this.userId = this.activity.getLoggedUserId();
        this.strName = this.activity.getStrUser();

        this.databaseManager = new DatabaseManager.CommentsManager(this, getContext(), this.postId);

        this.txtInputComment = this.view.findViewById(R.id.txt_new_comment);
        this.fabSend = this.view.findViewById(R.id.fab_send_comment);
        this.fabSend.setOnClickListener(this);
        
        this.comments = new ArrayList<>();

        this.databaseManager.commentsRequest();

        this.recyclerView = this.view.findViewById(R.id.recycler_view_comments);
        this.layoutManager = new LinearLayoutManager(this.getContext());
        this.adapter = new CommentAdapter(this.comments, this, this.userId, this.authorId);
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        this.swipeRefreshLayout = this.view.findViewById(R.id.layout_swipe_comments);
        this.swipeRefreshLayout.setOnRefreshListener(this);

        return this.view;
    }

    // endregion

    // region 3. Comment click listener

    @Override
    public void onAuthorListener(int authorId) {
        Fragment userFragment = new UserFragment(this.activity);
        Bundle args = new Bundle();
        args.putInt("user_id", authorId);
        userFragment.setArguments(args);
        this.activity.addToFragments(userFragment);
    }

    @Override
    public void onDeleteListener(int commentId) {
        this.databaseManager.deleteComment(commentId);
    }

    // endregion
    
    // region 4. Fab listener

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_send_comment) {
            sendComment();
        }
    }
    
    // endregion

    // region 5. Database manager listener

    @Override
    public void onCommentsResponse(ArrayList<Comment> comments) {
        for (Comment comment : comments) {
            this.comments.add(comment);
            this.adapter.notifyItemInserted(this.comments.size() - 1);
        }

        this.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onInsertCommentResponse(int id) {
        insertComment(id);
    }

    @Override
    public void onDeleteCommentResponse(int deleteId) {
        deleteComment(deleteId);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        this.swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    // endregion

    // region 6. Validate comment

    private boolean validateComment(String strComment) {
        if (strComment.isEmpty()) {
            this.txtInputComment.setError("Comment is empty");
            return false;
        } else if (strComment.length() > 250) {
            this.txtInputComment.setError("Comment is too long");
            return false;
        } else {
            this.txtInputComment.setError(null);
            return true;
        }
    }

    // endregion

    // region 7. Sending comment to php

    private void sendComment() {
        String strComment = this.txtInputComment.getEditText().getText().toString().trim();

        if (!validateComment(strComment)) {
            return;
        }

        this.newComment = new Comment(this.postId, this.userId, this.strName, strComment);

        this.databaseManager.sendComment(this.userId, strComment);
    }

    // endregion

    // region 8. Insert and delete comment

    private void insertComment(int id) {
        this.newComment.setId(id);
        this.comments.add(this.newComment);
        this.adapter.notifyItemInserted(this.comments.size() - 1);
        this.txtInputComment.getEditText().setText("");
        sendNotification();

        this.activity.refreshComment(this, this.postId, Constants.REFRESH_TYPE_INSERT_COMMENT);
    }

    private void deleteComment(int deleteId) {
        int ind = -1;
        for (int i = 0; i < this.comments.size(); i++) {
            if (this.comments.get(i).getId() == deleteId) {
                ind = i;
                break;
            }
        }
        if (ind != -1) {
            this.comments.remove(ind);
            this.adapter.notifyItemRemoved(ind);

            this.activity.refreshComment(this, this.postId, Constants.REFRESH_TYPE_DELETE_COMMENT);
        }
    }

    // endregion

    // region 9. Refresh fragment

    @Override
    public void onRefresh() {
        this.swipeRefreshLayout.setRefreshing(true);
        this.adapter.notifyItemRangeRemoved(0, this.comments.size());
        this.comments.clear();

        this.databaseManager.commentsRequest();
    }

    // endregion

    // region 10. Sending notification to php

    private void sendNotification() {
        // TODO send notification
    }

    // endregion

}