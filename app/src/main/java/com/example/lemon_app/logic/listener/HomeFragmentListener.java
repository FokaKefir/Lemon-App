package com.example.lemon_app.logic.listener;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.lemon_app.R;
import com.example.lemon_app.gui.activity.CreatePostActivity;
import com.example.lemon_app.gui.activity.MainActivity;
import com.example.lemon_app.gui.activity.PostActivity;
import com.example.lemon_app.gui.fragment.HomeFragment;
import com.example.lemon_app.gui.recyclerview.PostAdapter;
import com.example.lemon_app.model.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragmentListener implements PostAdapter.OnPostListener, Response.ErrorListener, Response.Listener<String>, View.OnClickListener {

    // region 1. Decl and Init

    private HomeFragment fragment;

    // endregion

    // region 2. Constructor

    public HomeFragmentListener(HomeFragment fragment) {
        this.fragment = fragment;
    }

    // endregion

    // region 3. Post click listener

    @Override
    public void onPostListener(int id) {
        Intent intent = new Intent(this.fragment.getActivity(), PostActivity.class);
        intent.putExtra("id", id);
        this.fragment.startActivity(intent);
    }

    // endregion

    // region 4. Loading posts from php

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

        this.fragment.setPosts(posts);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this.fragment.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }
    
    // endregion
    
    // region 5. Floating button listener

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this.fragment.getActivity(), CreatePostActivity.class);
        intent.putExtra("userID", MainActivity.getUserID());
        this.fragment.startActivity(intent);
    }

    // endregion


}
