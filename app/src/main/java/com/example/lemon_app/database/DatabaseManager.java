package com.example.lemon_app.database;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.lemon_app.model.Comment;
import com.example.lemon_app.model.Notification;
import com.example.lemon_app.model.Post;
import com.example.lemon_app.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.example.lemon_app.constants.Constants.COMMENTS_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.DELETE_COMMENT_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.DELETE_POST_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.FOLLOWERS;
import static com.example.lemon_app.constants.Constants.FOLLOWERS_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.FOLLOW_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.LIKE_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.LOGIN_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.NOTIFICATIONS_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.POSTS_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.REGISTER_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.SEARCH_USERS_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.UNFOLLOW_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.UNLIKE_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.UPLOAD_COMMENT_REQUEST_URL;
import static com.example.lemon_app.constants.Constants.USER_REQUEST_URL;

public class DatabaseManager {

    // region 1. Login manager

    public static class LoginManager implements Response.Listener<String>, Response.ErrorListener {
        private OnResponseListener onResponseListener;
        private Context context;

        public LoginManager(OnResponseListener onResponseListener, Context context) {
            this.onResponseListener = onResponseListener;
            this.context = context;
        }

        public void login(String name, String password) {
            Map<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("password", password);
            DataRequest dataRequest = new DataRequest(params, LOGIN_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");

                if (success){
                    int userId = jsonResponse.getInt("id");
                    this.onResponseListener.onSuccessfulLoginResponse(userId);

                } else {
                    String errorMessage = jsonResponse.getString("message");
                    this.onResponseListener.onFailedLoginResponse(errorMessage);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            this.onResponseListener.onErrorResponse(error);
        }

        public interface OnResponseListener {
            void onSuccessfulLoginResponse(int userId);
            void onFailedLoginResponse(String errorMessage);
            void onErrorResponse(VolleyError error);
        }
    }

    // endregion

    // region 2. Register manager

    public static class RegisterManager implements  Response.Listener<String>, Response.ErrorListener {

        private OnResponseListener onResponseListener;
        private Context context;

        public RegisterManager(OnResponseListener onResponseListener, Context context) {
            this.onResponseListener = onResponseListener;
            this.context = context;
        }

        public void register(String name, String password, String email, String image) {
            Map<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("password", password);
            params.put("email", email);
            params.put("image", image);
            DataRequest dataRequest = new DataRequest(params, REGISTER_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");

                if (success) {
                    this.onResponseListener.onSuccessfulRegisterResponse();
                } else {
                    String errorMessage = jsonResponse.getString("message");
                    this.onResponseListener.onFailedRegisterResponse(errorMessage);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onErrorResponse(VolleyError error) {
            this.onResponseListener.onErrorResponse(error);
        }

        public interface OnResponseListener{
            void onSuccessfulRegisterResponse();
            void onFailedRegisterResponse(String errorMessage);
            void onErrorResponse(VolleyError error);
        }
    }

    // endregion

    // region 3. Posts manager

    public static class PostsManager implements Response.Listener<String>, Response.ErrorListener {

        private OnResponseListener onResponseListener;
        private Context context;

        public PostsManager(OnResponseListener onResponseListener, Context context) {
            this.onResponseListener = onResponseListener;
            this.context = context;
        }

        public void postsRequest(int userId) {
            Map<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(userId));
            DataRequest dataRequest = new DataRequest(params, POSTS_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        public void deletePost(int postId) {
            Map<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(postId));
            DataRequest dataRequest = new DataRequest(params, DELETE_POST_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        public void likePost(int postId, int userId) {
            Map<String, String> params = new HashMap<>();
            params.put("post_id", String.valueOf(postId));
            params.put("user_id", String.valueOf(userId));
            DataRequest dataRequest = new DataRequest(params, LIKE_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        public void unlikePost(int postId, int userId) {
            Map<String, String> params = new HashMap<>();
            params.put("post_id", String.valueOf(postId));
            params.put("user_id", String.valueOf(userId));
            DataRequest dataRequest = new DataRequest(params, UNLIKE_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        @Override
        public void onResponse(String response) {
            // Get posts
            try {
                JSONArray jsonPosts = new JSONArray(response);
                ArrayList<Post> posts = new ArrayList<>();

                for (int ind = 0; ind < jsonPosts.length(); ind++) {
                    JSONObject jsonPost = jsonPosts.getJSONObject(ind);

                    int id = jsonPost.getInt("id");
                    int authorId = jsonPost.getInt("author_id");
                    String image = jsonPost.getString("image");
                    String author = jsonPost.getString("author");
                    String date = jsonPost.getString("date");
                    String description = jsonPost.getString("description");
                    int likes = jsonPost.getInt("likes");
                    int comments = jsonPost.getInt("comments");
                    boolean liked = jsonPost.getBoolean("liked");

                    Post post = new Post(id, authorId, image, author, date, description, likes, comments, liked);
                    posts.add(post);
                }

                this.onResponseListener.onPostsResponse(posts);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Delete post
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean deleted = jsonResponse.getBoolean("deleted");

                if (deleted) {
                    int deleteId = jsonResponse.getInt("id");
                    this.onResponseListener.onDeletePostResponse(deleteId);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Like post
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean liked = jsonResponse.getBoolean("liked");

                if (liked) {
                    int postId = jsonResponse.getInt("post_id");
                    this.onResponseListener.onLikePostResponse(postId);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Unlike
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean unliked = jsonResponse.getBoolean("unliked");

                if (unliked) {
                    int postId = jsonResponse.getInt("post_id");
                    this.onResponseListener.onUnlikePostResponse(postId);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onErrorResponse(VolleyError error) {
            this.onResponseListener.onErrorResponse(error);
        }

        public interface OnResponseListener {
            void onPostsResponse(ArrayList<Post> posts);
            void onDeletePostResponse(int deleteId);
            void onLikePostResponse(int postId);
            void onUnlikePostResponse(int postId);
            void onErrorResponse(VolleyError error);
        }
    }

    // endregion

    // region 4. Comments manager

    public static class CommentsManager implements Response.Listener<String>, Response.ErrorListener {

        private OnResponseListener onResponseListener;
        private Context context;
        private int postId;

        public CommentsManager(OnResponseListener onResponseListener, Context context, int postId) {
            this.onResponseListener = onResponseListener;
            this.context = context;
            this.postId = postId;
        }

        public void commentsRequest() {
            Map<String, String> params = new HashMap<>();
            params.put("post_id", String.valueOf(this.postId));
            DataRequest dataRequest = new DataRequest(params, COMMENTS_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        public void sendComment(int userId, String text) {
            Map<String, String> params = new HashMap<>();
            params.put("post_id", String.valueOf(this.postId));
            params.put("user_id", String.valueOf(userId));
            params.put("text", text);
            DataRequest dataRequest = new DataRequest(params, UPLOAD_COMMENT_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        public void deleteComment(int commentId) {
            Map<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(commentId));
            params.put("post_id", String.valueOf(this.postId));
            DataRequest dataRequest = new DataRequest(params, DELETE_COMMENT_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }
        
        @Override
        public void onResponse(String response) {
            // Get comments
            try {
                JSONArray jsonComments = new JSONArray(response);

                ArrayList<Comment> comments = new ArrayList<>();

                for (int ind = 0; ind < jsonComments.length(); ind++) {
                    JSONObject jsonComment= jsonComments.getJSONObject(ind);

                    int id = jsonComment.getInt("id");
                    int authorId = jsonComment.getInt("author_id");
                    String author = jsonComment.getString("author");
                    String text = jsonComment.getString("text");

                    Comment comment = new Comment(id, this.postId, authorId, author, text);
                    comments.add(comment);
                }

                this.onResponseListener.onCommentsResponse(comments);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Insert comment
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean uploaded = jsonResponse.getBoolean("uploaded");

                if (uploaded) {
                    int id = jsonResponse.getInt("id");
                    this.onResponseListener.onInsertCommentResponse(id);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

            // Delete comment
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean deleted = jsonResponse.getBoolean("deleted");

                if (deleted) {
                    int deleteId = jsonResponse.getInt("id");
                    this.onResponseListener.onDeleteCommentResponse(deleteId);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onErrorResponse(VolleyError error) {
            this.onResponseListener.onErrorResponse(error);
        }

        public interface OnResponseListener {
            void onCommentsResponse(ArrayList<Comment> comments);
            void onInsertCommentResponse(int id);
            void onDeleteCommentResponse(int deleteId);
            void onErrorResponse(VolleyError error);
        }
    }

    // endregion

    // region 5. User manager

    public static class UserManager implements Response.Listener<String>, Response.ErrorListener {

        private OnResponseListener onResponseListener;
        private Context context;

        public UserManager(OnResponseListener onResponseListener, Context context) {
            this.onResponseListener = onResponseListener;
            this.context = context;
        }
        
        public void userRequest(int loggedUserId, int userId) {
            Map<String, String> params = new HashMap<>();
            params.put("logged_id", String.valueOf(loggedUserId));
            params.put("id", String.valueOf(userId));
            DataRequest dataRequestUser = new DataRequest(params, USER_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequestUser);
        }
        
        public void followUser(int followerId, int followingId) {
            Map<String, String> params = new HashMap<>();
            params.put("follower_id", String.valueOf(followerId));
            params.put("following_id", String.valueOf(followingId));
            DataRequest dataRequest = new DataRequest(params, FOLLOW_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        public void unfollowUser(int followerId, int followingId) {
            Map<String, String> params = new HashMap<>();
            params.put("follower_id", String.valueOf(followerId));
            params.put("following_id", String.valueOf(followingId));
            DataRequest dataRequest = new DataRequest(params, UNFOLLOW_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        public void deletePost(int postId) {
            Map<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(postId));
            DataRequest dataRequest = new DataRequest(params, DELETE_POST_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        public void likePost(int postId, int userId) {
            Map<String, String> params = new HashMap<>();
            params.put("post_id", String.valueOf(postId));
            params.put("user_id", String.valueOf(userId));
            DataRequest dataRequest = new DataRequest(params, LIKE_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        public void unlikePost(int postId, int userId) {
            Map<String, String> params = new HashMap<>();
            params.put("post_id", String.valueOf(postId));
            params.put("user_id", String.valueOf(userId));
            DataRequest dataRequest = new DataRequest(params, UNLIKE_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        @Override
        public void onResponse(String response) {
            // Get user data
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");

                if (success) {
                    String name = jsonResponse.getString("name");
                    String strImage = jsonResponse.getString("image");
                    int numberOfPosts = jsonResponse.getInt("posts");
                    int userFollowers = jsonResponse.getInt("followers");
                    int following = jsonResponse.getInt("following");
                    boolean isFollowed;
                    try {
                        isFollowed = jsonResponse.getBoolean("is_followed");
                    } catch (Exception e) {
                        isFollowed = false;
                    }


                    JSONArray jsonPosts = jsonResponse.getJSONArray("post_array");
                    ArrayList<Post> posts = new ArrayList<>();

                    for (int ind = 0; ind < jsonPosts.length(); ind++) {
                        JSONObject jsonPost = jsonPosts.getJSONObject(ind);

                        int id = jsonPost.getInt("id");
                        int authorId = jsonPost.getInt("author_id");
                        String image = jsonPost.getString("image");
                        String author = jsonPost.getString("author");
                        String date = jsonPost.getString("date");
                        String description = jsonPost.getString("description");
                        int likes = jsonPost.getInt("likes");
                        int comments = jsonPost.getInt("comments");
                        boolean liked = jsonPost.getBoolean("liked");

                        Post post = new Post(id, authorId, image, author, date, description, likes, comments, liked);
                        posts.add(post);
                    }
                    
                    this.onResponseListener.onUserResponse(name, strImage, numberOfPosts, userFollowers, following, isFollowed, posts);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Delete post
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean deleted = jsonResponse.getBoolean("deleted");

                if (deleted) {
                    int deleteId = jsonResponse.getInt("id");
                    this.onResponseListener.onDeletePostResponse(deleteId);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Like post
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean liked = jsonResponse.getBoolean("liked");

                if (liked) {
                    int postId = jsonResponse.getInt("post_id");
                    this.onResponseListener.onLikePostResponse(postId);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Unlike
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean unliked = jsonResponse.getBoolean("unliked");

                if (unliked) {
                    int postId = jsonResponse.getInt("post_id");
                    this.onResponseListener.onUnlikePostResponse(postId);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Follow user
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean followed = jsonResponse.getBoolean("followed");

                if (followed) {
                    this.onResponseListener.onFollowUserResponse();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Unfollow user
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean unfollowed = jsonResponse.getBoolean("unfollowed");

                if (unfollowed) {
                    this.onResponseListener.onUnfollowUserResponse();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onErrorResponse(VolleyError error) {
            this.onResponseListener.onErrorResponse(error);
        }
        
        public interface OnResponseListener {
            void onUserResponse(String name, String strImage, int numberOfPosts, int userFollowers, int following, boolean isFollowed, ArrayList<Post> posts);
            void onDeletePostResponse(int deleteId);
            void onLikePostResponse(int postId);
            void onUnlikePostResponse(int postId); 
            void onFollowUserResponse();
            void onUnfollowUserResponse();
            void onErrorResponse(VolleyError error);
        }
    }

    // endregion

    // region 6. Followers manager

    public static class FollowersManager implements Response.Listener<String>, Response.ErrorListener {

        private OnResponseListener onResponseListener;
        private Context context;

        public FollowersManager(OnResponseListener onResponseListener, Context context) {
            this.onResponseListener = onResponseListener;
            this.context = context;
        }

        public void followersRequest(int loggedUserId, int userId, boolean type) {
            Map<String, String> params = new HashMap<>();
            params.put("logged_id", String.valueOf(loggedUserId));
            params.put("id", String.valueOf(userId));
            params.put("followers", String.valueOf(type == FOLLOWERS));
            DataRequest dataRequest = new DataRequest(params, FOLLOWERS_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        public void followUser(int followerId, int followingId) {
            Map<String, String> params = new HashMap<>();
            params.put("follower_id", String.valueOf(followerId));
            params.put("following_id", String.valueOf(followingId));
            DataRequest dataRequest = new DataRequest(params, FOLLOW_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        public void unfollowUser(int followerId, int followingId) {
            Map<String, String> params = new HashMap<>();
            params.put("follower_id", String.valueOf(followerId));
            params.put("following_id", String.valueOf(followingId));
            DataRequest dataRequest = new DataRequest(params, UNFOLLOW_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        @Override
        public void onResponse(String response) {
            // Get followers
            try {
                JSONArray jsonFollowers = new JSONArray(response);

                ArrayList<User> followers = new ArrayList<>();

                for (int ind = 0; ind < jsonFollowers.length(); ind++) {
                    JSONObject jsonFollower = jsonFollowers.getJSONObject(ind);

                    int id = jsonFollower.getInt("id");
                    String name = jsonFollower.getString("name");
                    String image = jsonFollower.getString("image");
                    boolean followed = jsonFollower.getBoolean("followed");

                    User follower = new User(id, image, name, followed);
                    followers.add(follower);
                }
                this.onResponseListener.onFollowersResponse(followers);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Follow user
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean followed = jsonResponse.getBoolean("followed");

                if (followed) {
                    int id = jsonResponse.getInt("id");
                    this.onResponseListener.onFollowResponse(id);
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
                    this.onResponseListener.onUnfollowResponse(id);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            this.onResponseListener.onErrorResponse(error);
        }

        public interface OnResponseListener {
            void onFollowersResponse(ArrayList<User> followers);
            void onFollowResponse(int id);
            void onUnfollowResponse(int id);
            void onErrorResponse(VolleyError error);
        }
    }

    // endregion

    // region 7. Search manager

    public static class SearchManager implements Response.Listener<String>, Response.ErrorListener {

        private OnResponseListener onResponseListener;
        private Context context;

        public SearchManager(OnResponseListener onResponseListener, Context context) {
            this.onResponseListener = onResponseListener;
            this.context = context;
        }

        public void search(String name, int loggedUserId) {
            Map<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("id", String.valueOf(loggedUserId));
            DataRequest dataRequest = new DataRequest(params, SEARCH_USERS_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        public void followUser(int followerId, int followingId) {
            Map<String, String> params = new HashMap<>();
            params.put("follower_id", String.valueOf(followerId));
            params.put("following_id", String.valueOf(followingId));
            DataRequest dataRequest = new DataRequest(params, FOLLOW_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        public void unfollowUser(int followerId, int followingId) {
            Map<String, String> params = new HashMap<>();
            params.put("follower_id", String.valueOf(followerId));
            params.put("following_id", String.valueOf(followingId));
            DataRequest dataRequest = new DataRequest(params, UNFOLLOW_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        @Override
        public void onResponse(String response) {
            // Get followers
            try {
                JSONArray jsonFollowers = new JSONArray(response);

                ArrayList<User> users = new ArrayList<>();

                for (int ind = 0; ind < jsonFollowers.length(); ind++) {
                    JSONObject jsonFollower = jsonFollowers.getJSONObject(ind);

                    int id = jsonFollower.getInt("id");
                    String name = jsonFollower.getString("name");
                    String image = jsonFollower.getString("image");
                    boolean followed = jsonFollower.getBoolean("followed");

                    User user = new User(id, image, name, followed);
                    users.add(user);

                }
                this.onResponseListener.onUsersResponse(users);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Follow user
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean followed = jsonResponse.getBoolean("followed");

                if (followed) {
                    int id = jsonResponse.getInt("id");
                    this.onResponseListener.onFollowResponse(id);
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
                    this.onResponseListener.onUnfollowResponse(id);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            this.onResponseListener.onErrorResponse(error);
        }

        public interface OnResponseListener {
            void onUsersResponse(ArrayList<User> users);
            void onFollowResponse(int id);
            void onUnfollowResponse(int id);
            void onErrorResponse(VolleyError error);
        }
    }

    // endregion

    // region 8. Notifications manager

    public static class NotificationsManager implements Response.Listener<String>, Response.ErrorListener {

        private OnResponseListener onResponseListener;
        private Context context;

        public NotificationsManager(OnResponseListener onResponseListener, Context context) {
            this.onResponseListener = onResponseListener;
            this.context = context;
        }

        public void notificationsRequest(int loggedUserId){
            Map<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(loggedUserId));
            DataRequest dataRequest = new DataRequest(params, NOTIFICATIONS_REQUEST_URL, this, this);
            Volley.newRequestQueue(this.context).add(dataRequest);
        }

        @Override
        public void onResponse(String response) {
            // Get notifications
            try {
                JSONArray jsonNotifications = new JSONArray(response);

                ArrayList<Notification> notifications = new ArrayList<>();

                for (int ind = 0; ind < jsonNotifications.length(); ind++) {
                    JSONObject jsonNotification = jsonNotifications.getJSONObject(ind);

                    int id = jsonNotification.getInt("id");
                    int userId = jsonNotification.getInt("user_id");
                    int postId = jsonNotification.getInt("post_id");
                    String username = jsonNotification.getString("username");
                    String image = jsonNotification.getString("image");
                    int type = jsonNotification.getInt("type");
                    boolean seen = jsonNotification.getBoolean("seen");

                    Notification notification;
                    if (postId == 0) {
                        notification = new Notification(id, userId, username, image, type, seen);
                    } else {
                        notification = new Notification(id, userId, postId, username, image, type, seen);
                    }

                    notifications.add(notification);
                }

                this.onResponseListener.onNotificationsResponse(notifications);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            this.onResponseListener.onErrorResponse(error);
        }

        public interface OnResponseListener {
            void onNotificationsResponse(ArrayList<Notification> notifications);
            void onErrorResponse(VolleyError error);
        }
    }

    // endregion
}
