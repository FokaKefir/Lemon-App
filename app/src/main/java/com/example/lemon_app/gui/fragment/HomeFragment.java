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

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    // region 0. Constants

    private static final String POSTS_URL = "http://192.168.1.3/lemon_app/api.php";

    // endregion

    // region 1. Decl and Init

    private View view;
    private HomeFragmentListener listener;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Post> posts;

    // endregion

    // region 2. Lifecycle

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_home, container, false);

        this.listener = new HomeFragmentListener(this);

        loadPosts();

        this.recyclerView = this.view.findViewById(R.id.recycler_view_post);
        this.recyclerView.setHasFixedSize(true);
        this.layoutManager = new LinearLayoutManager(this.getContext());
        this.adapter = new PostAdapter(this.posts, this.listener, getContext());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        return this.view;
    }

    // endregion

    // region 3. Loading posts

    private void loadPosts() {
        this.posts = new ArrayList<>();
        this.posts.add(new Post(10, String.valueOf(R.drawable.kep), "Jani", "2020. 12. 31.", "Szep kep", 1, 2));
        this.posts.add(new Post(20, String.valueOf(R.drawable.ic_baseline_notifications_24), "Tamas", "2000. 8. 2.", "Sokat dolgoztam vele", 0, 0));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, POSTS_URL, this.listener, this.listener);
        Volley.newRequestQueue(getContext()).add(stringRequest);

    }

    // endregion

    // region 4. Getters and Setters

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;

        this.adapter = new PostAdapter(this.posts, this.listener, getContext());
        this.recyclerView.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();
    }

    // endregion

}
