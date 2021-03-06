package com.example.lemon_app.gui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lemon_app.R;
import com.example.lemon_app.constants.Constants;
import com.example.lemon_app.database.DataRequest;
import com.example.lemon_app.database.DatabaseManager;
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
import static com.example.lemon_app.constants.Constants.FOLLOW_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.UNFOLLOW_REQUEST_URL;

public class FollowersFragment extends Fragment implements UserAdapter.OnUserListener, SwipeRefreshLayout.OnRefreshListener, DatabaseManager.FollowersManager.OnResponseListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private MainActivity activity;

    private View view;

    private DatabaseManager.FollowersManager databaseManager;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView txtFollowers;

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

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_followers, container, false);

        if (getArguments() != null) {
            this.userId = getArguments().getInt("user_id");
            this.type = getArguments().getBoolean("type");
        }

        this.databaseManager = new DatabaseManager.FollowersManager(this, getContext());

        this.followers = new ArrayList<>();

        this.databaseManager.followersRequest(this.activity.getLoggedUserId(), this.userId, this.type);

        this.txtFollowers = this.view.findViewById(R.id.txt_followers);
        if (this.type == FOLLOWERS)
            this.txtFollowers.setText("Followers");
        else
            this.txtFollowers.setText("Following");

        this.recyclerView = this.view.findViewById(R.id.recycler_view_followers);
        this.layoutManager = new LinearLayoutManager(this.getContext());
        this.adapter = new UserAdapter(this.followers, this, getContext(), this.activity.getLoggedUserId());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        this.swipeRefreshLayout = this.view.findViewById(R.id.layout_swipe_followers);
        this.swipeRefreshLayout.setOnRefreshListener(this);

        return this.view;
    }

    // endregion

    // region 3. RecyclerView listener

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
        this.databaseManager.followUser(this.activity.getLoggedUserId(), id);
    }

    @Override
    public void onUnfollowListener(int id) {
        this.databaseManager.unfollowUser(this.activity.getLoggedUserId(), id);
    }

    // endregion

    // region 4. Database manager listener

    @Override
    public void onFollowersResponse(ArrayList<User> followers) {
        for (User follower : followers) {
            this.followers.add(follower);
            this.adapter.notifyItemInserted(this.followers.size() - 1);
        }
        this.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onFollowResponse(int id) {
        follow(id);
    }

    @Override
    public void onUnfollowResponse(int id) {
        unfollow(id);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
        this.swipeRefreshLayout.setRefreshing(false);
    }

    // endregion

    // region 5. Follow and unfollow

    public void follow(int id) {
        int ind = getIndexById(id);
        if (ind != -1) {
            this.followers.get(ind).setFollowed(true);
            this.adapter.notifyItemChanged(ind);

            this.activity.refreshFollow(this, id, Constants.REFRESH_TYPE_FOLLOW);
            // TODO send notification
        }
    }

    public void unfollow(int id) {
        int ind = getIndexById(id);
        if (ind != -1) {
            this.followers.get(ind).setFollowed(false);
            this.adapter.notifyItemChanged(ind);

            this.activity.refreshFollow(this, id, Constants.REFRESH_TYPE_UNFOLLOW);
        }
    }

    // endregion

    // region 6. Refresh fragment

    public void refreshFollow(int userId, int type) {
        int ind = getIndexById(userId);
        if (ind != -1) {
            if (type == Constants.REFRESH_TYPE_FOLLOW) {
                this.followers.get(ind).setFollowed(true);
                this.adapter.notifyItemChanged(ind);
            } else if (type == Constants.REFRESH_TYPE_UNFOLLOW) {
                this.followers.get(ind).setFollowed(false);
                this.adapter.notifyItemChanged(ind);
            }
        }
    }

    @Override
    public void onRefresh() {
        this.swipeRefreshLayout.setRefreshing(true);
        this.adapter.notifyItemRangeRemoved(0, this.followers.size());
        this.followers.clear();

        this.databaseManager.followersRequest(this.activity.getLoggedUserId(), this.userId, this.type);
    }

    // endregion

    // region 7. Getters and Setters

    private int getIndexById(int id) {
        for (int i = 0; i < this.followers.size(); i++) {
            if (this.followers.get(i).getId() == id)
                return i;
        }
        return -1;
    }

    // endregion


}