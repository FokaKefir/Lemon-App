package com.example.lemon_app.constants;

public class Constants {

    // Image choosing constants
    public static final int STORAGE_PERMISSION_CODE = 123;
    public static final int PICK_IMAGE_REQUEST = 1;

    // Shared Preferences constants
    public static final String SHARED_PREFS = "login";

    // Token
    public static final String TOKEN_SECRET = "lemon#is#lime";

    // Insert URLs
    public static final String REGISTER_REQUEST_URL = "http://fokakefir.go.ro/lemon_app/php/insert/register.php";
    public static final String UPLOAD_IMAGE_REQUEST_URL = "http://fokakefir.go.ro/lemon_app/php/insert/upload_image.php";
    public static final String UPLOAD_POST_REQUEST_URL = "http://fokakefir.go.ro/lemon_app/php/insert/upload_post.php";
    public static final String UPLOAD_COMMENT_REQUEST_URL = "http://fokakefir.go.ro/lemon_app/php/insert/upload_comment.php";
    public static final String LIKE_REQUEST_URL = "http://fokakefir.go.ro/lemon_app/php/insert/like.php";
    public static final String FOLLOW_REQUEST_URL = "http://fokakefir.go.ro/lemon_app/php/insert/follow.php";

    // Select URLs
    public static final String COMMENTS_REQUEST_URL = "http://fokakefir.go.ro/lemon_app/php/select/comments.php";
    public static final String LOGIN_REQUEST_URL = "http://fokakefir.go.ro/lemon_app/php/select/login.php";
    public static final String POSTS_REQUEST_URL = "http://fokakefir.go.ro/lemon_app/php/select/posts.php";
    public static final String USER_REQUEST_URL = "http://fokakefir.go.ro/lemon_app/php/select/user.php";
    public static final String FOLLOWERS_REQUEST_URL = "http://fokakefir.go.ro/lemon_app/php/select/followers.php";
    public static final String SEARCH_USERS_REQUEST_URL = "http://fokakefir.go.ro/lemon_app/php/select/search_users.php";
    public static final String NOTIFICATIONS_REQUEST_URL = "http://fokakefir.go.ro/lemon_app/php/select/notifications.php";

    // Delete URls
    public static final String DELETE_COMMENT_REQUEST_URL = "http://fokakefir.go.ro/lemon_app/php/delete/delete_comment.php";
    public static final String DELETE_POST_REQUEST_URL = "http://fokakefir.go.ro/lemon_app/php/delete/delete_post.php";
    public static final String UNLIKE_REQUEST_URL = "http://fokakefir.go.ro/lemon_app/php/delete/unlike.php";
    public static final String UNFOLLOW_REQUEST_URL = "http://fokakefir.go.ro/lemon_app/php/delete/unfollow.php";

    // Update URLs

    // Image URL
    public static final String IMAGE_URL = "http://fokakefir.go.ro/lemon_app/images/";

    // Followers
    public static final boolean FOLLOWERS = true;
    public static final boolean FOLLOWING = false;

    // Refresh type
    public static final int REFRESH_TYPE_FOLLOW = 1;
    public static final int REFRESH_TYPE_UNFOLLOW = 2;
    public static final int REFRESH_TYPE_LIKE = 3;
    public static final int REFRESH_TYPE_UNLIKE = 4;
    public static final int REFRESH_TYPE_INSERT_COMMENT = 5;
    public static final int REFRESH_TYPE_DELETE_COMMENT = 6;

}
