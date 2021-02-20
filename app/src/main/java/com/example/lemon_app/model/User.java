package com.example.lemon_app.model;

public class User {

    // region 1. Decl and Init

    private int id;
    private String image;
    private String name;
    private boolean followed;

    // endregion

    // region 2. Constructor

    public User(int id, String image, String name, boolean followed) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.followed = followed;
    }


    // endregion

    // region 3. Getters and Setters

    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    // endregion

}
