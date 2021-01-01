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

import com.example.lemon_app.R;
import com.example.lemon_app.gui.activity.PostActivity;
import com.example.lemon_app.gui.recyclerview.PostAdapter;
import com.example.lemon_app.model.Post;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements PostAdapter.OnPostListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private View view;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    // endregion

    // region 2. Lifecycle

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_home, container, false);

        ArrayList<Post> posts = new ArrayList<>();
        posts.add(new Post(R.drawable.kep, "Jani", "2020. 12. 31.", "Szep kep", 1, 2));
        posts.add(new Post(R.drawable.ic_baseline_notifications_24, "Tamas", "2000. 8. 2.", "Sokat dolgoztam vele", 0, 0));
        posts.add(new Post(R.drawable.ic_baseline_notifications_24, "Tamas", "2000. 8. 2.", "Sokat dolgoztam vele", 0, 0));
        posts.add(new Post(R.drawable.ic_baseline_notifications_24, "Tamas", "2000. 8. 2.", "Sokat dolgoztam vele", 0, 0));
        posts.add(new Post(R.drawable.ic_baseline_notifications_24, "Tamas", "2000. 8. 2.", "Sokat dolgoztam vele", 0, 0));
        posts.add(new Post(R.drawable.ic_baseline_notifications_24, "Tamas", "2000. 8. 2.", "Sokat dolgoztam vele", 0, 0));

        this.recyclerView = this.view.findViewById(R.id.recycler_view_post);
        this.recyclerView.setHasFixedSize(true);
        this.layoutManager = new LinearLayoutManager(this.getContext());
        this.adapter = new PostAdapter(posts, this);

        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        return this.view;
    }

    // endregion

    // region 4. Listener

    @Override
    public void onPostListener(int position) {
        Intent intent = new Intent(getActivity(), PostActivity.class);
        intent.putExtra("Position", position);
        startActivity(intent);
    }

    // endregion


}
