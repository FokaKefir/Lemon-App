package com.example.lemon_app.gui.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lemon_app.R;
import com.example.lemon_app.model.Notification;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    // region 1. Decl and Init

    private ArrayList<Notification> notifications;
    private OnNotificationListener onNotificationListener;
    private Context context;

    // endregion

    // region 2. Constructor

    public NotificationAdapter(ArrayList<Notification> notifications, OnNotificationListener onNotificationListener, Context context) {
        this.notifications = notifications;
        this.onNotificationListener = onNotificationListener;
        this.context = context;
    }

    // endregion

    // region 3. Adapter

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_notification, parent, false);
        NotificationViewHolder viewHolder = new NotificationViewHolder(v, this.onNotificationListener);

        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification currentNotification = this.notifications.get(position);

        holder.id = currentNotification.getId();
        holder.userId = currentNotification.getUserId();
        holder.type = currentNotification.getType();
        holder.txtUserName.setText(currentNotification.getUsername());

        switch (currentNotification.getType()) {
            case Notification.TYPE_FOLLOW:
                holder.txtMessage.setText("started following you.");
                break;

            case Notification.TYPE_LIKE:
                holder.txtMessage.setText("liked your post.");
                holder.postId = currentNotification.getPostId();
                break;

            case Notification.TYPE_COMMENT:
                holder.txtMessage.setText("commented to your post.");
                holder.postId = currentNotification.getPostId();
                break;
        }

        if (!currentNotification.isSeen()) {
            holder.layout.setBackgroundResource(R.color.primaryColorVariant);
        } else {
            holder.layout.setBackgroundResource(R.color.white);
        }

        Glide.with(this.context)
                .load(currentNotification.getImage())
                .into(holder.imgUser);
    }

    @Override
    public int getItemCount() {
        return this.notifications.size();
    }

    // endregion

    // region 4. Holder class

    public static class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public int id;
        public int userId;
        public int postId;
        public int type;
        public ImageView imgUser;
        public TextView txtUserName;
        public TextView txtMessage;
        public RelativeLayout layout;

        private View itemView;

        private OnNotificationListener onNotificationListener;

        public NotificationViewHolder(@NonNull View itemView, OnNotificationListener onNotificationListener) {
            super(itemView);

            this.itemView = itemView;

            this.postId = -1;

            this.onNotificationListener = onNotificationListener;

            this.imgUser = itemView.findViewById(R.id.img_example_notification);
            this.txtUserName = itemView.findViewById(R.id.txt_example_notification_username);
            this.txtMessage = itemView.findViewById(R.id.txt_example_notification_message);
            this.layout = itemView.findViewById(R.id.layout_example_notification);

            this.txtUserName.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == this.itemView) {
                if (this.postId != -1) {
                    this.onNotificationListener.onPostClick(this.postId);
                } else {
                    this.onNotificationListener.onUserClick(this.userId);
                }
                this.onNotificationListener.onClick(this.id);
            } else if (view.getId() == R.id.txt_example_notification_username) {
                this.onNotificationListener.onUserClick(this.userId);
                if (this.type == Notification.TYPE_FOLLOW)
                    this.onNotificationListener.onClick(id);
            }
        }
    }

    // endregion

    // region 5. Listener interface

    public interface OnNotificationListener {
        void onUserClick(int userId);
        void onPostClick(int postId);
        void onClick(int notificationId);
    }

    // endregion

}