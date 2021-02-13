package com.example.lemon_app.gui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.lemon_app.R;
import com.example.lemon_app.database.DataRequest;
import com.example.lemon_app.gui.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.lemon_app.constants.Constants.USER_REQUEST_URL;

public class UserFragment extends Fragment implements Response.ErrorListener, Response.Listener<String>, View.OnClickListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private View view;

    private ImageView imgUser;

    private TextView txtName;
    private TextView txtPosts;
    private TextView txtFriends;

    private Button btnFollow;

    private int userId;

    // endregion

    // region 2. Lifecycle and Constructor

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_user, container, false);

        try {
            this.userId = getArguments().getInt("user_id");
        } catch (Exception e) {
            this.userId = MainActivity.getUserId();
        }

        this.imgUser = this.view.findViewById(R.id.img_user);
        this.txtName = this.view.findViewById(R.id.txt_user_name);
        this.txtPosts = this.view.findViewById(R.id.txt_user_posts);
        this.txtFriends = this.view.findViewById(R.id.txt_user_friends);
        this.btnFollow = this.view.findViewById(R.id.btn_follow);

        this.txtPosts.setOnClickListener(this);
        this.txtFriends.setOnClickListener(this);
        this.btnFollow.setOnClickListener(this);

        if (this.userId == MainActivity.getUserId())
            this.btnFollow.setVisibility(View.GONE);

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(this.userId));
        DataRequest dataRequest = new DataRequest(params, USER_REQUEST_URL, this, this);
        Volley.newRequestQueue(getContext()).add(dataRequest);

        return this.view;
    }

    // endregion

    // region 3. Listener

    @Override
    public void onClick(View view) {

    }

    // endregion

    // region 4.

    @SuppressLint("SetTextI18n")
    @Override
    public void onResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            boolean success = jsonResponse.getBoolean("success");

            if (success) {
                String name = jsonResponse.getString("name");
                String strImage = jsonResponse.getString("image");
                int numberOfPosts = jsonResponse.getInt("posts");
                int numberOfFriends = jsonResponse.getInt("friends");

                this.txtName.setText(name);
                this.txtPosts.setText(numberOfPosts + "\nposts");
                this.txtFriends.setText(numberOfFriends + "\nfriends");
                Glide.with(getContext()).load(strImage).into(this.imgUser);

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
