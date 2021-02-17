package com.example.lemon_app.gui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.lemon_app.constants.Constants.FOLLOW_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.UNFOLLOW_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.USER_REQUEST_URL;

public class UserFragment extends PostsFragment implements Response.ErrorListener, Response.Listener<String>, View.OnClickListener, PostAdapter.OnPostListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private View view;

    private ImageView imgUser;

    private TextView txtName;
    private TextView txtPosts;
    private TextView txtFollowers;
    private TextView txtFollowing;

    private FloatingActionButton fabAddUser;
    private FloatingActionButton fabRemoveUser;
    private FloatingActionButton fabSearchUser;

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private int userId;
    private int userFollowers;
    private ArrayList<Post> posts;

    // endregion

    // region 2. Lifecycle

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_user, container, false);

        if (getArguments() != null) {
            this.userId = getArguments().getInt("user_id");
        } else {
            this.userId = MainActivity.getUserId();
        }

        this.posts = new ArrayList<>();

        this.imgUser = this.view.findViewById(R.id.img_user);
        this.txtName = this.view.findViewById(R.id.txt_user_name);
        this.txtPosts = this.view.findViewById(R.id.txt_user_posts);
        this.txtFollowers = this.view.findViewById(R.id.txt_user_followers);
        this.txtFollowing = this.view.findViewById(R.id.txt_user_following);
        this.fabAddUser = this.view.findViewById(R.id.fab_add_user);
        this.fabRemoveUser = this.view.findViewById(R.id.fab_remove_user);
        this.fabSearchUser = this.view.findViewById(R.id.fab_search_user);

        this.txtFollowers.setOnClickListener(this);
        this.txtFollowing.setOnClickListener(this);
        this.fabAddUser.setOnClickListener(this);
        this.fabRemoveUser.setOnClickListener(this);
        this.fabSearchUser.setOnClickListener(this);

        if (this.userId != MainActivity.getUserId())
            this.fabSearchUser.setVisibility(View.GONE);

        Map<String, String> paramsUser = new HashMap<>();
        paramsUser.put("logged_id", String.valueOf(MainActivity.getUserId()));
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
        if (view.getId() == R.id.txt_user_followers) {

        } else if (view.getId() == R.id.txt_user_following) {

        } else if (view.getId() == R.id.fab_add_user) {
            Map<String, String> params = new HashMap<>();
            params.put("follower_id", String.valueOf(MainActivity.getUserId()));
            params.put("following_id", String.valueOf(this.userId));
            DataRequest dataRequest = new DataRequest(params, FOLLOW_REQUEST_URL, this, this);
            Volley.newRequestQueue(getContext()).add(dataRequest);
        } else if (view.getId() == R.id.fab_remove_user) {
            Map<String, String> params = new HashMap<>();
            params.put("follower_id", String.valueOf(MainActivity.getUserId()));
            params.put("following_id", String.valueOf(this.userId));
            DataRequest dataRequest = new DataRequest(params, UNFOLLOW_REQUEST_URL, this, this);
            Volley.newRequestQueue(getContext()).add(dataRequest);
        } else if (view.getId() == R.id.fab_search_user){
            Toast.makeText(getContext(), "search for users", Toast.LENGTH_SHORT).show();
        }
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
                this.userFollowers = jsonResponse.getInt("followers");
                int following = jsonResponse.getInt("following");

                this.txtName.setText(name);
                this.txtPosts.setText(numberOfPosts + "\nposts");
                this.txtFollowers.setText(this.userFollowers + "\nfollowers");
                this.txtFollowing.setText(following + "\nfollowing");
                Glide.with(getContext()).load(strImage).into(this.imgUser);

                if (this.userId != MainActivity.getUserId()) {
                    if (jsonResponse.getBoolean("is_followed"))
                        this.fabRemoveUser.setVisibility(View.VISIBLE);
                    else
                        this.fabAddUser.setVisibility(View.VISIBLE);
                }

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

        // Follow user
        try {
            JSONObject jsonResponse = new JSONObject(response);
            boolean followed = jsonResponse.getBoolean("followed");

            if (followed) {
                follow();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Unfollow user
        try {
            JSONObject jsonResponse = new JSONObject(response);
            boolean unfollowed = jsonResponse.getBoolean("unfollowed");

            if (unfollowed) {
                unfollow();
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

    @SuppressLint("SetTextI18n")
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

            this.txtPosts.setText(this.posts.size() + "\nposts");
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

    // region 9. Follow and unfollow user

    @SuppressLint("SetTextI18n")
    public void follow() {
        this.fabAddUser.setVisibility(View.INVISIBLE);
        this.fabRemoveUser.setVisibility(View.VISIBLE);
        this.userFollowers++;
        this.txtFollowers.setText(this.userFollowers + "\nfollowers");
        // TODO send notification
    }

    @SuppressLint("SetTextI18n")
    public void unfollow() {
        this.fabRemoveUser.setVisibility(View.INVISIBLE);
        this.fabAddUser.setVisibility(View.VISIBLE);
        this.userFollowers--;
        this.txtFollowers.setText(this.userFollowers + "\nfollowers");
        // TODO delete notification
    }

    // endregion

    // region 10. Getters and Setters

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
