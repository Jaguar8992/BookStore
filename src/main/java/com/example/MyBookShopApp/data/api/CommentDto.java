package com.example.MyBookShopApp.data.api;

public class CommentDto {
    private int bookId;
    private String text;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
