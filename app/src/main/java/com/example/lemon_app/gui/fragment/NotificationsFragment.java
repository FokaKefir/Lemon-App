package com.example.lemon_app.gui.fragment;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lemon_app.R;
import com.example.lemon_app.database.DataRequest;
import com.example.lemon_app.database.DatabaseManager;
import com.example.lemon_app.gui.activity.MainActivity;
import com.example.lemon_app.gui.recyclerview.NotificationAdapter;
import com.example.lemon_app.model.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.lemon_app.constants.Constants.NOTIFICATIONS_REQUEST_URL;

public class NotificationsFragment extends Fragment implements NotificationAdapter.OnNotificationListener, SwipeRefreshLayout.OnRefreshListener, DatabaseManager.NotificationsManager.OnResponseListener {

    // region 1. Decl and Init

    private MainActivity activity;

    private View view;

    private DatabaseManager.NotificationsManager databaseManager;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Notification> notifications;

    // endregion

    // region 2. Lifecycle and Constructor

    public NotificationsFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_notifications, container, false);

        this.databaseManager = new DatabaseManager.NotificationsManager(this, getContext());

        this.notifications = new ArrayList<>();

        this.databaseManager.notificationsRequest(this.activity.getLoggedUserId());

        this.recyclerView = this.view.findViewById(R.id.recycler_view_notifications);
        this.layoutManager = new LinearLayoutManager(this.getContext());
        this.adapter = new NotificationAdapter(this.notifications, this, getContext());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        this.swipeRefreshLayout = this.view.findViewById(R.id.layout_swipe_notifications);
        this.swipeRefreshLayout.setOnRefreshListener(this);

        return this.view;
    }

    // endregion

    // region 3. Notification listener

    @Override
    public void onUserClick(int userId) {
        Toast.makeText(activity, String.valueOf(userId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostClick(int postId) {
        Toast.makeText(activity, String.valueOf(postId), Toast.LENGTH_SHORT).show();
    }

    // endregion

    // region 4. Database manager listener

    @Override
    public void onNotificationsResponse(ArrayList<Notification> notifications) {
        for (Notification notification : notifications) {
            this.notifications.add(notification);
            this.adapter.notifyItemInserted(this.notifications.size() - 1);
        }
        this.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        this.swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    // endregion

    // region 5. Refresh fragment

    @Override
    public void onRefresh() {
        this.swipeRefreshLayout.setRefreshing(true);
        this.adapter.notifyItemRangeRemoved(0, this.notifications.size());
        this.notifications.clear();

        this.databaseManager.notificationsRequest(this.activity.getLoggedUserId());
    }

    // endregion

}
