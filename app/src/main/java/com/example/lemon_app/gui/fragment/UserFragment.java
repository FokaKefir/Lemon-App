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

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.example.lemon_app.R;
import com.example.lemon_app.constants.Constants;
import com.example.lemon_app.database.DatabaseManager;
import com.example.lemon_app.gui.activity.MainActivity;
import com.example.lemon_app.gui.recyclerview.PostAdapter;
import com.example.lemon_app.model.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;


import static com.example.lemon_app.constants.Constants.FOLLOWERS;
import static com.example.lemon_app.constants.Constants.FOLLOWING;

public class UserFragment extends PostsFragment implements View.OnClickListener, PostAdapter.OnPostListener, SwipeRefreshLayout.OnRefreshListener, DatabaseManager.UserManager.OnResponseListener{

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private MainActivity activity;

    private View view;

    private DatabaseManager.UserManager databaseManager;

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

        this.databaseManager = new DatabaseManager.UserManager(this, getContext());

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

        this.databaseManager.userRequest(this.activity.getLoggedUserId(), this.userId);

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

    @Override
    public void onAuthorListener(int authorId) {
        super.onAuthorListener(authorId);
    }

    @Override
    public void onDeleteListener(int postId) {
        this.databaseManager.deletePost(postId);
    }

    @Override
    public void onLikeListener(int postId) {
        this.databaseManager.likePost(postId, this.activity.getLoggedUserId());
    }

    @Override
    public void onUnlikeListener(int postId) {
        this.databaseManager.unlikePost(postId, this.activity.getLoggedUserId());
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
            this.databaseManager.followUser(this.activity.getLoggedUserId(), this.userId);
        } else if (view.getId() == R.id.fab_remove_user) {
            this.databaseManager.unfollowUser(this.activity.getLoggedUserId(), this.userId);
        } else if (view.getId() == R.id.fab_search_user){
            Fragment searchFragment = new SearchFragment(this.activity);
            this.activity.addToFragments(searchFragment);
        }
    }

    // endregion

    // region 5. Database manager listener

    @SuppressLint("SetTextI18n")
    @Override
    public void onUserResponse(String name, String strImage, int numberOfPosts, int userFollowers, int following, boolean isFollowed, ArrayList<Post> posts) {
        this.userFollowers = userFollowers;
        this.txtName.setText(name);
        this.txtPosts.setText(numberOfPosts + "\nposts");
        this.txtFollowers.setText(this.userFollowers + "\nfollowers");
        this.txtFollowing.setText(following + "\nfollowing");
        Glide.with(getContext()).load(strImage).into(this.imgUser);

        if (this.userId != this.activity.getLoggedUserId()) {
            if (isFollowed)
                this.fabRemoveUser.setVisibility(View.VISIBLE);
            else
                this.fabAddUser.setVisibility(View.VISIBLE);
        }

        for (Post post : posts) {
            this.posts.add(post);
            this.adapter.notifyItemInserted(this.posts.size() - 1);
        }

        this.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onFollowUserResponse() {
        follow();
    }

    @Override
    public void onUnfollowUserResponse() {
        unfollow();
    }

    @Override
    public void onDeletePostResponse(int deleteId) {
        deletePost(deleteId);
    }

    @Override
    public void onLikePostResponse(int postId) {
        likePost(postId);
    }

    @Override
    public void onUnlikePostResponse(int postId) {
        unlikePost(postId);
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

        this.databaseManager.userRequest(this.activity.getLoggedUserId(), this.userId);
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
