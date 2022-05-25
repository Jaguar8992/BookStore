package com.example.MyBookShopApp.data.entities;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tag2book")
public class TagToBook {

    @EmbeddedId
    private TagToBookKey id;

    @Column(name = "tag_id", nullable = false, insertable = false, updatable = false)
    private long tagId;

    @Column(name = "book_id", nullable = false, insertable = false, updatable = false)
    private long bookId;
}
