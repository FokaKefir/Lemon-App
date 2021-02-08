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
import com.example.lemon_app.gui.activity.PostActivity;
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

import static com.example.lemon_app.constants.Constants.POSTS_REQUEST_URL;

public class HomeFragment extends Fragment implements PostAdapter.OnPostListener, Response.ErrorListener, Response.Listener<String>, View.OnClickListener {

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
        this.view = inflater.inflate(R.layout.fragment_home, container, false);

        this.fabAddPost = this.view.findViewById(R.id.fab_add_post);
        this.fabAddPost.setOnClickListener(this);

        this.posts = new ArrayList<>();

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(MainActivity.getUserId()));
        DataRequest dataRequest = new DataRequest(params, POSTS_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequest);

        this.recyclerView = this.view.findViewById(R.id.recycler_view_post);
        this.recyclerView.setHasFixedSize(true);
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
        Intent intent = new Intent(getActivity(), PostActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    // endregion

    // region 4. Floating button listener

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), CreatePostActivity.class);
        intent.putExtra("id", MainActivity.getUserId());
        this.startActivity(intent);
    }

    // endregion

    // region 5. Loading posts from php

    @Override
    public void onResponse(String response) {
        ArrayList<Post> posts = new ArrayList<>();

        try {
            JSONArray jsonPosts = new JSONArray(response);

            for (int ind = 0; ind < jsonPosts.length(); ind++) {
                JSONObject jsonPost = jsonPosts.getJSONObject(ind);

                int id = jsonPost.getInt("id");
                String image = jsonPost.getString("image");
                String author = jsonPost.getString("author");
                String date = jsonPost.getString("date");
                String description = jsonPost.getString("description");
                int likes = jsonPost.getInt("likes");
                int comments = jsonPost.getInt("comments");

                Post post = new Post(id, image, author, date, description, likes, comments);
                posts.add(post);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setPosts(posts);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    // endregion

    // region 6. Set posts after getting data from php

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;

        this.adapter = new PostAdapter(this.posts, this, getContext());
        this.recyclerView.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();
    }

    // endregion

}
