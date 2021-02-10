package com.example.lemon_app.model;

public class Comment {

    // region 1. Decl and Init

    private int postId;
    private int authorId;
    private String author;
    private String text;

    // endregion

    // region 2. Constructor

    public Comment(int postId, int authorId, String author, String text) {
        this.postId = postId;
        this.authorId = authorId;
        this.author = author;
        this.text = text;
    }

    // endregion

    public int getPostId() {
        return postId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    // Getters and Setters

}
