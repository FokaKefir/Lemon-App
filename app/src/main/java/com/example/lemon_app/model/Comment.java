package com.example.lemon_app.model;

public class Comment {

    // region 1. Decl and Init

    private int id;
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

    public Comment(int id, int postId, int authorId, String author, String text) {
        this.id = id;
        this.postId = postId;
        this.authorId = authorId;
        this.author = author;
        this.text = text;
    }

    // endregion

    // region 3. Getters and Setters

    public int getId() {
        return id;
    }

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

    public void setId(int id) {
        this.id = id;
    }

    // endregion

}
