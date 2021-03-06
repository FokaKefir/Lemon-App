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
    private boolean liked;

    // endregion

    // region 2. Constructor

    public Post(int id, int authorId, String image, String author, String date, String description, int numberOfLikes, int numberOfComments, boolean liked) {
        this.id = id;
        this.authorId = authorId;
        this.image = image;
        this.author = author;
        this.date = date;
        this.description = description;
        this.numberOfLikes = numberOfLikes;
        this.numberOfComments = numberOfComments;
        this.liked = liked;
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

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void increaseLikes() {
        this.numberOfLikes++;
    }

    public void decreaseLikes() {
        this.numberOfLikes--;
    }

    public void increaseComments(){
        this.numberOfComments++;
    }

    public void decreaseComments(){
        this.numberOfComments--;
    }

    // endregion


}
