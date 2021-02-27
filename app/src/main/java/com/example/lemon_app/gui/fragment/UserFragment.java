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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.lemon_app.R;
import com.example.lemon_app.constants.Constants;
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

import static com.example.lemon_app.constants.Constants.FOLLOWERS;
import static com.example.lemon_app.constants.Constants.FOLLOWING;
import static com.example.lemon_app.constants.Constants.FOLLOW_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.UNFOLLOW_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.USER_REQUEST_URL;

public class UserFragment extends PostsFragment implements Response.ErrorListener, Response.Listener<String>, View.OnClickListener, PostAdapter.OnPostListener, SwipeRefreshLayout.OnRefreshListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private MainActivity activity;

    private View view;

    private SwipeRefreshLayout swipeRefreshLayout;

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

    // region 2. Lifecycle and Constructor

    public UserFragment(MainActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_user, container, false);

        if (getArguments() != null) {
            this.userId = getArguments().getInt("user_id");
        } else {
            this.userId = this.activity.getLoggedUserId();
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

        if (this.userId != this.activity.getLoggedUserId())
            this.fabSearchUser.setVisibility(View.GONE);

        Map<String, String> params = new HashMap<>();
        params.put("logged_id", String.valueOf(this.activity.getLoggedUserId()));
        params.put("id", String.valueOf(this.userId));
        DataRequest dataRequestUser = new DataRequest(params, USER_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequestUser);

        this.recyclerView = this.view.findViewById(R.id.recycler_view_user_posts);
        this.layoutManager = new LinearLayoutManager(this.getContext());
        this.adapter = new PostAdapter(this.posts, this, getContext(), this.activity.getLoggedUserId());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        this.swipeRefreshLayout = this.view.findViewById(R.id.layout_swipe_user);
        this.swipeRefreshLayout.setOnRefreshListener(this);

        return this.view;
    }

    // endregion

    // region 3. RecyclerView listeners

    @Override
    public void onCommentListener(int id) {
        Fragment commentsFragment = new CommentsFragment(this.activity);
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putInt("author_id", getPostById(id).getAuthorId());
        commentsFragment.setArguments(args);
        this.activity.addToFragments(commentsFragment);
    }

    // endregion

    // region 4. Button and text listener

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txt_user_followers) {
            Fragment followersFragment = new FollowersFragment(this.activity);
            Bundle args = new Bundle();
            args.putInt("user_id", this.userId);
            args.putBoolean("type", FOLLOWERS);
            followersFragment.setArguments(args);
            this.activity.addToFragments(followersFragment);
        } else if (view.getId() == R.id.txt_user_following) {
            Fragment followingFragment = new FollowersFragment(this.activity);
            Bundle args = new Bundle();
            args.putInt("user_id", this.userId);
            args.putBoolean("type", FOLLOWING);
            followingFragment.setArguments(args);
            this.activity.addToFragments(followingFragment);
        } else if (view.getId() == R.id.fab_add_user) {
            Map<String, String> params = new HashMap<>();
            params.put("follower_id", String.valueOf(this.activity.getLoggedUserId()));
            params.put("following_id", String.valueOf(this.userId));
            DataRequest dataRequest = new DataRequest(params, FOLLOW_REQUEST_URL, this, this);
            Volley.newRequestQueue(getContext()).add(dataRequest);
        } else if (view.getId() == R.id.fab_remove_user) {
            Map<String, String> params = new HashMap<>();
            params.put("follower_id", String.valueOf(this.activity.getLoggedUserId()));
            params.put("following_id", String.valueOf(this.userId));
            DataRequest dataRequest = new DataRequest(params, UNFOLLOW_REQUEST_URL, this, this);
            Volley.newRequestQueue(getContext()).add(dataRequest);
        } else if (view.getId() == R.id.fab_search_user){
            Fragment searchFragment = new SearchFragment(this.activity);
            this.activity.addToFragments(searchFragment);
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

                if (this.userId != this.activity.getLoggedUserId()) {
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
                this.swipeRefreshLayout.setRefreshing(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            this.swipeRefreshLayout.setRefreshing(false);
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

            this.txtPosts.setText(this.posts.size() + "\nposts");
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
            this.adapter.notifyItemChanged(ind);
            //this.adapter.onBindViewHolder(this.adapter.getMyHolder(ind), ind);

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
            this.adapter.notifyItemChanged(ind);
            //this.adapter.onBindViewHolder(this.adapter.getMyHolder(ind), ind);

            this.activity.refreshLike(this, postId, Constants.REFRESH_TYPE_UNLIKE);
        }
    }

    // endregion

    // region 8. Follow and unfollow user

    @SuppressLint("SetTextI18n")
    public void follow() {
        this.fabAddUser.setVisibility(View.INVISIBLE);
        this.fabRemoveUser.setVisibility(View.VISIBLE);
        this.userFollowers++;
        this.txtFollowers.setText(this.userFollowers + "\nfollowers");

        this.activity.refreshFollow(this, this.userId, Constants.REFRESH_TYPE_FOLLOW);
        // TODO send notification
    }

    @SuppressLint("SetTextI18n")
    public void unfollow() {
        this.fabRemoveUser.setVisibility(View.INVISIBLE);
        this.fabAddUser.setVisibility(View.VISIBLE);
        this.userFollowers--;
        this.txtFollowers.setText(this.userFollowers + "\nfollowers");

        this.activity.refreshFollow(this, this.userId, Constants.REFRESH_TYPE_UNFOLLOW);
    }

    // endregion

    // region 9. Refresh fragment

    @SuppressLint("SetTextI18n")
    public void refreshFollow(int userId, int type) {
        if (this.userId == userId) {
            if (type == Constants.REFRESH_TYPE_FOLLOW) {
                this.fabAddUser.setVisibility(View.INVISIBLE);
                this.fabRemoveUser.setVisibility(View.VISIBLE);
                this.userFollowers++;
                this.txtFollowers.setText(this.userFollowers + "\nfollowers");
            } else if (type == Constants.REFRESH_TYPE_UNFOLLOW) {
                this.fabRemoveUser.setVisibility(View.INVISIBLE);
                this.fabAddUser.setVisibility(View.VISIBLE);
                this.userFollowers--;
                this.txtFollowers.setText(this.userFollowers + "\nfollowers");
            }
        }
    }

    public void refreshLike(int postId, int type) {
        int ind = getIndById(postId);
        if (ind != -1) {
            if (type == Constants.REFRESH_TYPE_LIKE) {
                Post post = this.posts.get(ind);
                post.setLiked(true);
                post.increaseLikes();
                this.posts.set(ind, post);
                this.adapter.notifyItemChanged(ind);
                //this.adapter.onBindViewHolder(this.adapter.getMyHolder(ind), ind);

            } else if (type == Constants.REFRESH_TYPE_UNLIKE) {
                Post post = this.posts.get(ind);
                post.setLiked(false);
                post.decreaseLikes();
                this.posts.set(ind, post);
                this.adapter.notifyItemChanged(ind);
                //this.adapter.onBindViewHolder(this.adapter.getMyHolder(ind), ind);
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

            this.adapter.notifyItemChanged(ind);
            //this.adapter.onBindViewHolder(this.adapter.getMyHolder(ind), ind);
        }
    }

    @Override
    public void onRefresh() {
        this.swipeRefreshLayout.setRefreshing(true);
        this.adapter.notifyItemRangeRemoved(0, this.posts.size());
        this.posts.clear();

        Map<String, String> params = new HashMap<>();
        params.put("logged_id", String.valueOf(this.activity.getLoggedUserId()));
        params.put("id", String.valueOf(this.userId));
        DataRequest dataRequestUser = new DataRequest(params, USER_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequestUser);
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
