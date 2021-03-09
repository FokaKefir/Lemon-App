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

import com.android.volley.VolleyError;
import com.example.lemon_app.R;
import com.example.lemon_app.constants.Constants;
import com.example.lemon_app.database.DatabaseManager;
import com.example.lemon_app.gui.activity.MainActivity;
import com.example.lemon_app.gui.recyclerview.UserAdapter;
import com.example.lemon_app.model.User;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements UserAdapter.OnUserListener, View.OnClickListener, DatabaseManager.SearchManager.OnResponseListener {

    // region 1. Decl and Init

    private MainActivity activity;

    private View view;

    private DatabaseManager.SearchManager databaseManager;

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

        this.databaseManager = new DatabaseManager.SearchManager(this, getContext());

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
            this.databaseManager.search(name, this.activity.getLoggedUserId());
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
        this.databaseManager.followUser(this.activity.getLoggedUserId(), id);
    }

    @Override
    public void onUnfollowListener(int id) {
        this.databaseManager.unfollowUser(this.activity.getLoggedUserId(), id);
    }

    // endregion

    // region 5. Database manager listener

    @Override
    public void onUsersResponse(ArrayList<User> users) {
        for (User user : users) {
            this.users.add(user);
            this.adapter.notifyItemInserted(this.users.size() - 1);
        }
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
    }

    // endregion

    // region 6. Follow and unfollow

    public void follow(int id) {
        int ind = getIndexById(id);
        if (ind != -1) {
            this.users.get(ind).setFollowed(true);
            this.adapter.notifyItemChanged(ind);

            this.activity.refreshFollow(this, id, Constants.REFRESH_TYPE_FOLLOW);

            this.databaseManager.sendNotificationFollow(this.activity.getLoggedUserId(), id);
        }
    }

    public void unfollow(int id) {
        int ind = getIndexById(id);
        if (ind != -1) {
            this.users.get(ind).setFollowed(false);
            this.adapter.notifyItemChanged(ind);

            this.activity.refreshFollow(this, id, Constants.REFRESH_TYPE_UNFOLLOW);

            this.databaseManager.deleteNotificationFollow(this.activity.getLoggedUserId(), id);
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