package com.example.lemon_app.gui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lemon_app.R;
import com.example.lemon_app.gui.activity.MainActivity;

public class NotificationsFragment extends Fragment{

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private MainActivity activity;

    private View view;

    private SwipeRefreshLayout swipeRefreshLayout;

    // endregion

    // region 2. Lifecycle and Constructor

    public NotificationsFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_notifications, container, false);

        return this.view;
    }

    // endregion


}
