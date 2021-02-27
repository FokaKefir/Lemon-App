package com.example.lemon_app.gui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lemon_app.R;
import com.example.lemon_app.constants.Constants;
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

import static com.example.lemon_app.constants.Constants.FOLLOW_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.SEARCH_USERS_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.UNFOLLOW_REQUEST_URL;

public class SearchFragment extends Fragment implements UserAdapter.OnUserListener, View.OnClickListener, Response.ErrorListener, Response.Listener<String> {

    // region 1. Decl and Init

    private MainActivity activity;

    private View view;

    private EditText txtName;

    private Button btnSearch;

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<User> users;

    // endregion

    // region 2. Lifecycle and Constructor

    public SearchFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_search, container, false);

        this.users = new ArrayList<>();

        this.txtName = this.view.findViewById(R.id.txt_search_name);
        this.btnSearch = this.view.findViewById(R.id.btn_search);

        this.btnSearch.setOnClickListener(this);

        this.recyclerView = this.view.findViewById(R.id.recycler_view_users);
        this.layoutManager = new LinearLayoutManager(this.getContext());
        this.adapter = new UserAdapter(this.users, this, getContext(), this.activity.getLoggedUserId());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        return this.view;
    }

    // endregion

    // region 3. Button listener

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_search) {
            this.adapter.notifyItemRangeRemoved(0, this.users.size());
            this.users.clear();

            String name = this.txtName.getText().toString().trim();
            Map<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("id", String.valueOf(this.activity.getLoggedUserId()));
            DataRequest dataRequest = new DataRequest(params, SEARCH_USERS_REQUEST_URL, this, this);
            Volley.newRequestQueue(getContext()).add(dataRequest);
        }
    }

    // endregion

    // region 4. RecyclerView listener

    @Override
    public void onUserListener(int id) {
        Fragment userFragment = new UserFragment(this.activity);
        Bundle args = new Bundle();
        args.putInt("user_id", id);
        userFragment.setArguments(args);
        this.activity.addToFragments(userFragment);
    }

    @Override
    public void onFollowListener(int id) {
        Map<String, String> params = new HashMap<>();
        params.put("follower_id", String.valueOf(this.activity.getLoggedUserId()));
        params.put("following_id", String.valueOf(id));
        DataRequest dataRequest = new DataRequest(params, FOLLOW_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequest);
    }

    @Override
    public void onUnfollowListener(int id) {
        Map<String, String> params = new HashMap<>();
        params.put("follower_id", String.valueOf(this.activity.getLoggedUserId()));
        params.put("following_id", String.valueOf(id));
        DataRequest dataRequest = new DataRequest(params, UNFOLLOW_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequest);
    }

    // endregion

    // region 5. Load data from php

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
                this.users.add(follower);
                this.adapter.notifyItemInserted(this.users.size() - 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Follow user
        try {
            JSONObject jsonResponse = new JSONObject(response);
            boolean followed = jsonResponse.getBoolean("followed");

            if (followed) {
                int id = jsonResponse.getInt("id");
                follow(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Unfollow user
        try {
            JSONObject jsonResponse = new JSONObject(response);
            boolean unfollowed = jsonResponse.getBoolean("unfollowed");

            if (unfollowed) {
                int id = jsonResponse.getInt("id");
                unfollow(id);
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

    // region 6. Follow and unfollow

    public void follow(int id) {
        int ind = getIndexById(id);
        if (ind != -1) {
            this.users.get(ind).setFollowed(true);
            this.adapter.notifyItemChanged(ind);

            this.activity.refreshFollow(this, id, Constants.REFRESH_TYPE_FOLLOW);
            // TODO send notification
        }
    }

    public void unfollow(int id) {
        int ind = getIndexById(id);
        if (ind != -1) {
            this.users.get(ind).setFollowed(false);
            this.adapter.notifyItemChanged(ind);

            this.activity.refreshFollow(this, id, Constants.REFRESH_TYPE_UNFOLLOW);
        }
    }

    // endregion

    // region 7. Refresh fragment

    public void refreshFollow(int userId, int type) {
        int ind = getIndexById(userId);
        if (ind != -1) {
            if (type == Constants.REFRESH_TYPE_FOLLOW) {
                this.users.get(ind).setFollowed(true);
                this.adapter.notifyItemChanged(ind);
            } else if (type == Constants.REFRESH_TYPE_UNFOLLOW) {
                this.users.get(ind).setFollowed(false);
                this.adapter.notifyItemChanged(ind);
            }
        }
    }

    // endregion

    // region 8. Getters and Setters

    private int getIndexById(int id) {
        for (int i = 0; i < this.users.size(); i++) {
            if (this.users.get(i).getId() == id)
                return i;
        }
        return -1;
    }

    // endregion

}