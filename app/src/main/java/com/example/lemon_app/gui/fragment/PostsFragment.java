package com.example.lemon_app.gui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lemon_app.R;
import com.example.lemon_app.constants.Constants;
import com.example.lemon_app.gui.activity.CreatePostActivity;
import com.example.lemon_app.gui.activity.MainActivity;
import com.example.lemon_app.gui.recyclerview.PostAdapter;
import com.example.lemon_app.database.DataRequest;
import com.example.lemon_app.model.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.lemon_app.constants.Constants.DELETE_POST_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.LIKE_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.POSTS_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.UNLIKE_REQUEST_URL;

public class PostsFragment extends Fragment implements PostAdapter.OnPostListener, Response.ErrorListener, Response.Listener<String>, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private MainActivity activity;

    private View view;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton fabAddPost;

    private ArrayList<Post> posts;

    // endregion

    // region 2. Lifecycle and Constructor

    public PostsFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_posts, container, false);

        this.fabAddPost = this.view.findViewById(R.id.fab_add_post);
        this.fabAddPost.setOnClickListener(this);

        this.posts = new ArrayList<>();

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(this.activity.getLoggedUserId()));
        DataRequest dataRequest = new DataRequest(params, POSTS_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequest);

        this.recyclerView = this.view.findViewById(R.id.recycler_view_posts);
        this.layoutManager = new LinearLayoutManager(this.getContext());
        this.adapter = new PostAdapter(this.posts, this, getContext(), this.activity.getLoggedUserId());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        this.swipeRefreshLayout = this.view.findViewById(R.id.layout_swipe_posts);
        this.swipeRefreshLayout.setOnRefreshListener(this);

        return this.view;
    }

    // endregion

    // region 3. Post click listener

    @Override
    public void onCommentListener(int id) {
        Fragment commentsFragment = new CommentsFragment(this.activity);
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putInt("author_id", getPostById(id).getAuthorId());
        commentsFragment.setArguments(args);
        this.activity.addToFragments(commentsFragment);
    }

    @Override
    public void onAuthorListener(int authorId) {
        Fragment userFragment = new UserFragment(this.activity);
        Bundle args = new Bundle();
        args.putInt("user_id", authorId);
        userFragment.setArguments(args);
        this.activity.addToFragments(userFragment);
    }

    @Override
    public void onDeleteListener(int postId) {
        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(postId));
        DataRequest dataRequest = new DataRequest(params, DELETE_POST_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequest);
    }

    @Override
    public void onLikeListener(int postId) {
        Map<String, String> params = new HashMap<>();
        params.put("post_id", String.valueOf(postId));
        params.put("user_id", String.valueOf(this.activity.getLoggedUserId()));
        DataRequest dataRequest = new DataRequest(params, LIKE_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequest);
        // TODO send notification
    }

    @Override
    public void onUnlikeListener(int postId) {
        Map<String, String> params = new HashMap<>();
        params.put("post_id", String.valueOf(postId));
        params.put("user_id", String.valueOf(this.activity.getLoggedUserId()));
        DataRequest dataRequest = new DataRequest(params, UNLIKE_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequest);
        // TODO delete notification
    }

    // endregion

    // region 4. Fab listener

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add_post) {
            Intent intent = new Intent(this.activity, CreatePostActivity.class);
            intent.putExtra("id", this.activity.getLoggedUserId());
            this.startActivity(intent);
        }
    }

    // endregion

    // region 5. Loading posts from php

    @Override
    public void onResponse(String response) {
        // Get posts
        try {
            JSONArray jsonPosts = new JSONArray(response);

            for (int ind = 0; ind < jsonPosts.length(); ind++) {
                JSONObject jsonPost = jsonPosts.getJSONObject(ind);

                int id = jsonPost.getInt("id");
                int authorId = jsonPost.getInt("author_id");
                String image = jsonPost.getString("image");
                String author = jsonPost.getString("author");
                String date = jsonPost.getString("date");
                String description = jsonPost.getString("description");
                int likes = jsonPost.getInt("likes");
                int comments = jsonPost.getInt("comments");
                boolean liked = jsonPost.getBoolean("liked");

                Post post = new Post(id, authorId, image, author, date, description, likes, comments, liked);
                this.posts.add(post);
                this.adapter.notifyItemInserted(this.posts.size() - 1);
            }

            this.swipeRefreshLayout.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Delete post
        try {
            JSONObject jsonResponse = new JSONObject(response);
            boolean deleted = jsonResponse.getBoolean("deleted");

            if (deleted) {
                int deleteId = jsonResponse.getInt("id");
                deletePost(deleteId);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Like post
        try {
            JSONObject jsonResponse = new JSONObject(response);
            boolean liked = jsonResponse.getBoolean("liked");

            if (liked) {
                int postId = jsonResponse.getInt("post_id");
                likePost(postId);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Unlike
        try {
            JSONObject jsonResponse = new JSONObject(response);
            boolean unliked = jsonResponse.getBoolean("unliked");

            if (unliked) {
                int postId = jsonResponse.getInt("post_id");
                unlikePost(postId);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        this.swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    // endregion

    // region 6. Delete post

    private void deletePost(int deleteId) {
        int ind = -1;
        for (int i = 0; i < this.posts.size(); i++) {
            if (this.posts.get(i).getId() == deleteId) {
                ind = i;
                break;
            }
        }
        if (ind != -1) {
            this.posts.remove(ind);
            this.adapter.notifyItemRemoved(ind);
            this.adapter.removeHolder(ind);
        }
    }

    // endregion

    // region 7. Like and unlike post

    private void likePost(int postId) {
        int ind = getIndById(postId);
        if (ind != -1) {
            Post post = this.posts.get(ind);
            post.setLiked(true);
            post.increaseLikes();
            this.posts.set(ind, post);
            this.adapter.onBindViewHolder(this.adapter.getMyHolder(ind), ind);

            this.activity.refreshLike(this, postId, Constants.REFRESH_TYPE_LIKE);
        }
    }

    private void unlikePost(int postId) {
        int ind = getIndById(postId);
        if (ind != -1) {
            Post post = this.posts.get(ind);
            post.setLiked(false);
            post.decreaseLikes();
            this.posts.set(ind, post);
            this.adapter.onBindViewHolder(this.adapter.getMyHolder(ind), ind);

            this.activity.refreshLike(this, postId, Constants.REFRESH_TYPE_UNLIKE);
        }
    }

    // endregion

    // region 8. Refresh fragment

    public void refreshLike(int postId, int type) {
        int ind = getIndById(postId);
        if (ind != -1) {
            if (type == Constants.REFRESH_TYPE_LIKE) {
                Post post = this.posts.get(ind);
                post.setLiked(true);
                post.increaseLikes();
                this.posts.set(ind, post);
                this.adapter.onBindViewHolder(this.adapter.getMyHolder(ind), ind);
            } else if (type == Constants.REFRESH_TYPE_UNLIKE) {
                Post post = this.posts.get(ind);
                post.setLiked(false);
                post.decreaseLikes();
                this.posts.set(ind, post);
                this.adapter.onBindViewHolder(this.adapter.getMyHolder(ind), ind);
            }
        }
    }

    public void refreshComment(int postId, int type) {
        int ind = getIndById(postId);
        if (ind != -1) {
            Post post = this.posts.get(ind);
            if (type == Constants.REFRESH_TYPE_INSERT_COMMENT)
                post.increaseComments();
            else if (type == Constants.REFRESH_TYPE_DELETE_COMMENT)
                post.decreaseComments();
            this.adapter.onBindViewHolder(this.adapter.getMyHolder(ind), ind);
        }
    }

    @Override
    public void onRefresh() {
        this.swipeRefreshLayout.setRefreshing(true);
        this.adapter.notifyItemRangeRemoved(0, this.posts.size());
        this.posts.clear();

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(this.activity.getLoggedUserId()));
        DataRequest dataRequest = new DataRequest(params, POSTS_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequest);
    }

    // endregion

    // region 8. Getters and Setters

    private Post getPostById(int id) {
        Post post = null;
        int ind = -1;
        for (int i = 0; i < this.posts.size(); i++) {
            if (this.posts.get(i).getId() == id) {
                ind = i;
                break;
            }
        }
        if (ind != -1) {
            post = this.posts.get(ind);
        }
        return post;
    }

    private int getIndById(int id) {
        int ind = -1;
        for (int i = 0; i < this.posts.size(); i++) {
            if (this.posts.get(i).getId() == id) {
                ind = i;
                break;
            }
        }
        return ind;
    }

    // endregion



}
