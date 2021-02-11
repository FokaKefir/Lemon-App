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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lemon_app.R;
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
import static com.example.lemon_app.constants.Constants.POSTS_REQUEST_URL;

public class PostsFragment extends Fragment implements PostAdapter.OnPostListener, Response.ErrorListener, Response.Listener<String>, View.OnClickListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private View view;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton fabAddPost;

    private ArrayList<Post> posts;

    // endregion

    // region 2. Lifecycle

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_posts, container, false);

        this.fabAddPost = this.view.findViewById(R.id.fab_add_post);
        this.fabAddPost.setOnClickListener(this);

        this.posts = new ArrayList<>();

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(MainActivity.getUserId()));
        DataRequest dataRequest = new DataRequest(params, POSTS_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequest);

        this.recyclerView = this.view.findViewById(R.id.recycler_view_posts);
        this.layoutManager = new LinearLayoutManager(this.getContext());
        this.adapter = new PostAdapter(this.posts, this, getContext());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        return this.view;
    }

    // endregion

    // region 3. Post click listener

    @Override
    public void onPostListener(int id) {
        Fragment nextFragment = new CommentsFragment();
        Bundle data = new Bundle();
        data.putInt("id", id);
        nextFragment.setArguments(data);
        this.getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, nextFragment).addToBackStack(null).commit();
    }

    @Override
    public void onAuthorListener(int authorId) {
        Toast.makeText(getContext(), String.valueOf(authorId), Toast.LENGTH_SHORT).show();
        // TODO open author user page
    }

    @Override
    public void onDeleteListener(int postId) {
        deletePost(postId);
    }

    // endregion

    // region 4. Fab listener

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add_post) {
            Intent intent = new Intent(getActivity(), CreatePostActivity.class);
            intent.putExtra("id", MainActivity.getUserId());
            this.startActivity(intent);
        }
    }

    // endregion

    // region 5. Loading posts from php

    @Override
    public void onResponse(String response) {

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

                Post post = new Post(id, authorId, image, author, date, description, likes, comments);
                this.posts.add(post);
                this.adapter.notifyItemInserted(this.posts.size() - 1);
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
                for (int i = 0; i < this.posts.size(); i++) {
                    if (this.posts.get(i).getId() == deleteId) {
                        ind = i;
                        break;
                    }
                }
                if (ind != -1) {
                    this.posts.remove(ind);
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

    // region 6. Delete post
    private void deletePost(int postId) {
        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(postId));
        DataRequest dataRequest = new DataRequest(params, DELETE_POST_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequest);
    }
    // endregion

}
