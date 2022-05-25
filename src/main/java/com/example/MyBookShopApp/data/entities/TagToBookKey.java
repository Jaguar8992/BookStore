package com.example.MyBookShopApp.data.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class TagToBookKey implements Serializable {

    @Column(name = "tag_id", nullable = false, insertable = false, updatable = false)
    private long tagId;

    @Column(name = "book_id", nullable = false, insertable = false, updatable = false)
    private long bookId;
}
