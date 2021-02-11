package com.example.lemon_app.gui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lemon_app.R;
import com.example.lemon_app.database.DataRequest;
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


public class CommentsFragment extends Fragment implements CommentAdapter.OnCommentListener, View.OnClickListener, Response.Listener<String>, Response.ErrorListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private View view;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextInputLayout txtInputComment;
    private FloatingActionButton fabSend;

    private Comment newComment;
    private ArrayList<Comment> comments;
    private int postId;
    private int userId;
    private String strName;

    // endregion

    // region 2. Lifecycle

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_comments, container, false);

        this.postId = getArguments().getInt("id");
        this.userId = MainActivity.getUserId();
        this.strName = MainActivity.getStrUser();

        this.txtInputComment = this.view.findViewById(R.id.txt_new_comment);
        this.fabSend = this.view.findViewById(R.id.fab_send_comment);
        this.fabSend.setOnClickListener(this);
        
        this.comments = new ArrayList<>();

        Map<String, String> params = new HashMap<>();
        params.put("post_id", String.valueOf(this.postId));
        DataRequest dataRequest = new DataRequest(params, COMMENTS_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequest);

        this.recyclerView = this.view.findViewById(R.id.recycler_view_comments);
        this.layoutManager = new LinearLayoutManager(this.getContext());
        this.adapter = new CommentAdapter(this.comments, this);
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        return this.view;
    }

    // endregion

    // region 3. Comment click listener

    @Override
    public void onAuthorListener(int authorId) {
        Toast.makeText(getContext(), String.valueOf(authorId), Toast.LENGTH_SHORT).show();
        // TODO open author user page
    }

    @Override
    public void onDeleteListener(int commentId) {
        deleteComment(commentId);
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

    // region 5. Loading comments from php

    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            boolean uploaded = jsonResponse.getBoolean("uploaded");

            if (uploaded) {
                this.newComment.setId(jsonResponse.getInt("id"));
                this.comments.add(this.newComment);
                this.adapter.notifyItemInserted(this.comments.size() - 1);
                sendNotification();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray jsonComments = new JSONArray(response);

            for (int ind = 0; ind < jsonComments.length(); ind++) {
                JSONObject jsonComment= jsonComments.getJSONObject(ind);

                int id = jsonComment.getInt("id");
                int authorId = jsonComment.getInt("author_id");
                String author = jsonComment.getString("author");
                String text = jsonComment.getString("text");

                Comment comment = new Comment(id, this.postId, authorId, author, text);
                this.comments.add(comment);
                this.adapter.notifyItemInserted(this.comments.size() - 1);

                this.txtInputComment.getEditText().setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonResponse = new JSONObject(response);
            boolean deleted = jsonResponse.getBoolean("deleted");

            if (deleted) {
                int deleteId = jsonResponse.getInt("id");
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
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {
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

        Map<String, String> params = new HashMap<>();
        params.put("post_id", String.valueOf(this.postId));
        params.put("user_id", String.valueOf(this.userId));
        params.put("text", strComment);
        DataRequest dataRequest = new DataRequest(params, UPLOAD_COMMENT_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequest);
    }

    // endregion

    // region 8. Sending notification to php

    private void sendNotification() {
        // TODO send notification
    }

    // endregion

    // region 9. Delete comment

    private void deleteComment(int commentId) {
        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(commentId));
        DataRequest dataRequest = new DataRequest(params, DELETE_COMMENT_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequest);
    }

    // endregion

}