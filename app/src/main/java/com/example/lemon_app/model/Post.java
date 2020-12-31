package com.example.lemon_app.model;

import android.media.Image;
import android.widget.ImageView;

public class Post {

    // region 1. Decl and Init

    private int image;
    private String author;
    private String date;
    private String description;
    private int numberOfLikes;
    private int numberOfComments;

    // endregion

    // region 2. Constructor

    public Post(int image, String author, String date, String description, int numberOfLikes, int numberOfComments) {
        this.image = image;
        this.author = author;
        this.date = date;
        this.description = description;
        this.numberOfLikes = numberOfLikes;
        this.numberOfComments = numberOfComments;
    }

    // endregion

    // region 3. Getters and Setters

    public int getImage() {
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

    public void setImage(int image) {
        this.image = image;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    // endregion


}
