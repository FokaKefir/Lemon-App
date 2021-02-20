package com.example.lemon_app.gui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lemon_app.R;
import com.example.lemon_app.database.DataRequest;
import com.example.lemon_app.gui.activity.MainActivity;
import com.example.lemon_app.gui.recyclerview.UserAdapter;
import com.example.lemon_app.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.lemon_app.constants.Constants.FOLLOWERS;
import static com.example.lemon_app.constants.Constants.FOLLOWERS_REQUEST_URL;

public class FollowersFragment extends Fragment implements UserAdapter.OnUserListener, Response.ErrorListener, Response.Listener<String> {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private MainActivity activity;

    private View view;

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private int userId;
    private boolean type;
    private ArrayList<User> followers;

    // endregion

    // region 2. Lifecycle and Constructor

    public FollowersFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_followers, container, false);

        if (getArguments() != null) {
            this.userId = getArguments().getInt("user_id");
            this.type = getArguments().getBoolean("type");
        }

        this.followers = new ArrayList<>();

        Map<String, String> params = new HashMap<>();
        params.put("logged_id", String.valueOf(this.activity.getUserId()));
        params.put("id", String.valueOf(this.userId));
        params.put("followers", String.valueOf(this.type == FOLLOWERS));
        DataRequest dataRequest = new DataRequest(params, FOLLOWERS_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequest);

        this.recyclerView = this.view.findViewById(R.id.recycler_view_followers);
        this.layoutManager = new LinearLayoutManager(this.getContext());
        this.adapter = new UserAdapter(this.followers, this, getContext(), this.activity.getUserId());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        return this.view;
    }

    // endregion

    // region 3. RecyclerView listener

    @Override
    public void onUserListener(int id) {

    }

    @Override
    public void onFollowListener(int id) {

    }

    @Override
    public void onUnfollowListener(int id) {

    }

    // endregion

    // region 4. Load data from php

    @Override
    public void onResponse(String response) {
        // Get followers
        try {
            JSONArray jsonFollowers = new JSONArray(response);

            for (int ind = 0; ind < jsonFollowers.length(); ind++) {
                JSONObject jsonFollower = jsonFollowers.getJSONObject(ind);

                int id = jsonFollower.getInt("id");
                String name = jsonFollower.getString("name");
                String image = jsonFollower.getString("image");
                boolean followed = jsonFollower.getBoolean("followed");

                User follower = new User(id, image, name, followed);
                this.followers.add(follower);
                this.adapter.notifyItemInserted(this.followers.size() - 1);
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
}