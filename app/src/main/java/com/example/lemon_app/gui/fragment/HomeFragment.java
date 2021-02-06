package com.example.lemon_app.gui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;
import com.example.lemon_app.R;
import com.example.lemon_app.gui.activity.MainActivity;
import com.example.lemon_app.gui.recyclerview.PostAdapter;
import com.example.lemon_app.logic.database.DataRequest;
import com.example.lemon_app.logic.listener.HomeFragmentListener;
import com.example.lemon_app.model.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    // region 0. Constants

    // TODO Change the URL
    private static final String POSTS_REQUEST_URL = "http://192.168.1.4/lemon_app/posts.php";

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

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(MainActivity.getUserId()));
        DataRequest dataRequest = new DataRequest(params, POSTS_REQUEST_URL, this.listener, this.listener);
        Volley.newRequestQueue(getContext()).add(dataRequest);

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
