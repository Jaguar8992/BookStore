package com.example.MyBookShopApp.data.api;

import java.io.Serializable;

public class GradeRequest {
    private Integer bookId;
    private Integer value;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "GradeRequest{" +
                "bookId=" + bookId +
                ", value=" + value +
                '}';
    }
}
