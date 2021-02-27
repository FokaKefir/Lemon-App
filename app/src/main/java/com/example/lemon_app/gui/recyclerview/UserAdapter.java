package com.example.lemon_app.gui.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lemon_app.R;
import com.example.lemon_app.model.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    // region 1. Decl and Init

    private ArrayList<User> users;
    private OnUserListener onUserListener;
    private Context context;

    private int userId;

    // endregion

    // region 2. Constructor

    public UserAdapter(ArrayList<User> users, OnUserListener onUserListener, Context context, int userId) {
        this.users = users;
        this.onUserListener = onUserListener;
        this.context = context;
        this.userId = userId;
    }


    // endregion

    // region 3. Adapter

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_user, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(v, this.onUserListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User currentUser = users.get(position);

        holder.userId = currentUser.getId();
        holder.txtUserName.setText(currentUser.getName());

        if (holder.userId == this.userId){
            holder.btnFollow.setVisibility(View.GONE);
            holder.btnUnfollow.setVisibility(View.GONE);
        } else if (currentUser.isFollowed()) {
            holder.btnFollow.setVisibility(View.INVISIBLE);
            holder.btnUnfollow.setVisibility(View.VISIBLE);
        } else {
            holder.btnFollow.setVisibility(View.VISIBLE);
            holder.btnUnfollow.setVisibility(View.INVISIBLE);
        }

        Glide.with(this.context)
                .load(currentUser.getImage())
                .into(holder.imgUser);
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

    // endregion

    // region 4. Holder class

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public int userId;
        public ImageView imgUser;
        public TextView txtUserName;
        public Button btnFollow;
        public Button btnUnfollow;
        public LinearLayout layoutUser;

        private OnUserListener onUserListener;

        public UserViewHolder(@NonNull View itemView, OnUserListener onUserListener) {
            super(itemView);

            this.userId = 0;

            this.onUserListener = onUserListener;

            this.imgUser = itemView.findViewById(R.id.img_example_user);
            this.txtUserName = itemView.findViewById(R.id.txt_example_user_name);
            this.btnFollow = itemView.findViewById(R.id.btn_follow);
            this.btnUnfollow = itemView.findViewById(R.id.btn_unfollow);
            this.layoutUser = itemView.findViewById(R.id.layout_example_user);

            this.btnFollow.setOnClickListener(this);
            this.btnUnfollow.setOnClickListener(this);
            this.layoutUser.setOnClickListener(this);
            this.txtUserName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_follow) {
                this.onUserListener.onFollowListener(this.userId);
            } else if (view.getId() == R.id.btn_unfollow) {
                this.onUserListener.onUnfollowListener(this.userId);
            } else if (view.getId() == R.id.layout_example_user || view.getId() == R.id.txt_example_user_name) {
                this.onUserListener.onUserListener(this.userId);
            }
        }
    }

    // endregion

    // region 5. Listener interface

    public interface OnUserListener {
        void onUserListener(int id);
        void onFollowListener(int id);
        void onUnfollowListener(int id);
    }

    // endregion
}
