package com.example.lemon_app.model;

import android.media.Image;
import android.widget.ImageView;

public class Post {

    // region 1. Decl and Init

    private int id;
    private int authorId;
    private String image;
    private String author;
    private String date;
    private String description;
    private int numberOfLikes;
    private int numberOfComments;

    // endregion

    // region 2. Constructor

    public Post(int id, int authorId, String image, String author, String date, String description, int numberOfLikes, int numberOfComments) {
        this.id = id;
        this.authorId = authorId;
        this.image = image;
        this.author = author;
        this.date = date;
        this.description = description;
        this.numberOfLikes = numberOfLikes;
        this.numberOfComments = numberOfComments;
    }

    // endregion

    // region 3. Getters and Setters

    public int getId() {
        return id;
    }

    public int getAuthorId() {
        return authorId;
    }

    public String getImage() {
        return image;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    // endregion


}
