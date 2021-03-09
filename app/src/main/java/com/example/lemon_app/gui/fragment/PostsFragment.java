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

import com.android.volley.VolleyError;
import com.example.lemon_app.R;
import com.example.lemon_app.constants.Constants;
import com.example.lemon_app.database.DatabaseManager;
import com.example.lemon_app.gui.activity.CreatePostActivity;
import com.example.lemon_app.gui.activity.MainActivity;
import com.example.lemon_app.gui.recyclerview.PostAdapter;
import com.example.lemon_app.model.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PostsFragment extends Fragment implements PostAdapter.OnPostListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, DatabaseManager.PostsManager.OnResponseListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private MainActivity activity;

    private View view;

    private DatabaseManager.PostsManager databaseManager;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton fabAddPost;

    private ArrayList<Post> posts;
    private int postId;

    // endregion

    // region 2. Lifecycle and Constructor

    public PostsFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_posts, container, false);

        this.databaseManager = new DatabaseManager.PostsManager(this, getContext());

        this.fabAddPost = this.view.findViewById(R.id.fab_add_post);
        this.fabAddPost.setOnClickListener(this);

        this.posts = new ArrayList<>();

        if (getArguments() != null) {
            this.postId = getArguments().getInt("post_id");
            this.databaseManager.postRequest(this.postId, this.activity.getLoggedUserId());
            this.fabAddPost.setVisibility(View.GONE);
        } else {
            this.databaseManager.postsRequest(this.activity.getLoggedUserId());
            this.postId = 0;
        }

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

    // region 5. Database manager listener

    @Override
    public void onPostsResponse(ArrayList<Post> posts) {
        for (Post post : posts) {
            this.posts.add(post);
            this.adapter.notifyItemInserted(this.posts.size() - 1);
        }
        this.swipeRefreshLayout.setRefreshing(false);
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
            if (this.postId != 0){
                this.activity.removeFromFragments();
            }
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

            this.activity.refreshLike(this, postId, Constants.REFRESH_TYPE_LIKE);

            this.databaseManager.sendNotificationLike(this.activity.getLoggedUserId(), postId);
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

            this.activity.refreshLike(this, postId, Constants.REFRESH_TYPE_UNLIKE);

            this.databaseManager.deleteNotificationLike(this.activity.getLoggedUserId(), postId);
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
                this.adapter.notifyItemChanged(ind);
            } else if (type == Constants.REFRESH_TYPE_UNLIKE) {
                Post post = this.posts.get(ind);
                post.setLiked(false);
                post.decreaseLikes();
                this.posts.set(ind, post);
                this.adapter.notifyItemChanged(ind);
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
        }
    }

    @Override
    public void onRefresh() {
        this.swipeRefreshLayout.setRefreshing(true);
        this.adapter.notifyItemRangeRemoved(0, this.posts.size());
        this.posts.clear();

        if (this.postId != 0) {
            this.databaseManager.postRequest(this.postId, this.activity.getLoggedUserId());
        } else {
            this.databaseManager.postsRequest(this.activity.getLoggedUserId());
        }
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
