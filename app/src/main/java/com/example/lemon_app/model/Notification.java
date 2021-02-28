package com.example.lemon_app.model;

public class Notification {

    // region 0. Constants

    public static final int TYPE_FOLLOW = 1;
    public static final int TYPE_LIKE = 2;
    public static final int TYPE_COMMENT = 3;

    // endregion

    // region 1. Decl and Init

    private int id;
    private int userId;
    private int postId;
    private String username;
    private String image;
    private int type;
    private boolean seen;

    // endregion

    // region 2. Constructor

    public Notification(int id, int userId, int postId, String username, String image, int type, boolean seen) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.username = username;
        this.image = image;
        this.type = type;
        this.seen = seen;
    }

    public Notification(int id, int userId, String username, String image, int type, boolean seen) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.image = image;
        this.type = type;
        this.seen = seen;
    }

    // endregion

    // region 3. Getters and Setters

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getPostId() {
        return postId;
    }

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }

    public int getType() {
        return type;
    }

    public boolean isSeen() {
        return seen;
    }

    // endregion

}
