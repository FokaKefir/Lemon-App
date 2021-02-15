package com.example.lemon_app.gui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.lemon_app.R;
import com.example.lemon_app.database.DataRequest;
import com.example.lemon_app.gui.activity.MainActivity;
import com.example.lemon_app.gui.recyclerview.PostAdapter;
import com.example.lemon_app.model.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.lemon_app.constants.Constants.USER_REQUEST_URL;

public class UserFragment extends PostsFragment implements Response.ErrorListener, Response.Listener<String>, View.OnClickListener, PostAdapter.OnPostListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private View view;

    private ImageView imgUser;

    private TextView txtName;
    private TextView txtPosts;
    private TextView txtFriends;

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private int userId;
    private ArrayList<Post> posts;

    // endregion

    // region 2. Lifecycle and Constructor

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_user, container, false);

        try {
            this.userId = getArguments().getInt("user_id");
        } catch (Exception e) {
            this.userId = MainActivity.getUserId();
        }

        this.posts = new ArrayList<>();

        this.imgUser = this.view.findViewById(R.id.img_user);
        this.txtName = this.view.findViewById(R.id.txt_user_name);
        this.txtPosts = this.view.findViewById(R.id.txt_user_posts);
        this.txtFriends = this.view.findViewById(R.id.txt_user_friends);

        this.txtPosts.setOnClickListener(this);
        this.txtFriends.setOnClickListener(this);

        Map<String, String> paramsUser = new HashMap<>();
        paramsUser.put("id", String.valueOf(this.userId));
        DataRequest dataRequestUser = new DataRequest(paramsUser, USER_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequestUser);

        this.recyclerView = this.view.findViewById(R.id.recycler_view_user_posts);
        this.layoutManager = new LinearLayoutManager(this.getContext());
        this.adapter = new PostAdapter(this.posts, this, getContext());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        return this.view;
    }

    // endregion

    // region 3. RecyclerView listeners

    @Override
    public void onCommentListener(int id) {
        Fragment commentsFragment = new CommentsFragment(this);
        Bundle data = new Bundle();
        data.putInt("id", id);
        data.putInt("author_id", getPostById(id).getAuthorId());
        commentsFragment.setArguments(data);
        this.getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, commentsFragment).addToBackStack(null).commit();

    }

    // endregion

    // region 4. Button listener

    @Override
    public void onClick(View view) {

    }

    // endregion

    // region 5. Load data from php

    @SuppressLint("SetTextI18n")
    @Override
    public void onResponse(String response) {
        // Get user data
        try {
            JSONObject jsonResponse = new JSONObject(response);
            boolean success = jsonResponse.getBoolean("success");

            if (success) {
                String name = jsonResponse.getString("name");
                String strImage = jsonResponse.getString("image");
                int numberOfPosts = jsonResponse.getInt("posts");
                int numberOfFriends = jsonResponse.getInt("friends");

                this.txtName.setText(name);
                this.txtPosts.setText(numberOfPosts + " posts");
                this.txtFriends.setText(numberOfFriends + " friends");
                Glide.with(getContext()).load(strImage).into(this.imgUser);

                JSONArray jsonPosts = jsonResponse.getJSONArray("post_array");

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

            }
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
        int ind = -1;
        for (int i = 0; i < this.posts.size(); i++) {
            if (this.posts.get(i).getId() == postId) {
                ind = i;
                break;
            }
        }
        if (ind != -1) {
            Post post = this.posts.get(ind);
            post.setLiked(true);
            post.increaseLikes();
            this.posts.set(ind, post);
            //this.adapter.notifyItemChanged(ind);
            this.adapter.onBindViewHolder(this.adapter.getMyHolder(ind), ind);

        }
    }

    private void unlikePost(int postId) {
        int ind = -1;
        for (int i = 0; i < this.posts.size(); i++) {
            if (this.posts.get(i).getId() == postId) {
                ind = i;
                break;
            }
        }
        if (ind != -1) {
            Post post = this.posts.get(ind);
            post.setLiked(false);
            post.decreaseLikes();
            this.posts.set(ind, post);
            this.adapter.onBindViewHolder(this.adapter.getMyHolder(ind), ind);

        }
    }

    // endregion

    // region 8. Comment post

    public void adapterNotifyCommentChanged(Integer postId, boolean increase) {
        int position = getIndById(postId);
        Post post = this.posts.get(position);
        if (increase)
            post.increaseComments();
        else
            post.decreaseComments();
        this.adapter.onBindViewHolder(this.adapter.getMyHolder(position), position);
    }

    // endregion

    // region 9. Getters and Setters

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
