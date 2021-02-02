package com.example.lemon_app.gui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lemon_app.R;
import com.example.lemon_app.gui.activity.PostActivity;
import com.example.lemon_app.gui.recyclerview.PostAdapter;
import com.example.lemon_app.logic.listener.HomeFragmentListener;
import com.example.lemon_app.model.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    // region 0. Constants

    // TODO Change the URL
    private static final String POSTS_URL = "http://192.168.1.3/lemon_app/posts.php";

    // endregion

    // region 1. Decl and Init

    private View view;
    private HomeFragmentListener listener;

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

        this.listener = new HomeFragmentListener(this);

        this.fabAddPost = this.view.findViewById(R.id.fab_add_post);
        this.fabAddPost.setOnClickListener(this.listener);

        this.posts = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, POSTS_URL, this.listener, this.listener);
        Volley.newRequestQueue(getContext()).add(stringRequest);

        this.recyclerView = this.view.findViewById(R.id.recycler_view_post);
        this.recyclerView.setHasFixedSize(true);
        this.layoutManager = new LinearLayoutManager(this.getContext());
        this.adapter = new PostAdapter(this.posts, this.listener, getContext());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        return this.view;
    }

    // endregion

    // region 3. Getters and Setters

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;

        this.adapter = new PostAdapter(this.posts, this.listener, getContext());
        this.recyclerView.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();
    }

    // endregion

}
