package com.example.MyBookShopApp.data.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class PurchaseBookKey implements Serializable {

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "book_id", nullable = false)
    private int bookId;

    public PurchaseBookKey(int userId, int bookId) {
        this.userId = userId;
        this.bookId = bookId;
    }

    public PurchaseBookKey() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
}
