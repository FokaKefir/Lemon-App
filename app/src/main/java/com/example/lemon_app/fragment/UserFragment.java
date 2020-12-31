package com.example.lemon_app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lemon_app.R;

public class UserFragment extends Fragment implements View.OnClickListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private View view;
    private Button button;

    // endregion

    // region 2. Lifecycle

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_user, container, false);
        this.button = this.view.findViewById(R.id.button);
        this.button.setOnClickListener(this);

        return this.view;
    }

    // endregion

    // region 3. Listener

    @Override
    public void onClick(View view) {
        Toast.makeText(getContext(), "Click", Toast.LENGTH_SHORT).show();
        Fragment nextFragment = new TemplateFragment();
        this.getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, nextFragment, "findThisFragment").addToBackStack(null).commit();
    }

    // endregion
}
