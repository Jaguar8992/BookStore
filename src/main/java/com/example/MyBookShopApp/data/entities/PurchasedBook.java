package com.example.MyBookShopApp.data.entities;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "purchased_book")
public class PurchasedBook {

    @EmbeddedId
    private PurchaseBookKey id;

    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private int userId;

    @Column(name = "book_id", nullable = false, insertable = false, updatable = false)
    private int bookId;

    public PurchasedBook() {
    }

    public PurchasedBook(PurchaseBookKey id) {
        this.id = id;
    }
}
